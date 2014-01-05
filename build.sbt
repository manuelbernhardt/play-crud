name := "play-crud"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
//  "com.scalatags" % "scalatags_2.10" % "0.1.4"
)

play.Project.playScalaSettings

scalariformSettings

com.jamesward.play.BrowserNotifierPlugin.livereload

val root = project.in(file(".")).dependsOn(RootProject(uri("https://github.com/lihaoyi/scalatags.git")))