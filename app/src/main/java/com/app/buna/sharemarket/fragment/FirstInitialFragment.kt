package com.app.buna.sharemarket.fragment

import android.os.Bundle
import android.renderscript.ScriptGroup
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.app.buna.sharemarket.R
import com.app.buna.sharemarket.databinding.FragmentFirstInitialBinding

class FirstInitialFragment : Fragment() {

    var binding: FragmentFirstInitialBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMainImgAnim()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFirstInitialBinding.bind(view)

        startMainImgAnim()
    }

    private fun startMainImgAnim() {
        val anim = AnimationUtils.loadAnimation(context, R.anim.anim_right_rotate)
        binding?.mainImage?.startAnimation(anim)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}