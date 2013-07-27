name := "emit-reader"

organization := "com.github.mpeltonen"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.0",
  "com.sparetimelabs" % "purejavacomm" % "0.0.9"
)

resolvers += "sparetimelabs" at "http://www.sparetimelabs.com/maven2/"
