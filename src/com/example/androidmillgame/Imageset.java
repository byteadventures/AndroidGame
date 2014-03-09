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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
//import android.util.Log;

//--------------------------------------------------------------------------------------------------
public class Imageset{
	//private static final String TAG = Imageset.class.getSimpleName();
    private Bitmap img;//...............................................Image.for.every.enum
    private HashMap <String,Bitmap[]> imgsetdict = new HashMap <String, Bitmap[] >();
    
    public Imageset(Context context){//..................................................enum.constructor
        try{
            img = BitmapFactory.decodeResource(context.getResources(), R.drawable.imageset);
            imgsetdict.put("imageset", this.createImageSet(img, 56, 55, 20));
        }
        catch (Exception e) {
        	System.out.println("Error during imageset loading!");
        }
    }

//----------------------------------------------------------------------Create-sub-images-from-sheet
    public Bitmap[] createImageSet(Bitmap tmpimg, int offsetx, int offsety, int cnt){
        int width=tmpimg.getWidth();
        int heigh=tmpimg.getHeight();
        int subImagecnt=0;
        Bitmap[] Imageset= new Bitmap[cnt];
        try{
            for (int n=0;n<=heigh/offsety-1;n++){
                for(int m =0; m<=width/offsetx-1;m++){
                    Imageset[subImagecnt]=Bitmap.createBitmap(tmpimg, m*offsetx,n*offsety,offsetx,offsety);
                    subImagecnt++;
                    //Log.d(TAG, "subimagecnt"+ subImagecnt);
                    if (subImagecnt==cnt) break;
                }
                if (subImagecnt==cnt) break;
            }
        }
        catch (Exception e){
            System.out.println("Error during subimage creation! "+e);
        }
        return Imageset;
    }

    public Bitmap[] getImageSet(String name){
        return imgsetdict.get(name);
    }
}
//--------------------------------------------------------------------------------------------------