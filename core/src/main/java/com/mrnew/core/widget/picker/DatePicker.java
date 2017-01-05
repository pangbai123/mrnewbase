package com.mrnew.core.widget.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.mrnew.R;

import java.util.Calendar;

/**
 * 日期选择滚轮
 */
public class DatePicker extends FrameLayout {

    private Context mContext;
    private NumberPicker mYearPicker;
    private NumberPicker mMonthPicker;
    private NumberPicker mDayPicker;
    private Calendar mCalendar;

    private String[] mMonthDisplay;

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCalendar = Calendar.getInstance();
        initMonthDisplay();
        ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.date_picker, this, true);
        mYearPicker = (NumberPicker) findViewById(R.id.date_year);
        mMonthPicker = (NumberPicker) findViewById(R.id.date_month);
        mDayPicker = (NumberPicker) findViewById(R.id.date_day);

        mYearPicker.setMinValue(1950);
        mYearPicker.setMaxValue(2100);
        mYearPicker.setValue(mCalendar.get(Calendar.YEAR));

        mMonthPicker.setMinValue(0);
        mMonthPicker.setMaxValue(11);
        mMonthPicker.setDisplayedValues(mMonthDisplay);
        mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));

        mDayPicker.setMinValue(1);
        mDayPicker.setMaxValue(31);
        mDayPicker.setValue(20);
        mDayPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);


        mMonthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {
                mCalendar.set(Calendar.MONTH, newVal);
                updateDate();
            }
        });
        mDayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {

                mCalendar.set(Calendar.DATE, newVal);
                updateDate();
            }
        });
        mYearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {
                mCalendar.set(Calendar.YEAR, newVal);
                updateDate();

            }
        });

        updateDate();

    }

    private void initMonthDisplay() {
        mMonthDisplay = new String[12];
        for (int i = 0; i < 12; i++) {
            int a = i + 1;
            mMonthDisplay[i] = a + "";
        }
    }

    private void updateDate() {
        mYearPicker.setValue(mCalendar.get(Calendar.YEAR));
        mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));
        mDayPicker.setMinValue(mCalendar.getActualMinimum(Calendar.DATE));
        mDayPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DATE));
        mDayPicker.setValue(mCalendar.get(Calendar.DATE));
    }

    public DatePicker(Context context) {
        this(context, null);
    }

    public String getDate() {
        return mYearPicker.getValue() + "-"
                + (mMonthPicker.getValue() + 1) + "-" + mDayPicker.getValue();

    }

    public int getDay() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH);
    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
        updateDate();
    }

}
