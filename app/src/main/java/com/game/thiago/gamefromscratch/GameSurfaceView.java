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

    class Sprite {
        int x;
        int y;
        int directionX = 1;
        int directionY = 1;
        int speed = 10;
        int color = 0;
        boolean visible = false;
        Bitmap image;

        public Sprite(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Sprite(int x, int y, Bitmap image) {
            this(x, y);
            this.image = image;
        }

        public Sprite(int x, int y, Bitmap image, int color) {
            this(x, y, image);
            this.color = color;
        }
    }

    private void moveBlueballon(){

        Sprite blueballoon = sprites[1];
        blueballoon.visible = true;
        blueballoon.x = screenWidth -100;

        if ((blueballoon.x < 0) || ((blueballoon.x + blueballoon.image.getWidth()) > screenWidth)) {
            blueballoon.directionX *= -1;
        }
        if ((blueballoon.y < 0) || ((blueballoon.y + blueballoon.image.getHeight()) > screenHeight)) {
            blueballoon.directionY *= -1;
        }

        blueballoon.y += (blueballoon.directionY * blueballoon.speed);
        sprites[1] = blueballoon;
    }

    private void moveAcher(){

        Sprite archer = sprites[0];
        archer.visible = true;
        if ((archer.y < 0) || ((archer.y + archer.image.getHeight()) > screenHeight)) {
            archer.directionY *= -1;
        }
        archer.y += (archer.directionY * archer.speed);
        sprites[0] = archer;

    }

    private void moveArrow(){

        Sprite arrow = sprites[2];
        if (touched && arrow.visible == false){
            sprites[3].visible = false;
            arrow.visible = true;
            arrow.y = sprites[0].y; //Archer position
            arrow.x = sprites[0].x;
        }

        if ((arrow.x < 0) || ((arrow.x + arrow.image.getWidth()) > screenWidth)) {
            //arrow.directionX *= -1;
            arrow.visible = false;
        }
        if ((arrow.y < 0) || ((arrow.y + arrow.image.getHeight()) > screenHeight)) {
            //arrow.directionY *= -1;
            arrow.visible = false;
        }
        sprites[2] = arrow;

        arrow.x += (arrow.directionX * arrow.speed);

    }

    private void ballonColision(){
        Sprite arrow = sprites[2];
        Sprite blueballoon = sprites[1];
        Sprite popballoon = sprites[3];
        float blueballon_y_max = blueballoon.y + blueballoon.image.getHeight();
        float blueballon_y_min = blueballoon.y;

        float arrow_x_max = arrow.x + arrow.image.getWidth();


        if (
            arrow.y <  blueballon_y_max
            && arrow.y > blueballon_y_min
            && blueballoon.x < arrow_x_max
            && arrow.visible

        ){
            arrow.visible = false;
            blueballoon.visible = false;
            popballoon.x = blueballoon.x;
            popballoon.y = blueballoon.y;
            popballoon.visible = true;
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
        canvas.drawColor(Color.BLACK);
        for (int index = 0, length = sprites.length; index < length; index++) {
            Paint p = null;
            if (sprites[index].color != 0) {
                p = new Paint();
                ColorFilter filter = new LightingColorFilter(sprites[index].color, 0);
                p.setColorFilter(filter);
            }
            if (sprites[index].visible) {
                canvas.drawBitmap(sprites[index].image, sprites[index].x, sprites[index].y, p);
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