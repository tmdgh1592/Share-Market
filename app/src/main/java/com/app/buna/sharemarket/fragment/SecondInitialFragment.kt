package com.app.buna.sharemarket.fragment

import com.app.buna.sharemarket.view.AddressApiWebView
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.app.buna.sharemarket.R
import com.app.buna.sharemarket.adapter.LocationRecyclerAdapter
import com.app.buna.sharemarket.databinding.FragmentSecondInitialBinding
import com.app.buna.sharemarket.utils.FancyChocoBar
import com.app.buna.sharemarket.utils.NetworkStatus
import com.app.buna.sharemarket.viewmodel.InitialViewModel
import org.koin.android.ext.android.get

class SecondInitialFragment : Fragment() {

    private var binding: FragmentSecondInitialBinding? = null
    private val viewModel: InitialViewModel by lazy {
        ViewModelProvider(this, InitialViewModel.Factory(get(), requireContext(), this)).get(InitialViewModel::class.java)
    }
    private lateinit var recyclerAdapter: LocationRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondInitialBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = lifecycleOwner
            viewModel = viewModel
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.searchNowLocationBtn?.setOnClickListener {
            viewModel.getLocationList()
        }

        binding?.searchLocationLayout?.setOnClickListener {
            /* 인터넷 연결 상태 확인 */
            val status = NetworkStatus.getConnectivityStatus(context)
            // 인터넷이 연결되어 있다면
            if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                val intent = Intent(context, AddressApiWebView::class.java)
                startActivity(intent) // 주소 검색 액티비티 실행
            } else {
                FancyChocoBar(requireActivity()).showOrangeSnackBar(getString(R.string.internet_check))
            }
        }

        recyclerAdapter = LocationRecyclerAdapter(viewModel?.locationItems!!)

        with(binding) {
            this?.locationRecyclerView?.adapter = recyclerAdapter
            this?.locationRecyclerView?.layoutManager = LinearLayoutManager(context)
            this?.locationRecyclerView?.addItemDecoration(DividerItemDecoration(context, VERTICAL))
        }

        viewModel?.locationItems?.observe(viewLifecycleOwner, Observer {
            // 검색 결과가 없는 경우
            if (it.size == 0) {
                binding?.noResultView?.visibility = View.VISIBLE // 결과가 없습니다 화면 보이게 하기
                binding?.locationRecyclerView?.visibility = View.INVISIBLE// 리사이클러뷰 사라지게 하기
            }else{ // 검색 결과가 하나라도 있는 경우
                binding?.noResultView?.visibility = View.INVISIBLE
                binding?.locationRecyclerView?.visibility = View.VISIBLE
            }
            recyclerAdapter.updateData(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}