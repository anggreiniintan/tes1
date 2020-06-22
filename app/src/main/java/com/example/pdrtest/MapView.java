package com.example.pdrtest;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.InputStream;

public class MapView extends View {

    // path 배열의 빈공간 식별자
    private static int INF = 9999;

    public MapView(Context context) { super(context); }
    public MapView(Context context, AttributeSet att)  { super(context, att); }
    public MapView(Context context, AttributeSet att, int re) { super(context, att, re); }

    // Memetakan variabel pemuatan gambar
    private Bitmap floor_eighth;
    private Bitmap floor_seventh;
    private Bitmap imageSize;
    private int floor;

    // Variabel koordinat pengguna
    public float  x1=500,y1=700; // Harus diganti sesuai QRCODE bbut idunno how.
    public float addX = 0, addY = 0;   // Konversi koordinat waktu-nyata
    float resultX;
    float resultY;

    PointF pointF;

//    public float posisiawalx (){
//        if(MainActivity.resultposisi.getText().equals("D201")){
//            x1 = 120;
//        }else if(MainActivity.resultposisi.getText().equals("D202")){
//            x1 = 520;
//        } else {
//            x1 = 0;
//        }
//    return x1;
//    }
//
//    public float posisiawaly (float y){
//        if(MainActivity.resultposisi.getText().equals("D201")){
//            y = 720;
//        }else if(MainActivity.resultposisi.getText().equals("D202")){
//            y = 100;
//        }
//        return  y;
//
//    }


    @Override
    public  void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // paint
        Paint paint  = new Paint();
        paint.setColor(Color.rgb(255,94,0));
        paint.setTextSize(50);
        /*
         *  user show 구간
         */

//        float posx = posisiawalx();
//        float posy =posisiawaly(y1);
//            posx = posx + 40 * addX;
//            posy = posy + 40 * addY;

//            canvas.drawCircle(posx, posy, 10, paint);

        x1 = x1 + 40 * addX;
        y1 = y1 + 40  * addY;
        canvas.drawCircle(x1, y1, 10, paint);


        Log.d("x1", "x1" + x1);
        Log.d("y1", "y1" + y1);

//        Log.d("posx1", "posx1" + posx);
//        Log.d("posy1", "posy1" + posy);


        canvas.drawText("x: " + x1 + "\n y: " + y1, 200, 900, paint);
        canvas.drawText("addX: " + addX + "\n addY: " + addY, 200, 970, paint);

        addX = 0;
        addY = 0;
        canvas.save(); // Simpan kanvas
        canvas.restore(); // Kembalikan kanvas

    }

    public void setX(float _x1) {  addX = _x1;}
    public void setY(float _y1) {  addY = _y1;}

    public void setStartX(float _x1) {  x1 = _x1; addX = 0;}
    public void setStartY(float _y1) {  y1 = _y1; addY = 0;}

}
