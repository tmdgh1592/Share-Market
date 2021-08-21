package com.app.buna.sharingmarket.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.databinding.FragmentThirdInitialBinding
import com.app.buna.sharingmarket.activity.InitialActivity
import com.app.buna.sharingmarket.repository.PreferenceUtil
import com.app.buna.sharingmarket.viewmodel.ThirdInitialViewModel
import org.koin.android.ext.android.get

class InitialThirdFragment : Fragment() {

    private var binding: FragmentThirdInitialBinding? = null
    private val vm by lazy {
        ViewModelProvider(this, ThirdInitialViewModel.Factory(get(), requireContext(), (requireActivity() as InitialActivity))).get(
            ThirdInitialViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdInitialBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PreferenceUtil.putInt(requireContext(), "fragment_page", 2) // 현재까지 진행한 fragment_page 저장
        binding?.facebookSignInBtn?.setOnClickListener { vm.signInFacebook() } // facebook 로그인 버튼 클릭시 가입 or 로그인 진행
    }
}
