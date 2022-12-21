package pt.isel

import pt.isel.sample.Date
import pt.isel.sample.LazyObj
import pt.isel.sample.Person

/**
 * File with all the implementations of the classes/objects
 * ??(objects because:
 * > creates an instance automatically
 * > The instance is only created 1 time
 * > object is also a type
 * )??
 * and functions necessaries to convert from JSON to the Types specified below:
 * -Date -> JsonToDate
 **/

const val IDX_YEAR: Int = 0
const val IDX_MONTH: Int = 1
const val IDX_DAY: Int = 2

const val IDX_ID: Int = 0
const val IDX_NAME: Int = 1
const val IDX_DATE: Int = 2
const val IDX_SIBLING = 3

/**
 * it's like a static function that implements an interface
 **/
object JsonToDate: Converter<String, Date>{

    override fun converter(jsonDate: String): Date{

        val date = jsonDate.split('-')

        val year = date[IDX_YEAR].toInt()
        val montn = date[IDX_MONTH].toInt()
        val day = date[IDX_DAY].toInt()

        return Date(day,montn,year)
    }
}

object JsonToPerson: Converter<String, Person>{

    override fun converter(jsonPerson: String): Person{
        val person = jsonPerson.split(", ")

        val id = person[IDX_ID].toInt()
        val name = person[IDX_NAME]

        return Person(id,name)
    }
}

object JsonToLazyCounter: Converter<Int, Int> {
    var counter = 0

    override fun converter(input: Int): Int {
        return ++counter
    }

    fun resetCounter() {
        counter = 0
    }

}
