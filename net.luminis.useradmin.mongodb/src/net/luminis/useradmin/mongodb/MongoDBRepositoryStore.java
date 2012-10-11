/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.luminis.useradmin.mongodb;

import static net.luminis.useradmin.mongodb.MongoSerializerHelper.NAME;
import static net.luminis.useradmin.mongodb.MongoSerializerHelper.TYPE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.amdatu.mongo.MongoDBService;
import org.apache.felix.useradmin.RoleRepositoryStore;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

/**
 * Provides a repository store that uses MongoDB for storing the role information.
 * <p>
 * This service can also be configured at runtime by using the PID {@value #PID}.<br/>
 * The configuration options recognized by this service are:
 * </p>
 * <dl>
 * <dt>"useradmin.mongodb.collection"</dt>
 * <dd>The name of the database collection to use for this store. This value is mandatory;</dd>
 * </dl>
 * <p>
 * By default, the following values are used:
 * </p>
 * <table>
 * <tr><td>"<tt>useradmin.mongodb.collection</tt>"</td><td>"<tt>useradmin</tt>"</td></tr>
 * </table>
 * <p>
 * This class is thread-safe.
 * </p>
 */
public class MongoDBRepositoryStore implements RoleProvider, RoleRepositoryStore, UserAdminListener, ManagedService {
    
    /** The PID for the managed service reference. */
    public static final String PID = "net.luminis.useradmin.mongodb"; 

    /** The name of the MongoDB collection to use. */
    private static final String KEY_MONGODB_COLLECTION_NAME = "useradmin.mongodb.collection";

    /** Default MongoDB collection */
    private static final String DEFAULT_MONGODB_COLLECTION = "useradmin";

    private final MongoSerializerHelper m_helper;
    
    private volatile LogService m_log;
    private volatile MongoDBService m_mongoDB;
    private volatile AtomicReference<String> m_collectionNameRef;

    /**
     * Creates a new {@link MongoDBRepositoryStore} instance.
     */
    public MongoDBRepositoryStore() {
        m_collectionNameRef = new AtomicReference<String>(DEFAULT_MONGODB_COLLECTION);
        m_helper = new MongoSerializerHelper(this);
    }

    @Override
    public boolean addRole(Role role) throws IOException {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null!");
        }
        
