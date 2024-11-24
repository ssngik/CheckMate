package kr.co.company.capstone.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.ActivityBottomBinding

class MainActivity : AppCompatActivity(), MainContract.View {
    private var _binding : ActivityBottomBinding? = null
    private val binding get() = _binding!!
    private var presenter : MainPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityBottomBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Set Up Navigation
        setUpNavigation()

        presenter = MainPresenter(this)

        // 알림 및 외부 Intent 관리
        handleIntent(intent)
    }

    override fun setUpNavigation() {
        val navView = binding.bottomNavView

        // NavController 초기화
        val navController = findNavController(this, R.id.navHostFragment)

        // NavController와 BottomNavigationView 연결
        setupWithNavController(navView, navController)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    // 알림에 따른 동작 수행
    private fun handleIntent(intent: Intent?) {
        intent ?: return

        val navigateTo = intent.getStringExtra("navigateTo")
        val message = intent.getStringExtra("messageBody")
        val notificationId = intent.getLongExtra("notificationId", 0L)

        val navController = findNavController(this, R.id.navHostFragment)

        when (navigateTo) {
            "INVITE_SEND" -> { // 목표 초대 메시지
                val bundle = Bundle().apply {
                    putString("messageBody", message)
                    putLong("notificationId", notificationId)
                }
                navController.navigate(R.id.invitationReplyDialogFragment, bundle)
            }

            "TimeLine" -> {
                val bundle = Bundle().apply {
                    putLong("notificationId", notificationId)
                }
                navController.navigate(R.id.timeLineFragment, bundle)
            }

            else -> {
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}