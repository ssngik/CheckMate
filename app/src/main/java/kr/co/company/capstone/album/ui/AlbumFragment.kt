package kr.co.company.capstone.album.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import kr.co.company.capstone.R
import kr.co.company.capstone.album.contract.AlbumContract
import kr.co.company.capstone.album.presenter.AlbumPresenter
import kr.co.company.capstone.databinding.FragmentAlbumBinding
import kr.co.company.capstone.fragment.ErrorDialogFragment

class AlbumFragment : Fragment(), AlbumContract.View {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AlbumContract.Presenter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            presenter.onPermissionResult(isGranted)
        }

    // 이미지 다중 선택 모드 flag
    private var isMultiSelectMode = false

    private val args: AlbumFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AlbumPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        initListener()

        binding.postTitle.text = args.title
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.checkPermission() // 앨범 권한 확인
    }

    private fun initListener() {
        // multi_select_button 클릭 시 selected 속성 토글
        binding.multiSelectButton.setOnClickListener {
            it.isSelected = !it.isSelected // selected 상태 토글
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 권한 요청
    override fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun loadAlbumImages(images: List<String>) {
        Log.d("asdf" , images.toString())
    }

    override fun showConvinceDialog() {
        val dialog = ErrorDialogFragment.getErrorMessage("이미지를 불러오기 위한 권한을 허용해주세요.")
        dialog.setPositiveClickListener { navigateToSettings() }
        dialog.show(childFragmentManager, "PermissionConvinceDialog")
    }

    override fun navigateToSettings() {
        // 설정 화면 이동 Intent
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // 권한 설정 후 권한 상태 재확인
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            presenter.loadImages()
        }
    }

    override fun showPermissionRationaleDialog() {
        val dialog = ErrorDialogFragment.getErrorMessage("이미지를 불러오기 위한 권한이 필요해요.")
        dialog.setPositiveClickListener { requestPermission() }
        dialog.show(childFragmentManager, "PermissionRationaleDialog")
    }
}
