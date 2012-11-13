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
- MongoDb for storage of persistent data
- PhantomJS for executing integration tests (assumes "phantomjs" in $PATH)

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
