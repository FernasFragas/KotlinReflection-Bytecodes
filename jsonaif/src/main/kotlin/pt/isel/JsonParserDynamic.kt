package pt.isel

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.io.File
import java.net.URLClassLoader
import javax.lang.model.element.Modifier
import javax.tools.ToolProvider
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmName
import pt.isel.JsonParserType.*

object JsonParserDynamic : AbstractJsonParser() {

    private const val INTERFACE_IN_TYPE_POS = 0
    private const val INTERFACE_OUT_TYPE_POS = 1

    private val setters = mutableMapOf<KClass<*>, Map<String, Setter>>()

    private val jsonParserType = DYNAMIC

    private val compiler = ToolProvider.getSystemJavaCompiler()
    //apagar ficheiros todos os ficheiros gerados -> ver se foi gerado ou não

    val workDir = createWorkDir()
    /** Creates the directory if this directory doesn't exist and returns the workdir after
     * in order to work must be guaranteed that the directory exists (localWorDir.mkdirs())
     * before return the File. This must occur before the declaration of the classLoader **/
    private fun createWorkDir(): File {
        val localWorDir = File("build\\classes\\kotlin\\main\\pt\\isel\\Generated")
        localWorDir.mkdirs() //garante que a diretoria é criada
        return localWorDir
    }

    //classloader
    /** because the instance of the ClassLoader can be use multiple times **/
    val classLoader = URLClassLoader.newInstance(arrayOf(workDir.toURI().toURL()))

    override fun parsePrimitive(tokens: JsonTokens, klass: KClass<*>): Any? =
        basicParser[klass]?.invoke(tokens.popWordPrimitive())

    override fun parseObject(tokens: JsonTokens, klass: KClass<*>): Any? {

        val constr = klass.primaryConstructor as KFunction

        /**  creates the setters for klass    **/
        setters.computeIfAbsent(klass, ::createSetters)


        return if (constr.parameters.filterNot { it.isOptional }.isEmpty()) {
            instanceConstructor(tokens, klass, setters)
        }
        else {
            parameterConstructor(tokens, klass, constr.parameters, jsonParserType)
        }
    }

    private fun createSetters(klass: KClass<*>): Map<String, Setter> {
        val map = mutableMapOf<String, Setter>()

        klass.memberProperties
            .filterIsInstance<KMutableProperty1<Any, Any?>>()
            /**    Only MutableProperty1    **/
            .filter { it.visibility == KVisibility.PUBLIC }
            .forEach {

                var setTkPropType = it.returnType.classifier as KClass<*>

                val name = it.findAnnotation<JsonProperty>()?.name ?: it.name

                if(it.hasAnnotation<ItemType>()){
                    val ann = it.findAnnotation<ItemType>() as ItemType
                    setTkPropType = ann.type
                }

                /**     verify if there is an object that needs to be  converted    **/
                val converter = getConverter(it)
                var destTkPropType = setTkPropType
                if (converter != null) {
                    val setType =converter.javaClass.kotlin.supertypes.find { iterrr-> iterrr.classifier==Converter::class } as KType
                    setTkPropType = getConverterSourceType(setType)
                    destTkPropType = getConverterDestinyType(setType)
                }

                //////////////////////////////////
                //
                // STEP 1 : Generate source code

                val file = sourceCodeGenerator(name,setTkPropType.jvmName, destTkPropType, klass, converter)

                //////////////////////////////////
                //
                // STEP 2 : Save source code to file
                // (if a working dir has been provided)
                //

                /*val genDir = File("$workDir\\pt\\isel\\Generated")
                genDir.mkdirs()*/
                val sourceFile = File(workDir, "Setter${klass.simpleName}_$name.java")

                saveSourceCode(file,sourceFile)

                //////////////////////////////////
                //
                // STEP 3 : Compile source code
                //

                compiler.run(null,null,null,sourceFile.absolutePath)

                //////////////////////////////////
                //
                // STEP 4 : Load the new class
                //
            /**    \pt\isel\Generated   **/
                val className = "${file.packageName}.${file.typeSpec.name}"

                val k = classLoader.loadClass("$className").kotlin

                //////////////////////////////////
                //
                // STEP 5 : CREATE AND SAVE INSTANCE
                //

                map[name] = k.createInstance() as Setter
            }
        return map
    }

