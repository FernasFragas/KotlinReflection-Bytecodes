package pt.isel.sample

import pt.isel.ItemType

data class Classroom(val classroom: String, @ItemType(Student::class) val students: ArrayList<Student>)