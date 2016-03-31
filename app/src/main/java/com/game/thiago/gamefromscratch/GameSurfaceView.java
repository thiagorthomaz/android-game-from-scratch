package com.game.thiago.gamefromscratch;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import com.game.thiago.gamefromscratch.Exceptions.SpriteException;

public class GameSurfaceView extends SurfaceView implements Runnable {
    private boolean isRunning = false;
    private Thread gameThread;
    private SurfaceHolder holder;

    private int screenWidth;
    private int screenHeight;
    private boolean touched = false;

    private ArrayList<Sprite> sprites = new ArrayList<Sprite>();

    private final static int MAX_FPS = 60; //desired fps
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

        this.sprites.add(new Sprite(1, 1, 60, BitmapFactory.decodeResource(this.getResources(), R.drawable.archer)));
        this.sprites.add(new Sprite(2, 1, 60, BitmapFactory.decodeResource(this.getResources(), R.drawable.blueballoon)));
        this.sprites.add(new Sprite(3, 1, 1, BitmapFactory.decodeResource(this.getResources(), R.drawable.arrow)));
        this.sprites.add(new Sprite(4, 400, 800, BitmapFactory.decodeResource(this.getResources(), R.drawable.popballoon)));

    }

    public Sprite findSprite(int id) throws SpriteException{

        for (Sprite s : this.sprites) {
            if (s.getId() == id){
                return s;
            }
        }
        throw new SpriteException("Sprite not found!");

    }

    public Sprite updateSprite(int id, Sprite sprite) throws SpriteException{

        for (Sprite s : this.sprites) {
            if (s.getId() == id){
                int i = this.sprites.indexOf(s);
                this.sprites.set(i, sprite);
                return this.findSprite(s.getId());
            }
        }

        throw new SpriteException("Sprite not found, update failed!");

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

    private void prepareBlueballons(){

        try{
            Sprite blueballoon = this.findSprite(2);
            blueballoon.setVisible(true);

            blueballoon.x = screenWidth -100;

            if ((blueballoon.x < 0) || ((blueballoon.x + blueballoon.getWidth()) > screenWidth)) {
                blueballoon.directionX *= -1;
            }
            if ((blueballoon.y < 0) || ((blueballoon.y + blueballoon.getHeight()) > screenHeight)) {
                blueballoon.directionY *= -1;
            }

            this.updateSprite(2, blueballoon);

        }catch (SpriteException se) {
            Log.i("SpriteException", "prepareBlueballons: " + se.getMessage());
        }

    }

    private void moveAcher(){

        try {

            Sprite archer = this.findSprite(1);
            archer.setVisible(true);
            //if ((archer.y < 0) || ((archer.y + archer.getHeight()) > screenHeight)) {
            //    archer.directionY *= -1;
            //}
            //archer.y += (archer.directionY * archer.getSpeed());

            this.updateSprite(1, archer);

        }catch (SpriteException se) {
            Log.i("SpriteException", "moveAcher: " + se.getMessage());
        }


    }

    private void moveArrow(){

        try {

            //Sprite arrow = this.sprites.get(2);
            Sprite arrow = this.findSprite(3);
            if (touched && arrow.isVisible() == false){
                arrow.setVisible(true);
                //get archer position
                arrow.y = this.sprites.get(0).y;
                arrow.x = this.sprites.get(0).x;
            }

            if ((arrow.x < 0) || ((arrow.x + arrow.getWidth()) > screenWidth)) {
                arrow.setVisible(false);
                //this.sprites.remove(2);

            }
            if ((arrow.y < 0) || ((arrow.y + arrow.getHeight()) > screenHeight)) {
                arrow.setVisible(false);
                //this.sprites.remove(2);
            }

            arrow.x += (arrow.directionX * arrow.getSpeed());
            this.updateSprite(3, arrow);

        }catch (SpriteException se) {
            Log.i("SpriteException", "moveArrow: " + se.getMessage());
        }

    }

    private void ballonColision(){
        Sprite blueballoon = this.sprites.get(1);
        Sprite arrow = this.sprites.get(2);
        Sprite popballoon = this.sprites.get(3);

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

        this.sprites.set(1, blueballoon);
        this.sprites.set(2, arrow);
        this.sprites.set(3, popballoon);
    }

    protected void update() {

        prepareBlueballons();
        moveAcher();
        moveArrow();
        ballonColision();

        touched = false;

    }

    protected void render(Canvas canvas) {
        canvas.drawColor(Color.GREEN);
        for (Sprite s : this.sprites) {
            if (s.isVisible()) {
                Paint p = null;
                canvas.drawBitmap(s.getImage(), s.x, s.y, p);
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
            this.update();
            this.draw();
        }
    }

    public void draw(){
        long started = System.currentTimeMillis();
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
            this.update();
            sleepTime += FRAME_PERIOD;
        }
    }


}
