package com.example.whuinfoplatform.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.Entity.srch_info;
import com.example.whuinfoplatform.R;

import java.util.List;

public class srch_info_Adapter extends ArrayAdapter<srch_info> {
    private int resourceId;

    public srch_info_Adapter(Context context, int textViewResourceId, List<srch_info> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        srch_info srchinfo = getItem(position);
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
        TextView owner = (TextView) view.findViewById(R.id.owner);
        time.setText(srchinfo.getTime());
        form.setText(srchinfo.getForm());
        detail.setText(srchinfo.getDetail());
        owner.setText(srchinfo.getOwner());
        return view;
    }
}
