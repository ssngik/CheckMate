package kr.co.company.capstone.album.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kr.co.company.capstone.album.contract.AlbumContract
import kr.co.company.capstone.album.presenter.AlbumPresenter
import kr.co.company.capstone.databinding.FragmentAlbumBinding
import kr.co.company.capstone.fragment.ErrorDialogFragment

class AlbumFragment : Fragment(), AlbumContract.View {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AlbumContract.Presenter
    private lateinit var albumAdapter: AlbumAdapter
    private val args: AlbumFragmentArgs by navArgs()

    // 권한 요청
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            presenter.onPermissionResult(isGranted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AlbumPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adjustImageHeightToScreenWidth() // 이미지 뷰의 높이 -> 화면 너비
        initRecyclerView() // 리사이클러뷰 초기화
        initListener() // 리스너 초기화
        initTouchEvent() // 이미지 스크롤뷰 터치 이벤트 설정
        initUi() // 전달 받은 제목 설정

        presenter.checkPermission() // 권한 체크
    }

    private fun initUi() {
        binding.postTitle.text = args.title
    }

    private fun initTouchEvent() {
        binding.imageNestedScroll.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            v.onTouchEvent(event)
        }
    }

    private fun adjustImageHeightToScreenWidth() {
        val screenWidth = resources.displayMetrics.widthPixels
        val nestedScrollParams = binding.imageNestedScroll.layoutParams
        nestedScrollParams.height = screenWidth
        binding.imageNestedScroll.layoutParams = nestedScrollParams
    }

    // 리사이클러뷰 및 어댑터 초기화
    private fun initRecyclerView() {
        albumAdapter = AlbumAdapter (
            onItemClick = { uri ->
                presenter.onImageSelected(uri)
            },
            onItemLongClick =  { uri ->
                presenter.onImageLongClicked(uri)
            }
        )

        binding.albumRecyclerview.layoutManager = GridLayoutManager(context, 4)
        binding.albumRecyclerview.adapter = albumAdapter
    }

    // 리스너 초기화
    private fun initListener() {
        binding.multiSelectButton.setOnClickListener {
            presenter.toggleSelectionMode() // 멀티 선택 모드 토글
        }

        // post Fragment로 이동
        binding.btnNavigateToPost.setOnClickListener {
            val selectedUris = presenter.getSelectedUris().keys.toList()
            val uriStrings = selectedUris.map { it.toString() }.toTypedArray()

            val action = AlbumFragmentDirections.actionAlbumFragmentToPostFragment(uriStrings, args.title, args.goalId, args.mateId, args.userId)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 권한 요청 메서드
    override fun requestPermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
        requestPermissionLauncher.launch(permission)
    }

    // 앨범 이미지 로드
    override fun loadAlbumImages(images: List<Uri>) {
        albumAdapter.submitList(images)
        if (images.isNotEmpty()) {
            presenter.onImageSelected(images[0]) // 첫 번째 이미지를 기본 선택
        }
    }

    // 권한 설정 안내 다이얼로그 표시
    override fun showConvinceDialog() {
        val dialog = ErrorDialogFragment.getErrorMessage("이미지를 불러오기 위한 권한을 허용해주세요.")
        dialog.setPositiveClickListener { navigateToSettings() }
        dialog.show(childFragmentManager, "PermissionConvinceDialog")
    }

    // 설정 화면으로 이동
    override fun navigateToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    // 선택된 이미지 업데이트
    override fun updateSelectedImages(uri: Uri) {
        Glide.with(requireContext()).load(uri).into(binding.selectedImage)
        expandAppBar()
    }

    // 어댑터 새로고침
    override fun refreshAdapter() {
        val selectedUris = presenter.getSelectedUris()
        albumAdapter.updateSelection(selectedUris)
    }

    // 선택 번호 표시 :다중 선택 모드
    override fun showSelectionNumbers() {
        albumAdapter.isSelectionMode = true
        albumAdapter.notifyDataSetChanged()
    }

    // 선택 번호 숨김 :단일 선택 모드
    override fun hideSelectionNumbers() {
        albumAdapter.isSelectionMode = false
        albumAdapter.notifyDataSetChanged()
    }

    // 여러장 선택 버튼 상태 업데이트
    override fun updateMultiSelectButtonState(isSelected: Boolean) {
        binding.multiSelectButton.isSelected = isSelected
    }

    private fun checkPermissionStatus(): Boolean {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()

        val hasPermission = checkPermissionStatus()
        if (hasPermission) {
            presenter.loadImages()
        }
    }

    override fun showPermissionRationaleDialog() {
        val dialog = ErrorDialogFragment.getErrorMessage("이미지를 불러오기 위한 권한이 필요해요.")
        dialog.setPositiveClickListener { requestPermission() }
        dialog.show(childFragmentManager, "PermissionRationaleDialog")
    }

    // AppBar 확장
    private fun expandAppBar() {
        binding.imageAppbar.setExpanded(true, true)
    }
}
