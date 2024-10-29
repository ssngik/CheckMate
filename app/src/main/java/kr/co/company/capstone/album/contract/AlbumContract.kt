package kr.co.company.capstone.album.contract

interface AlbumContract {
    interface View {
        fun requestPermission()
        fun showPermissionRationaleDialog()
        fun loadAlbumImages(images: List<String>)
        fun showConvinceDialog()    // 권한 설명 다이얼로그
        fun navigateToSettings()

    }
    interface Presenter{
        fun checkPermission()
        fun onPermissionResult(granted: Boolean)
        fun loadImages()
    }
    interface Model {
        fun fetchImages(): List<String>
    }
}