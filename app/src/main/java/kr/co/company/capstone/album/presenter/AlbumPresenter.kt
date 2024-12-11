package kr.co.company.capstone.album.presenter

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kr.co.company.capstone.album.contract.AlbumContract
import kr.co.company.capstone.album.model.AlbumModel

class AlbumPresenter(
    private val view: AlbumContract.View
) : AlbumContract.Presenter {

    private val selectedUris = LinkedHashMap<Uri, Int>() // 선택된 이미지 URI와 순서 저장
    private var isMultiSelectionMode = false // 다중 선택 모드 여부
    private val maxSelection = 3 // 최대 선택 개수

    // 권한 체크
    override fun checkPermission() {
        val fragment = view as Fragment
        val context = fragment.requireContext()
        val permission = getRequiredPermission()

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 허용된 경우
            loadImages()
        } else {
            // 권한이 허용되지 않은 경우
            view.requestPermission()
        }
    }

    // 필요 권한 반환
    private fun getRequiredPermission(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    // 권한 요청 결과 처리
    override fun onPermissionResult(granted: Boolean) {
        val fragment = view as Fragment
        val permission = getRequiredPermission()

        if (granted) {
            loadImages()
        } else {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                fragment.requireActivity(),
                permission
            )

            if (shouldShowRationale) {
                // 사용자가 권한을 한 번 거부한 경우
                view.showPermissionRationaleDialog()
            } else {
                // 사용자가 다시 묻지 않음을 선택하고 거부한 경우
                view.showConvinceDialog()
            }
        }
    }

    // 다중 선택 모드에서 이미지 선택 또는 해제 처리
    private fun handleMultiSelection(uri: Uri) {
        if (selectedUris.containsKey(uri)) {
            // 이미 선택된 이미지인 경우 선택 해제 (마지막 한 장은 해제 불가)
            if (selectedUris.size > 1) {
                selectedUris.remove(uri)
                reassignSelectionNumbers() // 선택 순서 재정렬
                view.refreshAdapter()
            }
        } else {
            // 최대 선택 개수 체크
            if (selectedUris.size < maxSelection) {
                selectedUris[uri] = selectedUris.size + 1
                view.updateSelectedImages(uri)
                view.refreshAdapter()
            }
        }
    }

    // 선택된 이미지들 순서 재정렬
    private fun reassignSelectionNumbers() {
        var index = 1
        for (key in selectedUris.keys) {
            selectedUris[key] = index++
        }
    }

    // 이미지 로드
    override fun loadImages() {
        val images = AlbumModel((view as Fragment).requireContext()).fetchImages()
        view.loadAlbumImages(images)
    }

    // 다중 선택 모드 취소
    override fun cancelMultiSelectionMode() {
        isMultiSelectionMode = false
        view.hideSelectionNumbers()

        // 마지막으로 선택 됐던 이미지로 설정
        val lastSelectedUri = selectedUris.keys.lastOrNull()
        selectedUris.clear()
        lastSelectedUri?.let {
            selectedUris[it] = 1
            view.updateSelectedImages(it)
        }
        view.refreshAdapter()
    }

    // 선택된 이미지 URI 리스트 반환
    override fun getSelectedUris(): Map<Uri, Int> = selectedUris

    // 이미지 클릭 시 호출
    override fun onImageSelected(uri: Uri) {
        if (isMultiSelectionMode) {
            handleMultiSelection(uri)
        } else {
            // 단일 선택 모드
            selectedUris.clear()
            selectedUris[uri] = 1
            view.updateSelectedImages(uri)
            view.refreshAdapter()
        }
    }

    // 이미지 길게 클릭 시 호출
    override fun onImageLongClicked(uri: Uri) {
        if (!isMultiSelectionMode) {
            isMultiSelectionMode = true
            view.updateMultiSelectButtonState(isMultiSelectionMode)
            view.showSelectionNumbers()

            // 길게 누른 해당 아이템이 첫 번째 선택된 이미지
            selectedUris[uri] = 1
            view.updateSelectedImages(uri)
            view.refreshAdapter()
        } else {
            handleMultiSelection(uri)
        }
    }

    // 다중 선택 모드 토글
    override fun toggleSelectionMode() {
        isMultiSelectionMode = !isMultiSelectionMode
        view.updateMultiSelectButtonState(isMultiSelectionMode)
        if (isMultiSelectionMode) {
            // 다중 선택 모드로 전환
            view.showSelectionNumbers()
            view.refreshAdapter()
        } else {
            // 단일 선택 모드로 전환
            cancelMultiSelectionMode()
        }
    }
}
