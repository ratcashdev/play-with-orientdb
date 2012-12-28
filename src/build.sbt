name := "Play with OrientDB"

version := "1.0-SNAPSHOT"

organization := "ratcash.net"

//scalaVersion := "2.10.0-RC1"

// libraryDependencies += "com.orientechnologies" % "orientdb" % {orientDBVersion}

libraryDependencies += "com.orientechnologies" % "orientdb-core" % {orientDBVersion}

libraryDependencies += "com.orientechnologies" % "orient-commons" % {orientDBVersion}

libraryDependencies += "com.orientechnologies" % "orientdb-client" % {orientDBVersion}

libraryDependencies += "com.orientechnologies" % "orientdb-nativeos" % {orientDBVersion}
 
libraryDependencies += "com.orientechnologies" % "orientdb-server" % {orientDBVersion}
 
libraryDependencies += "com.orientechnologies" % "orientdb-object" % {orientDBVersion}

libraryDependencies += "com.orientechnologies" % "orientdb-enterprise" % {orientDBVersion}

libraryDependencies += "com.orientechnologies" % "orientdb-distributed" % {orientDBVersion}

libraryDependencies += "com.hazelcast" % "hazelcast" % "2.1.2"

libraryDependencies += "org.javassist" % "javassist" % "3.16.1-GA"

libraryDependencies += "net.java.dev.jna" % "jna" % "3.4.0"

libraryDependencies += "com.sun.mail" % "javax.mail" % "1.4.5"

libraryDependencies += "org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.0.Final"

libraryDependencies += "org.reflections" % "reflections" % "0.9.8"

libraryDependencies += "javax.activation" % "activation" % "1.1.1"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/public/"