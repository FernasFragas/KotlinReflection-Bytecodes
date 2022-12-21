package pt.isel

/**
 * interface implemented for all the object that convert from JSON
 */
interface Converter<in T, out S> {

    fun converter(input: T): S?

}