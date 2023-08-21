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