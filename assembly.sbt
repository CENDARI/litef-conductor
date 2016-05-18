import AssemblyKeys._ // put this at the top of the file

assemblySettings

val meta = """META.INF(.)*""".r

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case meta(_) => MergeStrategy.discard
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case PathList("org", "jdom2", xs @ _*) => MergeStrategy.last
    case x => old(x)
  }
}
