package com.app.buna.sharingmarket.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.callbacks.ILogoutCallback
import com.app.buna.sharingmarket.databinding.FragmentMainMyBinding
import com.app.buna.sharingmarket.repository.PreferenceUtil
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.viewmodel.MainViewModel
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
        toolbar =
            binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
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
                    val surveyDialog = AlertDialog.Builder(requireContext())
                        .setView(surveyView)
                        .setPositiveButton(getString(R.string.code_submit)) { dialog, id ->

                        }.setNegativeButton(getString(R.string.cancel)) { dialog, id ->

                        }
                    // 회원탈퇴
                    vm?.unregister()
                }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
        }


        /* 로그아웃 */
        // 로그아웃할지 질문
        binding?.logoutBtn?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.ask_logout))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    // 로그아웃
                    vm?.logout(object : ILogoutCallback {
                        override fun success() { // 성공 콜백시 액티비티 종료
                            FancyToastUtil(requireContext()).showSuccess(getString(R.string.logout_success))
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