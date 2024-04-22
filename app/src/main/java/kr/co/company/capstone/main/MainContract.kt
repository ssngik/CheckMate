package kr.co.company.capstone.main

import kr.co.company.capstone.dto.notification.NotificationDetailListResponseKt

interface MainContract {
    interface View{
        fun setUpNavigation()
    }

    interface Presenter{}
}