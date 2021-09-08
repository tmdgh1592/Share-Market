package com.app.buna.sharingmarket.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.ActivitySearchBinding
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.viewmodel.SearchViewModel
import org.koin.android.ext.android.get


class SearchActivity : AppCompatActivity() {

    var binding: ActivitySearchBinding? = null
    val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, SearchViewModel.Factory(get())).get(SearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding?.lifecycleOwner = this
        binding?.viewModel = viewModel

        binding?.autoCompleteTextView?.setAdapter(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                viewModel.getList(this) // 자동완성에 사용할 단어
            )
        )

        // 엔터키 눌렀을 때
        binding?.autoCompleteTextView?.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    finishWithKeyword()
                    return true
                }
                return true
            }
        })

        binding?.searchKeywordBtn?.setOnClickListener {
            finishWithKeyword()
        }
    }


    // 엔터키 눌렀을 때 2글자 이상이면 MainActivity에 키워드 전달
    fun finishWithKeyword() {
        if (viewModel.keyword.length!! < 2) {
            FancyToastUtil(this).showWarning(getString(R.string.not_enough_keyword_length))
            return
        }
        val keywordIntent = Intent().putExtra("keyword", viewModel.keyword)
        setResult(RESULT_OK, keywordIntent)
        finish()
    }
}