    /**
     * Generates the source code of a setter
     * @param propName name of the property
     * @param propertyTypeName String with the name of the type of the property (it also can be the type of the in if the property has a converter)
     * @param destinationKlass type destination of the property, only useful when the respective property has a converter
     * @param mainKlass main class of the setter (ex SetterStudent_name, mainklass is the Student)
     * @param converter Converter in case the property has a converter
     */
    fun sourceCodeGenerator(propName: String, propertyTypeName: String, destinationKlass: KClass<*>, mainKlass: KClass<*>, converter: Converter<Any,Any>?) : JavaFile {

        val code = codeGenerator(propName, propertyTypeName, destinationKlass, mainKlass.jvmName, converter)

        /** constroi função apply */
        val applyOverride = MethodSpec.methodBuilder("apply")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .returns(Void.TYPE)
            .addParameter(Any::class.java, "target")
            .addParameter(JsonTokens::class.java, "tokens")
            .addCode(code)
            .build()

        /** cria a classe que implementa setter */
        val klassCode = TypeSpec
            .classBuilder("Setter${mainKlass.simpleName}_$propName")
            .addSuperinterface(Setter::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addMethod(applyOverride)
            .build()

        return JavaFile.builder("pt.isel.Generated", klassCode).build()
    }

    /**
     * Builds a CodeBlock with the code to be added to the method apply
     * @param propName name of the property
     * @param propertyKlassName String with the name of the type of the property (it also can be the type of the in if the property has a converter)
     * @param destinationKlassName type destination of the property, only useful when the respective property has a converter
     * @param mainKlassName main class of the setter (ex SetterStudent_name, mainklass is the Student)
     * @param converter Converter in case the property has a converter
     */
    private fun codeGenerator(propName: String, propertyKlassName: String, destinationKlassName: KClass<*>, mainKlassName: String, converter: Converter<Any,Any>?): CodeBlock {

        //fazer um mapa q tem todos os tipos primitivos e chama-lo aqui
        val parsePrimitive = generatorParserForPrimitives[destinationKlassName]
        //se devolver null é pq não é prirmitivo e deve correr o code que lá está agora

        val propertyCodeLine =
            if(parsePrimitive != null)
                "$propertyKlassName v = $parsePrimitive(tokens.popWordPrimitive())"
            else
                getJavaCodeForNonPrimitive(propertyKlassName)

        val converterCodeline =
            if(converter != null)
                getJavaCodeForConverter(destinationKlassName.jvmName,converter.javaClass.simpleName, mainKlassName, propName)
            else {
                "(($mainKlassName) target).set${propName.replaceFirstChar { it.uppercase() }}(v)"
            }

        return CodeBlock
            .builder()
            .addStatement("$propertyCodeLine;\n$converterCodeline")
            .build()

    }

    fun saveSourceCode(code: JavaFile, file: File) {
        file.printWriter().use { out ->
            code.writeTo(out)
        }
    }

    /**
     * returns a String with the line of code necessary to create an instance of the property with the type
     * propertyKlassName
     * @param propertyKlassName name of the type of the instance
     */
    private fun getJavaCodeForNonPrimitive(propertyKlassName: String): String =
        "$propertyKlassName v = ($propertyKlassName) pt.isel.JsonParserDynamic.INSTANCE.parse(tokens, kotlin.jvm." +
                "JvmClassMappingKt.getKotlinClass($propertyKlassName.class))"


    /**
     *  returns a String with the line of code necessary to convert a property that has a converter
     * @param destinationKlassName name of the out type of the converter
     * @param converter name of the converter of the respective property
     * @param mainKlassName name of the class of the property
     * @param propName name of the property
     */
    private fun getJavaCodeForConverter(destinationKlassName: String, converter: String, mainKlassName: String, propName: String): String =
        "if (v!=null) { \n" +
            " ${destinationKlassName} newv = pt.isel.${converter}.INSTANCE.converter(v); \n" +
        "if (newv!=null) { \n" +
            "(($mainKlassName) target).set${propName.replaceFirstChar { it.uppercase() }}(newv);" +
        " \n} \n}"


    /**
     * acede ao value da Annotation JsonConvert que é um tipo e uma instância ao msm tempo,
     * uma vez que a kclass usada está declarada em JsonTo como um object
     * @param elem: KAnnotatedElement it's a KAnnotatedElement because KMutableProperty1 and KParameter implement KAnnotatedElement
     * @return : Converter<Any,Any>?
     */
    private fun getConverter(elem: KAnnotatedElement): Converter<Any,Any>? {
        return if (elem.hasAnnotation<JsonConvert>()) {
            val annotation = elem.findAnnotation<JsonConvert>() as JsonConvert
            return annotation.value.objectInstance as Converter<Any,Any>
        } else null
    }

    /**
     * returns the type of the in of the Converter
     */
    private fun getConverterSourceType(setType: KType): KClass<*> {
        val type = setType.arguments[INTERFACE_IN_TYPE_POS].type as KType
        return type.classifier as KClass<*>
    }

    /**
     * returns the type of the out of the Converter
     */
    private fun getConverterDestinyType(setType: KType): KClass<*> {
        val type = setType.arguments[INTERFACE_OUT_TYPE_POS].type as KType
        return type.classifier as KClass<*>
    }

}