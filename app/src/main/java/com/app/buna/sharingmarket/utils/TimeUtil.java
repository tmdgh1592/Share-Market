package com.app.buna.sharingmarket.utils;

import com.app.buna.sharingmarket.TIME_MAXIMUM;

public class TimeUtil {
    public static String getTimeAgo(long regTime) { // regTime : 예전 시간
        long curTime = System.currentTimeMillis(); // curTime : 현재 시간
        long diffTime = (curTime - regTime) / 1000; // 시간 차이를 구해서 1000으로 나눔

        String msg;

        if (diffTime < TIME_MAXIMUM.SEC) {
            // sec
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            // min
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            // hour
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            // day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            // day
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
