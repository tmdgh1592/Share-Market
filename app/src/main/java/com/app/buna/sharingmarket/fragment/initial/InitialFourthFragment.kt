package com.app.buna.sharingmarket.fragment.initial

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.Sosock
import com.app.buna.sharingmarket.databinding.FragmentFourthInitialBinding
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
import com.app.buna.sharingmarket.viewmodel.InitialViewModel
import kotlinx.android.synthetic.main.fragment_fourth_initial.*

class InitialFourthFragment : Fragment() {

    private var binding: FragmentFourthInitialBinding? = null
    val vm: InitialViewModel by lazy {
        ViewModelProvider(this, InitialViewModel.FactoryWithFragment(requireActivity().application, requireContext(), this)).get(
            InitialViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFourthInitialBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm
            context = requireContext()
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceUtil.putInt(requireContext(), "fragment_page", 3) // 현재까지 진행한 fragment_page 저장
        initView()
    }

    fun initView(){

        // lottie listener 등록
        binding?.welcomeAnimationView?.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) { return }
            override fun onAnimationCancel(p0: Animator?) { return }
            override fun onAnimationRepeat(p0: Animator?) { return }
            override fun onAnimationEnd(p0: Animator?) {
                // 애니메이션이 끝나면 view 제거
                welcome_animation_view.animate().alpha(0.0f)
                    .setDuration(300)
                    .setListener(object: AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            welcome_animation_view.visibility = View.INVISIBLE
                            sosock_scroll_view.visibility = View.VISIBLE
                            register_btn.visibility = View.VISIBLE
                        }
                    })
            }
        })

        // welcome_message 텍스트 뷰에 닉네임 입력
        SpannableStringBuilder().append("쉐어마켓에 오신 것을 환영합니다!\n ${vm.getUserName(requireContext())}님의 소속을 선택해주세요.").let {
            it.setSpan(StyleSpan(Typeface.BOLD), 20, 20+vm.getUserName(requireContext()).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // "닉네임" <- Bold
            it.setSpan(StyleSpan(Typeface.BOLD), 23+vm.getUserName(requireContext()).length, 25+vm.getUserName(requireContext()).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // "소속" <- Bold
            binding?.welcomeMessage?.text = it
        }


        // 소속 선택시(즉, 값이 변경될 시) 가입 완료 버튼 활성화
        vm.mySoSock.observe(viewLifecycleOwner, Observer {
            binding?.registerBtn?.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_green))

            // 선택한 소속에 대한 설명
            when(vm.mySoSock.value) {
                // 제대로 된 소속을 선택한 경우
                Sosock.PERSONAL -> binding?.belongInformation?.text = context?.getString(R.string.personal_information)
                Sosock.AGENCY -> binding?.belongInformation?.text = context?.getString(R.string.agency_information)
                Sosock.COMPANY -> binding?.belongInformation?.text = context?.getString(R.string.company_information)

                //예외 처리
                else -> {
                    binding?.registerBtn?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_50))
                    binding?.belongInformation?.text = ""
                }
            }

        })
    }
}