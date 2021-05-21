name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.11"

libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs
libraryDependencies += guice

// depedency on play - mongo connection
libraryDependencies ++= Seq(
  "uk.co.panaxiom" %% "play-jongo" % "2.0.0-jongo1.3"
)

// dependency for google cloud message service (push service)
libraryDependencies += "com.google.android.gcm" % "gcm-server" % "1.0.2"
libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"
// dependency for restfb
libraryDependencies += "com.restfb" % "restfb" % "1.6.14"

resolvers += "GCM Server Repository" at "https://raw.github.com/slorber/gcm-server-repository/master/releases/"
