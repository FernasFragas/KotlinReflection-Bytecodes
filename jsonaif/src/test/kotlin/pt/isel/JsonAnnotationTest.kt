package pt.isel

import pt.isel.sample.Date
import pt.isel.sample.Person
import pt.isel.sample.Student
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonAnnotationTest{

    @Test
    fun parseComposeObjectWithAnnotation() {
        val json = "{ id: 94646, name: \"Ze Manel\", birth_date: { year: 1999, month: 9, day: 19}}}"
        val p = JsonParserReflect.parse(json, Person::class) as Person
        val birth = p.birth as Date
        val date = Date(19,9,1999)
        assertEquals(date.day,birth.day)
        assertEquals(date.month,birth.month)
        assertEquals(date.year,birth.year)
    }
/*
    @Test
    fun parseComposeObjectWithAnnotation1() {
        val json = "{ id: 94646, name: \"Ze Manel\", birth_date: { year: 1999, month: 9, day: 19}, sibling: \"41231, \"Kata Badala\"\"}"
        val p = JsonParserReflect.parse(json, Person::class) as Person
        val birth = p.birth as Date
        val date = Date(19,9,1999)
        assertEquals(date.day,birth.day)
        assertEquals(date.month,birth.month)
        assertEquals(date.year,birth.year)
    }
*/
    @Test
    fun converterDate() {
        val strD = "1998-11-17"
        val date = JsonToDate.converter(strD) as Date
        val originalDate = Date(17,11,1998)
        assertEquals(originalDate.day,date.day)
        assertEquals(originalDate.month,date.month)
        assertEquals(originalDate.year,date.year)
    }

    /*@Test
    fun parseWithoutConverterDate() {
        val json = "{ name: \"Maria Papoila\", nr: 73753, birth: { year: 1999, month: 9, day: 19} }"
        val student = JsonParserReflect.parse(json, Student::class) as Student
        val date = Date(19,9,1999)
        assertEquals("Maria Papoila", student.name)
        assertEquals(73753, student.nr)
        assertEquals(date, student.birth)
    }*/

    @Test
    fun  parseWithConverterDate() {
        val json = "{ name: \"Maria Papoila\", nr: 73753, birth: \"1998-11-17\" }"
        val student = JsonParserReflect.parse(json, Student::class) as Student
        val date = Date(17,11,1998)
        assertEquals("Maria Papoila", student.name)
        assertEquals(73753, student.nr)
        assertEquals(date, student.birth)
    }

    @Test
    fun parseWithConverterPerson() {
        val json = "{ id: 94646, name: \"Ze Manel\", birth: { year: 1999, month: 9, day: 19}, sibling: \"1234, Kata Badala\"}"
        val p = JsonParserReflect.parse(json, Person::class) as Person
        assertEquals(94646, p.id)
        assertEquals("Ze Manel", p.name)
        assertEquals(19, p.birth?.day)
        assertEquals(9, p.birth?.month)
        assertEquals(1999, p.birth?.year)
        val s = Person(1234,"Kata Badala")
        assertEquals(s.id, p.sibling?.id)
        assertEquals(s.name, p.sibling?.name)
    }


    @Test
    fun converterPerson() {
        val strD = "1234, Joao Ferreira"
        val person = JsonToPerson.converter(strD) as Person
        val originalPerson = Person(1234, "Joao Ferreira")
        assertEquals(originalPerson.id, person.id)
        assertEquals(originalPerson.name, person.name)
    }
/*
    @Test
    fun converterWithoutPerson() {
        val json = "{ id: 1000, name: \"Joao\", birth: { year: 2001, month: 6, day: 10}, sibling: {id: 1234, name: \"Kata Badala\"}}"
        val p = JsonParserReflect.parse(json, Person::class) as Person
        assertEquals(1000, p.id)
        assertEquals("Joao", p.name)
        assertEquals(10, p.birth?.day)
        assertEquals(6, p.birth?.month)
        assertEquals(2001, p.birth?.year)
        val s = Person(1234,"Kata Badala")
        assertEquals(s.id, p.sibling?.id)
        assertEquals(s.name, p.sibling?.name)
    }
*/
}