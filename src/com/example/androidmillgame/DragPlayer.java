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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

//---------------------------------------------------------------------------------------------------------------------
public class DragPlayer {
    private static final String TAG = Imageset.class.getSimpleName();
    private boolean isActive = false;

    private int posx;
    private int posy;
    private int origx;
    private int origy;

    private Paint paint;

//---------------------------------------------------------------------------------------------------------------------
    public DragPlayer() {
        paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.MAGENTA);
    }

    public void setActive() {
        isActive = true;
    }

    public void setNoActive() {
        isActive = false;
    }

    public void Update(int x, int y) {
        posx = x;
        posy = y;
    }

//---------------------------------------------------------------------------------------------------------------------
    public void setOrig(int x, int y) {
        origx = x;
        origy = y;
    }

//---------------------------------------------------------------------------------------------------------------------
    public void Draw(Canvas canvas) {
        if (isActive) {
            try {
                canvas.drawLine(origx, origy, posx, posy, this.paint);
            }
            catch (Exception e) {
                Log.d(TAG, "Error during Draggable drawing! " + e);
            }
        }
    }
}
//--------------------------------------------------------------------------------------------------