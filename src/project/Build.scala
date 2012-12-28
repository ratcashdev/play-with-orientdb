import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "Play with OrientDB"
    val appVersion      = "1.0-SNAPSHOT"

    // IMPORTANT. The plugin can't be compiled without this
    val orientDBVersion = "1.3.0-SNAPSHOT"

    val appDependencies = Seq(
      javaCore

      // disable JDBC and javaEBean
      //javaJdbc,
      //javaEbean

      // Add your project dependencies here,
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here   

    )

}
