package com.mrnew.core.widget.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.mrnew.R;

import java.util.Calendar;

/**
 * 时间选择滚轮
 */
public class TimePicker extends FrameLayout {

    private Context mContext;
    private NumberPicker mYearPicker;
    private NumberPicker mMonthPicker;
    private NumberPicker mDayPicker;
    private NumberPicker hourPicker;
    private NumberPicker minPicker;

    boolean is24Hour;
    boolean isAm = true;

    private Calendar mCalendar;

    private String[] mMonthDisplay;

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCalendar = Calendar.getInstance();
        initMonthDisplay();
        ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.time_picker, this, true);
        mYearPicker = (NumberPicker) findViewById(R.id.date_year);
        mMonthPicker = (NumberPicker) findViewById(R.id.date_month);
        mDayPicker = (NumberPicker) findViewById(R.id.date_day);
        hourPicker = (NumberPicker) findViewById(R.id.time_hours);
        minPicker = (NumberPicker) findViewById(R.id.time_minutes);


        mYearPicker.setMinValue(1950);
        mYearPicker.setMaxValue(2100);
        mYearPicker.setValue(mCalendar.get(Calendar.YEAR));

        mMonthPicker.setMinValue(0);
        mMonthPicker.setMaxValue(11);
        mMonthPicker.setDisplayedValues(mMonthDisplay);
        mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));

        mDayPicker.setMinValue(1);
        mDayPicker.setMaxValue(31);
        mDayPicker.setValue(mCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        mDayPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(mCalendar.get(Calendar.HOUR_OF_DAY));
        hourPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

        minPicker.setMinValue(0);
        minPicker.setMaxValue(59);
        minPicker.setValue(mCalendar.get(Calendar.MINUTE));
        minPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

        mYearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {
                mCalendar.set(Calendar.YEAR, newVal);
                updateDate();

            }
        });

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

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {

                mCalendar.set(Calendar.HOUR_OF_DAY, newVal);
                updateDate();
            }
        });

        minPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {

                mCalendar.set(Calendar.MINUTE, newVal);
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
        hourPicker.setMinValue(mCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        hourPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        hourPicker.setValue(mCalendar.get(Calendar.HOUR_OF_DAY));
        minPicker.setMinValue(mCalendar.getActualMinimum(Calendar.MINUTE));
        minPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.MINUTE));
        minPicker.setValue(mCalendar.get(Calendar.MINUTE));
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public String getDate() {
        return mYearPicker.getValue() + "-"
                + (mMonthPicker.getValue() + 1) + "-" + mDayPicker.getValue();

    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH);
    }


    public int getDay() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMin() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
        updateDate();
    }

}
