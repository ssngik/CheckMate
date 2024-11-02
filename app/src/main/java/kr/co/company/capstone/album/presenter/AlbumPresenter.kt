package kr.co.company.capstone.album.presenter

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kr.co.company.capstone.album.contract.AlbumContract
import kr.co.company.capstone.album.model.AlbumModel

class AlbumPresenter(
    private val view: AlbumContract.View
) : AlbumContract.Presenter {

    override fun checkPermission() {
        val fragment = view as Fragment
        val context = fragment.requireContext()

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 허용된 경우
            loadImages()
        } else {
            // 권한이 허용되지 않은 경우
            view.requestPermission()
        }
    }

    override fun onPermissionResult(granted: Boolean) {
        val fragment = view as Fragment

        if (granted) {
            loadImages()
        } else {
            val shouldShowRationale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }else {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            if (shouldShowRationale) {
                // 사용자가 권한을 한 번 거부한 경우
                view.showPermissionRationaleDialog()
            } else {
                // 사용자가 다시 묻지 않음을 선택하고 거부한 경우
                view.showConvinceDialog()
            }
        }
    }

    override fun loadImages() {
        val images = AlbumModel((view as Fragment).requireContext()).fetchImages()
        view.loadAlbumImages(images)
    }
}
