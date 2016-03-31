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
    private int id_archer = 1;
    private int id_arrow = 2;
    private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
    private ArrayList<Sprite> blueballons = new ArrayList<Sprite>();

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
                loadBlueBallons();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

        });

        this.sprites.add(new Sprite(this.id_archer, 1, 60, BitmapFactory.decodeResource(this.getResources(), R.drawable.archer)));
        this.sprites.add(new Sprite(this.id_arrow, 1, 1, BitmapFactory.decodeResource(this.getResources(), R.drawable.arrow)));
        this.sprites.add(new Sprite(4, 1, 60, BitmapFactory.decodeResource(this.getResources(), R.drawable.popballoon)));

    }

    private void loadBlueBallons(){

        this.blueballons.add(new Sprite(1, 900, 10, BitmapFactory.decodeResource(this.getResources(), R.drawable.blueballoon)));
        this.blueballons.add(new Sprite(2, 1000, 10, BitmapFactory.decodeResource(this.getResources(), R.drawable.blueballoon)));
        this.blueballons.add(new Sprite(3, 1000, 200, BitmapFactory.decodeResource(this.getResources(), R.drawable.blueballoon)));
        this.blueballons.add(new Sprite(4, 1000, 300, BitmapFactory.decodeResource(this.getResources(), R.drawable.blueballoon)));
        this.blueballons.add(new Sprite(6, 1000, 400, BitmapFactory.decodeResource(this.getResources(), R.drawable.blueballoon)));

    }
    public Sprite findSprite(int id) throws SpriteException{

        for (Sprite s : this.sprites) {
            if (s.getId() == id){
                return s;
            }
        }
        throw new SpriteException("Sprite not found!");

    }

    public Sprite findBlueballons(int id) throws SpriteException{

        for (Sprite b : this.blueballons) {
            if (b.getId() == id){
                return b;
            }
        }
        throw new SpriteException("Blueballon not found!");

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

    public Sprite updateBlueballon(int id, Sprite blueballon) throws SpriteException{
        for (Sprite b : this.blueballons) {
            if (b.getId() == id){
                int i = this.blueballons.indexOf(b);
                this.blueballons.set(i, blueballon);
                return this.findBlueballons(b.getId());
            }
        }

        throw new SpriteException("Blueballon not found, update failed!");
    }


    public Sprite removeBlueballon(int id, Sprite blueballon) throws SpriteException{
        for (Sprite b : this.blueballons) {
            if (b.getId() == id){
                int i = this.blueballons.indexOf(b);
                this.blueballons.remove(i);
                return this.findBlueballons(b.getId());
            }
        }

        throw new SpriteException("Blueballon not found, update failed!");
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

    private void drawBlueballons(){

        try{
            for (Sprite blueballoon : this.blueballons) {
                blueballoon.setVisible(true);

                if ((blueballoon.x < 0) || ((blueballoon.x + blueballoon.getWidth()) > screenWidth)) {
                    blueballoon.directionX *= -1;
                }
                if ((blueballoon.y < 0) || ((blueballoon.y + blueballoon.getHeight()) > screenHeight)) {
                    blueballoon.directionY *= -1;
                }

                this.updateBlueballon(blueballoon.getId(), blueballoon);
            }

        }catch (SpriteException se) {
            Log.i("SpriteException", "drawBlueballons: " + se.getMessage());
        }

    }

    private void moveAcher(){

        try {

            Sprite archer = this.findSprite(this.id_archer);
            archer.setVisible(true);
            if ((archer.y < 0) || ((archer.y + archer.getHeight()) > screenHeight)) {
                archer.directionY *= -1;
            }
            archer.y += (archer.directionY * archer.getSpeed());

            this.updateSprite(this.id_archer, archer);

        }catch (SpriteException se) {
            Log.i("SpriteException", "moveAcher: " + se.getMessage());
        }


    }

    private void moveArrow(){

        try {

            Sprite arrow = this.findSprite(this.id_arrow);
            if (touched && arrow.isVisible() == false){
                arrow.setVisible(true);
                //get archer position
                Sprite archer = this.findSprite(this.id_archer);
                arrow.y = archer.getY();
                arrow.x = archer.getX();
            }

            if ((arrow.x < 0) || ((arrow.x + arrow.getWidth()) > screenWidth)) {
                arrow.setVisible(false);
            }

            if ((arrow.y < 0) || ((arrow.y + arrow.getHeight()) > screenHeight)) {
                arrow.setVisible(false);
            }

            arrow.x += (arrow.directionX * arrow.getSpeed());
            this.updateSprite(this.id_arrow, arrow);


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

            Sprite arrow = this.findSprite(this.id_arrow);

            if (arrow.isVisible()) {
                for (Sprite blueballoon : this.blueballons) {

                    if (this.isCollision(arrow, blueballoon)) {
                        Sprite popballoon = this.findSprite(4);
                        arrow.setVisible(false);
                        blueballoon.setVisible(false);
                        popballoon.setVisible(true);
                        popballoon.setX(blueballoon.getX());
                        popballoon.setY(blueballoon.getY());
                        this.removeBlueballon(blueballoon.getId(), blueballoon);
                        this.updateSprite(4, popballoon);
                        this.updateSprite(this.id_arrow, arrow);
                    }
                }
            }

        }catch (SpriteException se){
            Log.i("SpriteException", "ballonColision: " + se.getMessage());
        }

    }

    protected void update() {

        drawBlueballons();
        moveAcher();
        moveArrow();
        ballonCollision();

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

        if (this.blueballons.size() > 0) {
            for (Sprite b : this.blueballons) {
                Paint p = null;
                canvas.drawBitmap(b.getImage(), b.x, b.y, p);
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
