package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityBasicBinding;
import com.example.whuinfoplatform.databinding.ActivityInfoHallBinding;
import com.example.whuinfoplatform.databinding.ActivityRootBinding;

public class Info_Hall_Activity extends rootActivity {
    private ActivityInfoHallBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding= ActivityInfoHallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.startFindInfoActivity.setOnClickListener(v->{
            String kwd = binding.startEditText.getText().toString();
            if(!kwd.equals("")){
                Intent intent = new Intent(Info_Hall_Activity.this, Search_Info_promote_Activity.class);
                intent.putExtra("kwd",kwd);
                Intent intent1=getIntent();
                int id=intent1.getIntExtra("id",0);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            else{
                Toast.makeText(Info_Hall_Activity.this,"搜索不能为空！",Toast.LENGTH_SHORT).show();
            }
        });
        binding.startPublishInfoActivity.setOnClickListener(v->{
            Intent intent1=getIntent();
            int id=intent1.getIntExtra("id",0);
            Intent intent2 = new Intent(Info_Hall_Activity.this, Publish_Info_promote_Activity.class);
            intent2.putExtra("id",id);
            startActivity(intent2);
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initWidget() {
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("信息大厅");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}

/*@Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        String kwd = editText.getText().toString();
                        if(!kwd.equals("")){
                            Intent intent = new Intent(Info_Hall_Activity.this, Search_Info_promote_Activity.class);
                            intent.putExtra("kwd",kwd);
                            startActivity(intent);
                            break;
                        }
                        else{
                            Toast.makeText(Info_Hall_Activity.this,"搜索不能为空！",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    default:
                        break;
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button2:
                        Intent intent = new Intent(Info_Hall_Activity.this, Publish_Info_promote_Activity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }*/