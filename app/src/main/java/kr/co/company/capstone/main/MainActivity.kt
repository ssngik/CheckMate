package kr.co.company.capstone.main

import android.content.Intent
import android.os.Bundle
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

        val bundle = when (navigateTo) {
            // 목표 초대 메시지 처리
            "INVITE_SEND" -> Bundle().apply {
                putString("messageBody", intent.getStringExtra("messageBody"))
                putLong("notificationId", intent.getLongExtra("notificationId", 0L))
            }
            // 게시물 업로드 알림
            "POST_UPLOAD" -> Bundle().apply {
                putLong("goalId", intent.getLongExtra("goalId", 0L))
                putLong("userId", intent.getLongExtra("userId", 0L))
            }
            // 이외는 처리할 것 없음.
            else -> null
        }


        bundle?.let {
            val navController = findNavController(this, R.id.navHostFragment)
            val destinationId = when (navigateTo) {
                "INVITE_SEND" -> R.id.invitationReplyDialogFragment
                "POST_UPLOAD" -> R.id.timeLineFragment
                else -> null
            }
            destinationId?.let { navController.navigate(it, bundle) }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}