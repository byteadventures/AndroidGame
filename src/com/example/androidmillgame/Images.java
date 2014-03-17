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

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

//---------------------------------------------------------------------------------------------------------------------

public class Images {
    private static final String TAG = Imageset.class.getSimpleName();
    private HashMap<String, Bitmap> imgdict = new HashMap<String, Bitmap>();

    public Images(Context context) {
        try {
            imgdict.put("background", BitmapFactory.decodeResource(context.getResources(), R.drawable.backgroundv2));
            imgdict.put("hud", BitmapFactory.decodeResource(context.getResources(), R.drawable.hud));
            imgdict.put("resumeok", BitmapFactory.decodeResource(context.getResources(), R.drawable.resumeok));
            imgdict.put("resumenop", BitmapFactory.decodeResource(context.getResources(), R.drawable.resumenop));
        }
        catch (Exception e) {

            Log.d(TAG, "Error during images loading! " + e);
        }
    }

    public Bitmap getImage(String name) {
        return imgdict.get(name);
    }
}
//----------------------------------------------------------------------------------------------------------------------