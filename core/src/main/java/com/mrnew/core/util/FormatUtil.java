package com.mrnew.core.util;

import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.EditText;

import java.text.DecimalFormat;

/**
 * Created by mrnew on 2017/1/5.
 * email:474923660@qq.com
 * 数字金额格式化相关类
 */
public class FormatUtil {

    /**
     * 将传入的金额（单位元）格式化为元
     * 格式如：1  1.1 1.01 1,000
     *
     * @param amount
     * @return
     */
    public static String formatMoney(double amount) {
        DecimalFormat formater = new DecimalFormat("###,##0.##");
        return formater.format(amount);
    }

    /**
     * 处理数字
     *
     * @param number
     * @return
     */
    public static String formatNumber(double number) {
        DecimalFormat formater = new DecimalFormat("#0.##");
        return formater.format(number);
    }

    /**
     * 通用处理金额输入，限定小数点后只能输入两位小数
     *
     * @param edit
     */
    public static void formatInput(EditText edit) {
        String str = edit.getText().toString();
        String newstr = "" + str;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (str.contains(".")) {
            int index = str.indexOf(".");

            if (index == 0) {
                newstr = "0" + str;
            }
            int lastIndex = str.lastIndexOf(".");
            if (index != lastIndex) {
                newstr = str.substring(0, lastIndex)
                        + str.substring(lastIndex + 1);
            }

            String[] temp2 = newstr.split("\\.");
            if (temp2.length == 2) {
                if (temp2[1].length() > 2) {
                    newstr = temp2[0] + "." + temp2[1].substring(0, 2);
                }
            }
            if (!newstr.equals(str)) {
                edit.setText(newstr);
                CharSequence text = edit.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
            }
        } else {
            if (str.charAt(0) == '0' && str.length() > 1) {
                edit.setText(str.substring(1));
                CharSequence text = edit.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
            }
        }
    }

}
