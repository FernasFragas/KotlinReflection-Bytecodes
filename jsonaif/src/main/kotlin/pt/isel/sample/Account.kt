package pt.isel.sample

//data class Account(val balance:Double, val transactions: Map<String, List<Double>>)

data class Account(val balance:Double, val transactions: ArrayList<String>?=null)