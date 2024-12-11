package kr.co.company.capstone.album.contract

import android.net.Uri

interface AlbumContract {
    interface View {
        fun requestPermission()
        fun showPermissionRationaleDialog()
        fun loadAlbumImages(images: List<Uri>)
        fun showConvinceDialog()
        fun navigateToSettings()
        fun updateSelectedImages(uri : Uri)

        fun refreshAdapter()
        fun showSelectionNumbers()
        fun hideSelectionNumbers()
        fun updateMultiSelectButtonState(isSelected: Boolean)
    }

    interface Presenter{
        fun checkPermission()
        fun onPermissionResult(granted: Boolean)
        fun loadImages()
        fun onImageSelected(uri : Uri)

        fun onImageLongClicked(uri : Uri)
        fun toggleSelectionMode()
        fun cancelMultiSelectionMode()
        fun getSelectedUris(): Map<Uri, Int>

    }
    interface Model {
        fun fetchImages(): List<Uri>
    }
}