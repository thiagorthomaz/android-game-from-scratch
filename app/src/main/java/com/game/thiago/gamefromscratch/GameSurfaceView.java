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
import com.game.thiago.gamefromscratch.Model.Archer;
import com.game.thiago.gamefromscratch.Model.ArcherDAO;
import com.game.thiago.gamefromscratch.Model.Arrow;
import com.game.thiago.gamefromscratch.Model.ArrowDAO;
import com.game.thiago.gamefromscratch.Model.Blueballoon;
import com.game.thiago.gamefromscratch.Model.BlueballoonDAO;
import com.game.thiago.gamefromscratch.Model.PopballonDAO;
import com.game.thiago.gamefromscratch.Model.Sprite;

public class GameSurfaceView extends SurfaceView implements Runnable {
    private boolean isRunning = false;
    private Thread gameThread;
    private SurfaceHolder holder;

    private int screenWidth;
    private int screenHeight;
    private boolean touched = false;

    private ArcherDAO archer_dao;
    private Archer archer;
    private ArrowDAO arrow_dao;
    private BlueballoonDAO blueballoon_dao;
    private PopballonDAO popballoon_dao;

    private final static int MAX_FPS = 60; //desired fps
    private final static int FRAME_PERIOD = 1000 / MAX_FPS; // the frame period

    public GameSurfaceView(final Context context) {
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

        this.archer_dao = new ArcherDAO(context);
        this.archer = this.archer_dao.getModel();

        this.blueballoon_dao = new BlueballoonDAO(context);
        this.arrow_dao = new ArrowDAO(context);
        this.popballoon_dao = new PopballonDAO(context);

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

    private void moveAcher(){

        try {

            if ((this.archer.getY() < 0) || ((this.archer.getY() + this.archer.getHeight()) > screenHeight)) {
                this.archer.directionY *= -1;
            }

            archer.setY(this.archer.getY() + (this.archer.directionY * this.archer.getSpeed()));
            this.archer_dao.updateSprite(this.archer);

        }catch (SpriteException se) {
            Log.i("SpriteException", "moveAcher: " + se.getMessage());
        }


    }

    private void moveArrow(){

        try {

            for (Sprite arrow : this.arrow_dao.getSprites()) {
                if (arrow instanceof Arrow) {

                    if ((arrow.getX() < 0) || ((arrow.getX() + arrow.getWidth()) > screenWidth)) {
                        arrow.setVisible(false);
                    }

                    if ((arrow.getY() < 0) || ((arrow.getY() + arrow.getHeight()) > screenHeight)) {
                        arrow.setVisible(false);
                    }

                    arrow.setX(arrow.getX() + (arrow.directionX * arrow.getSpeed()));
                    this.arrow_dao.updateSprite(arrow);
                }
            }
        }catch (SpriteException se) {
            Log.i("SpriteException", "moveArrow: " + se.getMessage());
        }

    }

    private boolean isCollision(Sprite s, Sprite collide_with){

        if (s.getX() < (collide_with.getX() + collide_with.getWidth()) &&
            (s.getX() + s.getWidth()) > collide_with.getX() &&
            s.getY() < (collide_with.getY() + collide_with.getHeight()) &&
            (s.getY() + s.getHeight()) > collide_with.getY()
        ) {
            return true;
        }
        return false;

    }
    private void ballonCollision(){

        try{

            for (Sprite arrow : this.arrow_dao.getSprites()) {
                if (arrow instanceof Arrow) {
                    for (Sprite blueballoon : this.blueballoon_dao.getSprites()) {

                        if ( blueballoon instanceof Blueballoon &&
                            this.isCollision(arrow, blueballoon)) {
                            Sprite popballoon = this.popballoon_dao.getModel();
                            arrow.setVisible(false);
                            blueballoon.setVisible(false);
                            popballoon.setVisible(true);
                            popballoon.setX(blueballoon.getX());
                            popballoon.setY(blueballoon.getY());
                            this.blueballoon_dao.removeSprite(blueballoon);
                            this.blueballoon_dao.updateSprite(popballoon);
                            this.arrow_dao.removeSprite(arrow);
                        }
                    }
                }
            }

        }catch (SpriteException se){
            Log.i("SpriteException", "ballonColision: " + se.getMessage());
        }

    }

    private void createArrow(){
        if (touched) {
            Arrow arrow = (Arrow)arrow_dao.createArrow(this.archer);
        }
    }

    protected void update() {

        moveAcher();
        createArrow();
        moveArrow();
        ballonCollision();

        touched = false;

    }

    protected void render(Canvas canvas) {
        canvas.drawColor(Color.GREEN);

        for (Sprite b : this.blueballoon_dao.getSprites()) {
            Paint p = null;
            if (b.isVisible()) {
                canvas.drawBitmap(b.getImage(), b.getX(), b.getY(), p);
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
