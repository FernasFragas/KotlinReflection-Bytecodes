package pt.isel.sample

import pt.isel.JsonConvert
import pt.isel.JsonProperty
import pt.isel.JsonToPerson

data class Person (val id: Int, val name: String,
                   @property:JsonProperty("birth_date") @param:JsonProperty("birth_date") val birth: Date? = null,
                   @property:JsonConvert(JsonToPerson::class) @param:JsonConvert(JsonToPerson::class) var sibling: Person? = null)
