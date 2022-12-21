package pt.isel

import pt.isel.sample.Date
import pt.isel.sample.Person

fun parsePerson(json: String, parser: JsonParser) : Person {
    return parser.parse(json, Person::class) as Person
}

fun parseDate(json: String, parser: JsonParser) : Date {
    return parser.parse(json, Date::class) as Date
}
