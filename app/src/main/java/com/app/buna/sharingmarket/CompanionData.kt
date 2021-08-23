package com.app.buna.sharingmarket

class REQUEST_CODE {
    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1000 // 위치 권한
        const val API_COMPLETED_FINISH = 3000 // 다음 주소 찾기 request code
        const val RC_SIGN_IN = 900 // 구글 로그인 request code
        const val REGISTER_OK_CODE = 4000 // 판매 글 등록 성공
        const val REGISTER_FAIL_CODE = 4001 // 판매 글 등록 실패
        const val IMAGE_PICKER_REQUEST_CODE = 5000 // 이미지 피커 갤러리 request 코드
    }
}

class CONST {
    companion object {
        const val SPLASH_DURATION: Long = 2000 // 스플래시 화면 전환 1500ms
    }
}

class SOSOCK {
    companion object {
        const val PERSONAL = "PERSONAL"
        const val AGENCY = "AGENCY"
        const val COMPANY = "COMPANY"
    }
}

class TIME_MAXIMUM {
    companion object {
        const val SEC = 60L
        const val MIN = 60L
        const val HOUR = 24L
        const val DAY = 30L
        const val MONTH = 12L
    }
}