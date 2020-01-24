name := "Phonebook"

version := "0.1"

scalaVersion := "2.12.10"

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

val http4sVersion = "0.20.16"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion
)

scalacOptions ++= Seq("-Ypartial-unification")

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test
testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v", "-s")
