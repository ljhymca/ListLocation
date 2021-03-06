package org.techtown.listlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.geometry.Utmk;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RangeActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView sizeBarView;
    Button sendbutton;

    private static Handler mHandler;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range);

        MapFragment mapFragment= (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);//지도 객체 생성

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();

        }
        mapFragment.getMapAsync(this);
//        Utmk utmk = new Utmk(953935,1952044.1);
//        LatLng latLng = utmk.toLatLng();
//
//        Toast.makeText(this,"위도: "+latLng.latitude + "경도: " +latLng.longitude,
//                Toast.LENGTH_LONG).show();//들어갔을때 위도 경도 표시
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();//오버레이 생성
        locationOverlay.setVisible(true);//오버레이생성
        locationOverlay.setPosition(new LatLng(37.5670135, 126.9783740));//좌표지정
        Marker marker2 = new Marker();
        marker2.setPosition(new LatLng(1, 1));//일단 마커 위치지정
        marker2.setMap(naverMap);//마커 생성
        CircleOverlay markerCircle = new CircleOverlay();//원 생성

        //SeekBar할당
        SeekBar sizeBar = (SeekBar) findViewById(R.id.sizeBar);
        final TextView sizeBarView =(TextView)findViewById(R.id.sizeBarView);

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeBarView.setText(""+progress);
                markerCircle.setRadius(progress);//원 반지름
                Log.e("바운스", ""+ markerCircle.getBounds());
                markerCircle.getBounds();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        naverMap.setOnMapClickListener((point, coord)-> { //지도 화면클릭시
            Toast.makeText(this, getString(R.string.format_map_click, coord.latitude, coord.longitude), Toast.LENGTH_SHORT).show();
            String progress = sizeBarView.getText().toString();
            final long range = Long.parseLong(progress);
            marker2.setPosition(new LatLng(coord.latitude, coord.longitude));//클릭 좌표로 마커 위치 이동
            markerCircle.setCenter(coord);//원 중심을 선택한 위치로
            markerCircle.setRadius(range);//원 반지름
            markerCircle.setColor(0x4000FFFF);//불투명도 지정
            markerCircle.setMap(naverMap);
            sendbutton = (Button) findViewById(R.id.sendButton);
            long now = System.currentTimeMillis();//시간생성
            Date date = new Date(now);
            SimpleDateFormat Now = new SimpleDateFormat("yyyyMMddHHmmss");
            String formatDate = Now.format(date);
            markerCircle.getBounds();
            Log.e("바운스", ""+ markerCircle.getBounds());

            sendbutton.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View v){//버튼 클릭시 현재 위도 경도 값 전송
                    databaseReference.child("ids").child(formatDate).child("latitude").setValue(coord.latitude);//위도
                    databaseReference.child("ids").child(formatDate).child("longitude").setValue(coord.longitude);//경도
                    databaseReference.child("ids").child(formatDate).child("range").setValue(range);//원 범위
                    databaseReference.child("ids").child(formatDate).child("name").setValue("랜덤값");
                    //databaseReference.child("id").child("setRange").child(formatDate).child("southlatitude").setValue(markerCircle.getBounds().getSouthWest().latitude);
                    //databaseReference.child("id").child("setRange").child(formatDate).child("southlongitude").setValue(markerCircle.getBounds().getSouthWest().longitude);
                    //databaseReference.child("id").child("setRange").child(formatDate).child("northlatitude").setValue(markerCircle.getBounds().getNorthEast().latitude);
                    //databaseReference.child("id").child("setRange").child(formatDate).child("northlongitude").setValue(markerCircle.getBounds().getNorthEast().longitude);
                    //id 값 넣을 자리
                    Intent intent = new Intent(RangeActivity.this, ListActivity.class);
                    startActivity(intent);
                }
            });
        });
    }


}