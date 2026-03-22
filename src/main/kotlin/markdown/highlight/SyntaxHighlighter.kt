package markdown.highlight

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle

private enum class TokenType { KEYWORD, STRING, COMMENT, NUMBER, ANNOTATION, PLAIN }

private data class Rule(val regex: Regex, val type: TokenType)

private object C {
    val keyword    = Color(0xFF0033B3)
    val string     = Color(0xFF067D17)
    val comment    = Color(0xFF8C8C8C)
    val number     = Color(0xFF1750EB)
    val annotation = Color(0xFF808000)
}

private fun r(pattern: String, type: TokenType) = Rule(Regex(pattern), type)

private fun kw(vararg words: String) = Rule(
    Regex("\\b(${words.sortedByDescending { it.length }.joinToString("|") { Regex.escape(it) }})\\b"),
    TokenType.KEYWORD
)

private val KOTLIN = listOf(
    r("/\\*[\\s\\S]*?\\*/",             TokenType.COMMENT),
    r("//[^\n]*",                        TokenType.COMMENT),
    r("\"\"\"[\\s\\S]*?\"\"\"",         TokenType.STRING),
    r("\"(?:[^\"\\\\]|\\\\.)*\"",       TokenType.STRING),
    r("'(?:[^'\\\\]|\\\\.)*'",          TokenType.STRING),
    r("@\\w+",                           TokenType.ANNOTATION),
    kw("val","var","fun","class","object","interface","enum","sealed","data","abstract",
       "open","override","private","public","protected","internal","import","package",
       "return","if","else","when","for","while","do","try","catch","finally","throw",
       "null","true","false","this","super","is","as","in","out","by","companion",
       "constructor","init","get","set","lateinit","suspend","inline","reified",
       "crossinline","noinline","typealias","expect","actual","external","operator",
       "infix","tailrec","const","dynamic","it"),
    r("\\b0[xX][0-9a-fA-F]+|\\b\\d+\\.?\\d*([eE][+-]?\\d+)?[fFLl]?\\b", TokenType.NUMBER),
)

private val JAVA = listOf(
    r("/\\*[\\s\\S]*?\\*/",             TokenType.COMMENT),
    r("//[^\n]*",                        TokenType.COMMENT),
    r("\"(?:[^\"\\\\]|\\\\.)*\"",       TokenType.STRING),
    r("'(?:[^'\\\\]|\\\\.)*'",          TokenType.STRING),
    r("@\\w+",                           TokenType.ANNOTATION),
    kw("abstract","assert","boolean","break","byte","case","catch","char","class",
       "const","continue","default","do","double","else","enum","extends","final",
       "finally","float","for","if","implements","import","instanceof","int",
       "interface","long","native","new","null","package","private","protected",
       "public","return","short","static","super","switch","synchronized","this",
       "throw","throws","transient","try","var","void","volatile","while","true","false",
       "record","sealed","permits","yield"),
    r("\\b0[xX][0-9a-fA-F]+|\\b\\d+\\.?\\d*[fFLl]?\\b", TokenType.NUMBER),
)

private val JS = listOf(
    r("/\\*[\\s\\S]*?\\*/",             TokenType.COMMENT),
    r("//[^\n]*",                        TokenType.COMMENT),
    r("`[\\s\\S]*?`",                   TokenType.STRING),
    r("\"(?:[^\"\\\\]|\\\\.)*\"",       TokenType.STRING),
    r("'(?:[^'\\\\]|\\\\.)*'",          TokenType.STRING),
    r("@\\w+",                           TokenType.ANNOTATION),
    kw("break","case","catch","class","const","continue","debugger","default","delete",
       "do","else","export","extends","false","finally","for","function","if","import",
       "in","instanceof","let","new","null","return","static","super","switch","this",
       "throw","true","try","typeof","undefined","var","void","while","with","yield",
       "async","await","of","from","as","type","interface","enum","implements",
       "abstract","readonly","namespace","declare"),
    r("\\b\\d+\\.?\\d*([eE][+-]?\\d+)?[nN]?\\b", TokenType.NUMBER),
)

