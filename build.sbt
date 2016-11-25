name := "spring-kafka-test"

organization := "com.github.dnvriend"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

val AkkaVersion = "2.4.14"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "org.springframework.kafka" % "spring-kafka" % "1.1.1.RELEASE",
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % Test
)

enablePlugins(PlayScala)
