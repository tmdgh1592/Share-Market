package com.app.buna.sharingmarket.utils;

import com.app.buna.sharingmarket.TimeMaximum;

public class TimeUtil {
    public static String getTimeAgo(long regTime) { // regTime : 예전 시간
        long curTime = System.currentTimeMillis(); // curTime : 현재 시간
        long diffTime = (curTime - regTime) / 1000; // 시간 차이를 구해서 1000으로 나눔

        String msg;

        if (diffTime < TimeMaximum.SEC) {
            // sec
            msg = "방금 전";
        } else if ((diffTime /= TimeMaximum.SEC) < TimeMaximum.MIN) {
            // min
            msg = diffTime + "분 전";
        } else if ((diffTime /= TimeMaximum.MIN) < TimeMaximum.HOUR) {
            // hour
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TimeMaximum.HOUR) < TimeMaximum.DAY) {
            // day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TimeMaximum.DAY) < TimeMaximum.MONTH) {
            // day
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
