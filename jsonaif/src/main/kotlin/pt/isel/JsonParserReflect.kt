package pt.isel

import kotlin.reflect.*
import kotlin.reflect.full.*
import pt.isel.JsonParserType.*

object JsonParserReflect  : AbstractJsonParser() {

    /**
     * For each domain class we keep a Map<String, Setter> relating properties names with their setters.
     * This is for Part 2 of Jsonaif workout.
     */
    private val setters = mutableMapOf<KClass<*>, Map<String, Setter>>()

    private val jsonParserType = REFLECT

    override fun parsePrimitive(tokens: JsonTokens, klass: KClass<*>) =
        basicParser[klass]?.invoke(tokens.popWordPrimitive())

    override fun parseObject(tokens: JsonTokens, klass: KClass<*>): Any? {

        var params: List<KParameter> = mutableListOf()
        val constr = klass.primaryConstructor
        if (constr != null)
            params = constr.parameters.map { it }

        setters.computeIfAbsent(klass, ::createSetters)

        return if (params.filterNot { it.isOptional }.isEmpty()) {
            instanceConstructor(tokens, klass, setters)
        }
        else {
            parameterConstructor(tokens, klass, params, jsonParserType)
        }


    }

    private fun createSetters(klass: KClass<*>): Map<String, Setter> {
        val map = mutableMapOf<String, Setter>()

        klass.memberProperties
            .filterIsInstance<KMutableProperty1<Any, Any?>>()
            /**    Only MutableProperty1    **/
            .filter { it.visibility == KVisibility.PUBLIC }
            .forEach {

                val converter = getConverter(it)

                val name = it.findAnnotation<JsonProperty>()?.name ?: it.name

                val setTkPropType = getPropertyOfTypeInstanceConstructor(it) //garantir que o constructor não é null

                map[name] = object : Setter {

                    override fun apply(target: Any, tokens: JsonTokens) {

                        var obj = JsonParserReflect.parse(tokens, setTkPropType)
                        if(obj !=null && converter != null) {
                            obj = converter.converter(obj)
                        }
                        /**     gets the correspondent obj of the token     **/
                        it.setter.call(target, obj)
                    }

                }
            }
        return map
    }

}



