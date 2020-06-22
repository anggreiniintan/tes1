package com.example.pdrtest;

import android.nfc.Tag;
import android.util.Log;

// Kelas untuk menghitung jarak dan arah perjalanan
public class Calculator {

    //Variabel sensor percepatan
    private double acc_X, acc_Y, acc_Z;

    //Variabel penjumlahan vektor percepatan
    double accVector;

    // Variabel diperlukan untuk HPF
     double preAcc;
     double newAcc;

    private double[] HFilter_data;
    private int HFData_amount;
    private int adjustValue;

    // Variabel yang diperlukan untuk LPF
    private double LFilter_data[];
    private int LFilterData_amount;

    // Variabel diperlukan untuk zero-crossing
    private int zeroCount;

    // Langkah
    public int step;

    // Jarak yang diukur
     double distance;
    double temp;

    // Apakah suatu gerakan telah terjadi
    private boolean IsMoved;

    float peak = 0;
    float minpeak = 0;
    long timeOfLastPeak = 0;
    long timeOfNow = 0;
    //Puncak
    long timeOfThisPeak = 0;
    private final String TAG = "Calculator";
    private static final int ABOVE = 1;
    private static final int BELOW = 0;
    private static int CURRENT_STATE = 0;
    private static int PREVIOUS_STATE = BELOW;
    private long streakStartTime;
    private long streakPrevTime;

    // inisialisasi
    public void init()
    {
        // Inisialisasi variabel
        preAcc = 0;
        newAcc = 0;
        HFilter_data = new double[1024];

        HFData_amount = 0;
        adjustValue = 5;

        LFilter_data = new double[1024];
        LFilterData_amount = 0;
        zeroCount = 0;

        step = 0;
        distance = 0;

        IsMoved = false;
    }

    /* *******************************************
     *
     *  Metode teknik PDR
     *: Jumlah vektor percepatan, difilter melalui HPF-> LPF
     * Setelah mengukur jumlah langkah melalui Zero-Crossing
     * Hitung jarak total dengan mengukur panjang langkahnya menggunakan Pendekatan Weinberg.
     *
     * *******************************************/

    public void cal_PDR()
    {

        /*
         *  1. Hitung percepatan sebagai jumlah vektor
         *
         *    x, y, z sintesis vektor
         */
        cal_Vector();


        /*
         *  2. Penyaringan lulus tinggi (HPF)
         *    : Hapus nilai gravitasi melalui HPF.
         */
        cal_HPF();


        /*
         *  3. Pemfilteran frekuensi rendah
         *
         *   -Penyaringan low-pass (LPF)
         *    : Nilai kebisingan dihilangkan melalui LPF.
         *
         *    Hitung 10 rata-rata dari nilai melalui HPF.
         *
         */
//        cal_LPF();
        // HPF 초기화
        if(HFData_amount == HFilter_data.length)
            HFData_amount = 0;


        /*
         *  4. Zero-crossing Method WeinBerg Approach
         *
         *   -Zero-crossing Method
         *    :  Metode untuk menemukan di mana akselerasi 0 dan memotongnya satu siklus
         *
         *  -WeinBerg Approach
         *    : Algoritma untuk mengukur panjang langkah langkah.
         */
        cal_Step();

    }


    public void cal_Vector()
    {
        accVector = Math.sqrt(
                acc_X * acc_X +
                        acc_Y * acc_Y +
                        acc_Z * acc_Z);
    }

    public void cal_HPF()
    {

        final float alpha = (float)0.8;               // alpha = t / (t + Dt)

        // Hitung akselerasi dengan mengurangi data gravitasi dari nilai saat ini.
        preAcc = alpha * preAcc + (1 - alpha) * accVector;
        newAcc = accVector - preAcc;

        // Masukkan data melalui HPF dalam array data.
        HFilter_data[HFData_amount++] = newAcc;
    }

//    public void cal_LPF()
//    {
//
//        if(HFData_amount > 3)
//        {
//            int i, firstIndex = 0;
//            double data = 0;
//
//            // Dieksekusi setelah mendapatkan 5 atau lebih data
//            int midIndex = HFData_amount - 4;
//
//            if(midIndex < 5)
//                firstIndex = midIndex;
//            else if(midIndex >= 5 && midIndex < HFilter_data.length)
//                firstIndex = midIndex - 5;
//
//            // Rata-rata 10 data.
//            for (i = firstIndex; i < midIndex + 4; i++){
//                data += HFilter_data[i];
//            }
//            data = data / 10.0;
//
//            // Data yang baru difilter (data LPF) dimasukkan ke dalam array baru.
//            LFilter_data[LFilterData_amount++] = data;
//            adjustValue++;   // Jika nilai LPF normal, variabel penyesuaian +1
//
//            // Algoritma untuk disaring saat berhenti
//            // Jika nilai rata-rata (-0.08 <= data <= 0,08) saat dalam keadaan berhenti adalah 5 kali berturut-turut, nilainya dibuang. Itu dinilai akan dihentikan
//            // Jika data 0 dalam kasus di mana nilai rata-rata data dalam keadaan bergerak terus-menerus diinput lebih dari 5 kali, zeroCount +1.
//            if(data < 0.08 && data > -0.08 && adjustValue >= 5){
//                zeroCount++;
//                adjustValue = 0;
//            }
//        }
//
//    }


