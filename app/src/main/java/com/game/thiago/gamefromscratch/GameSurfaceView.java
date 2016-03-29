package com.game.thiago.gamefromscratch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements Runnable {
    private boolean isRunning = false;
    private Thread gameThread;
    private SurfaceHolder holder;

    private int screenWidth;
    private int screenHeight;
    private boolean touched = false;
    private Sprite[] sprites;

    private final static int MAX_FPS = 40; //desired fps
    private final static int FRAME_PERIOD = 1000 / MAX_FPS; // the frame period

    public GameSurfaceView(Context context) {
        super(context);

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                screenWidth = width;
                screenHeight = height;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

        });

        sprites = new Sprite[] {
                new Sprite(1, 1, BitmapFactory.decodeResource(this.getResources(), R.drawable.archer)),
                new Sprite(600, 400, BitmapFactory.decodeResource(this.getResources(), R.drawable.blueballoon)),
                new Sprite(1, 1, BitmapFactory.decodeResource(this.getResources(), R.drawable.arrow)),
                new Sprite(400, 800, BitmapFactory.decodeResource(this.getResources(), R.drawable.popballoon))
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touched = true;
        return super.onTouchEvent(event);
    }

    /**
     * Start or resume the game.
     */
    public void resume() {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Pause the game loop
     */
    public void pause() {
        isRunning = false;
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    private void moveBlueballon(){

        Sprite blueballoon = sprites[1];
        blueballoon.setVisible(true);
        blueballoon.x = screenWidth -100;

        if ((blueballoon.x < 0) || ((blueballoon.x + blueballoon.getWidth()) > screenWidth)) {
            blueballoon.directionX *= -1;
        }
        if ((blueballoon.y < 0) || ((blueballoon.y + blueballoon.getHeight()) > screenHeight)) {
            blueballoon.directionY *= -1;
        }

        blueballoon.y += (blueballoon.directionY * blueballoon.getSpeed());
        sprites[1] = blueballoon;
    }

    private void moveAcher(){

        Sprite archer = sprites[0];
        archer.setVisible(true);
        if ((archer.y < 0) || ((archer.y + archer.getHeight()) > screenHeight)) {
            archer.directionY *= -1;
        }
        archer.y += (archer.directionY * archer.getSpeed());
        sprites[0] = archer;

    }

    private void moveArrow(){

        Sprite arrow = sprites[2];
        if (touched && arrow.isVisible() == false){
            sprites[3].setVisible(false);
            arrow.setVisible(true);
            arrow.y = sprites[0].y; //Archer position
            arrow.x = sprites[0].x;
        }

        if ((arrow.x < 0) || ((arrow.x + arrow.getWidth()) > screenWidth)) {
            //arrow.directionX *= -1;
            arrow.setVisible(false);
        }
        if ((arrow.y < 0) || ((arrow.y + arrow.getHeight()) > screenHeight)) {
            //arrow.directionY *= -1;
            arrow.setVisible(false);
        }
        sprites[2] = arrow;

        arrow.x += (arrow.directionX * arrow.getSpeed());

    }

    private void ballonColision(){
        Sprite arrow = sprites[2];
        Sprite blueballoon = sprites[1];
        Sprite popballoon = sprites[3];
        float blueballon_y_max = blueballoon.y + blueballoon.getHeight();
        float blueballon_y_min = blueballoon.y;

        float arrow_x_max = arrow.x + arrow.getWidth();


        if (
            arrow.y <  blueballon_y_max
            && arrow.y > blueballon_y_min
            && blueballoon.x < arrow_x_max
            && arrow.isVisible()

        ){
            arrow.setVisible(false);
            blueballoon.setVisible(false);
            popballoon.x = blueballoon.x;
            popballoon.y = blueballoon.y;
            popballoon.setVisible(true);
        }

        sprites[1] = blueballoon;
        sprites[2] = arrow;
        sprites[3] = popballoon;
    }

    protected void step() {

        moveBlueballon();
        moveAcher();
        moveArrow();
        ballonColision();

        touched = false;

    }

    protected void render(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (int index = 0, length = sprites.length; index < length; index++) {
            Paint p = null;
            if (sprites[index].isVisible()) {
                canvas.drawBitmap(sprites[index].getImage(), sprites[index].x, sprites[index].y, p);
            }

        }
    }

    @Override
    public void run() {
        while(isRunning) {
            // We need to make sure that the surface is ready
            if (! holder.getSurface().isValid()) {
                continue;
            }
            long started = System.currentTimeMillis();

            // update
            step();
            // draw
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                render(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            float deltaTime = (System.currentTimeMillis() - started);
            int sleepTime = (int) (FRAME_PERIOD - deltaTime);
            if (sleepTime > 0) {
                try {
                    gameThread.sleep(sleepTime);
                }
                catch (InterruptedException e) {
                }
            }
            while (sleepTime < 0) {
                step();
                sleepTime += FRAME_PERIOD;
            }
        }
    }


}
