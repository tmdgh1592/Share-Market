package com.app.buna.sharingmarket.fragment.main

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.InitialActivity
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.activity.MyBoardsActivity
import com.app.buna.sharingmarket.activity.MyHeartsActivity
import com.app.buna.sharingmarket.callbacks.ILocationDialogCallback
import com.app.buna.sharingmarket.callbacks.ILogoutCallback
import com.app.buna.sharingmarket.databinding.FragmentMainMyBinding
import com.app.buna.sharingmarket.databinding.UnregisterSurveyLayoutBinding
import com.app.buna.sharingmarket.fragment.dialog.LocationFragmentDialog
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.get

class MainMyFragment : Fragment() {

    private var binding: FragmentMainMyBinding? = null
    private val vm: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(get(), requireContext()))
            .get(MainViewModel::class.java)
    }
    private lateinit var toolbar: Toolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMyBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        // * 툴바 관련
        toolbar =
            binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
        (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기

        /* 프로필 수정 */
        if (vm.getProfileUriInPref() != "null") { // Preference에 Profile Uri가 있는 경우
            Glide.with(this).load(Uri.parse(vm.getProfileUriInPref()))
                .into(binding?.profileImageView!!)
        } else { // Preference에 Profile Uri가 없는 경우
            if (NetworkStatus.isConnectedInternet(requireContext())) { // Preference에 기록된게 없기때문에 Storage에서 가져옴
                vm.getProfile(Firebase.auth.uid.toString()) { profileUrl ->
                    if (profileUrl != null) {
                        Glide.with(this).load(Uri.parse(profileUrl)).into(binding?.profileImageView!!)
                    }
                }
            }
        }

        val startForProfileImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val imgUri = data?.data!!
                    if (imgUri != null) {
                        if (NetworkStatus.isConnectedInternet(requireContext())) { // 인터넷이 연결되어 있으면 프로필 수정
                            Glide.with(requireContext()).load(imgUri).circleCrop()
                                .into(binding?.profileImageView!!)
                            vm.saveProfile(imgUri) {
                                vm.saveProfileUriInPref(imgUri) // Preference에도 uri를 저장
                                //FancyToastUtil(MainActivity.instance.getContext()).showSuccess(getString(R.string.profile_change)) // 성공 Toast
                            }
                        } /*else {
                            FancyToastUtil(MainActivity.instance.getContext()).showFail(getString(R.string.internet_check)) // 실패 Toast
                        }*/
                    }
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    //Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }

        binding?.profileImageView?.setOnClickListener {
            ImagePicker.with(this)
                .crop()        //Crop image(Optional), Check Customization for more option
                .compress(1024)   //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                ) //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent {
                    startForProfileImageResult.launch(it)
                }
        }

        /* 내 위치 수정 */
        binding?.editLocationBtn?.setOnClickListener {
            val locationDialog = LocationFragmentDialog()
            locationDialog.setCallback(object : ILocationDialogCallback {
                override fun changeLocation(jibun: String) {
                    vm.saveLocationInFirebase(jibun = jibun!!)
                    vm.saveLocationInPref(jibun = jibun!!)
                    binding?.location?.text = jibun
                }
            })
            locationDialog.show(parentFragmentManager, "Location Fragment Show")
        }


        /* 내가 쓴 글 */
        binding?.myBoardBtn?.setOnClickListener {
            val intent =
                Intent(requireContext(), MyBoardsActivity::class.java) // 내가 쓴 글을 보여주는 액티비티로 이동
            startActivity(intent)
        }


        /* 관심 게시글 */
        binding?.myHeartBtn?.setOnClickListener {
            val intent =
                Intent(requireContext(), MyHeartsActivity::class.java) // 내가 쓴 글을 보여주는 액티비티로 이동
            startActivity(intent)
        }


        /* 앱 공유 버튼 */
        binding?.shareAppBtn?.setOnClickListener {
            val msg = Intent(Intent.ACTION_SEND).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name))
                putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=${requireContext().packageName}"
                )
                type = "text/plain"
            }
            startActivity(Intent.createChooser(msg, getString(R.string.share_app)))
        }


        /* 회원 탈퇴 */
        binding?.unregisterBtn?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.ask_unregister))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    // 탈퇴 이유 설문조사
                    val surveyBinding = DataBindingUtil.bind<UnregisterSurveyLayoutBinding>(
                        layoutInflater.inflate(
                            R.layout.unregister_survey_layout,
                            null
                        )
                    )  // 탈퇴 설문조사 view
                    // 설문조사 선택 옵션 선택
                    surveyBinding?.surveyRadioGroup?.setOnCheckedChangeListener { _, id ->
                        when (id) {
                            R.id.survey_type_1 -> {
                                surveyBinding.surveyType5Layout.visibility =
                                    View.GONE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 1
                                vm.surveyText = getString(R.string.survey_type_1)
                            }
                            R.id.survey_type_2 -> {
                                surveyBinding.surveyType5Layout.visibility =
                                    View.GONE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 2
                                vm.surveyText = getString(R.string.survey_type_2)
                            }
                            R.id.survey_type_3 -> {
                                surveyBinding.surveyType5Layout.visibility =
                                    View.GONE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 3
                                vm.surveyText = getString(R.string.survey_type_3)
                            }
                            R.id.survey_type_4 -> {
                                surveyBinding.surveyType5Layout.visibility =
                                    View.GONE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 4
                                vm.surveyText = getString(R.string.survey_type_4)
                            }
                            R.id.survey_type_5 -> { // 기타 사유
                                surveyBinding.surveyType5Layout.visibility =
                                    View.VISIBLE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 5
                                vm.surveyText = ""
                            }
                        }
                    }
                    val surveyDialog =
                        AlertDialog.Builder(requireContext()) // 탈퇴 설문조사하는 다이얼로그 show()
                            .setView(surveyBinding?.root)
                            .setCancelable(false)
                            .setPositiveButton(
                                android.R.string.ok,
                                null
                            ) //onClick오버라이딩할거니까 null로해줘요.
                            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()

                    surveyDialog.setOnShowListener {
                        val posBtn = surveyDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        posBtn.setOnClickListener {
                            if (vm.surveyText != null && vm.surveyOptionId != null) { // 라디오버튼 선택했는지 null체크

                                if (vm.surveyOptionId!! == 5) {
                                    // 기타 사유인 경우엔 survey text를 EditTextView에 작성한 내용으로 수정
                                    vm.surveyText =
                                        surveyBinding?.surveyType5EditText?.text.toString()
                                }

                                if (vm.surveyOptionId!! in 1..4 || (vm.surveyOptionId!! == 5 && surveyBinding?.surveyType5EditText?.text!!.length > 10)) { // 기타 사유인 경우 EditText값도 확인해줘야함
                                    // 회원탈퇴
                                    vm?.unregister(vm.surveyText!!, vm.surveyOptionId!!) {
                                        surveyDialog.dismiss()
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                InitialActivity::class.java
                                            )
                                        )
                                        requireActivity().finish()
                                    }
                                } else if (vm.surveyOptionId!! == 5 && surveyBinding?.surveyType5EditText?.text!!.length < 10) {
                                    FancyToastUtil(requireContext()).showWarning(getString(R.string.survey_submit_condition_1))
                                } else {
                                    FancyToastUtil(requireContext()).showWarning(getString(R.string.survey_submit_condition_2))
                                }
                            } else {
                                FancyToastUtil(requireContext()).showWarning(getString(R.string.survey_submit_condition_2))
                            }
                        }
                        val negBtn = surveyDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        negBtn.setOnClickListener {
                            surveyDialog.dismiss()
                            vm.surveyOptionId = null
                            vm.surveyText = null
                        }
                    }
                    surveyDialog.show() // 설문조사 dialog 보여주기
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> // 회원탈퇴 묻는 dialog의 취소버튼
                    dialog.dismiss()
                }.create()
                .show()
        }


        /* 로그아웃 */
        // 로그아웃할지 질문하는 Dialog show()
        binding?.logoutBtn?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.ask_logout))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    // 로그아웃
                    vm?.logout(object : ILogoutCallback {
                        override fun success() { // 성공 콜백시 액티비티 종료
                            FancyToastUtil(requireContext()).showGreen(getString(R.string.logout_success))
                            startActivity(Intent(requireContext(), InitialActivity::class.java))
                            requireActivity().finish()
                        }
                        override fun fail() { // 실패할 경우 실패 문구 출력
                            FancyToastUtil(requireContext()).showRed(getString(R.string.logout_fail))
                        }
                    })
                }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home_tool_bar, menu)
    }


    companion object {
        val instance = MainMyFragment()
    }


}