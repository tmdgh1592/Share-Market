package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.TAGS.Companion.TAG
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.model.items.SliderItem
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.template.model.*

class BoardViewModel(application: Application, val context: Context) : AndroidViewModel(application) {

    lateinit var item: ProductItem

    class Factory(val application: Application, val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BoardViewModel(application, context) as T
        }
    }

    fun getUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun clickHeart(callback: (Boolean) -> Unit) {
        if (item.favorites.containsKey(getUid())){
            FirebaseRepository.instance.clickHeart(item, true, getUid()!!, callback) // 좋아요 누른 상태인 경우
        }else {
            FirebaseRepository.instance.clickHeart(item, false, getUid()!!, callback) // 누르지 않은 상태인 경우
        }
    }

    fun removeBoard(callback: (Boolean) -> Unit) {
        FirebaseRepository.instance.removeProductData(item, callback)
    }

    // 이미지 슬라이더에 넣을 uri(String)로 이루어진 SliderItem List 반환
    fun getSlideItem(): ArrayList<SliderItem> {
        val slideItemList = ArrayList<SliderItem>()

        item.imgPath.values.forEach { path ->
            slideItemList.add(SliderItem(path))
        }

        return slideItemList
    }

    // 공유 :: 카카오 링크 보내기
    fun sendKakaoLink() {
        // 메시지 템플릿 만들기 (피드형)
        // 대표 이미지가 있을 경우
        var defaultFeed: FeedTemplate

        if (item.imgPath.size >= 1) {
            defaultFeed = FeedTemplate(
                content = Content(
                    title = item.title,
                    description = item.content,
                    imageUrl = item.imgPath.values.first(),
                    link = Link(
                        mobileWebUrl = "https://play.google.com/store/apps/details?id=com.app.buna.sharingmarket"
                    )
                ), social = Social(
                    likeCount = item.likeCount
                ),
                buttons = listOf(
                    Button(
                        "쉐어마켓에서 보기",
                        Link(
                            androidExecParams = mapOf(
                                "key1" to "value1",
                                "key2" to "value2"
                            ) // 내 앱에서 파라미터보낼건 필요없음 (앱 다운로드만 유도할것이다)
                        )
                    )
                )
            )
        } else { // 업로드할 사진이 없을 경우
            defaultFeed = FeedTemplate(
                content = Content(
                    title = item.title,
                    description = item.content,
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/sharing-market.appspot.com/o/app_images%2Fapp_icon.png?alt=media&token=af6cde24-c7af-4ee1-bf98-958de3ba3fbc",
                    link = Link(
                        mobileWebUrl = "https://play.google.com/store/apps/details?id=com.app.buna.sharingmarket"
                    )
                ), social = Social(
                    likeCount = item.likeCount
                ),
                buttons = listOf(
                    Button(
                        "쉐어마켓에서 보기",
                        Link(
                            androidExecParams = mapOf(
                                "key1" to "value1",
                                "key2" to "value2"
                            ) // 내 앱에서 파라미터보낼건 필요없음 (앱 다운로드만 유도할것이다)
                        )
                    )
                )
            )
        }

        // 피드 메시지 보내기
        LinkClient.instance.defaultTemplate(context, defaultFeed) { linkResult, error ->
            if (error != null) {
                Log.e(TAG, "카카오링크 보내기 실패", error)
                // 카카오톡이 설치되어 있지 않은 경우, 플레이스토어에서 카카오톡 실행
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kakao.talk")).addFlags(FLAG_ACTIVITY_NEW_TASK))
                } catch (e: Exception) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.kakao.talk")).addFlags(FLAG_ACTIVITY_NEW_TASK))
                }
            }
            else if (linkResult != null) {
                Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                context.startActivity(linkResult.intent)

                // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
            }
        }
    }

    fun getProfile(uid: String, callback: (String?) -> Unit) {
        FirebaseRepository.instance.getProfile(uid, callback)
    }

    // 나눔 완료로 상태표시
    fun shareDone(isDone: Boolean, callback: () -> Unit) {
        FirebaseRepository.instance.shareDone(isDone, item.documentId, callback)
    }
}