name := "Phonebook"

version := "0.1"

scalaVersion := "2.13.1"

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test
testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v", "-s")
