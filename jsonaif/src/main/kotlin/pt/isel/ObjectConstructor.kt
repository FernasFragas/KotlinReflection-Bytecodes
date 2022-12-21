package pt.isel

import kotlin.reflect.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import pt.isel.JsonParserType.*

private const val INTERFACE_IN_TYPE_POS = 0

fun instanceConstructor(tokens: JsonTokens, klass: KClass<*>, setters: Map<KClass<*>, Map<String, Setter>>): Any {

    val instance = klass.createInstance()

    tokens.pop(OBJECT_OPEN)
    while (tokens.current != OBJECT_END) {

        val tkName: String = tokens.popWordFinishedWith(':')

        val setter = setters[klass]?.get(tkName) as Setter
        setter.apply(instance, tokens)

        if (tokens.current == COMMA) { // The last element finishes with ] rather than a comma
            tokens.pop(COMMA) // Discard COMMA
        } else {
            tokens.tryAdvance()
            break
        }
        tokens.trim()
    }

    return instance
}


fun parameterConstructor(tokens: JsonTokens, klass: KClass<*>, params: List<KParameter>, jsonParserType: JsonParserType): Any {

    val mapParams = mutableMapOf<KParameter, Any?>()

    tokens.pop(OBJECT_OPEN)
    while (tokens.current != OBJECT_END) {

        val tkName: String = tokens.popWordFinishedWith(':')

        /** gets the kparam from the primaryConstructor, this is KParameter**/
        val kParam: KParameter = params
            .find { it.findAnnotation<JsonProperty>()?.name == tkName || it.name==tkName } as KParameter

        val converter = getConverter(kParam)

        val type = getPropertyOfTypeParameterConstructor(kParam)


        /** gets th value of the parameter**/
        var obj = getParser(tokens,type, jsonParserType)
        if(obj !=null && converter != null)
            obj = converter.converter(obj) ?: obj
        mapParams[kParam] = obj

        if (tokens.current == COMMA) { // The last element finishes with ] rather than a comma
            tokens.pop(COMMA) // Discard COMMA
        }else {
            tokens.tryAdvance()
            break
        }
        tokens.trim()
    }

    val instance = klass.primaryConstructor as KFunction

    return instance.callBy(mapParams)
}


//In this implementation we work with KMutableProperty1<Any,Any>
fun getPropertyOfTypeInstanceConstructor(it: KMutableProperty1<Any,Any?>): KClass<*> {
    return getPropertyType(it) ?: it.returnType.classifier as KClass<*>
}

// In this version we work with kparam
fun getPropertyOfTypeParameterConstructor(it: KParameter): KClass<*> {
    return getPropertyType(it) ?:it.type.classifier as KClass<*>
}

/**
 * general getPropertyType used in getPropertyTypeInstanceConstructor and in getPropertyTypeParameterConstructor
 * returns the type of property it,
 * return null if the property does not have or an annotation of ItemType or a converter
 * @param it is a KAnnotatedElement because it has to work with KParameter and KMutableProperty1
 * thw function will return the type of this property
 */
fun getPropertyType(it: KAnnotatedElement): KClass<*>? {

    val converter = getConverter(it)

    return if (it.hasAnnotation<ItemType>()) {
        (it.findAnnotation<ItemType>() as ItemType).type

    } else if (converter != null) { //TODO(tirar !!)
        val type = converter.javaClass.kotlin.supertypes.find { iterrr -> iterrr.classifier == Converter::class } as KType
        type.arguments[INTERFACE_IN_TYPE_POS].type!!.classifier as KClass<*>
    }
    else null
}

/**
 * acede ao value da Annotation JsonConvert que é um tipo e uma instancia ao msm tempo,
 * uma vez que a kclass usada está declarada em JsonTo como um object
 * @param elem: KAnnotatedElement it's a KAnnotatedElement because KMutableProperty1 and KParameter implement KAnnotatedElement
 * @return : Converter<*,*>?
 */
fun getConverter(elem: KAnnotatedElement): Converter<Any,Any>? {
    return if (elem.hasAnnotation<JsonConvert>()) {
        val annotation = elem.findAnnotation<JsonConvert>() as JsonConvert
        return annotation.value.objectInstance as Converter<Any,Any>
    } else null
}


fun getParser(tokens: JsonTokens,klass: KClass<*> , jsonParserType: JsonParserType): Any?{
    return when (jsonParserType) {
        DYNAMIC -> JsonParserDynamic.parse(tokens, klass)
        REFLECT -> JsonParserReflect.parse(tokens, klass)
    }
}


