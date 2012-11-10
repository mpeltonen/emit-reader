name := "emit-reader"

organization := "com.github.mpeltonen"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.0-RC2"

unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "src" / "main" / "conf") }

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.10.0-RC2" % "2.1.0-RC2",
  "com.sparetimelabs" % "purejavacomm" % "0.0.9"
)

resolvers += "sparetimelabs" at "http://www.sparetimelabs.com/maven2/"
