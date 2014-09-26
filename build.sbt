name := """conductor"""

version := "1.0"

scalaVersion := "2.11.2"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "spray nightlies" at "http://nightlies.spray.io"

resolvers += "litef nightlies" at "https://raw.githubusercontent.com/ivan-cukic/litef-maven-repository/master/snapshots/"

// conflictManager := ConflictManager.latestCompatible

// conflictWarning := ConflictWarning.disable

libraryDependencies ++= {
    object V {
        val spray = "1.3.1"
        val akka  = "2.3.6"
    }
    Seq(
        "com.typesafe.akka"  %% "akka-actor"         % V.akka          withSources,
        "com.typesafe.akka"  %% "akka-slf4j"         % V.akka          ,
        "ch.qos.logback"      % "logback-classic"    % "1.0.13"        ,
        "io.spray"            % "spray-can"          % V.spray         withSources,
        "io.spray"            % "spray-routing"      % V.spray         withSources,
        "io.spray"           %% "spray-json"         % "1.3.0"         withSources,
        "io.spray"            % "spray-client"       % V.spray         withSources,
        "io.spray"            % "spray-http"         % V.spray         withSources,
        "com.typesafe.slick" %% "slick"              % "2.1.0"         withSources,
        "org.scalatest"       % "scalatest_2.11"     % "2.2.2"         ,
        "postgresql"          % "postgresql"         % "9.1-901.jdbc4" ,
        "com.novocode"        % "junit-interface"    % "0.7"           ,
        "ivan"               %% "javelin-ontologies" % "1.2"           ,
        "ivan"               %% "javelin"            % "1.2"           ,
        "ivan"               %% "scala-utils"        % "1.2"
        // "io.spray"            % "spray-testkit"      % V.spray
        //     exclude("com.typesafe.akka", "akka-actor_2.10")         ,
        // "com.typesafe.akka"  %% "akka-testkit"       % V.akka
        //     exclude("com.typesafe.akka", "akka-testkit_2.10")       ,
        // "org.specs2"         %% "specs2"             % "1.14"       ,
        // "org.slf4j"           % "slf4j-nop"          % "1.6.4"      ,
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
    "-encoding", "UTF-8"
)

seq(Revolver.settings: _*)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

Twirl.settings
