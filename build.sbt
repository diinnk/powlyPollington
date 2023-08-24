import scala.io.Source
import scala.util.Try

ThisBuild / scalaVersion := "2.13.11"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val powlyPollington = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """PowlyPollington""",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
    )
  )

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "2.1.214",
)

def getRawLastLine: String = {
  val readmeFile = Source.fromFile("VERSION")
  val versionLines = (for {
    x <- readmeFile.getLines()
    if x.trim.nonEmpty
  } yield x.replaceAll("<.*>", "")).toList.zipWithIndex
  val lastLine = Try(versionLines.maxBy(_._2)._2).toOption.getOrElse(0)
  readmeFile.close()
  versionLines.find(_._2 == lastLine).getOrElse(("",0))._1.trim
}

val rawLastLine: String = getRawLastLine
//swaggerAPIVersion := rawLastLine

Compile / sourceGenerators += Def.task {
  val githubActionsBuild = sys.env.getOrElse("BUILD_NUMBER", "0-SNAPSHOT")
  val file = (Compile / sourceManaged).value / "com/github/diinnk/powly/VersionInfo.scala"
  val rawVerNum = rawLastLine.replaceFirst(" -.*$", "")
  val rawVerText = rawLastLine.replaceFirst(rawVerNum, "").stripPrefix(" - ")

  IO.write(
    file,
    s"""package com.github.diinnk.powly
       |object VersionInfo {
       |  val rawVerNum: String = "$rawVerNum"
       |  val rawVerText: String = "$rawVerText"
       |  val buildNumber: String = "$githubActionsBuild"
       |  val verNumCombo: String = "$rawVerNum.$githubActionsBuild"
       |  val finalVerStr: String = "PowlyPollington: "+verNumCombo+" - $rawVerText"
       |}""".stripMargin
  )
  Seq(file)
}.taskValue