private val PYTHON = listOf(
    r("\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?'''",          TokenType.STRING),
    r("f\"(?:[^\"\\\\]|\\\\.)*\"|f'(?:[^'\\\\]|\\\\.)*'", TokenType.STRING),
    r("\"(?:[^\"\\\\]|\\\\.)*\"",                          TokenType.STRING),
    r("'(?:[^'\\\\]|\\\\.)*'",                             TokenType.STRING),
    r("#[^\n]*",                                            TokenType.COMMENT),
    r("@\\w+",                                              TokenType.ANNOTATION),
    kw("False","None","True","and","as","assert","async","await","break","class",
       "continue","def","del","elif","else","except","finally","for","from","global",
       "if","import","in","is","lambda","nonlocal","not","or","pass","raise","return",
       "try","while","with","yield"),
    r("\\b\\d+\\.?\\d*([eE][+-]?\\d+)?[jJ]?\\b", TokenType.NUMBER),
)

private val BASH = listOf(
    r("#[^\n]*",                         TokenType.COMMENT),
    r("\"(?:[^\"\\\\]|\\\\.)*\"",       TokenType.STRING),
    r("'[^']*'",                         TokenType.STRING),
    r("\\$\\{?[\\w#@*?!-]+}?",          TokenType.ANNOTATION),
    kw("if","then","else","elif","fi","for","while","do","done","case","in","esac",
       "function","return","exit","echo","read","local","export","source","alias",
       "unset","readonly","shift","break","continue","true","false"),
    r("\\b\\d+\\b",                      TokenType.NUMBER),
)

private val SQL = listOf(
    r("--[^\n]*",                        TokenType.COMMENT),
    r("/\\*[\\s\\S]*?\\*/",             TokenType.COMMENT),
    r("'(?:[^'\\\\]|\\\\.)*'",          TokenType.STRING),
    kw("SELECT","FROM","WHERE","AND","OR","NOT","INSERT","INTO","VALUES","UPDATE","SET",
       "DELETE","CREATE","TABLE","DROP","ALTER","INDEX","JOIN","LEFT","RIGHT","INNER",
       "OUTER","ON","GROUP","BY","ORDER","HAVING","LIMIT","OFFSET","DISTINCT","AS",
       "UNION","ALL","EXISTS","IN","BETWEEN","LIKE","NULL","IS","PRIMARY","KEY",
       "FOREIGN","REFERENCES","UNIQUE","DEFAULT","CONSTRAINT","CASCADE","RETURNING",
       "select","from","where","and","or","not","insert","into","values","update","set",
       "delete","create","table","drop","alter","index","join","left","right","inner",
       "outer","on","group","order","having","limit","offset","distinct","as","union",
       "all","exists","in","between","like","null","is","primary","key","foreign",
       "references","unique","default","constraint","cascade","returning"),
    r("\\b\\d+\\.?\\d*\\b",             TokenType.NUMBER),
)

