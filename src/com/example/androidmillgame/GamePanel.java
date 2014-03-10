/*
 *Copyright (C) 2014  Zoltán Bíró

 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.

 *You should have received a copy of the GNU General Public License
 *along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//--------------------------------------------------------------------------------------------------
package com.example.androidmillgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

//--------------------------------------------------------------------------------------------------
public class GamePanel extends View implements Runnable {

    private  int     PWIDTH          = 600;                           // ...........................................size.of.panel
    private  int     PHEIGHT         = 600;

    // ........................................no.of.frames.that.can.be.skipped.in.any.one.animation.loop
    // ...................................................i.e.the.games.state.is.updated.but.not.rendered
    private static final int     MAX_FRAME_SKIPS = 5;

    private Thread               animator;                                        // ................................the.thread.that.performs.the.animation
    private volatile boolean     running         = false;                         // ....................used.to.stop.the.animation.thread
    private volatile boolean     isPaused        = false;

    private final long           period;                                          // ................................period.between.drawing.in._nanosecs_
    private long                 gameStartTime;                                   // .............................................
                                                                                   // when.the.game.started

    // .............................................................................off-screen.rendering
    private Canvas               dbg;
    private Bitmap               dbImage         = null;

    private final GameController GC;
    private final HUD            Display;
    private DragPlayer           DraggablePoint;
    private SmartJointFactory    generatedJoints;

    int                          xforreset       = 0;
    int                          yforreset       = 0;
    private Images               imgres;
    private static final String  TAG             = Imageset.class.getSimpleName();

    // --------------------------------------------------------------------------------------------------
    public GamePanel(Context context, long period) {
        super(context);
        this.period = period;
  
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display wsize = wm.getDefaultDisplay();
        Point size = new Point();
        wsize.getSize(size);

        PWIDTH = size.x;
        PHEIGHT = size.y;
        Log.d(TAG, "PWIDTH: "+PWIDTH);//1024
        Log.d(TAG, "PHEIGHT: "+PHEIGHT);//552
        // setDoubleBuffered(false);
        // setBackground(Color.black);
        // setPreferredSize( new Dimension(PWIDTH, PHEIGHT));
        setFocusable(true);
        requestFocus();// ...........................the.JPane.now.has.focus,.so.receives.key.events
        imgres = new Images(context);// ...........................................................preload.resources
        generatedJoints = new SmartJointFactory(context);
        DraggablePoint = new DragPlayer();
        GC = new GameController(generatedJoints.Joints, DraggablePoint, context);
        Display = new HUD(300, 300, imgres.getImage("hud"));
        this.startGame();
    }

    // ...................................................................................event.listeners
    public boolean onTouchEvent(MotionEvent event) { // handle user action
        int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            GC.mPressed((int) event.getX(), (int) event.getY());
            xforreset = (int) event.getX();
            yforreset = (int) event.getY();
            return true;
        }
        if (action == MotionEvent.ACTION_UP) {
            GC.mReleased((int) event.getX(), (int) event.getY());
            return true;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (GC.millevent == 0)
                GC.UpdateMouseLocation((int) event.getX(), (int) event.getY());
            return true;
        }
        return super.onTouchEvent(event);
    }

    // ....................................wait.for.the.JPanel.to.be.added.to.the.JFrame.before.starting
    /*
     * @Override public void addNotify(){
     * super.addNotify();//.....................
     * ..................................creates.the.peer
     * startGame();//.........
     * ....................................................start.the.thread }
     */
    // ...................................................................initialise.and.start.the.thread
    private void startGame() {
        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start();
        }
    }

    // -------------------------------------------------------------------------game-life-cycle-methods
    // ....................................................called.by.the.JFrame's.window.listener.methods

    public void resumeGame() {// ....................called.when.the.JFrame.is.activated./.deiconified
        isPaused = false;
    }

    public void pauseGame() {// .....................called.when.the.JFrame.is.deactivated./.iconified
        isPaused = true;
    }

    public void stopGame() {// ......................................called.when.the.JFrame.is.closing
        running = false;
    }

    // --------------------------------------------------------------------------------------------------
    @Override
    // .......................................The.frame.of.the.animation.are.drawn.inside.the.while.loop.
    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        long excess = 0L;

        gameStartTime = System.currentTimeMillis();
        beforeTime = gameStartTime;

        running = true;

        while (running) {
            gameUpdate();

            this.postInvalidate();
            afterTime = System.currentTimeMillis();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) {// ..........................................some.time.left.in.this.cycle
                try {
                    Thread.sleep(sleepTime / 1000000L);// .......................................nano
                                                       // -> ms
                } catch (InterruptedException ex) {
                    System.out.println("Error during thread sleep " + ex);
                }
            }

            beforeTime = System.currentTimeMillis();

            // .....................................If frame animation is taking
            // too long, update the game state
            // ...........................................without rendering it,
            // to get the updates/sec nearer to
            // .................................................................................the
            // required FPS.

            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                excess -= period;
                gameUpdate(); // update state but don't render
                skips++;
            }
        }
        System.exit(0); // so window disappears
    }

    private void gameUpdate() {
        if (!isPaused && !GC.getGameOver()) {
            GC.Update();
            Display.Update(GC.getInfoForHUD());
        }
        if (GC.getGameOver()) {
            Display.Update(GC.getInfoForHUD());
            if ((xforreset < 280 + 40) && (xforreset > 280))
                if ((yforreset < 335 + 40) && (yforreset > 335))
                    GC.setReset();
        }
    }

    protected void onDraw(Canvas canvas) {// ...........use.active.rendering.to.put.the.buffered.image.on-screen

        try {
            // ..............................................draw.the.background:.use.the.image.or.a.black.color
            canvas.drawBitmap(imgres.getImage("background"), 0, 0, null);
            for (int i = 0; i < 24; i++) {// ......................................................render.joints
                generatedJoints.Joints[i].onDraw(canvas);
            }
            DraggablePoint.onDraw(canvas);
            Display.onDraw(canvas);
            if (GC.getGameOver())
                canvas.drawBitmap(imgres.getImage("resumeok"), 300 - 20, 335, null);
            else
                canvas.drawBitmap(imgres.getImage("resumenop"), 300 - 20, 335, null);
            // Log.d(TAG, "render now");
        } catch (Exception e) {
            System.out.println("Graphics context error: " + e);
        }
    }
}
// --------------------------------------------------------------------------------------------------