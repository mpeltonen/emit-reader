name := "emit-reader"

organization := "com.github.mpeltonen"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.0-M7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.10.0-M7" % "2.1-M2",
  "com.sparetimelabs" % "purejavacomm" % "0.0.8"
)

resolvers += "sparetimelabs" at "http://www.sparetimelabs.com/maven2/"