private val JSON = listOf(
    r("\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"(?=\\s*:)", TokenType.KEYWORD),
    r("\"(?:[^\"\\\\]|\\\\.)*\"",                    TokenType.STRING),
    r("\\b(true|false|null)\\b",                     TokenType.ANNOTATION),
    r("-?\\b\\d+\\.?\\d*([eE][+-]?\\d+)?\\b",       TokenType.NUMBER),
)

private val HTML = listOf(
    r("<!--[\\s\\S]*?-->",               TokenType.COMMENT),
    r("\"[^\"]*\"|'[^']*'",             TokenType.STRING),
    r("</[\\w-]+>",                      TokenType.KEYWORD),
    r("<[\\w-]+",                        TokenType.KEYWORD),
    r("[\\w-]+(?=\\s*=)",               TokenType.ANNOTATION),
)

private val CSS = listOf(
    r("/\\*[\\s\\S]*?\\*/",                                TokenType.COMMENT),
    r("\"[^\"]*\"|'[^']*'",                               TokenType.STRING),
    r("#[0-9a-fA-F]{3,8}\\b",                             TokenType.NUMBER),
    r("\\b\\d+\\.?\\d*(px|em|rem|%|vh|vw|pt|s|ms|deg)?\\b", TokenType.NUMBER),
    r("[\\w-]+(?=\\s*:)",                                  TokenType.KEYWORD),
    r("[.#]?[\\w-]+(?=\\s*\\{)",                          TokenType.ANNOTATION),
)

private val RUST = listOf(
    r("/\\*[\\s\\S]*?\\*/",                              TokenType.COMMENT),
    r("//[^\n]*",                                         TokenType.COMMENT),
    r("r#*\"[\\s\\S]*?\"#*|\"(?:[^\"\\\\]|\\\\.)*\"",  TokenType.STRING),
    r("'(?:[^'\\\\]|\\\\.)*'",                           TokenType.STRING),
    r("#!?\\[\\w+",                                       TokenType.ANNOTATION),
    kw("as","async","await","break","const","continue","crate","dyn","else","enum",
       "extern","false","fn","for","if","impl","in","let","loop","match","mod","move",
       "mut","pub","ref","return","self","Self","static","struct","super","trait","true",
       "type","unsafe","use","where","while","i8","i16","i32","i64","i128","isize",
       "u8","u16","u32","u64","u128","usize","f32","f64","bool","char","str","String"),
    r("\\b0[xXoObB][0-9a-fA-F_]+|\\b\\d[\\d_]*\\.?[\\d_]*([eE][+-]?\\d+)?\\b", TokenType.NUMBER),
)

private val GO = listOf(
    r("/\\*[\\s\\S]*?\\*/",             TokenType.COMMENT),
    r("//[^\n]*",                        TokenType.COMMENT),
    r("`[^`]*`",                         TokenType.STRING),
    r("\"(?:[^\"\\\\]|\\\\.)*\"",       TokenType.STRING),
    r("'(?:[^'\\\\]|\\\\.)*'",          TokenType.STRING),
    kw("break","case","chan","const","continue","default","defer","else","fallthrough",
       "for","func","go","goto","if","import","interface","map","nil","package","range",
       "return","select","struct","switch","true","false","type","var","iota",
       "int","int8","int16","int32","int64","uint","uint8","uint16","uint32","uint64",
       "float32","float64","complex64","complex128","bool","string","byte","rune","error"),
    r("\\b\\d+\\.?\\d*([eE][+-]?\\d+)?[i]?\\b", TokenType.NUMBER),
)

private fun getRules(lang: String): List<Rule>? = when (lang.lowercase()) {
    "kotlin", "kt"                   -> KOTLIN
    "java"                           -> JAVA
    "javascript", "js",
    "typescript", "ts", "jsx", "tsx" -> JS
    "python", "py"                   -> PYTHON
    "bash", "sh", "shell", "zsh"    -> BASH
    "sql"                            -> SQL
    "json"                           -> JSON
    "html", "xml", "svg"             -> HTML
    "css", "scss", "sass", "less"   -> CSS
    "rust", "rs"                     -> RUST
    "go", "golang"                   -> GO
    else                             -> null
}

private data class Token(val text: String, val type: TokenType)

private fun tokenize(code: String, rules: List<Rule>): List<Token> {
    val tokens = mutableListOf<Token>()
    var pos = 0
    val plain = StringBuilder()

    fun flushPlain() {
        if (plain.isNotEmpty()) {
            tokens.add(Token(plain.toString(), TokenType.PLAIN))
            plain.clear()
        }
    }

    while (pos < code.length) {
        var matched = false
        for (rule in rules) {
            val m = rule.regex.matchAt(code, pos) ?: continue
            if (m.value.isEmpty()) continue
            flushPlain()
            tokens.add(Token(m.value, rule.type))
            pos += m.value.length
            matched = true
            break
        }
        if (!matched) plain.append(code[pos++])
    }
    flushPlain()
    return tokens
}

fun highlight(code: String, language: String): AnnotatedString {
    val rules = getRules(language) ?: return buildAnnotatedString { append(code) }
    return buildAnnotatedString {
        for ((text, type) in tokenize(code, rules)) {
            when (type) {
                TokenType.KEYWORD    -> withStyle(SpanStyle(color = C.keyword))    { append(text) }
                TokenType.STRING     -> withStyle(SpanStyle(color = C.string))     { append(text) }
                TokenType.COMMENT    -> withStyle(SpanStyle(color = C.comment, fontStyle = FontStyle.Italic)) { append(text) }
                TokenType.NUMBER     -> withStyle(SpanStyle(color = C.number))     { append(text) }
                TokenType.ANNOTATION -> withStyle(SpanStyle(color = C.annotation)) { append(text) }
                TokenType.PLAIN      -> append(text)
            }
        }
    }
}
