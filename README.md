In order to use OrientDB from Play (2.1) you need to &nbsp;do the following:

# Execute&nbsp;
{code}
git clone git@github.com:ratcashdev/play-with-orientdb.git
{code}
to a subfolder under your Play 2.x installation.
# Issue "play publish-local" from the play-with-orientdb/src/ folder


Next in the project that will use this plugin, do the following:
# Add
{code}
"ratcash.net" % "play-with-orientdb_2.10" % "1.0-SNAPSHOT"
{code}
to your Build.scala, under appDependencies (more here ﻿﻿[lg:Working with Play 2.x Plugins])
# Disable EBean in you Build.scala file
# Edit
{code}
conf/play.plugins
{code}
to include:
{code}
10000:modules.orientdb.ODBPlugin
{code}
# if you would like to connect to remote databases, you'll also need to download and install OrientDB itself and create a *remote:local/<YourDBName>* database in it.
# Copy the src/conf/db.config file from the plugin to the project's /conf folder that is going to use the plugin (this is only needed if you'll use embedded OrientDB database, i.e. local or memory)
# Finally, add the following to your conf/application.conf
{code}
# OrientDB configuration
# ~~~~~
#orientdb.db.url = "remote:localhost/MyDB"
orientdb.db.url = "memory:temp"
#orientdb.db.username = "admin"
#orientdb.db.password = "admin"
#orientdb.db.graph.url = "memory:temp"
orientdb.db.config.file = "/conf/db.config"
orientdb.db.entities.package = "models.*"
orientdb.db.open-in-vieww.documentdb = "true"
orientdb.db.open-in-vieww.objectdb = "true"
orientdb.db.open-in-vieww.graphdb = "true"
{code}


NOTE: Play 2.1-RC1 can't currently do 'publish-local' when there's no ROUTES file, but when there is one (in the plugin), then it overrides the application-specific routes. Probably this will only work in the final 2.1 framework.