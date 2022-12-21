package pt.isel

import org.junit.Test
import pt.isel.sample.LazyObj
import kotlin.test.assertEquals

class JsonParseLazyTest {

    @Test
    fun parseSequenceTest(){
        val json = "[{ name: \"lazyobj1\", counter: 0},{ name: \"lazy_obj_2\", counter: 0}, { name: \"lazy_obj_3\", counter: 0}]"
        val sequence = JsonParserReflect.parseSequence<LazyObj>(json).iterator()
        var count = 0
        var currElem: LazyObj? = LazyObj("lazy_obj_0",0)
        while (count < 2){
            currElem = sequence.next()
            println("Sequence = $currElem")
            ++count
        }

        assertEquals(count, currElem!!.counter)
        JsonToLazyCounter.resetCounter()
    }
    @Test
    fun parseSequenceTest2(){
        val json = "[{ name: \"lazyobj1\", counter: 0},{ name: \"lazy_obj_2\", counter: 0}, { name: \"lazy_obj_3\", counter: 0}]"
        val sequence = JsonParserReflect.parseSequence<LazyObj>(json).iterator()
        var count = 0
        var currElem: LazyObj? = LazyObj("lazy_obj_0",0)
        while (count < 3){
            currElem = sequence.next()
            println("Sequence = $currElem")
            ++count
            assertEquals(count, currElem!!.counter)
        }
        JsonToLazyCounter.resetCounter()
    }
}

