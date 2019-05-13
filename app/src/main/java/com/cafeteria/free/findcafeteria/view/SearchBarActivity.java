package com.cafeteria.free.findcafeteria.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.SearchHistory;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SearchBarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryViewAdapter historyViewAdapter;

    private GestureDetector gestureDetector;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean isGPSEnabled, isNetworkEnabled;
    private MyPermissionListener mPermissionListener;
    private EditText searchEditText;

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

        mPermissionListener = new MyPermissionListener();

        searchEditText = findViewById(R.id.searchEditText);
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
                updateRecyclerView(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                summitKeyword(searchEditText.getText().toString());
                return true;
            }

            return false;
        });


        clearButton.setOnClickListener(v -> {
            Logger.d("");
            searchEditText.setText("");
        });

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            super.onBackPressed();
        });

        ImageButton myLocationButton = findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(view -> {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            new TedPermission(this)
                    .setPermissionListener(mPermissionListener)
                    .setDeniedMessage("위치 권한이 필요합니다\n1.설정을 누르세요\n2.권한을 누르세요\n3.위치를 켜주세요")
                    .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    .setGotoSettingButton(true)
                    .setGotoSettingButtonText("설정")
                    .check();

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


    private class MyPermissionListener implements PermissionListener {
        private LocationManager locationManager = (LocationManager) SearchBarActivity.this.getSystemService(Context.LOCATION_SERVICE);

        @Override
        public void onPermissionGranted() {

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled || !isGPSEnabled) {

                Toast.makeText(SearchBarActivity.this, "현재 위치를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                Logger.d(isNetworkEnabled + " 검사중 " + isGPSEnabled);

            } else {    // 권한을 받은 상태
                getCurrentLocation();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(SearchBarActivity.this, "권한 필요 : " + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        OnCompleteListener<Location> completeListener = task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location currentLocation = task.getResult();
                Double myLatitude = currentLocation.getLatitude();
                Double myLongitude = currentLocation.getLongitude();

                String currentAddress = getCurrentAddress(myLatitude, myLongitude);

                Toast.makeText(this, "내위치 : " + currentAddress, Toast.LENGTH_SHORT).show();
                searchEditText.setText(currentAddress);
            } else {
            }
        };

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(SearchBarActivity.this, completeListener);
    }


    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);

        Logger.d(address.toString());

        String result = "";

        if (TextUtils.isEmpty(address.getLocality())) {
            result += address.getAdminArea();
        }
        else {
            result += address.getLocality();
        }

        if (TextUtils.isEmpty(address.getSubLocality())) {

            if (!TextUtils.isEmpty(address.getSubAdminArea())) {
                result += " " +address.getSubAdminArea();
            }
        }
        else {
            result += " " + address.getSubLocality();
        }

        return result;
    }

    private void summitKeyword(String keyword) {
        Logger.d(keyword);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("keyword", keyword);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateRecyclerView(String filter) {

        new Thread(() -> {
            List<SearchHistory> histories = AppDatabase.getInstance(this).getSearchHistoryDao().getAll();
            List<String> keywords = histories.stream()
                    .filter(history -> {
                        if (TextUtils.isEmpty(filter)) {
                            return true;
                        } else {
                            return history.getKeyword().contains(filter);
                        }
                    })
                    .map(history -> history.getKeyword())
                    .collect(Collectors.toList());

            runOnUiThread(() -> {
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
