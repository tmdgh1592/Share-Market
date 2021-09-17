package com.app.buna.sharingmarket.fragment.main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.FragmentTreeBinding
import com.app.buna.sharingmarket.databinding.LayoutPlantTreeDescBinding
import com.app.buna.sharingmarket.viewmodel.TreeViewModel
import org.koin.android.ext.android.get

class TreeFragment : Fragment() {

    var binding: FragmentTreeBinding? = null
    val vm: TreeViewModel by lazy {
        ViewModelProvider(
            this,
            TreeViewModel.Factory(get())
        ).get(TreeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTreeBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        // 내 트리 코인 DTO 가져오기
        vm.getMyTree { treeItem ->
            vm.myTreeItem = treeItem!!
            vm.treeCoin.postValue(vm.myTreeItem.totalSeed)

            // 이벤트 코인을 주는 문구를 클릭한 적이 없다면
            if (!vm.isClickedTree()) {
                binding?.campainClickMe?.run {
                    visibility = View.VISIBLE
                    startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.anim_click_tree
                        )
                    )
                    setOnClickListener {
                        // 클릭해주세요 버튼 사라지게 하고 비활성화 하기
                        startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_fade_out))
                        visibility = View.GONE
                        isEnabled = false
                        // 최초 클릭시 트리 코인 2개 지급해줌
                        vm?.getMyTree { vm?.addTreeCoin(2) {

                            // 나무심기 캠페인 설명 Dialog 보여주기
                            val plantDescView = DataBindingUtil.inflate<LayoutPlantTreeDescBinding>(LayoutInflater.from(requireContext()), R.layout.layout_plant_tree_desc, null ,false)
                            val plantDescDialog = AlertDialog.Builder(requireContext()).setView(plantDescView.root).create()
                            plantDescDialog.show()
                        } }
                    }
                }
            }
        }

        AnimationUtils.loadAnimation(requireContext(), R.anim.anim_top_down_infinite).let {
            with(binding) {
                this?.plantTop1ImageView?.startAnimation(it)
                this?.plantTop2ImageView?.startAnimation(it)
                this?.plantTop3ImageView?.startAnimation(it)
                this?.plantTop4ImageView?.startAnimation(it)
                this?.plantTop5ImageView?.startAnimation(it)
            }
        }

        vm?.treeCoin.observe(viewLifecycleOwner, Observer { coin ->
            binding?.treeCoinCount?.text = coin.toString()
        })
    }


    companion object {
        val instance = TreeFragment()
    }
}