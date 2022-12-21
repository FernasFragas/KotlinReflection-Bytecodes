package pt.isel

import java.io.File
import kotlin.reflect.KClass


abstract class AbstractJsonParser : JsonParser {

    override fun parse(source: String, klass: KClass<*>): Any? {
        return parse(JsonTokens(source), klass)
    }

    fun parse(tokens: JsonTokens, klass: KClass<*>) = when (tokens.current) {
        OBJECT_OPEN -> parseObject(tokens, klass)
        ARRAY_OPEN -> parseArray(tokens, klass)
        DOUBLE_QUOTES -> parseString(tokens)
        else -> parsePrimitive(tokens, klass)
    }

    inline fun <reified T> parseSequence (json:String):Sequence<T?>{
        return sequence {
            if (json.first() != ARRAY_OPEN) throw Exception()
            val tokens = JsonTokens(json)
            tokens.pop(ARRAY_OPEN)
            while (tokens.current != ARRAY_END) {
                //val str = "${tokens.popWordPrimitive()},"
                val v = parse(tokens,T::class)
                yield(v as T?)
                if (tokens.current == COMMA) // The last element finishes with ] rather than a comma
                    tokens.pop(COMMA) // Discard COMMA
                else break
                tokens.trim()
            }
            tokens.pop(ARRAY_END) // Discard square bracket ] ARRAY_END
        }

    }

    inline fun <reified T : Any> parseFolderEager(path: String) : List<T> {
        val list = mutableListOf<T>()
        File(path)
            .walk()
            .filter{it.isFile}
            .forEach { file ->
                val lines = file.readLines() //uselines
                println("lines = $lines")
                lines.forEach {
                    val parsed = parseObject(JsonTokens(it),T::class) ?: throw Exception()
                    println("parsed = $lines")
                    list.add(parsed as T)
                }
            }
        return list
    }

    inline fun <reified  T : Any> parseFolderLazy(path: String): Sequence<T?>{
        return sequence {
            File(path)
                .walk()
                .filter{it.isFile}
                .forEach { file ->
                    val lines = file.readLines() //uselines
                    lines.forEach {
                        val parsed = parseObject(JsonTokens(it),T::class)?: throw Exception()
                        yield(parsed as T )
                    }
                }

        }
    }


    abstract fun parsePrimitive(tokens: JsonTokens, klass: KClass<*>): Any?

    abstract fun parseObject(tokens: JsonTokens, klass: KClass<*>): Any?

    private fun parseString(tokens: JsonTokens): String {
        tokens.pop(DOUBLE_QUOTES) // Discard double quotes "
        return tokens.popWordFinishedWith(DOUBLE_QUOTES)
    }

    private fun parseArray(tokens: JsonTokens, klass: KClass<*>): List<Any?> {
        val list = mutableListOf<Any?>()
        tokens.pop(ARRAY_OPEN) // Discard square brackets [ ARRAY_OPEN
        while (tokens.current != ARRAY_END) {
            val v = parse(tokens, klass)
            list.add(v)
            if (tokens.current == COMMA) // The last element finishes with ] rather than a comma
                tokens.pop(COMMA) // Discard COMMA
            else break
            tokens.trim()
        }
        tokens.pop(ARRAY_END) // Discard square bracket ] ARRAY_END
        return list;
    }


}
