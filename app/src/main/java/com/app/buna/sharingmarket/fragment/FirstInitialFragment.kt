package com.app.buna.sharingmarket.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.FragmentFirstInitialBinding
import com.app.buna.sharingmarket.activity.InitialActivity

class FirstInitialFragment : Fragment() {

    var binding: FragmentFirstInitialBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFirstInitialBinding.bind(view)

        binding!!.locateSettingBtn.setOnClickListener {
            (activity as InitialActivity).replaceFragment(SecondInitialFragment()) // 위치 설정을 위한 다음 프래그먼트 실행
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // binding 객체 해제
    }
}