package pt.isel

import kotlin.reflect.KClass


interface JsonParser {

    fun parse(source: String, klass: KClass<*>): Any?

}

inline fun <reified T> JsonParser.parse(source: String): T {
    return parse (source, T::class) as T
}

inline fun <reified T> JsonParser.parseArray(source: String): List<T> {
    return parse (source, T::class) as List<T>
}

