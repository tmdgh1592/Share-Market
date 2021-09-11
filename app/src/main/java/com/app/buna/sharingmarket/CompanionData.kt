package com.app.buna.sharingmarket

class REQUEST_CODE {
    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1000 // 위치 권한
        const val API_COMPLETED_FINISH = 3000 // 다음 주소 찾기 request code
        const val RC_SIGN_IN = 900 // 구글 로그인 request code
        const val REGISTER_OK_CODE = 4000 // 판매 글 등록 성공
        const val REGISTER_FAIL_CODE = 4001 // 판매 글 등록 실패
        const val IMAGE_PICKER_REQUEST_CODE = 5000 // 이미지 피커 갤러리 request 코드
        const val DELETE_BOARD_CODE_FROM_MAIN = 6000 // 게시글 삭제
        const val DELETE_BOARD_CODE_FROM_MY_BOARD = 6001 // 내 글 삭제
        const val DELETE_BOARD_CODE_FROM_MY_HEART = 6002 // 좋아요 누른글 삭제
        const val UPDATE_BOARD_CODE = 6002 // 게시글 업데이트
        const val SEARCH_BOARD_CODE = 6003 // 키워드로 상품 검색
        const val REFRESH_MAIN_HOME_FRAGMENT_CODE = 6004 // 화면 갱신
        const val REFRESH_MAIN_CHAT_FRAGMENT_CODE = 6005 // 채팅 화면 갱신
        const val SELECT_USER_CODE = 6006 // 나눔 완료시 유저 리스트를 보여주고 돌아오기 위함
    }
}

class Const {
    companion object {
        const val SPLASH_DURATION: Long = 2000 // 스플래시 화면 전환 1500ms
        const val MAX_PHOTO_SIZE: Int = 5 // 사진 선택 최대개수
    }
}

class WriteType {
    companion object {
        const val SHARE = 0
        const val EXCHANGE = 1
    }
}

class Channel {
    companion object {
        const val CHANNEL_ID = "1000"
        const val CHANNEL_DESC = "푸시 알림을 전달받기 위한 채널입니다."
        const val NOTI_ID = 1000
    }
}

class CommentType {
    companion object {
        const val COMMENT = 0 // 단순 문자인 경우
        const val PICTURE = 1 // 사진인 경우
        const val DATE_DIVIDER = 2 // 날짜 구분선인 경우
    }
}

class Tags {
    companion object {
        const val TAG: String = "TAG"
    }
}

class Sosock {
    companion object {
        const val PERSONAL = "PERSONAL"
        const val AGENCY = "AGENCY"
        const val COMPANY = "COMPANY"
    }
}

class MenuId {
    companion object {
        const val DELETE = 0
        const val UPDATE = 1
    }
}

class TimeMaximum {
    companion object {
        const val SEC = 60L
        const val MIN = 60L
        const val HOUR = 24L
        const val DAY = 30L
        const val MONTH = 12L
    }
}