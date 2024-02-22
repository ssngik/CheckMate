//package kr.co.company.capstone.util
//
//import android.content.DialogInterface
//import androidx.fragment.app.DialogFragment
//
//class CustomDialog (
//    customDialogInterface: DialogInterface,
//    text : String, id: Int
//) : DialogFragment(){
//
//    // 뷰 바인딩 정의
//    private var _binding: DialogPackageDeleteBinding? = null
//    private val binding get() = _binding!!
//
//    private var confirmDialogInterface: ConfirmDialogInterface? = null
//
//    private var text: String? = null
//    private var id: Int? = null
//
//    init {
//        this.text = text
//        this.id = id
//        this.confirmDialogInterface = confirmDialogInterface
//    }
//
//}