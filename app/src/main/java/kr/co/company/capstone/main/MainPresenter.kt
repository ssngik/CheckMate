package kr.co.company.capstone.main

import android.content.Intent
import android.util.Log
import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.service.NotificationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainPresenter (val mainContractView : MainContract.View) : MainContract.Presenter {}
