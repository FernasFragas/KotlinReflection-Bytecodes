package pt.isel.sample

import pt.isel.JsonConvert
import pt.isel.JsonToLazyCounter
import pt.isel.JsonToPerson

data class LazyObj(val name: String, @property:JsonConvert(JsonToLazyCounter::class) @param:JsonConvert(JsonToLazyCounter::class) val counter: Int)
