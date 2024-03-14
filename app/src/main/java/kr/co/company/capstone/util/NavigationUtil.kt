package kr.co.company.capstone.util

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation

object NavigationUtil {

    // 데이터 전달이 필요한 경우 화면 전환
    fun navigateTo(view : View, destinationId : Int){
        val navController = findNavController(view)
        navController.navigate(destinationId)
    }

    // 데이터 전달이 필요한 경우
    fun navigateTo(view: View, destinationId: Int, args: Bundle){
        val navController = findNavController(view)
        navController.navigate(destinationId, args)
    }

    private fun findNavController(view : View): NavController{
        return Navigation.findNavController(view)
    }

}