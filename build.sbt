import sbt._
import Keys._
import org.typelevel.sbt.gha.JavaSpec.Distribution.Zulu

val jacksonVersion = "2.15.1"

lazy val root = (project in file("."))
  .settings(
    name := "jackson-module-scala-duration",
    organization := "com.github.pjfanning",

    ThisBuild / scalaVersion := "2.13.10",
    ThisBuild / crossScalaVersions := Seq("2.11.12", "2.12.17", "2.13.10", "3.2.2"),

    sbtPlugin := false,

    scalacOptions ++= Seq("-deprecation", "-Xcheckinit", "-encoding", "utf8", "-g:vars", "-unchecked", "-optimize"),
    Test / parallelExecution := true,
    homepage := Some(new java.net.URL("https://github.com/pjfanning/jackson-module-scala-duration/")),
    description := "A library for serializing/deserializing scala durations using Jackson.",

    publishMavenStyle := true,
    publishTo := sonatypePublishToBundle.value,

    licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),

    scmInfo := Some(
      ScmInfo(
        url("https://github.com/pjfanning/jackson-module-scala-duration"),
        "scm:git@github.com:pjfanning/jackson-module-scala-duration.git"
      )
    ),

    developers := List(
      Developer(id="pjfanning", name="PJ Fanning", email="", url=url("https://github.com/pjfanning"))
    ),

    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion % Test,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    ),

    // enable publishing the main API jar
    publishArtifact := true,

    // build.properties
    Compile / resourceGenerators += Def.task {
      val file = (Compile / resourceManaged).value / "com" / "github" / "pjfanning" / "scala" / "duration" / "build.properties"
      val contents = "version=%s\ngroupId=%s\nartifactId=%s\n".format(version.value, organization.value, name.value)
      IO.write(file, contents)
      Seq(file)
    }.taskValue,

    ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec(Zulu, "8")),
    ThisBuild / githubWorkflowPublishTargetBranches := Seq(
      RefPredicate.Equals(Ref.Branch("main")),
      RefPredicate.StartsWith(Ref.Tag("v"))
    ),

    ThisBuild / githubWorkflowPublish := Seq(
      WorkflowStep.Sbt(
        List("ci-release"),
        env = Map(
          "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
          "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
          "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
          "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
          "CI_SNAPSHOT_RELEASE" -> "+publishSigned"
        )
      )
    )
  )
