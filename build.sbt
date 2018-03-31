lazy val akkaVersion = "2.4.20"
lazy val scalaFxVersion = "8.0.144-R12"

lazy val emitreader = (project in file(".")).
  settings(
    organization := "com.github.mpeltonen",
    name := "emit-reader",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.12.4",
    resolvers += "sparetimelabs" at "http://www.sparetimelabs.com/maven2/",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.github.purejavacomm" % "purejavacomm" % "1.0.2",
      "org.scalafx" %% "scalafx" % scalaFxVersion,
      "io.reactivex.rxjava2" % "rxjavafx" % "2.2.2"
    ),
    unmanagedJars in Compile += Attributed.blank(file(s"${System.getProperty("java.home")}/lib/ext/jfxrt.jar"))
  )
