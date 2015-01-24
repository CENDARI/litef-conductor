resolvers += "spray repo" at "http://repo.spray.io"

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.1")

addSbtPlugin("io.spray" % "sbt-twirl"    % "0.7.0")

addSbtPlugin("com.dscleaver.sbt" % "sbt-quickfix" % "0.4.1")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")

