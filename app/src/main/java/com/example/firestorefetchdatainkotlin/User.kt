package com.example.firestorefetchdatainkotlin

import java.io.Serializable

data class User(var firstName:String?=null, var lastName:String?=null, var age: Int? =0, var img:String?):Serializable{

constructor() : this(null, null, 0, null)
}
