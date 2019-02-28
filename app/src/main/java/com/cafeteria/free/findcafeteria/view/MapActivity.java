package com.cafeteria.free.findcafeteria.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.databinding.ActivityMapBinding;

public class MapActivity extends AppCompatActivity {

    ActivityMapBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_map);
        binding.setActivity(this);


        //경도, 위도 값이


    }

}
