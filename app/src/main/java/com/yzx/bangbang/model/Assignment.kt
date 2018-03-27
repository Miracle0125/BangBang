package model

import java.io.Serializable

class Assignment(var id: Int,
                 var title: String,
                 var content: String,
                 var employer_id: Int,
                 var employer_name: String,
                 var date: String,
                 var price: Float,
                 var servants: Int,
                 var images: Int,
                 var comments: Int,
                 var status: Int,
                 var latitude: Double,
                 var longitude: Double) :Serializable