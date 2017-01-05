package com.mrnew.core;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * 列表adapter基类
 *
 * @param <T>
 * @param <Holder>
 */
public abstract class BaseListAdapter<T, Holder> extends ArrayAdapter<T> {
    protected LayoutInflater inflater;
    protected Activity mContext;
    protected List<T> mList;

    public BaseListAdapter(Context context, List list) {
        super(context, 0, list);
        this.inflater = LayoutInflater.from(context);
        mContext = (Activity) context;
        mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = onCreateView(inflater, holder, parent, getItemViewType(position));
        }
        holder = (Holder) convertView.getTag();
        onBindViewHolder(holder, getItem(position), position);
        return convertView;
    }

    public abstract View onCreateView(LayoutInflater inflater, Holder holder, ViewGroup parent, int viewType);

    //将数据与界面进行绑定的操作
    public abstract void onBindViewHolder(Holder holder, T data, int position);

}
