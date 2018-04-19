package model

import java.io.Serializable


//status "打开", "进行中", "结束", "关闭"
class Assignment(var id: Int,
                 var title: String,
                 var content: String,
                 var employer_id: Int,
                 var employer_name: String,
                 var date: String,
                 var price: Float,
                 var avg_price: Float,
                 var servants: Int,
                 var images: Int,
                 var comments: Int,
                 var status: Int,
                 var latitude: Double,
                 var longitude: Double,
                 var what: Int) : Serializable{

    companion object {
        const val STATUS_OPEN = 0
        const val STATUS_GOING = 1
        const val STATUS_CHECKING = 2
        const val STATUS_FINISHED = 3
        const val STATUS_CLOSED = 4
    }
}