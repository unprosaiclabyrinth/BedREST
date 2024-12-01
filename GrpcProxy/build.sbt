
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.1"

lazy val root = (project in file("."))
  .settings(
    name := "GrpcProxy"
  )

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M19",
  "com.softwaremill.sttp.client4" %% "slf4j-backend" % "4.0.0-M19",
  "com.softwaremill.sttp.client4" %% "upickle" % "4.0.0-M19",

  "io.grpc" % "grpc-netty" % "1.65.1",          // gRPC runtime
  "io.grpc" % "grpc-protobuf" % "1.65.1",       // Protobuf support for gRPC
  "io.grpc" % "grpc-stub" % "1.64.0",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "0.11.17",

  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "com.typesafe" % "config" % "1.4.3",
  "org.scalatest" %% "scalatest" % "3.2.19" % "test",
)

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

assembly / assemblyJarName := "grpc-proxy-fat.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) if xs.contains("spring.schemas") => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) if xs.contains("spring.handlers") => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case "proxy.conf" => MergeStrategy.concat
  case "logback.xml" => MergeStrategy.first
  case x if x.endsWith(".proto") => MergeStrategy.rename
  case _ => MergeStrategy.first
}