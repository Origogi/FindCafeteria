package com.cafeteria.free.findcafeteria.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.databinding.ActivityMapBinding;
import com.cafeteria.free.findcafeteria.util.Logger;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String query = getIntent().getStringExtra("query");
        Logger.d(query);
        Toast.makeText(this,query,Toast.LENGTH_LONG).show();


        //경도, 위도 값이


    }

}