        try {
            DBCollection coll = getCollection();
            
            DBCursor cursor = coll.find(getTemplateObject(role));
            try {
                if (cursor.hasNext()) {
                    // Role already exists...
                    return false;
                }
            } finally {
                cursor.close();
            }
            
            // Role does not exist; insert it...
            DBObject data = m_helper.serialize(role);
            
            WriteResult result = coll.insert(data);

            return (result.getLastError() != null) && result.getLastError().ok();
        }
        catch (MongoException e) {
            m_log.log(LogService.LOG_WARNING, "Add role failed!", e);
            throw new IOException("AddRole failed!", e);
        }
    }

    @Override
    public void close() throws IOException {
    	m_log.log(LogService.LOG_INFO, "MongoDB user admin repository closed...");
    }

    @Override
    public Role[] getAllRoles() throws IOException {
        try {
            List<Role> roles = new ArrayList<Role>();
            
            DBCollection coll = getCollection();

            DBCursor cursor = coll.find();
            try {
                while (cursor.hasNext()) {
                    roles.add(m_helper.deserialize(cursor.next()));
                }
            } finally {
                cursor.close();
            }

            return roles.toArray(new Role[roles.size()]);
        }
        catch (MongoException e) {
            m_log.log(LogService.LOG_WARNING, "Get all roles failed!", e);
            throw new IOException("GetAllRoles failed!", e);
        }
    }

    @Override
    public Role getRole(String name) {
        DBCollection coll = getCollection();

        DBCursor cursor = coll.find(getTemplateObject(name));
        try {
            if (cursor.hasNext()) {
                return m_helper.deserialize(cursor.next());
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    @Override
    public Role getRoleByName(String name) throws IOException {
        try {
            return getRole(name);
        }
        catch (MongoException e) {
            m_log.log(LogService.LOG_WARNING, "Get role by name failed!", e);
            throw new IOException("GetRoleByName failed!", e);
        }
    }
    
    @Override
    public void initialize() throws IOException {
    	m_log.log(LogService.LOG_INFO, "MongoDB user admin repository initialized...");
    }

    @Override
    public boolean removeRole(Role role) throws IOException {
        try {
            DBCollection coll = getCollection();

            WriteResult result = coll.remove(getTemplateObject(role));

            return (result.getLastError() != null) && result.getLastError().ok();
        }
        catch (MongoException e) {
            m_log.log(LogService.LOG_WARNING, "Remove role failed!", e);
            throw new IOException("RemoveRole failed!", e);
        }
    }
    
    @Override
    public void roleChanged(UserAdminEvent event) {
        if (UserAdminEvent.ROLE_CHANGED == event.getType()) {
            // Only the changes are interesting, as the creation and 
            // removal are already caught by #addRole and #removeRole.... 
            Role changedRole = event.getRole();

            try {
                DBCollection coll = getCollection();

                DBObject query = getTemplateObject(changedRole);
                DBObject update = m_helper.serializeUpdate(changedRole);

                WriteResult result = coll.update(query, update, false /* upsert */, false /* multi */);

                if (result.getLastError() != null && !result.getLastError().ok()) {
                    m_log.log(LogService.LOG_WARNING, "Failed to update changed role: " + changedRole.getName());
                }
            }
            catch (MongoException e) {
                m_log.log(LogService.LOG_WARNING, "Failed to update changed role: " + changedRole.getName(), e);
            }
        }
    }
    
    /**
     * @param log the log-service to set, cannot be <code>null</code>.
     */
    public void setLogService(LogService log) {
        m_log = log;
    }

    /**
     * @param mongoDB the mongoDB-service to set, cannot be <code>null</code>.
     */
    public void setMongoDBService(MongoDBService mongoDB) {
		m_mongoDB = mongoDB;
	}

	@Override
	@SuppressWarnings({"rawtypes","unchecked"})
    public void updated(Dictionary properties) throws ConfigurationException {
        String newCollectionName = DEFAULT_MONGODB_COLLECTION;
        
        if (properties != null) {
            // Use values supplied...
            newCollectionName = getMandatoryProperty(properties, KEY_MONGODB_COLLECTION_NAME);
        }

        String oldCollName;
        do {
        	oldCollName = m_collectionNameRef.get();
        }
        while (!m_collectionNameRef.compareAndSet(oldCollName, newCollectionName));
        
    	m_log.log(LogService.LOG_INFO, "MongoDB user admin repository configuration updated to use " + newCollectionName);
    }
    
    /**
     * Returns the current database collection.
     * 
     * @return the database collection to work with, cannot be <code>null</code>.
     * @throws MongoException in case no connection to MongoDB exists.
     */
    private DBCollection getCollection() {
    	DB db = m_mongoDB.getDB();
        if (db == null) {
            throw new MongoException("No connection to MongoDB?!");
        }
        String collName = m_collectionNameRef.get();
        if (collName == null) {
        	throw new MongoException("No database collection name?!");
        }
        return db.getCollection(collName);
    }

    /**
     * Returns the mandatory value for the given key.
     * 
     * @param properties the properties to get the mandatory value from;
     * @param key the key of the value to retrieve;
     * @return the value, never <code>null</code>.
     * @throws ConfigurationException in case the given key had no value.
     */
    private String getMandatoryProperty(Dictionary<Object, Object> properties, String key) throws ConfigurationException {
        String result = getProperty(properties, key);
        if (result == null || "".equals(result.trim())) {
            throw new ConfigurationException(key, "cannot be null or empty!");
        }
        return result;
    }
    
    /**
     * Returns the value for the given key.
     * 
     * @param properties the properties to get the value from;
     * @param key the key of the value to retrieve;
     * @return the value, can be <code>null</code> in case no such key is present.
     * @throws ConfigurationException in case the given key had no value.
     */
    private String getProperty(Dictionary<Object, Object> properties, String key) throws ConfigurationException {
        Object result = properties.get(key);
        if (result == null || !(result instanceof String)) {
            return null;
        }
        return (String) result;
    }
    
    /**
     * Creates a template object for the given role.
     * 
     * @param role the role to create a template object for, cannot be <code>null</code>.
     * @return a template object for MongoDB, never <code>null</code>.
     */
    private DBObject getTemplateObject(Role role) {
        BasicDBObject query = new BasicDBObject();
        query.put(TYPE, role.getType());
        query.put(NAME, role.getName());
        return query;
    }
    
    /**
     * Creates a template object for the given (role)name.
     * 
     * @param name the name of the role to create a template object for, cannot be <code>null</code>.
     * @return a template object for MongoDB, never <code>null</code>.
     */
    private DBObject getTemplateObject(String name) {
        BasicDBObject query = new BasicDBObject();
        query.put(NAME, name);
        return query;
    }
}
