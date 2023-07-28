import org.jsoup.Jsoup
import java.io.File
import java.net.MalformedURLException

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar markdown-link-titles-shadow.jar file [createCopy]")
    }
    val fileName = args[0]
    val file = File(fileName)
    val lines = file.readLines()
    
    val markdownLinkRegex = "\\[([^]]+)]\\((https?://\\S+)\\)".toRegex()
    val plainLinkRegex = "(?<![(<\\[])(https?://[^\\s\"']+)".toRegex()
    
    val processed = lines.map {
        process(it, markdownLinkRegex) { match ->
            val url = match.groups[2]!!
            getFormattedTitle(url.value)
        }
    }.joinToString("\n") {
        process(it, plainLinkRegex) { match ->
            getFormattedTitle(match.value)
        }
    }
    
    println(processed)
    
    when {
        args.size < 2 -> file.writeText(processed)
        args[1] == "doCopy" || args[1] == "copy" ->
            file.resolveSibling(file.nameWithoutExtension + ".entitled." + file.extension).writeText(processed)
        
        else -> file.writeText(processed)
    }
}

private fun process(it: String, regex: Regex, transform: (MatchResult) -> String): String {
    var line = it
    var match: MatchResult?
    while (true) {
        match = regex.find(line)
        if (match == null) break
        
        val title = transform(match)
        print(line)
        print(" -> ")
        line = line.replaceRange(match.range, title)
        println(line)
    }
    return line
}

fun getFormattedTitle(url: String): String = "${getTitle(url)} (<$url>)"

fun getTitle(url: String): String = try {
    println("> $url")
    Jsoup.connect(url).get().title().also { println("< $it") }.ifBlank { "No Title" }
} catch (e: MalformedURLException) {
    throw e
} catch (e: Exception) {
    "§§§§ " + (e.message?.replace(url, "<url>") ?: e::class.simpleName ?: "Unknown Exception")
}
