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
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

//---------------------------------------------------------------------------------------------------------------------
public class GamePanel extends View implements Runnable {

    protected static int PWIDTH = 600; // .................................................................size.of.panel
    protected static int PHEIGHT = 600;

// ...........................................................no.of.frames.that.can.be.skipped.in.any.one.animation.loop
// ......................................................................i.e.the.games.state.is.updated.but.not.rendered
    private static final int MAX_FRAME_SKIPS = 5;

    private Thread animator; // .................................................the.thread.that.performs.the.animation
    private volatile boolean running = false; // ......................................used.to.stop.the.animation.thread
    private volatile boolean isPaused = false;

    private final long period; // ..................................................period.between.drawing.in._nanosecs_
    private long gameStartTime; // ............................................................// when.the.game.started
// .................................................................................................off-screen.rendering

    private final GameController GC;
    private final HUD Display;
    private DragPlayer DraggablePoint;
    private SmartJointFactory generatedJoints;

    int xforreset = 0;
    int yforreset = 0;
    private Images imgres;
    private static final String TAG = Imageset.class.getSimpleName();

    protected static float dpi;
    protected static float scale;
    private Rect backgroundRect;
    private Rect background;
    private Rect resumeRect;

    // ----------------------------------------------------------------------------------------------------------------
    public GamePanel(Context context, long period) {
        super(context);
        this.period = period;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display wsize = wm.getDefaultDisplay();
        Point size = new Point();
        wsize.getSize(size);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wsize.getMetrics(displayMetrics);
        this.dpi = displayMetrics.densityDpi;
        this.PWIDTH = size.x;
        this.PHEIGHT = size.y;

        setFocusable(true);
        requestFocus();// ...............................................the.JPane.now.has.focus,.so.receives.key.events
        imgres = new Images(context);// ..............................................................preload.resources
        generatedJoints = new SmartJointFactory(context);
        DraggablePoint = new DragPlayer();
        backgroundRect = new Rect(PWIDTH / 2 - PHEIGHT / 2, 0, PWIDTH / 2 + PHEIGHT / 2, PHEIGHT);
        background = new Rect(0, 0, imgres.getImage("background").getWidth(), imgres.getImage("background").getHeight());
        scale = imgres.getImage("background").getHeight() / PHEIGHT;
        int resumescaled = imgres.getImage("resumenop").getWidth();
        resumeRect = new Rect(PWIDTH / 2 - resumescaled / 2, PHEIGHT / 2 - resumescaled / 2 + Pd2px.pd2px(50), PWIDTH
                / 2 + resumescaled / 2, PHEIGHT / 2 + resumescaled / 2 + Pd2px.pd2px(50));
        GC = new GameController(generatedJoints.Joints, DraggablePoint, context);
        Display = new HUD(PWIDTH / 2, PHEIGHT / 2, imgres.getImage("hud"));
        this.startGame();
    }

//......................................................................................................event.listeners
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
            if (GC.millevent == 0) GC.UpdateMouseLocation((int) event.getX(), (int) event.getY());
            return true;
        }
        return super.onTouchEvent(event);
    }

//.........................................................wait.for.the.JPanel.to.be.added.to.the.JFrame.before.starting
    private void startGame() {
        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start();
        }
    }

// ----------------------------------------------------------------------------------------------game-life-cycle-methods
// .......................................................................called.by.the.JFrame's.window.listener.methods

    public void resumeGame() {// .....................................called.when.the.JFrame.is.activated./.deiconified
        isPaused = false;
    }

    public void pauseGame() {// ......................................called.when.the.JFrame.is.deactivated./.iconified
        isPaused = true;
    }

    public void stopGame() {// ........................................................called.when.the.JFrame.is.closing
        running = false;
    }

//---------------------------------------------------------------------------------------------------------------------
    @Override
//...........................................................The.frame.of.the.animation.are.drawn.inside.the.while.loop.
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
            if (sleepTime > 0) {// ........................................................some.time.left.in.this.cycle
                try {
                    Thread.sleep(sleepTime / 1000000L);// ...................................................nano-> ms
                }
                catch (InterruptedException ex) {
                    System.out.println("Error during thread sleep " + ex);
                }
            }
            beforeTime = System.currentTimeMillis();
            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                excess -= period;
                gameUpdate(); //...........................................................update.state.but.don't.render
                skips++;
            }
        }
        System.exit(0); //..........................................................................so.window.disappears
    }

    private void gameUpdate() {
        if (!isPaused && !GC.getGameOver()) {
            GC.Update();
            Display.Update(GC.getInfoForHUD());
        }
        if (GC.getGameOver()) {
            Display.Update(GC.getInfoForHUD());
            if (resumeRect.contains(xforreset, yforreset)) GC.setReset();
        }
    }

    protected void onDraw(Canvas canvas) {// ...................use.active.rendering.to.put.the.buffered.image.on-screen

        try {
//...................................................................draw.the.background:.use.the.image.or.a.black.color
            canvas.drawRGB(0, 0, 0);
            canvas.drawBitmap(imgres.getImage("background"), background, backgroundRect, null);
            for (int i = 0; i < 24; i++) {// ..............................................................render.joints
                generatedJoints.Joints[i].Draw(canvas);
            }
            DraggablePoint.Draw(canvas);
            Display.Draw(canvas);
            if (GC.getGameOver())
                canvas.drawBitmap(imgres.getImage("resumeok"), null, resumeRect, null);
            else canvas.drawBitmap(imgres.getImage("resumenop"), null, resumeRect, null);
        }
        catch (Exception e) {
            Log.d(TAG, "Graphics context error: " + e);
        }
    }
}
// ---------------------------------------------------------------------------------------------------------------------