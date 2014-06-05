/*
 * This file is public domain. Alternatively, you
 * can use it under Creative Commons Zero license
 */

package indexer.config

object Indexer {
    /**
     * Built-in indexer classes. Do not put the abstract classes
     * in this list.
     */
    val builtin = Seq(
        // "indexer.PDFIndexer",
        // "indexer.PlainTextIndexer",
        // "indexer.ApacheTikaIndexer",
        "indexer.BasicInfoIndexer",
        "indexer.xml.EAGIndexer"
    )
}
