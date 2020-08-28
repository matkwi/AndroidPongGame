package com.example.firstgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

class SquashCourtView extends SurfaceView implements Runnable {

    Canvas canvas;

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

    Thread ourThread = null;
    SurfaceHolder ourHolder;
    volatile boolean playingSquash;
    Paint paint;
    private boolean stopRacketLeft = false;
    private boolean stopRacketRight = false;

    @Override
    public void run() {
        while (playingSquash) {
            updateCourt();
            drawCourt();
            controlFPS();
        }
    }

    public void updateCourt() {
        if (racketIsMovingRight) {
            racketPosition.x = racketPosition.x + 10;
        }

        if (racketIsMovingLeft) {
            racketPosition.x = racketPosition.x - 10;
        }

        //detect collisions
        //hit right of screen
        if (ballPosition.x + ballWidth > screenWidth) {
            ballIsMovingLeft = true;
            ballIsMovingRight = false;
        }

        //hit left of screen
        if (ballPosition.x < 0) {
            ballIsMovingLeft = false;
            ballIsMovingRight = true;
        }

        //Edge of ball has hit bottom of screen
        if (ballPosition.y > screenHeight - ballWidth) {
            lives = lives - 1;
            if (lives == 0) {
                lives = 3;
                score = 0;
            }
            ballPosition.y = 1 + ballWidth; //back to top of screen

            //what horizontal direction should we use
            //for the next falling ball
            Random randomNumber = new Random();
            int startX = randomNumber.nextInt(screenWidth - ballWidth) + 1;
            ballPosition.x = startX + ballWidth;
            int ballDirection = randomNumber.nextInt(3);
            switch (ballDirection) {
                case 0:
                    ballIsMovingLeft = true;
                    ballIsMovingRight = false;
                    break;
                case 1:
                    ballIsMovingRight = true;
                    ballIsMovingLeft = false;
                    break;
                case 2:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = false;
                    break;
            }
        }

        //we hit the top of the screen
        if (ballPosition.y <= 0) {
            ballIsMovingDown = true;
            ballIsMovingUp = false;
            ballPosition.y = 1;
        }

        //depending upon the two directions we should
        //be moving in adjust our x any positions
        if (ballIsMovingDown) {
            ballPosition.y += 6;
        }

        if (ballIsMovingUp) {
            ballPosition.y -= 10;
        }

        if (ballIsMovingLeft) {
            ballPosition.x -= 12;
        }

        if (ballIsMovingRight) {
            ballPosition.x += 12;
        }

        //Has ball hit racket
        if (ballPosition.y + ballWidth >= (racketPosition.y - racketHeight / 2)) {
            int halfRacket = racketWidth / 2;
            if (ballPosition.x + ballWidth > (racketPosition.x - halfRacket) && ballPosition.x - ballWidth < (racketPosition.x + halfRacket)) {
                //rebound the ball vertically
                score++;
                ballIsMovingUp = true;
                ballIsMovingDown = false;
                //now decide how to rebound the ball horizontally
                if (ballPosition.x > racketPosition.x) {
                    ballIsMovingRight = true;
                    ballIsMovingLeft = false;
                } else {
                    ballIsMovingRight = false;
                    ballIsMovingLeft = true;
                }
            }
        }

        if (racketPosition.x + racketWidth > screenWidth) {
            racketIsMovingLeft = false;
            racketIsMovingRight = false;
            stopRacketRight = true;
        } else {
            stopRacketRight = false;
        }

        if (racketPosition.x - racketWidth < 0) {
            racketIsMovingLeft = false;
            racketIsMovingRight = false;
            stopRacketLeft = true;
        } else {
            stopRacketLeft = false;
        }


    }

    public void drawCourt() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            //Paint paint = new Paint();
            canvas.drawColor(Color.BLACK); //the background
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(45);
            canvas.drawText("Score:" + score + " Lives:" + lives + " fps:" + fps, 20, 40, paint);
            //Draw the squash racket
            canvas.drawRect(racketPosition.x - (racketWidth / 2), racketPosition.y - (racketHeight / 2), racketPosition.x + (racketWidth / 2), racketPosition.y + racketHeight, paint);
            //Draw the ball
            canvas.drawRect(ballPosition.x, ballPosition.y, ballPosition.x + ballWidth, ballPosition.y + ballWidth, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void controlFPS() {
        long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
        long timeToSleep = 15 - timeThisFrame;

        if (timeThisFrame > 0) {
            fps = (int) (1000 / timeThisFrame);
        }

        if (timeToSleep > 0) {
            try {
                ourThread.sleep(timeToSleep);
            } catch (InterruptedException e) {
            }
        }
        lastFrameTime = System.currentTimeMillis();
    }

    public void pause() {
        playingSquash = false;
        try {
            ourThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playingSquash = true;
        ourThread = new Thread(this);
        ourThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (motionEvent.getX() >= screenWidth / 2) {
                    if(!stopRacketRight) {
                        racketIsMovingRight = true;
                        racketIsMovingLeft = false;
                    }
                } else {
                    if(!stopRacketLeft) {
                        racketIsMovingRight = false;
                        racketIsMovingLeft = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                racketIsMovingRight = false;
                racketIsMovingLeft = false;
                break;
        }
        return true;
    }

    public SquashCourtView(Context context, int sizeX, int sizeY) {
        super(context);

        screenWidth = sizeX;
        screenHeight = sizeY;

        //The game objects
        racketPosition = new Point();
        racketPosition.x = screenWidth / 2;
        racketPosition.y = screenHeight - 20;
        racketWidth = screenWidth / 8;
        racketHeight = 10;
        ballWidth = screenWidth / 35;
        ballPosition = new Point();
        ballPosition.x = screenWidth / 2;
        ballPosition.y = 1 + ballWidth;
        lives = 3;





        ourHolder = getHolder();
        paint = new Paint();
        ballIsMovingDown = true;

        //Send the ball in random direction
        Random randomNumber = new Random();
        int ballDirection = randomNumber.nextInt(3);
        switch (ballDirection) {
            case 0:
                ballIsMovingLeft = true;
                ballIsMovingRight = false;
            case 1:
                ballIsMovingRight = true;
                ballIsMovingLeft = false;
                break;
            case 2:
                ballIsMovingLeft = false;
                ballIsMovingRight = false;
                break;
        }
    }
}
