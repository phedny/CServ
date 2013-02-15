Limesco CServ
=============

CServ is the central server system used by Limesco. The project is based on Java
OSGi and relies on some components released by the Amdatu project.

Build status
------------

Travis-CI is used for continuous integration. The build status is reported:

[![Build Status](https://travis-ci.org/Limesco/CServ.png)](https://travis-ci.org/Limesco/CServ)

Environment requirements
------------------------

CServ depends on the following external systems:
- MongoDb

Importing in Eclipse
--------------------

To import this project in Eclipse, `bndtools` must be installed. For
installation instructions, see http://bndtools.org/installation.html.

When `bndtools` is installed in Eclipse, go to File -> Import... -> Existing
Projects into Workspace. Select the CServ repository root directory, and finish
the wizard. Also, go to Window -> Open Perspective -> Other... -> Bndtools.

To the left, all `nl.limesco.cserv.*` should be visible. Below that, a
"Repositories" tab should be visible that shows items under "Workspace CServ"
and "Bndtools Hub". Continue below to finish setting up CServ.

Configuration
-------------

For configuration, the OSGi ConfigAdmin is used in combination with the Felix
FileInstall MetaType provider. Configuration files need to be present in the
nl.limesco.cserv/conf/ directory and have a .xml extension. This directory is
not checked into the Git repository, since it contains confidential information,
like credentials for external services.

Instead, in the nl.limesco.cserv/conf-samples/ directory are some template
configuration files. To get started, copy those files into the conf/ directory
and fill the empty configuration entries. Configuration files for which no
suitable entries can be filled may be left out, resulting in dependent OSGi
services to be unavailable.

Users and groups
----------------

After configuring, the CServ instance will have access to a Mongo database.
This Mongo database should contain a user to set up the rest of the instance.
First, create your own user (replace 'myuser' and 'mypassword'):

    > use cserv
    switched to db cserv
    > db.auth("cserv", "yourpassword")
    1
    > db.useradmin.insert({
    ... 'type': NumberInt(1),
    ... 'name': 'myuser',
    ... 'properties': {},
    ... 'credentials': {'password': 'mypassword'}
    ... })
    >

The password is stored plain-text in this query. When you first log in
succesfully, it will be immediately removed and restored in a more secure
fashion by the `LoginHelperServiceImpl`. If you want to link this user to a
Limesco account later on, you should set
'nl%2Elimesco%2Ecserv%2Eauth%2EaccountId' to its account ID later in the
"properties" field.

Then, create two groups with your user in it (replace 'myuser'):

    > db.useradmin.insert({
    ... 'type': NumberInt(2),
    ... 'name': "USER",
    ... 'members': ["myuser"],
    ... 'properties': {},
    ... 'credentials': {},
    ... 'requiredMembers': []
    ... })
    > db.useradmin.insert({
    ... 'type': NumberInt(2),
    ... 'name': "ADMIN",
    ... 'members': ["myuser"],
    ... 'properties': {},
    ... 'credentials': {},
    ... 'requiredMembers': []
    ... })

Now, you are ready to start the CServ instance and administer it as 'myuser'.

Running CServ
-------------

Make sure Mongo is running. Then, inside Eclipse, right click
`nl.limesco.cserv` in the package explorer to the left, Debug As, Bnd OSGi Run
Launcher. A new Apache Felix Gogo Console will open. After a few seconds, CServ
is loaded. To see any unavailable packages, type "dm notavail", there should
be no output.

To add users and SIMs, see the `limesco-pm` project at
https://github.com/Limesco/limesco-pm, especially `bin/ui.pl`.
