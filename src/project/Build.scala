import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "OrientDB Module for Play 2x"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      javaCore,
      //javaJdbc,
      //javaEbean
      // Add your project dependencies here,

      "com.orientechnologies" % "orientdb-core" % "1.3.0-SNAPSHOT",

      "com.orientechnologies" % "orient-commons" % "1.3.0-SNAPSHOT",

      "com.orientechnologies" % "orientdb-client" % "1.3.0-SNAPSHOT",

      "com.orientechnologies" % "orientdb-nativeos" % "1.3.0-SNAPSHOT",
       
      "com.orientechnologies" % "orientdb-server" % "1.3.0-SNAPSHOT",
       
      "com.orientechnologies" % "orientdb-object" % "1.3.0-SNAPSHOT",

      //"com.orientechnologies" % "orientdb-parent" % "1.3.0-SNAPSHOT",

      "com.orientechnologies" % "orientdb-distributed" % "1.3.0-SNAPSHOT",

      "com.orientechnologies" % "orientdb-enterprise" % "1.3.0-SNAPSHOT",

      "com.hazelcast" % "hazelcast" % "2.1.2",

      "org.javassist" % "javassist" % "3.16.1-GA",

      "net.java.dev.jna" % "jna" % "3.4.0",

      "com.sun.mail" % "javax.mail" % "1.4.5",

      "org.reflections" % "reflections" % "0.9.8",

      "org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.0.Final",

      "javax.activation" % "activation" % "1.1.1"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
