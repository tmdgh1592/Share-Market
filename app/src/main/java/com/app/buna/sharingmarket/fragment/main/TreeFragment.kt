package com.app.buna.sharingmarket.fragment.main

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.FragmentTreeBinding
import com.app.buna.sharingmarket.viewmodel.TreeViewModel
import com.bumptech.glide.Glide
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import org.koin.android.ext.android.get


class TreeFragment : Fragment() {

    var binding: FragmentTreeBinding? = null
    val vm: TreeViewModel by lazy {
        ViewModelProvider(
            this,
            TreeViewModel.Factory(get())
        ).get(TreeViewModel::class.java)
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
            vm.treeCoin.postValue(vm.myTreeItem?.hasCoinCount)

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
                        startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.anim_fade_out
                            )
                        )
                        binding?.celebrateAnimationView?.playAnimation() // 축하 애니메이션 보여주기
                        visibility = View.GONE
                        isEnabled = false
                        // 최초 클릭시 트리 코인 2개 지급해줌
                        vm.getMyTree {
                            vm.addTreeCoin(2) {
                                val nickname = vm.getNickname() // 사용자의 닉네임 가져오기

                                // Dialog를 띄워서 캠페인 취지를 설명해준다.
                                showDialog(
                                    getString(R.string.campain_dialog_title),
                                    getString(R.string.campain_dialog_content, nickname, nickname)
                                )
                            }
                        }
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

        vm?.getTreeProfile { profileUrlList ->
            try {
                if (profileUrlList[0] != null) {
                    binding?.plantTop1ImageView?.let {
                        Glide.with(requireContext()).load(Uri.parse(profileUrlList[0]))
                            .into(it)
                        it.setOnClickListener {
                            showProfile(profileUrlList[0])
                        }
                    }
                }
                if (profileUrlList[1] != null) {
                    binding?.plantTop2ImageView?.let {
                        Glide.with(requireContext()).load(Uri.parse(profileUrlList[1]))
                            .into(it)
                        it.setOnClickListener {
                            showProfile(profileUrlList[1])
                        }
                    }
                }
                if (profileUrlList[2] != null) {
                    binding?.plantTop3ImageView?.let {
                        Glide.with(requireContext()).load(Uri.parse(profileUrlList[2]))
                            .into(it)
                        it.setOnClickListener {
                            showProfile(profileUrlList[2])
                        }
                    }
                }
                if (profileUrlList[3] != null) {
                    binding?.plantTop4ImageView?.let {
                        Glide.with(requireContext()).load(Uri.parse(profileUrlList[3]))
                            .into(it)
                        it.setOnClickListener {
                            showProfile(profileUrlList[3])
                        }
                    }
                }
                if (profileUrlList[4] != null) {
                    binding?.plantTop5ImageView?.let {
                        Glide.with(requireContext()).load(Uri.parse(profileUrlList[4]))
                            .into(it)
                        it.setOnClickListener {
                            showProfile(profileUrlList[4])
                        }
                    }
                }
            } catch (e: IllegalStateException) {
                print(e.message)
            }
        }

        vm?.treeCoin.observe(viewLifecycleOwner, Observer { coin ->
            binding?.treeCoinCount?.text = coin.toString()
        })

        binding?.plantTreeBtn?.setOnClickListener {
            vm?.giveTreeCoin { treeCoinCount ->
                if (treeCoinCount == 0) { // 기부할 수 있는 트리 코인이 없는 경우
                    makeSnackBar(it, getString(R.string.giving_tree_coin_not_enough)).show()
                } else { // 기부에 성공한 경우
                    val myName = FirebaseAuth.getInstance().currentUser?.displayName ?: "null"
                    binding?.celebrateAnimationView?.playAnimation()
                    makeSnackBar(it, getString(R.string.success_give_tree_coin, myName)).show()
                }
            }
        }

        // 나눔&나무 캠페인에 대한 설명
        binding?.questionMark?.let { view ->
            try {
                view.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.anim_fade_in_fade_out
                    )
                )
            } catch (e: java.lang.IllegalStateException) {
                print(e.message)
            }
            view.setOnClickListener {
                view.clearAnimation()
                showDialog(
                    getString(R.string.campain_dialog_question_title),
                    getString(R.string.campain_dialog_question_content, vm.getNickname())
                )
            }
        }
    }

    fun makeSnackBar(view: View, text: String): Snackbar {
        val snackBar = Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_LONG
        )
        snackBar.view?.run {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.app_pressed_green
                )
            )
            (findViewById<TextView>(com.google.android.material.R.id.snackbar_text)).run {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                typeface = Typeface.DEFAULT_BOLD
            }
        }

        return snackBar
    }


    // Gif dialog 보여주기
    fun showDialog(title: String, content: String) {
        FancyGifDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(content)
            .setNegativeBtnBackground("#4D535353")
            .setNegativeBtnText(getString(R.string.cancel))
            .setPositiveBtnBackground("#FF9ACE97")
            .setPositiveBtnText(getString(R.string.ok))
            .setGifResource(R.drawable.campain_dialog_gif) //Pass your Gif here
            .isCancellable(false).build()
    }

    fun showProfile(profileUrl: String?) {
        if (profileUrl != null) {
            val profileUri = Uri.parse(profileUrl)
            val profilePopup = ImagePopup(requireContext()).apply {
                windowWidth = 800
                windowHeight = 800
                isHideCloseIcon = true
                isImageOnClickClose = true
                initiatePopupWithPicasso(profileUri)
            }
            profilePopup.viewPopup()
        }
    }

}