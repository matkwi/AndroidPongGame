package com.example.firstgame;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Canvas canvas;
    SquashCourtView squashCourtView;

    //For getting display details like the number of pixels
    Display display;
    Point size;
    int screenWidth;
    int screenHeight;

    //Game objects
    int racketWidth;
    int racketHeight;
    Point racketPosition;
    Point ballPosition;
    int ballWidth;

    //for ball movement
    boolean ballIsMovingLeft;
    boolean ballIsMovingRight;
    boolean ballIsMovingUp;
    boolean ballIsMovingDown;

    //for racket movement
    boolean racketIsMovingLeft;
    boolean racketIsMovingRight;

    //stats
    long lastFrameTime;
    int fps;
    int score;
    int lives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        squashCourtView = new SquashCourtView(this, size.x, size.y);
        setContentView(squashCourtView);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Could this be an object with getters and setters
        //Don't want just anyone changing screen size.
        //Get the screen size in pixels
//        display = getWindowManager().getDefaultDisplay();
//        size = new Point();
//        display.getSize(size);
//        screenWidth = size.x;
//        screenHeight = size.y;
//
//        //The game objects
//        racketPosition = new Point();
//        racketPosition.x = screenWidth / 2;
//        racketPosition.y = screenHeight - 20;
//        racketWidth = screenWidth / 8;
//        racketHeight = 10;
//        ballWidth = screenWidth / 35;
//        ballPosition = new Point();
//        ballPosition.x = screenWidth / 2;
//        ballPosition.y = 1 + ballWidth;
//        lives = 3;



//        ImageView ourFrame = (ImageView) findViewById(R.id.imageView);
//        Bitmap ourBitmap = Bitmap.createBitmap(1080,1920, Bitmap.Config.ARGB_8888);
//        Canvas ourCanvas = new Canvas(ourBitmap);
//        Paint paint = new Paint();
//
//        paint.setColor(Color.argb(255, 255, 255, 255));
//
//        ourCanvas.drawColor(Color.BLACK);
//
//        ourCanvas.drawLine(0, 0, 500, 500, paint);
//
//
//        ourCanvas.drawPoint(300, 500, paint);
//
//        ourCanvas.drawCircle(800, 800, 100, paint);
//
//        ourFrame.setImageBitmap(ourBitmap);
    }

    @Override
    protected void onStop() {
        super.onStop();
        while (true) {
            squashCourtView.pause();
            break;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        squashCourtView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        squashCourtView.resume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            squashCourtView.pause();
            finish();
            return true;
        }
        return false;
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//        return false;
//    }
}
