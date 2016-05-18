import AssemblyKeys._ // put this at the top of the file

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("META-INF", "cxf", "bus-extensions.txt") => MergeStrategy.first
    case PathList("META-INF", "jdom-info.xml") => MergeStrategy.last
    case PathList("org", "apache", "commons", "logging", xs @ _*) => MergeStrategy.first
    case PathList("org", "jdom2", xs @ _*) => MergeStrategy.last
    case x => old(x)
  }
}
