package pt.isel.sample

import pt.isel.JsonConvert
import pt.isel.JsonToDate
import pt.isel.JsonToPerson

data class Student (var nr: Int = 0, var name: String? = null,
                    @property:JsonConvert(JsonToDate::class) @param:JsonConvert(JsonToDate::class) var birth: Date? = null)
