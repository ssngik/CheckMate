package kr.co.company.capstone.main

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
    }

    override fun setUpNavigation() {
        val navView = binding.bottomNavView

        // NavController 초기화
        val navController = findNavController(this, R.id.navHostFragment)

        // NavController와 BottomNavigationView 연결
        setupWithNavController(navView, navController)
    }

    companion object {
        private const val LOG_TAG = "MainActivity"
    }

}