package com.example.whuinfoplatform.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.Entity.my_msg;
import com.example.whuinfoplatform.R;

import java.util.List;


    public class my_msg_Adapter extends ArrayAdapter<my_msg> {
        private int resourceId;

        public my_msg_Adapter(Context context, int textViewResourceId, List<my_msg> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            my_msg mymsg = getItem(position);
            //View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            View view;
            if(convertView==null){
                view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            }
            else
                view=convertView;
            TextView last_time = (TextView) view.findViewById(R.id.last_time);
            TextView last_detail = (TextView) view.findViewById(R.id.last_detail);
            TextView oppo_name = (TextView) view.findViewById(R.id.oppo_name);
            TextView last = (TextView) view.findViewById(R.id.last);
            last_time.setText(mymsg.getLastTime());
            last_detail.setText(mymsg.getLastDetail());
            oppo_name.setText(mymsg.getOppoName());
            if(!mymsg.getLast().equals("")){
                last.setText(mymsg.getLast());
                last.setVisibility(View.VISIBLE);
            }
            else last.setVisibility(View.GONE);
            return view;
        }
    }
