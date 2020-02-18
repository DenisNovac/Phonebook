name := "Phonebook"

version := "0.1"

scalaVersion := "2.12.10"
//scalaVersion := "2.13.1"  // не поддерживается http4s



// https://mvnrepository.com/artifact/org.typelevel/cats
libraryDependencies += "org.typelevel" %% "cats" % "0.9.0"
// https://mvnrepository.com/artifact/org.typelevel/cats-effect
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.1"



val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)


val http4sVersion = "0.20.16"  // stable версия

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion
)


libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.30"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.30"
libraryDependencies += "org.log4s" %% "log4s" % "1.8.2"

lazy val doobieVersion = "0.8.8"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion
)

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test


testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v", "-s")
scalacOptions ++= Seq("-Ypartial-unification")


libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.12.2"  // чтение конфига