import com.github.retronym.SbtOneJar._

name := """conductor"""

version := "2.03.11"

scalaVersion := "2.10.3"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "spray nightlies" at "http://nightlies.spray.io"

resolvers += "litef nightlies" at "https://raw.githubusercontent.com/ivan-cukic/litef-maven-repository/master/snapshots/"

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](version)

buildInfoKeys ++= Seq[BuildInfoKey](
        "gitHash" -> new java.lang.Object() {
            override def toString(): String = {
                try {
                    val extracted = new java.io.InputStreamReader(
                        java.lang.Runtime.getRuntime().exec("git rev-parse HEAD").getInputStream())
                    (new java.io.BufferedReader(extracted)).readLine()
                } catch {
                    case t: Throwable => "get git hash failed"
                }
            }
        }.toString()
    )

buildInfoPackage := "info"

libraryDependencies ++= {
    object V {
        val spray = "1.3.1"
        val akka  = "2.3.9"
    }
    Seq(
        "com.typesafe.akka"  %% "akka-actor"         % V.akka          withSources,
        "com.typesafe.akka"  %% "akka-slf4j"         % V.akka          withSources,
        "com.typesafe.akka"  %% "akka-testkit"       % V.akka          % "test",
        "ch.qos.logback"      % "logback-classic"    % "1.0.13"        withSources,
        "io.spray"            % "spray-can"          % V.spray         withSources,
        "io.spray"            % "spray-routing"      % V.spray         withSources,
        // "io.spray"            % "spray-testkit"      % V.spray         % "test",
        "io.spray"           %% "spray-json"         % "1.2.3"         withSources,
        "io.spray"            % "spray-client"       % V.spray         withSources,
        "io.spray"            % "spray-http"         % V.spray         withSources,
        // "org.specs2"         %% "specs2"             % "1.14"          % "test",
        // "com.novocode"        % "junit-interface"    % "0.7"           % "test->default",
        "com.typesafe.slick" %% "slick"              % "2.0.0"         withSources,
        // "org.slf4j"           % "slf4j-nop"          % "1.6.4"         withSources,
        "postgresql"          % "postgresql"         % "9.1-901.jdbc4" withSources,
        // "org.scalatest"       % "scalatest_2.10"     % "2.0",
        "ivan"               %% "javelin-ontologies" % "2.0",
        "ivan"               %% "javelin"            % "2.0",
        "ivan"               %% "scala-utils"        % "2.0"
    )
}

libraryDependencies <+= scalaVersion( "org.scala-lang" % "scala-compiler" % _ )

scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-Xlint",
    "-Ywarn-dead-code",
    "-language:_",
    "-target:jvm-1.7",
    "-encoding", "UTF-8",
    "-Xfatal-warnings"
)

seq(Revolver.settings: _*)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

Twirl.settings

javaOptions in Revolver.reStart += "-Dconfig.file=./application.conf"

oneJarSettings
