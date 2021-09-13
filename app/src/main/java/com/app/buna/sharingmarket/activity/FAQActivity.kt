package com.app.buna.sharingmarket.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.adapter.FAQRecyclerAdapter
import com.app.buna.sharingmarket.databinding.ActivityFaqBinding
import com.app.buna.sharingmarket.model.items.faq.FaqItem

class FAQActivity : AppCompatActivity() {
    private var binding: ActivityFaqBinding? = null
    private lateinit var faqList: ArrayList<FaqItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ShareMarket_FAQ)
        super.onCreate(savedInstanceState)
        setFaqList()
        initView()
    }

    // FAQ에 보여줄 리스트 가져오기
    private fun setFaqList() {
        val faqList = ArrayList<FaqItem>()

        val faqTitles = resources.getStringArray(R.array.faq_titles) // 접혀져 있을 때의 내용들
        val faqDetails = resources.getStringArray(R.array.faq_details) // 펼친 상태에서 보여줄 내용들

        for (i in faqTitles.indices) {
            val faqItem = FaqItem().apply {
                title = faqTitles[i]
                detail = faqDetails[i]
            }
            faqList.add(faqItem)
        }

        this.faqList = faqList
    }

    // 뷰 초기화
    fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_faq)
        binding?.faqRecyclerView?.apply {
            layoutManager = object : LinearLayoutManager(this@FAQActivity) {
                // ScrollView가 있기 때문에 Recyclerview 스크롤 막기
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            setHasFixedSize(true)
            adapter = FAQRecyclerAdapter(faqList)
        }

        /* 뒤로가기 버튼*/
        binding?.backBtn?.setOnClickListener {
            finish()
        }
        /* 1:1 문의하기 버튼 */
        binding?.askCenterBtn?.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                setData(Uri.parse("mailto:"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.chat_subject))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.chat_email)))
                type = "message/rfc882"
            }
            try {
                startActivity(
                    Intent.createChooser(
                        emailIntent,
                        getString(R.string.email_chooser_title)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.no_email_client), Toast.LENGTH_LONG).show()
            }
        }
    }
}