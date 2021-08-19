package com.app.buna.sharingmarket.textwatcher

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout

class MyTextWatcher {

    class PhoneNumTextWatcher(val editText: EditText, val button: LinearLayout) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            val now = s.toString().length
            Log.d("before text count", start.toString())
            Log.d("now text count", now.toString())

            /*if(editText.isFocusable() && !s.toString().equals("")) {
                if ((start == 2 && now == 3) || (start == 7 && now == 8)) { // 010 0000
                    editText.append(" ")
                }
            }*/

            if(now >= 10) {
                button.isEnabled = true
            }else if(now < 10){
                button.isEnabled = false
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    class CodeNumTextWatcher(val button: LinearLayout) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            val now = s.toString().length
            Log.d("before text count", start.toString())
            Log.d("now text count", now.toString())

            /*if(editText.isFocusable() && !s.toString().equals("")) {
                if ((start == 2 && now == 3) || (start == 7 && now == 8)) { // 010 0000
                    editText.append(" ")
                }
            }*/

            if(now >= 6) {
                button.isEnabled = true
            }else if(now < 6){
                button.isEnabled = false
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

}