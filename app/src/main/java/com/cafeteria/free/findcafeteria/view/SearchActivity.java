package com.cafeteria.free.findcafeteria.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cafeteria.free.findcafeteria.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history);

        EditText search = findViewById(R.id.searchEditText);
        ImageButton clearButton = findViewById(R.id.clearButton);

        clearButton.setOnClickListener(v -> {
            search.setText("");
        });

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
        });

    }
}
