val sharedSettings = Seq(
  organization := "io.manuelbernhardt",
  version := "1.0-SNAPSHOT"
) ++ scalariformSettings ++ play.Project.playScalaSettings

lazy val crud = project.
  settings(sharedSettings :_*).
  settings(
    libraryDependencies += "com.typesafe.play" %% "play" % "2.2.1",
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
  )

lazy val `user-sample` = project.
  settings(sharedSettings :_*).
  dependsOn(crud)

val root = project.in(file(".")).
  settings(sharedSettings :_*).
  aggregate(crud, `user-sample`)