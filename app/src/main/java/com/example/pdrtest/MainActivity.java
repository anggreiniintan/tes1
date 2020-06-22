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

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // Objek sensor
    private static SensorManager mSensormanager;
    Sensor mAccelerometer;            // Sensor akseleremoter
    Sensor mGyroSensor;             // Sensor gyroskop
    // TODO : 추후 층수 구분에 기압 센서 사용해야 함
    float azimuthX = 0.0f;
    float azimuthY = 0.0f;
    float azimuthZ = 0.0f;
    double Gyakar;


    // Objek untuk pergerakan dan pengukuran
    private int step = -1;                 // Langkah
    public double distance;           // Mengukur jarak
    DecimalFormat mFormat;            // Format objek yang menentukan jumlah sebenarnya dari digit output


    // Variabel diperlukan untuk jalur navigasi
    private int path[] = new int[4];     // Array tersimpan setelah perhitungan
    private int newPath[] = new int[4];  // Penyimpanan penataan ulang untuk memudahkan menggambar pada tampilan
    private int pathIndex = 1;           // Pengidentifikasi indeks diperlukan saat menyusun ulang
    private boolean isRequested = false; // Apakah rute tersebut dicari atau tidak
    private int start;                   // Jalur awal-diperlukan jalur untuk penataan ulang
    private int dst;                     // Diperlukan titik kedatangan rute untuk penataan ulang


    // Class
    Calculator cal;             // Perhitungan
    MapView cv;                 // Tampilan
    Button initButton;

    private CSVWriter csvWriter;
    private File file;

    public static TextView resultposisi;

    // Nama debugging
    private static final String TAG = MainActivity.class.getName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultposisi = findViewById(R.id.resultt);

        init(); //Inisialisasi untuk penggunaan sensor

        file = new File("/sdcard/ENKK/PDRTEST.csv");
        try {
            csvWriter = new CSVWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // Metode inisialisasi
    public void init()
    {
        // Buat objek format
        mFormat = new DecimalFormat();
        mFormat.applyLocalizedPattern("0.##");    // Format yang dapat dicetak ke dua tempat desimal

        // Mendeklarasikan objek sensor
        // Dapatkan objek SensorManager dari layanan sistem.
        mSensormanager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        //Objek SensorManager digunakan untuk mendapatkan objek sensor akselerasi.
        mAccelerometer = mSensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Gunakan SensorManager untuk mendapatkan objek sensor giroskop.
//        mGyroSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mGyroSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Inisialisasi kalkulator
        cal = new Calculator();
        cal.init();

        // Inisialisasi tampilan peta
        cv = (MapView) findViewById(R.id.cv);
//        if(resultposisi.getText() == "D201"){
////            cv.setStartX(120);
////            cv.setStartX(200);
//            cv.setX(120);
//            cv.setY(100);
//        }
        if(Scann.resulttextView.getText() == "D201"){
//            cv.setStartX(120);
//            cv.setStartX(200);
            cv.setX(120);
            cv.setY(100);
        }


            //Tombol Atur ulang
        initButton = findViewById(R.id.initButton);
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    cv.setStartX(500);
                    cv.setStartY(200);

                cal.step=0;
            }
        });

        resultposisi.setText("Posisi Awal : " + Scann.resulttextView.getText());
    }

    public void drawUserPos()
    {

        // Koordinat pengguna
        // Koordinasikan pembaruan hanya dalam situasi bergerak
        if(cal.getIsMoved()) {

            TextView tvD = (TextView)findViewById(R.id.textViewD); // Untuk pengujian

            // Nilai pergerakan bervariasi tergantung pada arah x
            if(315 <= Gyakar && 360 > Gyakar || 0 <= Gyakar && Gyakar < 45){ // 북

                cv.setY(-(float) cal.getDistance());
                tvD.setText("Arah saat ini: Utara" + Gyakar);

            } else if (45 <= Gyakar && Gyakar < 135){ // 동

                cv.setX((float) cal.getDistance());
                tvD.setText("Arah saat ini: Timur" + Gyakar);

            } else if (135 <= Gyakar && Gyakar < 225){ // 남

                cv.setY((float) cal.getDistance());
                tvD.setText("Arah saat ini: Selatan" + Gyakar);

            } else if (225 <= Gyakar & Gyakar < 315){ // 서

                cv.setX(-(float) cal.getDistance());
                tvD.setText("Arah saat ini: Barat" + Gyakar);
            }

            TextView stepc = (TextView) findViewById(R.id.step);
            stepc.setText("Step" + cal.step);
        }

        // Pembaruan layar setelah setiap proses
        cv.invalidate();

    }

    protected void onResume() {
        super.onResume();
        //Daftarkan pendengar untuk menerima nilai sensor dalam konteks ini
        mSensormanager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensormanager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_UI);

    }
    protected void onPause()
    {
        super.onPause();

        // Lepaskan pendengar saat nilai sensor tidak diperlukan
        mSensormanager.unregisterListener(this);
    }

   // Eksekusi metode setiap kali nilai sensor berubah
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // Karena sensor dipanggil pada saat yang sama, ia dikelola sebagai disinkronkan
        synchronized (this)
        {
            switch (event.sensor.getType())
            {
                // Dalam hal data ditransmisikan oleh sensor akselerasi
                case Sensor.TYPE_ACCELEROMETER:

                   double accVector = cal.accVector;
                   double preAcc = cal.preAcc;
                   double newAcc = cal.newAcc;
                  int step = cal.step;
                   double distance = cal.temp;
                    // Lewati nilai percepatan yang akan dihitung dan lanjutkan dengan perhitungan PDR
                    cal.setAcc(event.values[0], event.values[1], event.values[2]);
                    cal.cal_PDR();
                    csvWriter.writeNext(new String[]{String.valueOf(event.values[0]), String.valueOf(event.values[1]),
                            String.valueOf(event.values[2]),String.valueOf(accVector),String.valueOf(preAcc),String.valueOf(newAcc),
                            String.valueOf(step), String.valueOf(distance) });


                    // Pembaruan kanvas penuh
                    drawUserPos();

                break;

                // Dalam hal data ditransmisikan oleh sensor giroskop
//                case Sensor.TYPE_ORIENTATION:
                case Sensor.TYPE_GYROSCOPE:

                    azimuthX = event.values[0];
                    azimuthY = event.values[1];
                    azimuthZ = event.values[2];

                    TextView tvX = (TextView)findViewById(R.id.textViewX);
                    tvX.setText("GyroX: " + String.valueOf(event.values[0]));
                    TextView tvY = (TextView)findViewById(R.id.textViewY);
                    tvY.setText("GyroY: " + String.valueOf(event.values[1]));
                    TextView tvZ = (TextView)findViewById(R.id.textViewZ);
                    tvZ.setText("GyroZ: " + String.valueOf(Gyakar));

                    Gyakar = Math.sqrt(azimuthX*azimuthX + azimuthY*azimuthY) * 57.29578;

                break;
            }
        }
    }

    // Metode yang disebut ketika akurasi diubah. Tidak digunakan dengan baik
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
