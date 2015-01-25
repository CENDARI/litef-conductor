import com.github.retronym.SbtOneJar._

name := """conductor"""

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "spray nightlies" at "http://nightlies.spray.io"

resolvers += "litef nightlies" at "https://raw.githubusercontent.com/ivan-cukic/litef-maven-repository/master/snapshots/"

libraryDependencies ++= {
    object V {
        val spray = "1.2.1"
        val akka  = "2.2.4"
    }
    Seq(
        "com.typesafe.akka"  %% "akka-actor"         % V.akka          withSources,
        "com.typesafe.akka"  %% "akka-slf4j"         % V.akka          withSources,
        "com.typesafe.akka"  %% "akka-testkit"       % V.akka          % "test",
        "ch.qos.logback"      % "logback-classic"    % "1.0.13"        withSources,
        "io.spray"            % "spray-can"          % V.spray         withSources,
        "io.spray"            % "spray-routing"      % V.spray         withSources,
        "io.spray"            % "spray-testkit"      % V.spray         % "test",
        "io.spray"           %% "spray-json"         % "1.2.3"         withSources,
        "io.spray"            % "spray-client"       % V.spray         withSources,
        "io.spray"            % "spray-http"         % "1.2.0"         withSources,
        "org.specs2"         %% "specs2"             % "1.14"          % "test",
        "com.novocode"        % "junit-interface"    % "0.7"           % "test->default",
        "com.typesafe.slick" %% "slick"              % "2.0.0"         withSources,
        // "org.slf4j"           % "slf4j-nop"          % "1.6.4"         withSources,
        "postgresql"          % "postgresql"         % "9.1-901.jdbc4" withSources,
        "org.scalatest"       % "scalatest_2.10"     % "2.0",
        "ivan"               %% "javelin-ontologies" % "1.3",
        "ivan"               %% "javelin"            % "1.3",
        "ivan"               %% "scala-utils"        % "1.2"
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

