package com.cafeteria.free.findcafeteria.view;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.cafeteria.free.findcafeteria.viewmodel.CafeteriaViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import io.reactivex.subjects.PublishSubject;

public class TestActivity extends AppCompatActivity {

    private EditText etInput;

   // @BindView(R.id.textView)
    private TextView tvOutput;

    private CafeteriaViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // ButterKnife.bind(this);

        etInput = findViewById(R.id.editText);
        tvOutput = findViewById(R.id.textView);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                Logger.d("text=" + arg0.toString());
                viewModel.onKeywordChanged(arg0.toString());
            }

        });


        viewModel = ViewModelProviders.of(this).get(CafeteriaViewModel.class);

        viewModel.getCafeteriaInfo().observe(this, data -> {
            tvOutput.setText(data);
        });


    }

//    @OnTextChanged(value = R.id.editText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
//    void onTextChanged(String text) {
//        Logger.d("text=" + text);
//        viewModel.onKeywordChanged(text);
//    }

}
