package kr.co.company.capstone.fragment;

import android.Manifest;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import id.zelory.compressor.Compressor;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.util.FileTransferUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.post.PostRegisterResponse;
import kr.co.company.capstone.service.PostRegisterService;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DoMyGoalFragment extends Fragment {

    List<ImageView> imageViewList = new ArrayList<>();

    private List<Uri> imageUriList = new ArrayList<>();
    private long goalId,teamMateId;
    private String title;
    private final String LOG_TAG = "DoMyGoalFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
        {
            Bundle bundle = getArguments();
            goalId = bundle.getLong("goalId");
            teamMateId = bundle.getLong("teamMateId");
            title = bundle.getString("goalTitle");
        }else
        {
            Log.d(LOG_TAG, "arguments is null");
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_certification_detail, container, false);

        ProgressBar loading = setUpLoading(view); // 로딩 화면 초기화 및 설정
        EditText userTextField = view.findViewById(R.id.text_field); // userTextField 초기화

        // 등록 버튼 초기화
        Button registerButton = view.findViewById(R.id.Post);

        // ImageView 초기화
        initializeImageViews(view);

        // textView 설정 (목표 이름, 오늘 날짜)
        setTextViews(view);

        //카메라 권한을 요청 및 처리
        //요청 허용, 거부에 따른 Toast 메시지
        requestCameraPermission();

        // 이미지 리스너 등록
        setImageSelectListener();

        // 등록 버튼 리스너
        clickRegisterButton(loading, userTextField, registerButton);

        return view;
    }

    // 이미지 리스너 등록
    private void setImageSelectListener() {
        View.OnClickListener imageSelectListener = getImageSelectListener();
        imageViewList.forEach(iv -> iv.setOnClickListener(imageSelectListener));
    }

    // 등록 버튼 리스너
    private void clickRegisterButton(ProgressBar loading, EditText userTextField, Button registerButton) {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {

                // 로딩 표시
                loading.setVisibility(View.VISIBLE);

                // 필드값 삽입
                // Map 에 mateId 와 사용자 입력 텍스트 설정
                Map<String, RequestBody> map = putFiledValueToMap();

                // 등록 (API 호출)
                registerUserData(view, map);
            }


            private void registerUserData(View view, Map<String, RequestBody> map) throws IOException {
                PostRegisterService.getService().register(map, getImages(imageUriList))
                        .enqueue(new Callback<PostRegisterResponse>() {
                            @SneakyThrows
                            @Override
                            public void onResponse(Call<PostRegisterResponse> call, Response<PostRegisterResponse> response) {
                                if (response.isSuccessful()) {
                                    // 등록 성공시 타임라면 화면으로 이동
                                    TimeLineFragment timeLineFragment = new TimeLineFragment();

                                    Bundle next = new Bundle();

                                    next.putLong("goalId", goalId);
                                    timeLineFragment.setArguments(next);

                                    Navigation.findNavController(view).navigate(R.id.action_doMyGoalFragment_to_timeLineFragment, next);
                                } else {
                                    // 등록 실패시
                                    ErrorMessage errorMessage = ErrorMessage.getErrorByResponse(response);
                                    Toast.makeText(getActivity(),
                                            errorMessage.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d(LOG_TAG, response.body().toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<PostRegisterResponse> call, Throwable t) {
                                Log.d(LOG_TAG, "ERROR");
                                Log.d(LOG_TAG, t.getMessage());
                            }
                        });
            }

            // Map 에 mateId 와 사용자 입력 텍스트 설정
            @NotNull
            private Map<String, RequestBody> putFiledValueToMap() {
                Map<String, RequestBody> map = new HashMap<>();

                String userInputText = userTextField.getText().toString();

                RequestBody mateIdFiled = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(teamMateId));
                RequestBody textDataField = RequestBody.create(MediaType.parse("text/plain"), userInputText);

                map.put("mateId", mateIdFiled);
                map.put("content", textDataField);

                return map;
            }
        });
    }

    // Permission Listener 생성
    @NotNull
    private PermissionListener setPermissionListener() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(requireActivity().getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(requireActivity().getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
            }
        };
        return permissionListener;
    }

    // 카메라 권한을 요청 및 처리
    private void requestCameraPermission() {

        // Permission Listener 생성
        PermissionListener permissionListener = setPermissionListener();

        // 권한 요청
        TedPermission.with(getActivity())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("카메라 권한을 거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 로딩 표시 초기화 및 설정
    private ProgressBar setUpLoading(View view){
        ProgressBar loading = view.findViewById(R.id.do_my_goal_loading);

        loading.bringToFront();
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green_400), PorterDuff.Mode.SRC_IN);
        loading.setVisibility(View.GONE);

        return loading;
    }

    // TextView 설정
    private void setTextViews(View view) {
        TextView goalTitle = view.findViewById(R.id.goal_title); // 목표 이름
        TextView todayDate = view.findViewById(R.id.today); // 오늘 날짜 표시


        goalTitle.setText(title);

        // 현재 날짜 표시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            todayDate.setText(LocalDate.now().toString());
        }
    }

    // ImageView 초기화
    private void initializeImageViews(View view) {
        ImageView firstIv = view.findViewById(R.id.first_image);
        ImageView secondIv = view.findViewById(R.id.second_image);
        ImageView thirdIv = view.findViewById(R.id.third_image);

        imageViewList.add(firstIv);
        imageViewList.add(secondIv);
        imageViewList.add(thirdIv);
    }

    /**
     * @param imageUriList 이미지 URI 목록
     * @return 이미지 파일들을 MultipartBody.Part 형태로 담은 ArrayList
     */

    // Image URI 목록을 받아와 압축 후, MultipartBody.Part 형태로 변환하여 이미지 목록 반환
    private ArrayList<MultipartBody.Part> getImages(List<Uri> imageUriList) throws IOException {
        ArrayList<MultipartBody.Part> imageList = new ArrayList<>();
        for (Uri imageUri : imageUriList) {
            String path = getImageFilePath(imageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), getCompressedImage(path));
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("images",
                    getOriginalImageFilename(path), requestFile);
            imageList.add(uploadFile);
        }
        return imageList;
    }
    // Image 파일 이름
    private String getOriginalImageFilename(String path) {
        String[] split = path.split("[/]");
        String filename = split[split.length - 1];
        return filename;
    }

    // Image 파일 경로 가져오기
    private String getImageFilePath(Uri uri){
        return FileTransferUtil.getPath(uri, getActivity());
    }

    // Image 압축
    private File getCompressedImage(String path) throws IOException {
        return new Compressor(getActivity().getApplicationContext()).compressToFile(new File(path));
    }

    @NotNull
    private View.OnClickListener getImageSelectListener() {
        View.OnClickListener imageSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다이얼로그 생성
                TedBottomPicker.with(getActivity())
                        .setPeekHeight(1600)
                        .showCameraTile(false) // 카메라 아이코 숨김
                        .showGalleryTile(false) // 갤러리 아이콘 숨김
                        .setPreviewMaxCount(1000)
                        .setSelectMaxCount(3)
                        .setSelectMaxCountErrorText("3장 이하로 선택해주세요.")
                        .showTitle(false)
                        .setCompleteButtonText("선택")
                        .setEmptySelectionText("No Select")
                        .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                            @Override
                            public void onImagesSelected(List<Uri> uriList) {
                                // 이미지 선택 시 콜백 메소드
                                imageUriList.clear();
                                imageViewList.forEach(iv -> iv.setImageResource(0));
                                IntStream.range(0, uriList.size()).forEach(
                                        i -> {
                                            Glide.with(DoMyGoalFragment.this).load(uriList.get(i)).into(imageViewList.get(i));
                                            imageUriList.add(uriList.get(i));
                                        }
                                );
                            }
                        });
            }
        };

        return imageSelectListener;
    }

}