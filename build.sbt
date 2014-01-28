name := "play-crud"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
)

play.Project.playScalaSettings

scalariformSettings

com.jamesward.play.BrowserNotifierPlugin.livereload

val root = project.in(file("."))