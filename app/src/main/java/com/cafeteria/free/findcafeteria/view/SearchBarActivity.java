package com.cafeteria.free.findcafeteria.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.SearchHistory;
import com.cafeteria.free.findcafeteria.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchBarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryViewAdapter historyViewAdapter;

    private GestureDetector gestureDetector;
    
    private RecyclerView.OnItemTouchListener itemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());

            if (childView != null && gestureDetector.onTouchEvent(e)) {
                TextView textView = childView.findViewById(R.id.history_textView);
                String keyword = textView.getText().toString();
                summitKeyword(keyword);

                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText searchEditText = findViewById(R.id.searchEditText);
        ImageButton clearButton = findViewById(R.id.clearButton);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && s.subSequence(s.length() - 1, s.length()).toString().equalsIgnoreCase("\n")) {
                    summitKeyword(s.subSequence(0, s.length() - 1).toString());
                }
                else {
                    updateRecyclerView(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        clearButton.setOnClickListener(v -> {
            Logger.d("");
            searchEditText.setText("");
        });

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(layoutManager);

        historyViewAdapter = new HistoryViewAdapter();
        recyclerView.setAdapter(historyViewAdapter);

        recyclerView.addOnItemTouchListener(itemTouchListener);

        updateRecyclerView("");
    }


    private void summitKeyword(String keyword) {
        Logger.d(keyword);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("keyword", keyword);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateRecyclerView(String filter) {

        new Thread(()-> {
            List<SearchHistory> histories = AppDatabase.getInstance(this).getSearchHistoryDao().getAll();
            List<String> keywords = histories.stream()
                    .filter(history-> {
                        if (TextUtils.isEmpty(filter)) {
                            return true;
                        }
                        else {
                            return history.getKeyword().contains(filter);
                        }
                    })
                    .map(history-> history.getKeyword())
                    .collect(Collectors.toList());

            runOnUiThread(()-> {
                historyViewAdapter.setKeywords(keywords);
            });

        }).start();
    }

    private class HistoryViewAdapter extends RecyclerView.Adapter<HistoryHolder> {

        private List<String> keywords = new ArrayList<>();

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
            notifyDataSetChanged();
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
            return new HistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {
            String keyword = keywords.get(position);
            holder.keywordTv.setText(keyword);
        }

        @Override
        public int getItemCount() {
            return keywords.size();
        }
    }

    private class HistoryHolder extends RecyclerView.ViewHolder {

        TextView keywordTv;

        public HistoryHolder(View itemView) {

            super(itemView);
            keywordTv = itemView.findViewById(R.id.history_textView);
        }
    }
}
