
name := "dispatcher"

version := "0.1"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

resolvers += ScalaToolsSnapshots

scalacOptions += "-unchecked"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-core" % "0.8.5" % "compile->default",
  "net.databinder" %% "dispatch-lift-json" % "0.8.5" % "compile->default",
  "net.databinder" %% "dispatch-http" % "0.8.5" % "compile->default",
  "net.databinder" %% "dispatch-http-json" % "0.8.5" % "compile->default",
  "org.scala-tools.testing" %% "specs" % "1.6.9-SNAPSHOT" % "test"
)
