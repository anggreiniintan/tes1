package com.example.pdrtest;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

public class MapView extends View {

    // path 배열의 빈공간 식별자
    private static int INF = 9999;

    public MapView(Context context) { super(context); }
    public MapView(Context context, AttributeSet att)  { super(context, att); }
    public MapView(Context context, AttributeSet att, int re) { super(context, att, re); }

    // 맵 이미지 로드 변수
    private Bitmap floor_eighth;
    private Bitmap floor_seventh;
    private Bitmap imageSize;
    private int floor;

    // 유저 좌표 변수
    public float x1 = 550.0f, y1 = 700.0f;
    public float addX = 0, addY = 0;   // 실시간 좌표 변환
    float resultX;
    float resultY;

    // 경로시 필요한 변수
    private int pathData[]; // 순차로 재배열된 경로 배열
    private Path path;  // 경로를 그릴 변수
    private boolean IsNaviOn; // 경로 탐색 여부
//    private ArrayList<BeaconWorker> bk; // 비콘 집합 -> 비콘 좌표 획득
    public float startX, startY;   // 경로 시작지점

    // imageCount
    private int imageCount = 0;

    public  void init()
    {
        IsNaviOn = false;
    }

    // 경로 탐색시 시작지점
    public void setStartPos()
    {
//        startX = bk.get(pathData[0]).getX();
//        startY = bk.get(pathData[0]).getY();
    }


//    public void setBeacon(ArrayList<BeaconWorker> bk)
//    {
//        this.bk = bk;
//    }

    @Override
    public  void onDraw(Canvas canvas)
    {

        // paint
        Paint paint  = new Paint();
        paint.setColor(Color.rgb(255,94,0));
        paint.setTextSize(50);

        /*
         *  map show 구간
         */
        // 사진 580 x 787 을 화면크기에 맞게 1.89배 조정
        if (floor == 7)
        {
//            floor_seventh = BitmapFactory.decodeResource(getResources(), R.drawable.floor_7);
//            imageSize = Bitmap.createScaledBitmap(floor_seventh,
//                    580 * 192 / 100,
//                    787 * 192 / 100,
//                    true);
//            canvas.drawBitmap(imageSize, -5 , -6 , null);
        }
        else if(floor == 8)
        {
//            floor_eighth = BitmapFactory.decodeResource(getResources(), R.drawable.floor_8);
//            imageSize = Bitmap.createScaledBitmap(floor_eighth,
//                    580 * 189 / 100,
//                    787 * 189 / 100,
//                    true);
//            canvas.drawBitmap(imageSize, 0 , 0 , null);
        }

        AssetManager am = getResources().getAssets() ;
        InputStream is = null ;


        // 맵뷰에 지도 그리기
//        try {
//            // 애셋 폴더에 저장된 field.png 열기.
//            is = am.open("office3.png") ;
//         // 입력스트림 is를 통해 field.png 을 Bitmap 객체로 변환.
//            Bitmap bm = BitmapFactory.decodeStream(is) ;
//         // 만들어진 Bitmap 객체를 맵뷰에 표시.
//            imageSize = Bitmap.createScaledBitmap(bm,
//                    bm.getWidth() - 50,
//                    bm.getHeight(),
//                    true);
//            canvas.drawBitmap(imageSize, 0 , 0 , null);
//         is.close() ;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // 이미지 객체 해제
//        if (is != null) {
//            try {
//                is.close() ;
//            } catch (Exception e) {
//                e.printStackTrace() ;
//            }
//        }


        /*
         *  경로 show 구간
         */
        // 매 실행시 path 초기화
        path = new Path();

        // paint 객체 준비
        Paint pathPaint = new Paint();
        pathPaint.setColor(Color.GREEN);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(10.0f);
        pathPaint.setStrokeCap(Paint.Cap.BUTT);
        pathPaint.setStrokeJoin(Paint.Join.MITER);

        // 네비 서비스 실행시만
        if(IsNaviOn) {

            // path 먼저 이어놓기
            path.moveTo(startX, startY);        // 시작점
            for (int i = 1; i < pathData.length; i++)
            {   // 나머지 경유지점 간선
//                if (pathData[i] != INF)
//                    path.lineTo(
//                            bk.get(pathData[i]).getX(),
//                            bk.get(pathData[i]).getY());
//                else break;
            }
        }

        // path 먼저 이어놓기
        path.moveTo(startX, startY);        // 시작점

        // 완성한 path 그리기
        canvas.drawPath(path, pathPaint);

        /*
         *  user show 구간
         */
        x1 = x1 + 40 * addX;
        y1 = y1 + 40 * addY;

        canvas.drawCircle(
                x1,
                y1,
                10,
                paint);

        canvas.drawText("x: " + x1 + "\n y: " + y1, 200, 900, paint);
        canvas.drawText("addX: " + addX + "\n addY: " + addY, 200, 970, paint);

        addX = 0;
        addY = 0;

    }



    public void setFloor(int _floor) { floor = _floor; }

    public void setX(float _x1) {  addX = _x1;}
    public void setY(float _y1) {  addY = _y1;}

    public void setStartX(float _x1) {  x1 = _x1; addX = 0;}
    public void setStartY(float _y1) {  y1 = _y1; addY = 0;}

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public void setPath(int _path[]) { pathData = _path; }
    public void setIsNaviOn(boolean naviOn) { IsNaviOn = naviOn; }
}
