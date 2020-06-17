package com.example.pdrtest;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // 센서 객체
    private static SensorManager mSensormanager;
    Sensor mAccelerometer;            // 가속도 센서
    Sensor mGyroSensor;             // 자이로 스코프 센서
    // TODO : 추후 층수 구분에 기압 센서 사용해야 함
    float azimuthX = 0.0f;             // X 방위
    float azimuthY = 0.0f;             // Y 방위
    float azimuthZ = 0.0f;             // Z 방위


    // 이동 및 측정을 위한 객체
    private int step;                 // 걸음수
    public double distance;           // 측정 거리
    DecimalFormat mFormat;            // 실수의 출력 자리수를 지정하는 포맷 객체


    // 경로 탐색시 필요한 변수
    private int path[] = new int[4];     // 계산후 저장된 배열
    private int newPath[] = new int[4];  // 표시부에 편리하게 그리기 위해 재배열 저장소
    private int pathIndex = 1;           // 재배열시 필요한 인덱스 식별자
    private boolean isRequested = false; // 경로 탐색 여부
    private int start;                   // 경로 시작점 - 재배열시 필요
    private int dst;                     // 경로 도착점 - 재배열시 필요


    // 각 클래스
    Calculator cal;             // 계산부
    MapView cv;                 // 표시부
    Button initButton;

    // 디버깅 네임
    private static final String TAG = MainActivity.class.getName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // 센서 사용을 위한 초기화
    }


    // 초기화 메소드
    public void init()
    {
        // 포맷 객체를 생성
        mFormat = new DecimalFormat();
        mFormat.applyLocalizedPattern("0.##");    // 소수점 두자리까지 출력될 수 있는 형식을 지정

        // 센서 객체 선언
        // 시스템서비스로부터 SensorManager 객체를 얻는다.
        mSensormanager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        // SensorManager 를 이용해서 가속도 센서 객체를 얻는다.
        mAccelerometer = mSensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // SensorManager 를 이용해서 자이로스코프 센서 객체를 얻는다.
//        mGyroSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mGyroSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // 계산부 초기화
        cal = new Calculator();
        cal.init();

        // 맵뷰 초기화
        cv = (MapView) findViewById(R.id.cv);
        cv.init();

        // 초기화 버튼
        initButton = findViewById(R.id.initButton);
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.setStartX(550);
                cv.setStartY(700);
            }
        });

    }

    public void drawUserPos()
    {
        // TODO : 차후 층수 문제는 기압 센서 활용하여 해결
//        if(floor == 7) cv.setFloor(7);
//        else if(floor == 8) cv.setFloor(8);

        // 경로
        if(isRequested){

            // 시작노드 저장
            newPath[0] = start;

            // 리커시브로 역추적하여 경유노드 load
            reArrange_Path(start, dst);

            // 추적이 끝나면 사용한 변수 초기화
            pathIndex = 1;

            // 재배열 완료된 path 전달후 표시부 세팅
            cv.setPath(newPath);
            cv.setStartPos(); // 시작 지점 설정
            cv.setIsNaviOn(true);
        }

        // 사용자 좌표
        // 이동중인 상황만 좌표 업데이트
        if(cal.getIsMoved()) {

            TextView tvD = (TextView)findViewById(R.id.textViewD); // 테스트용

            // x 방위에 따라 이동 값이 달라짐
            if(315 <= azimuthX && 360 > azimuthX || 0 <= azimuthX && azimuthX < 45){ // 북

                cv.setY(-(float) cal.getDistance());
                tvD.setText("현재 방향 : 북" + azimuthX);

            } else if (45 <= azimuthX && azimuthX < 135){ // 동

                cv.setX((float) cal.getDistance());
                tvD.setText("현재 방향 : 동" + azimuthX);

            } else if (135 <= azimuthX && azimuthX < 225){ // 남

                cv.setY((float) cal.getDistance());
                tvD.setText("현재 방향 : 남" + azimuthX);

            } else if (225 <= azimuthX && azimuthX < 315){ // 서

                cv.setX(-(float) cal.getDistance());
                tvD.setText("현재 방향 : 서" + azimuthX);
            }

        }

        // 모든 과정후 화면 업데이트
        cv.invalidate();

    }

    protected void onResume() {
        super.onResume();
        // 센서 값을 이 컨텍스트에서 받아볼 수 있도록 리스너를 등록
        mSensormanager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensormanager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_UI);
        scannerView.startCamera();

    }


    protected void onPause()
    {
        super.onPause();

        // 센서 값이 필요하지 않는 시점에 리스너를 해제
        mSensormanager.unregisterListener(this);
        scannerView.stopCamera();
    }


    // 센서값이 변경될 때마다 메소드 실행
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // 센서들이 동시에 호출되므로 synchronized 로 관리
        synchronized (this)
        {
            switch (event.sensor.getType())
            {
                // 가속 센서가 전달한 데이터인 경우
                case Sensor.TYPE_ACCELEROMETER:

                    // 계산할 가속도 값을 넘기고 PDR법 계산 진행
                    cal.setAcc(event.values[0], event.values[1], event.values[2]);
                    cal.cal_PDR();

                    // 전체 캔버스 업데이트
                    drawUserPos();

                break;

                // 자이로 스코프 센서가 전달한 데이터인 경우
//                case Sensor.TYPE_ORIENTATION:
                case Sensor.TYPE_GYROSCOPE:

                    TextView tvX = (TextView)findViewById(R.id.textViewX);
                    tvX.setText("X: " + String.valueOf(event.values[0]));
                    TextView tvY = (TextView)findViewById(R.id.textViewY);
                    tvY.setText("Y: " + String.valueOf(event.values[1]));
                    TextView tvZ = (TextView)findViewById(R.id.textViewZ);
                    tvZ.setText("Z: " + String.valueOf(event.values[2]));

                    azimuthX = event.values[0];
                    azimuthY = event.values[1];
                    azimuthZ = event.values[2];

                break;
            }
        }
    }

    // 정확도 변경시 호출되는 메소드. 잘 사용되지 않음
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // 인텐트 플래그가 FLAG_ACTIVITY_SINGLE_TOP이 실행될 때만
    // 해당 함수 호출 ( 이미 생성된 액티비티 사용하는 것 )
    @Override
    protected void onNewIntent(Intent intent){

        setIntent(intent);
        processIntent();
        super.onNewIntent(intent);
    }


    private void processIntent(){

        // 인텐트 전달
        Intent receiveIntent = getIntent();

        // 키값을 이용한 전달값 인수
        Toast toast =  Toast.makeText(this, "경로 안내를 시작합니다.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,200);
        toast.show();

        // 목적지, 도착징 정보 받음
        start =receiveIntent.getIntExtra("start",0);
        dst = receiveIntent.getIntExtra("destination",0);

        // 계산된 경로 배열 받음
        path = receiveIntent.getIntArrayExtra("path");
        pathIndex = 1;
        for(int i = 0; i< newPath.length ;i++){
            newPath[i] = 9999;  // 새로 저장할 경로 배열 초기화 ( 9999 = 빈공간 )
        }

        // 경로 탐색 여부 세팅
        cv.setIsNaviOn(true);
        isRequested = true;

    }

    // 경로 담아져있는 배열 순차로 재배열
    void reArrange_Path(int start,int end){

        // 리커시브로 경로를 역추적
        if(path[end] != start)
            reArrange_Path(start,path[end]);

        // 역추적하면서 순차적으로 경유지점 저장
        newPath[pathIndex++] = end;
    }

}
