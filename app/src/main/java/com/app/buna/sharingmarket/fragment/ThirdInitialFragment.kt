package com.app.buna.sharingmarket.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.CONST
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.FragmentThirdInitialBinding
import com.app.buna.sharingmarket.view.InitialActivity
import com.app.buna.sharingmarket.viewmodel.ThirdInitialViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.get
import org.koin.android.ext.android.get

class ThirdInitialFragment : Fragment() {

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

        binding?.facebookSignInBtn?.setOnClickListener { vm.signInFacebook() } // facebook 로그인 버튼 클릭시 가입 or 로그인 진행
    }






}
