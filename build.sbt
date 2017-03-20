lazy val akkaVersion = "2.4.17"

lazy val emitreader = (project in file(".")).
  settings(
    organization := "com.github.mpeltonen",
    name := "emit-reader",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.12.1",
    resolvers += "sparetimelabs" at "http://www.sparetimelabs.com/maven2/",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.sparetimelabs" % "purejavacomm" % "1.0.1"
    )
  )
