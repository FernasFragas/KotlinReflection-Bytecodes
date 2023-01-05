package pt.isel

import org.junit.Test
import pt.isel.sample.Person
import pt.isel.sample.Student
import kotlin.test.assertEquals
import pt.isel.*
import kotlin.io.path.Path

class JsonParseFolderTest{

    val path = "${Path("testFiles").toAbsolutePath()}"

    @Test fun parseFolderEager() {
        val e = JsonParserReflect.parseFolderEager<Student>(path)
        val file1 = e[0]
        val file2 = e[1]

        assertEquals("Ze Manel",file1.name)
        assertEquals("Saúl",file2.name)
        assertEquals(7353,file1.nr)
        assertEquals(47184,file2.nr)

    }

    @Test fun parseFolderLazy() {
        val e = JsonParserReflect.parseFolderLazy<Student>(path)
        val list = e.toList()
        val file1 = list[0]
        val file2 = list[1]

        assertEquals("Ze Manel",file1?.name)
        assertEquals("Saúl",file2?.name)
        assertEquals(7353,file1?.nr)
        assertEquals(47184,file2?.nr)
    }


}