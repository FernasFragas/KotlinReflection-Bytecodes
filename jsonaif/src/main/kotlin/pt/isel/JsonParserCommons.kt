package pt.isel

import kotlin.reflect.KClass

val basicParser: Map<KClass<*>, (String) -> Any> = mapOf(
            Byte::class to { it.toByte() },
            Short::class to { it.toShort() },
            Int::class to { it.toInt() },
            Long::class to { it.toLong() },
            Float::class to { it.toFloat() },
            Double::class to { it.toDouble() },
            Boolean::class to { it.toBoolean() }
    )

val generatorParserForPrimitives: Map<KClass<*>, String> = mapOf(
    Byte::class to  "Byte.parseByte" ,
    Short::class to  "Short.parseShort" ,
    Int::class to  "Integer.parseInt" ,
    Long::class to  "Long.parseLong" ,
    Float::class to  "Float.parseFloat" ,
    Double::class to  "Double.parseDouble"
)
