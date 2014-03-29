name := """conductor"""

version := "1.0"

scalaVersion := "2.10.2"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "spray nightlies" at "http://nightlies.spray.io"

libraryDependencies ++= Seq(
    "com.typesafe.akka"  %% "akka-actor"       % "2.2.0"         withSources,
    "com.typesafe.akka"  %% "akka-slf4j"       % "2.2.0"         withSources,
    "com.typesafe.akka"  %% "akka-testkit"     % "2.2.0"         % "test",
    "ch.qos.logback"      % "logback-classic"  % "1.0.13"        withSources,
    "io.spray"            % "spray-can"        % "1.2-20130712"  withSources,
    "io.spray"            % "spray-routing"    % "1.2-20130712"  withSources,
    "io.spray"           %% "spray-json"       % "1.2.3"         withSources,
    "org.specs2"         %% "specs2"           % "1.14"          % "test",
    "io.spray"            % "spray-testkit"    % "1.2-20130712"  % "test",
    "com.novocode"        % "junit-interface"  % "0.7"           % "test->default",
    "com.typesafe.slick" %% "slick"            % "2.0.0"         withSources,
    // "org.slf4j"           % "slf4j-nop"        % "1.6.4"         withSources,
    "postgresql"          % "postgresql"       % "9.1-901.jdbc4" withSources,
    "org.scalatest"       % "scalatest_2.10"   % "2.0"
)

scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-Xlint",
    "-Ywarn-dead-code",
    "-language:_",
    "-target:jvm-1.7",
    "-encoding", "UTF-8"
)

seq(Revolver.settings: _*)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

Twirl.settings
