package pt.isel

import pt.isel.sample.Classroom
import pt.isel.sample.Date
import pt.isel.sample.Person
import pt.isel.sample.Student
import javax.tools.ToolProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonParserDynamicTest {

    private val compiler = ToolProvider.getSystemJavaCompiler()

    @Test
    fun test(){

      /*  val s: JavaFile = JsonParserDynamic.sourceCodeGenerator("name", String::class, Student::class,)
        s.writeTo(System.out)
        val workDir = File("src/main/kotlin/pt/isel/Generated")
        val sourceFile = File(workDir, "SetterStudent_name.java")

        saveSourceCode(s,sourceFile)

        compiler.run(null,null,null,sourceFile.absolutePath)
*/

    }

    @Test fun parseSimpleObjectViaConstructor() {
        val json = "{ id: 94646, name: \"Ze Manel\"}"
        val p = JsonParserDynamic.parse(json, Person::class) as Person
        assertEquals(94646, p.id)
        assertEquals("Ze Manel", p.name)
    }

    @Test fun parseSimpleObjectViaPropertiesDynamic() {
        val json = "{ name: \"Ze Manel\", nr: 7353}"
        val student = JsonParserDynamic.parse(json, Student::class) as Student
        assertEquals("Ze Manel", student.name)
        assertEquals(7353, student.nr)
    }

    @Test fun parseClassroom() {
        val json = "{ classroom: \"LEIC43D\", students:[{ name: \"Mafalda Rodrigues\", nr: 47184}, { name: \"Ines Martins\", nr: 47188}, { name: \"Fernando Fragateiro\", nr: 46071}]}"
        val cl = JsonParserDynamic.parse(json, Classroom::class) as Classroom

        assertEquals("LEIC43D", cl.classroom)
        assertEquals(Student(47184,"Mafalda Rodrigues"), cl.students[0])
        assertEquals(Student(47188,"Ines Martins"), cl.students[1])
        assertEquals(Student(46071,"Fernando Fragateiro"), cl.students[2])
    }
    @Test
    fun parseComposeObjectWithAnnotation() {
        val json = "{ id: 94646, name: \"Ze Manel\", birth_date: { year: 1999, month: 9, day: 19}}}"
        val p = JsonParserDynamic.parse(json, Person::class) as Person
        val birth = p.birth as Date
        val date = Date(19,9,1999)
        assertEquals(date.day,birth.day)
        assertEquals(date.month,birth.month)
        assertEquals(date.year,birth.year)
    }

    @Test
    fun  parseWithConverterDate() {
        val json = "{ name: \"Maria Papoila\", nr: 73753, birth: \"1998-11-17\" }"
        val student = JsonParserDynamic.parse(json, Student::class) as Student
        val date = Date(17,11,1998)
        assertEquals("Maria Papoila", student.name)
        assertEquals(73753, student.nr)
        assertEquals(date, student.birth)
    }
}