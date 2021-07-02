package com.example.whuinfoplatform.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.R;

import java.util.List;

public class my_info_Adapter extends ArrayAdapter<my_info> {
    private int resourceId;

    public my_info_Adapter(Context context, int textViewResourceId, List<my_info> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        my_info myinfo = getItem(position);
        //View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        View view;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        }
        else
            view=convertView;
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView form = (TextView) view.findViewById(R.id.form);
        TextView detail = (TextView) view.findViewById(R.id.detail);
        TextView answered = (TextView) view.findViewById(R.id.answered);
        time.setText(myinfo.getTime());
        form.setText(myinfo.getForm());
        detail.setText(myinfo.getDetail());
        answered.setText(myinfo.getAnswered());
        return view;
    }
}

