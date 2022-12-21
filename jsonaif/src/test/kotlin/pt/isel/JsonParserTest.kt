package pt.isel

import pt.isel.sample.*
import pt.isel.sample.Person
import pt.isel.sample.Student
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonParserTest {

    @Test fun parseSimpleObjectViaProperties() {
        val json = "{ name: \"Ze Manel\", nr: 7353}"
        val student = JsonParserReflect.parse(json, Student::class) as Student
        assertEquals("Ze Manel", student.name)
        assertEquals(7353, student.nr)
    }

    @Test
    fun parseSequenceTest(){
        val json = "[2,5,1,2,4]"
        val sequence = JsonParserReflect.parseSequence<Int>(json).iterator()
        var count = 0
        while (sequence.hasNext()){
            println("Sequence = ${sequence.next()}")
            ++count
        }

        assertEquals(5,count)
    }

    @Test fun parsePersonGeneric() {
        val json = "{ name: \"Joao Pedro\", id: 2504071}"
        val json1 = "{ name: \"Horacio Domingo\", id: 2504072}"
        val student = JsonParserReflect.parse<Person>(json)
        val student1 = JsonParserReflect.parse<Person>(json1)
        assertEquals("Joao Pedro", student.name)
        assertEquals(2504071, student.id)
        assertEquals("Horacio Domingo", student1.name)
        assertEquals(2504072, student1.id)
    }

    @Test fun parseStudentGeneric(){
        val json = "{ name: \"Ze Manel\", nr: 7353}"
        val student = JsonParserReflect.parse<Student>(json)
        assertEquals("Ze Manel", student.name)
        assertEquals(7353, student.nr)
    }

    @Test fun parseSequenceLazy() {
        val json = "[\"LEIC\", \"LEIM\", \"LMATE\", \"LEETC\"]"
        val seq = JsonParserReflect.parseSequence<String>(json)

    }

    @Test fun parseSimpleObjectViaConstructor() {
        val json = "{ id: 94646, name: \"Ze Manel\"}"
        val p = JsonParserReflect.parse(json, Person::class) as Person
        assertEquals(94646, p.id)
        assertEquals("Ze Manel", p.name)
    }


    @Test fun parseAccount() {
        val json = "{ balance: 485.90, transactions:[\"+590\",\"-200\",\"-30\"]}"
        val acc = JsonParserReflect.parse(json, Account::class) as Account
        assertEquals(485.90, acc.balance)
        assertEquals("+590", acc.transactions?.get(0) ?: "")
        assertEquals("-30", acc.transactions?.get(2) ?: "")
    }

    @Test fun parsePrimitive(){
        val json = "[\"LEIC\", \"LEIM\", \"LMATE\", \"LEETC\"]"
        val arr = JsonParserReflect.parseArray<String>(json)
        assertEquals(arr[0], "LEIC")
    }


    @Test fun parseAccountWithoutTrans(){
        val json = "{ balance: 1000.00 }"
        val acc = JsonParserReflect.parse(json, Account::class) as Account
        assertEquals(1000.00, acc.balance)
        assertNull(acc.transactions)
    }


    @Test fun parseIsel(){
        val cursos = arrayListOf("LEIC", "LEIM", "LMATE", "LEETC")
        val json = "{ nAlunos: 8000, cursos: [\"LEIC\", \"LEIM\", \"LMATE\", \"LEETC\"], diretor: \"Artur Ferreira\"}"
        val isel = JsonParserReflect.parse(json, Isel::class) as Isel

        assertEquals(cursos, isel.cursos)
        assertEquals(8000,isel.nAlunos)
    }


    @Test fun parseIselWithoutDir(){
        val cursos = arrayListOf("LEIC", "LEIM", "LMATE", "LEETC")
        val json = "{ nAlunos: 8000, cursos: [\"LEIC\", \"LEIM\", \"LMATE\", \"LEETC\"]}"
        val isel = JsonParserReflect.parse(json, Isel::class) as Isel

        assertEquals(cursos, isel.cursos)
        assertNull(isel.diretor)
    }

    @Test fun parseClassroom() {
        val json = "{ classroom: \"LEIC43D\", students:[{ name: \"Mafalda Rodrigues\", nr: 47184}, { name: \"Ines Martins\", nr: 47188}, { name: \"Fernando Fragateiro\", nr: 46071}]}"
        val cl = JsonParserReflect.parse(json, Classroom::class) as Classroom

        assertEquals("LEIC43D", cl.classroom)
        assertEquals(Student(47184,"Mafalda Rodrigues"), cl.students[0])
        assertEquals(Student(47188,"Ines Martins"), cl.students[1])
        assertEquals(Student(46071,"Fernando Fragateiro"), cl.students[2])
    }

}
