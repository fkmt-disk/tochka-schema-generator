lazy val root = (project in file("."))
  .settings(
    sbtPlugin := true,
    name := "tochka-schema-generator",
    organization := "orz.mongo.tochka",
    version := "0.1",
    scalaVersion := "2.10.4"
  )
