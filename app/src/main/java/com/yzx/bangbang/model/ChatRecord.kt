package model

class ChatRecord (var id: Int,
                  var convr_id:String,
                  var poster: Int,
                  var receiver: Int,
                  var body: String,
                  var date: String,
                  var type: Int,
                    var read:Int
){
    companion object {
        const val TYPE_TEXT=0
        const val TYPE_IMAGE=1
    }
}
