package kr.co.company.capstone.fragment

import androidx.navigation.Navigation.createNavigateOnClickListener
import android.os.Bundle
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import kr.co.company.capstone.R
import android.widget.TextView
import kr.co.company.capstone.util.SharedPreferenceUtil
import kr.co.company.capstone.service.LoginService
import android.content.Intent
import kr.co.company.capstone.activity.LoginActivity
import android.widget.RelativeLayout
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.co.company.capstone.Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingPageFragment : Fragment() {
    private val LOG_TAG = "SettingPageFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting_icon, container, false)

        /*
         * UI 설정 부분
         */

        // 사용자 닉네임 UI 설정
        val myNickName = view.findViewById<TextView>(R.id.Nickname)
        myNickName.text = SharedPreferenceUtil.getString(context, "nickName")

        /*
        * 기능 부분
        * */

        // 로그아웃 버튼 리스너 설정
        setClickLogOutButton(view)

        // 닉네임 변경으로 이동
        editNickName(view)

        // 화면 모드 설정
        setWhiteOrDarkMode(view)

        // 이메일 보내기
        clickEmail(view)

        // 개인정보 처리방침
        privacyInfo(view)
        return view
    }

    // 닉네임 변경
    private fun editNickName(view: View) {
        val editNickNameTextButton = view.findViewById<TextView>(R.id.edit_nickname)
        editNickNameTextButton.setOnClickListener(createNavigateOnClickListener(R.id.editNicknameFragment))
    }

    private fun setClickLogOutButton(view: View) {
        val logout = view.findViewById<TextView>(R.id.Logout)
        logout.text = SharedPreferenceUtil.getString(context, "nickName") + " 로그아웃"
        logout.setOnClickListener {
            LoginService.getService().logout().enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    if (response.isSuccessful) {
                        SharedPreferenceUtil.removeKey(activity, "accessToken")
                        SharedPreferenceUtil.removeKey(activity, "refreshToken")
                        requireActivity().finish()
                        Log.d(LOG_TAG, "succes!")

                        // 초기 로그인 화면으로 이동
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        showErrorDialog("로그아웃 할 수 없습니다.")
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    showErrorDialog("문제가 발생했습니다.")
                }
            })
        }
    }
    private fun showErrorDialog(errorMessage : String){
        ErrorDialogFragment.getErrorMessage(errorMessage).show(parentFragmentManager, "error_dialog")
    }

    // 화면 모드 변경
    private fun setWhiteOrDarkMode(view: View) {

        // 사용자 지원 안내 정보 구역
        val settingSupportArea = view.findViewById<RelativeLayout>(R.id.setting_support_area)
        settingSupportArea.isClickable = true

        // 화면 테마 Text Button
        val displayThemeTextButton = view.findViewById<TextView>(R.id.Screen_mode)

        // 다크 | 라이트 모드 설정 버튼 클릭 리스너
        displayThemeTextButton.setOnClickListener {
            val words = arrayOf("라이트 모드", "다크 모드")
            AlertDialog.Builder(activity).setTitle("화면 스타일 선택")
                .setSingleChoiceItems(words, -1) { dialog, which -> // 사용자 지원 안내 부분 터치 불가 상태
                    settingSupportArea.isClickable = false
                    when (which) {
                        0 -> {
                            Theme.applyTheme(Theme.LIGHT_MODE)
                            SharedPreferenceUtil.setString(activity, "scnMode", Theme.LIGHT_MODE)
                        }
                        1 -> {
                            Theme.applyTheme(Theme.DARK_MODE)
                            SharedPreferenceUtil.setString(activity, "scnMode", Theme.DARK_MODE)
                        }
                    }

                    // 변경 완료 알림
                    Toast.makeText(activity, words[which] + "로 변경되었습니다.", Toast.LENGTH_SHORT).show()

                    // 사용자 지원 안내 부분 터치 가능 상태
                    settingSupportArea.isClickable = true
                }.show()
        }
    }

    // 이메일 눌렀을 때
    private fun clickEmail(view: View) {
        val emailTextButton = view.findViewById<TextView>(R.id.Support_email)
        emailTextButton.setOnClickListener { view1: View? ->
            val email = Intent(Intent.ACTION_SEND)
            email.type = "plain/text"
            val address = arrayOf("teamcheckus@gmail.com")
            email.putExtra(Intent.EXTRA_EMAIL, address)
            email.putExtra(Intent.EXTRA_SUBJECT, "help")
            startActivity(email)
        }
    }

    // 정책 관련 안내
    private fun privacyInfo(view: View) {
        val privacy = view.findViewById<TextView>(R.id.Privacy_policy)
        privacy.setOnClickListener { v: View? ->
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://checkus.notion.site/0b59fd1da54e44aa9cc5fa10b36868de")
            )
            startActivity(intent)
        }
    }
}