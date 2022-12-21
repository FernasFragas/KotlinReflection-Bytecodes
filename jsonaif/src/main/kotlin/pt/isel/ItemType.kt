package pt.isel

import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ItemType(val type: KClass<*>)
