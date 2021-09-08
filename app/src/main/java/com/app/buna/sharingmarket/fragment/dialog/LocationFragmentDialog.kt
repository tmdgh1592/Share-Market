package com.app.buna.sharingmarket.fragment.dialog

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.activity.AddressApiWebView
import com.app.buna.sharingmarket.adapter.LocationRecyclerAdapter
import com.app.buna.sharingmarket.callbacks.ILocationDialogCallback
import com.app.buna.sharingmarket.databinding.FragmentSecondInitialBinding
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.app.buna.sharingmarket.viewmodel.InitialViewModel
import org.koin.android.ext.android.get

class LocationFragmentDialog : DialogFragment() {
    private var binding: FragmentSecondInitialBinding? = null
    private val vm: InitialViewModel by lazy {
        ViewModelProvider(this, InitialViewModel.FactoryWithFragment(get(), requireContext(), this))
            .get(InitialViewModel::class.java)
    }
    private lateinit var recyclerAdapter: LocationRecyclerAdapter
    private lateinit var locationChangedCallback: ILocationDialogCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondInitialBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* * * * * * * /
        /  View 초기화 /
        * * * * * * * */

        // 현재 위치로 검색 버튼
        binding?.searchNowLocationBtn?.setOnClickListener {
            vm.getLocationList(requireContext())
        }

        // 다음 주소 API 액티비티 실행
        binding?.searchLocationLayout?.setOnClickListener {
            /* 인터넷 연결 상태 확인 */
            // 인터넷이 연결되어 있다면
            if (NetworkStatus.isConnectedInternet(requireContext())) {
                val intent = Intent(context, AddressApiWebView::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                activity?.startActivityForResult(intent, REQUEST_CODE.API_COMPLETED_FINISH) // 주소 검색 액티비티 실행
            } else {
                FancyToastUtil(requireContext()).showFail(getString(R.string.internet_check))
            }
        }

        recyclerAdapter = LocationRecyclerAdapter(vm?.locationItems!!, vm, requireContext(), true)

        with(binding) {
            this?.locationRecyclerView?.adapter = recyclerAdapter
            this?.locationRecyclerView?.layoutManager = LinearLayoutManager(context)
            this?.locationRecyclerView?.addItemDecoration(DividerItemDecoration(context, VERTICAL))
        }

        vm?.locationItems?.observe(viewLifecycleOwner, Observer {
            // 검색 결과가 없는 경우
            if (it.isEmpty()) {
                binding?.noResultView?.visibility = View.VISIBLE // 결과가 없습니다 화면 보이게 하기
                binding?.locationRecyclerView?.visibility = View.INVISIBLE// 리사이클러뷰 사라지게 하기
            }else{ // 검색 결과가 하나라도 있는 경우
                binding?.noResultView?.visibility = View.INVISIBLE
                binding?.locationRecyclerView?.visibility = View.VISIBLE
            }
            // 로케이션 아이템 리스트에 변화가 있을 시 업데이트
            recyclerAdapter.updateData()
        })
    }

    // 다이얼로그가 사라질 때 프로필 주소 변경을 위한 콜백
    fun setCallback(callback: ILocationDialogCallback){
        locationChangedCallback = callback
    }

    // 사라질 때 콜백 호출
    override fun onDismiss(dialog: DialogInterface) {
        locationChangedCallback.changeLocation(vm.getLocation(requireContext()))
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}