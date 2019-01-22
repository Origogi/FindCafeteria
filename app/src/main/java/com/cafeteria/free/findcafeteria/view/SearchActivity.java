package com.cafeteria.free.findcafeteria.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cafeteria.free.findcafeteria.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);


        List<CardViewDto> cardViewDtos = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            cardViewDtos.add(new CardViewDto(R.drawable.sample, "Main title#" + i , "Sub title#" + i));
        }

        recyclerViewAdapter.setCardViewDtos(cardViewDtos);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
