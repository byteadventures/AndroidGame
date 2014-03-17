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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

//---------------------------------------------------------------------------------------------------------------------
public class HUD {

    private boolean isActive = true;

    Bitmap image;

    Bitmap hudimage = null;

    private int angleplaced1 = 0;
    private int angleplaced2 = 0;
    private int angleremoved1 = 0;
    private int angleremoved2 = 0;

    private int origx;
    private int origy;
    private int diam;

    private Paint paint;
    private RectF rectf;
    private Rect hudRect;
    private Rect hud;

//---------------------------------------------------------------------------------------------------------------------
    public HUD(int x, int y, Bitmap image) {
        this.origx = x;
        this.origy = y;
        this.hudimage = image;
        diam = this.hudimage.getWidth();
        this.rectf = new RectF(origx - diam / 2 + Pd2px.pd2px(15), origy - diam / 2 + Pd2px.pd2px(15), origx + diam / 2
                - Pd2px.pd2px(15), origy + diam / 2 - Pd2px.pd2px(15));
        hudRect = new Rect(origx - hudimage.getWidth() / 2 + Pd2px.pd2px(10), origy - hudimage.getHeight() / 2
                + Pd2px.pd2px(10), origx + hudimage.getWidth() / 2 - Pd2px.pd2px(10), origy + hudimage.getHeight() / 2
                - Pd2px.pd2px(10));
        hud = new Rect(0, 0, hudimage.getWidth(), hudimage.getHeight());
        paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);
    }

    public void setActive() {
        isActive = true;
    }

    public void setNoActive() {
        isActive = false;
    }

    public void Update(int[] tmp) {
        angleplaced1 = 20 * tmp[0];
        angleplaced2 = 20 * tmp[1];
        angleremoved1 = 20 * tmp[2];
        angleremoved2 = 20 * tmp[3];
    }

    public void Reset() {
        angleplaced1 = 0;
        angleplaced2 = 0;
        angleremoved1 = 0;
        angleremoved2 = 0;
    }

    public void Draw(Canvas canvas) {
        if (isActive) {
            this.paint.setColor(Color.GRAY);
            canvas.drawArc(rectf, 0, 360, true, this.paint);
            this.paint.setColor(Color.GREEN);
            canvas.drawArc(rectf, 90 + angleplaced1, 180 - angleplaced1, true, this.paint);
            canvas.drawArc(rectf, -90, angleremoved1, true, this.paint);
            this.paint.setColor(Color.RED);
            canvas.drawArc(rectf, -90 + angleplaced2, 180 - angleplaced2, true, this.paint);
            canvas.drawArc(rectf, 90, angleremoved2, true, this.paint);
            canvas.drawBitmap(hudimage, hud, hudRect, null);
        }
    }
}
//----------------------------------------------------------------------------------------------------------------------