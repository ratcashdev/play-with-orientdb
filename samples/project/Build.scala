import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "OrientDB-Sample"
  val appVersion      = "1.0-SNAPSHOT"

  // IMPORTANT: this is needed for the OrientDB plugin to know
  // which version of the DB is to be used. This variable is used inside build.sbt
  val orientDBVersion = "1.3.0"

  val appDependencies = Seq(
    javaCore,

    // disable JDBC and javaBean. they mess up classes for OrientDB
    //javaJdbc,
    //javaEbean

    // Add OrientDBPlugin as a component
    "ratcash.net" % "play-with-orientdb_2.10" % "1.0-SNAPSHOT",

    // add dependencies for the classes to be used
    "com.orientechnologies" % "orientdb-core" % {orientDBVersion},
    "com.orientechnologies" % "orient-commons" % {orientDBVersion},
    "com.orientechnologies" % "orientdb-object" % {orientDBVersion}
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    
    // needed for OrientDB plugin (after you did a publish-local for the plugin's project)
    resolvers += "Local Play Repository" at "d:/Downloads/play-2.1-RC1/repository/local",

    // Alternatively, you define GitHub as dependency for OrientDBPlugin.
    //resolvers += Resolver.url("GitHub OrientDBPlugin Repository", url("http://ratcashdev.github.com/releases/"))(Resolver.ivyStylePatterns)

    // needed for the OrientDB-specific classes
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/public/"
  )
  
}