    public void cal_Step() {
//        double max = HFilter_data[0], min = HFilter_data[0];
//        timeOfLastPeak = timeOfThisPeak;
//
//        for (int i = 1; i<HFilter_data.length ; i++){
//
//            if(max < HFilter_data[i]) max = HFilter_data[i];
//            if(min > HFilter_data[i]) min = HFilter_data[i];
//
//        }
//        Log.v(TAG, " max :" +  max );
//        Log.v(TAG, " min :" +  min );

        if (newAcc > 0.6) {
            CURRENT_STATE = ABOVE;
            if (PREVIOUS_STATE != CURRENT_STATE) {
                streakStartTime = System.currentTimeMillis();
                if ((streakStartTime - streakPrevTime) <= 250f) {
                    streakPrevTime = System.currentTimeMillis();
                    return;
                }
                streakPrevTime = streakStartTime;
                step++;   // +1 langkah
                cal_StepSize();
            }
            Log.v(TAG, " step :" + step);
            Log.v(TAG, " newAcc :" + newAcc);
            PREVIOUS_STATE = CURRENT_STATE;
//        if(max > 4.0) {
//            cal_StepSize();
//            step++;   // +1 langkah
//        }
//
//       if(min > 4.0 &&( max > (min + 0.621)) && timeOfNow - timeOfLastPeak >= 1000) {
//            cal_StepSize();
//            step++;   // +1 langkah
//        }
//        // hitung ketika accelerometer mendekati nol untuk memotong satu siklus
//        // Ketika hitungan 2, itu diakui sebagai satu siklus.
////        if(zeroCount == 2){
//
//            // Bahkan jika smartphone bergerak sedikit, akselerasinya berubah.
//            // Proses untuk menyaring kehalusan sampai batas tertentu.
//            // Disetujui hanya ketika nilai absolut percepatan adalah 0,7
//            // atau lebih dalam data yang diperoleh selama satu siklus.
//            boolean access = false;
//            for (int i = 0 ; i < HFData_amount ; i++){
//
//                if(HFilter_data[i] >= 5.7 || HFilter_data[i] <= -5.7)
//                    access = true;
//            }
//
//            if(access){
//                // Sekarang potong satu siklus dan potong beberapa langkah.
//                // Hitung langkah langkahnya.
//                cal_StepSize();
//                step++;   // +1 langkah
//            }
//
//            // Potong satu langkah, inisialisasi setelah perhitungan, dan ulangi.
////            zeroCount = 0;
////            LFilterData_amount = 0;
////            LFilter_data = new double[1024];
//
////        }
        } else if (newAcc < 0.6) {
            CURRENT_STATE = BELOW;
            PREVIOUS_STATE = CURRENT_STATE;
        }
    }

    // scarlett Approach - 보폭 측정
    public void cal_StepSize()
    {
        // 순차방문을 통해 비교하여 max 와 min 을 찾아낸다.
        double max = HFilter_data[0], min = HFilter_data[0];

        for (int i = 1; i<HFilter_data.length ; i++){

            if(max < HFilter_data[i]) max = HFilter_data[i];
            if(min > HFilter_data[i]) min = HFilter_data[i];
        }

        // 보폭 상수 0.55
        final double k = 0.473;

        int sum = (int) newAcc;
        int n = step;
        for (int i = 1; i <= n; i++) {
            sum = sum + i;
        }
        double temp1,temp2;
        // Weinberg Approach 계산식
//        temp = k * Math.pow(max - min, 1.0 / 4.0);
        temp1 = ((sum /n )- min );
        temp2 = max - min ;
        temp = k  * (temp1 / temp2);
//        Log.v(TAG, " sum :" +  sum );
//        Log.v(TAG, " temp :" +  temp );
//        Log.v(TAG, " n :" +  n );
//        Log.v(TAG, " max :" +  max );
//        Log.v(TAG, " min :" +  min );
//        Log.v(TAG, " temp1 :" +  temp1 );
//        Log.v(TAG, " temp2 :" +  temp2 );

        // Saring sekali lagi ketika dalam keadaan berhenti, 0,2 atau lebih diakui sebagai nilai
        if(temp >= 0.2){
            // Tentukan arah menggunakan jarak yang ditempuh oleh accelerometer dan giroskop
            // Bagaimana cara bergerak pada sumbu x, y dari peta
            // TODO: Saat ini, ia hanya bergerak pada sudut 90 derajat,
            //  jadi Anda perlu menghitung sudutnya sehingga dapat bergerak secara diagonal.
            distance = temp; // 바꾼 방식.
//            distance += temp; // 기존 방식. x축 + 방향 distance 축적 방식
            IsMoved = true;
        }
    }

    // Berfungsi untuk menerima nilai percepatan
    public void setAcc(float _acc_X, float _acc_Y, float _acc_Z){
        acc_X = _acc_X;
        acc_Y = _acc_Y;
        acc_Z = _acc_Z;
    }


    public double getDistance() {

        double returnValue;
        returnValue = distance;
        distance = 0;

        return returnValue;
    }
    public void setDistance(double _distance) { distance = _distance; }
    public boolean getIsMoved() { return IsMoved; }
    public void setIsMoved(boolean _isMoved) { IsMoved = _isMoved; }
}
