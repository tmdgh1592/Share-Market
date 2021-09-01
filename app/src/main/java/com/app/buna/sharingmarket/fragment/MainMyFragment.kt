package com.app.buna.sharingmarket.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.InitialActivity
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.callbacks.ILogoutCallback
import com.app.buna.sharingmarket.databinding.FragmentMainMyBinding
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.unregister_survey_layout.view.*
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
        binding = FragmentMainMyBinding.inflate(inflater, container, false).apply {
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
        toolbar = binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
        (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기


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
                    val surveyView = layoutInflater.inflate(R.layout.unregister_survey_layout, null) // 탈퇴 설문조사 view
                    // 설문조사 선택 옵션 선택
                    surveyView.survey_radio_group.setOnCheckedChangeListener { _, id ->
                        when (id) {
                            R.id.survey_type_1 -> {
                                surveyView.survey_type_5_layout.visibility = View.GONE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 1
                                vm.surveyText = getString(R.string.survey_type_1)
                            }R.id.survey_type_2 -> {
                                surveyView.survey_type_5_layout.visibility = View.GONE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 2
                                vm.surveyText = getString(R.string.survey_type_2)
                            }R.id.survey_type_3 -> {
                                surveyView.survey_type_5_layout.visibility = View.GONE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 3
                                vm.surveyText = getString(R.string.survey_type_3)
                            }R.id.survey_type_4 -> {
                                surveyView.survey_type_5_layout.visibility = View.GONE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 4
                                vm.surveyText = getString(R.string.survey_type_4)
                            }R.id.survey_type_5 -> { // 기타 사유
                                surveyView.survey_type_5_layout.visibility = View.VISIBLE // 설문조사 에딧텍스트 사라지게함
                                vm.surveyOptionId = 5
                                vm.surveyText = ""
                            }
                        }
                    }
                    val surveyDialog = AlertDialog.Builder(requireContext()) // 탈퇴 설문조사하는 다이얼로그 show()
                        .setView(surveyView)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, null) //onClick오버라이딩할거니까 null로해줘요.
                        .setNegativeButton(android.R.string.cancel){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    surveyDialog.setOnShowListener {
                        val posBtn = surveyDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        posBtn.setOnClickListener {
                            if (vm.surveyText != null && vm.surveyOptionId != null){ // 라디오버튼 선택했는지 null체크

                                if (vm.surveyOptionId!! == 5) {
                                    // 기타 사유인 경우엔 survey text를 EditTextView에 작성한 내용으로 수정
                                    vm.surveyText = surveyView.survey_type_5_edit_text.text.toString()
                                }

                                if(vm.surveyOptionId!! in 1..4 || (vm.surveyOptionId!! == 5 && surveyView.survey_type_5_edit_text.text.length > 10)) { // 기타 사유인 경우 EditText값도 확인해줘야함
                                    // 회원탈퇴
                                    vm?.unregister(vm.surveyText!!, vm.surveyOptionId!!) {
                                        surveyDialog.dismiss()
                                        startActivity(Intent(requireContext(), InitialActivity::class.java))
                                        requireActivity().finish()
                                    }
                                } else if (vm.surveyOptionId!! == 5 && surveyView.survey_type_5_edit_text.text.length < 10) {
                                    FancyChocoBar(requireActivity()).showOrangeSnackBar(getString(R.string.survey_submit_condition_1))
                                } else {
                                    FancyChocoBar(requireActivity()).showOrangeSnackBar(getString(R.string.survey_submit_condition_2))
                                }
                            } else {
                                FancyChocoBar(requireActivity()).showOrangeSnackBar(getString(R.string.survey_submit_condition_2))
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
                }.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> // 회원탈퇴 묻는 dialog의 취소버튼
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
                            FancyToastUtil(requireContext()).showSuccess(getString(R.string.logout_success))
                            startActivity(Intent(requireContext(), InitialActivity::class.java))
                            requireActivity().finish()
                        }
                        override fun fail() { // 실패할 경우 실패 문구 출력
                            FancyToastUtil(requireContext()).showFail(getString(R.string.logout_fail))
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