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
import android.media.MediaPlayer;

//--------------------------------------------------------------------------------------------------
public class SoundEffect{


    private HashMap <String,MediaPlayer> soundeffects = new HashMap <String, MediaPlayer >();
    SoundEffect(Context context){//..................................................enum.constructor
        MediaPlayer MPlayer;
        try {
            MPlayer=MediaPlayer.create(context, R.raw.jump);
            soundeffects.put("jump", MPlayer);
            MPlayer=MediaPlayer.create(context, R.raw.mill);
            soundeffects.put("mill", MPlayer);
            MPlayer=MediaPlayer.create(context, R.raw.move);
            soundeffects.put("move", MPlayer);
            MPlayer=MediaPlayer.create(context, R.raw.placement);
            soundeffects.put("placement", MPlayer);
            MPlayer=MediaPlayer.create(context, R.raw.remove);
            soundeffects.put("remove", MPlayer);
            
        }
        catch (Exception e) {
            System.out.println("Error during sound loading! "+e);
        }
    }
//--------------------------------Play-or-Re-play-the-sound-effect-from-the-beginning,-by-rewinding.
       public void play(String name) {
           try{
               if (soundeffects.get(name).isPlaying()) soundeffects.get(name).stop();//..............Stop.the.player.if.it.is.still.running
               soundeffects.get(name).start();//............................................................Start.playing
           }
           catch(Exception e){
               System.out.println("Error during sound play! "+e);
           }
       }
}
//--------------------------------------------------------------------------------------------------