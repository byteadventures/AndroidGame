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
import android.util.Log;
//--------------------------------------------------------------------------------------------------
public class GameController {

    private int pressed=-1;
    private int released=-1;
    //private int clicked=-1;
    private int pressedx;
    private int pressedy;
    private int turn=1;

    private int cntplayer1=0;
    private int cntplayer2=0;
    private int cntplacement=0;
    private int cntplaced1=0;
    private int cntplaced2=0;
    private int cntremoved1=0;
    private int cntremoved2=0;

    private int player1phase=0;
    private int player2phase=0;
    private final PlayerSprite[] Joints;
    private final DragPlayer DraggablePoint;

    public int millevent=0;
    int[] mill2= new int [2];
    int[] mill1= new int[2];
    private SoundEffect sounds;
    private boolean gameover=false;

    private static final String TAG = Imageset.class.getSimpleName();
//--------------------------------------------------------------------------------------------------
    public GameController(PlayerSprite[] joints, DragPlayer draggable,Context context){
       Joints=joints;
       DraggablePoint=draggable;
       sounds = new SoundEffect(context);//...........................................preload.sounds
    }
  
    public boolean getGameOver(){return gameover;}
    
    public int[] getInfoForHUD(){
        int[] tmp={cntplaced1,cntplaced2,cntremoved1,cntremoved2};
        return tmp;
    }

    public void mPressed(int x, int y){ 
        int n=-1;
        int i =0;
        while ((n<0)&&(i<24)){//............................................search.for.pressed.joint
            n =  Joints[i].ItIsMe(x, y);
            i++;
        }
        if (n!=-1) {
            pressed=n;
            //Log.d(TAG, "pressed"+ pressed);
            released=-1; 
        }
        pressedx=x;
        pressedy=y;
        DraggablePoint.Update(pressedx,pressedy);//............................for.accurate.dragwork
        DraggablePoint.setOrig(x,y);
        DraggablePoint.setActive();
    }

    public void mReleased(int x, int y){
        int n=-1;
        int i =0;
        while ((n<0)&&(i<24)){
            n =  Joints[i].ItIsMe(x, y);
            i++;
        }
        DraggablePoint.setNoActive();
        if (n!=-1) released=n; 
    }

   /* public void mClicked(int x, int y){
        int n=-1;
        int i =0;
        while ((n<0)&&(i<24)){
            n =  Joints[i].ItIsMe(x, y);
            i++;
        }
        if (n!=-1) clicked=n;
    }*/

    public void setReset(){
        gameover=false;
        millevent=0;
        pressed=-1;
        released=-1;
        cntplayer1=0;
        cntplayer2=0;
        cntplacement=0;
        player1phase=0;
        player2phase=0;
        turn=1;
        cntplaced1=0;
        cntplaced2=0;
        cntremoved1=0;
        cntremoved2=0;
        for (int i=0;i<24;i++){
            Joints[i].resetMill();
            Joints[i].clearPlayer();
        }
    }


    public void Update(){
        if (pressed!=-1) {
            if((player1phase>=1)&&(player2phase>=1)) DraggablePoint.Update(pressedx,pressedy);
//.............................................................................................move
                this.MoveThings();
            } 
//......................................................................................handle.mills
        this.HandleMills();
//.........................................................................................placement
        if ((player1phase==0)&&(player2phase==0)&&(millevent==0)&&(pressed!=-1)&&(pressed==released)){
            if (Joints[pressed].getPlayer()==0){
                this.placement(); 
            }
        }
//.................................................................................game.phase.update
        if ((player1phase==0)&&(player2phase==0)&&(cntplacement==18)){
            player1phase=1;
            player2phase=1;
        }
        if ((player1phase==1)&&(cntplayer1==3)) player1phase=2;  
        if ((player2phase==1)&&(cntplayer2==3)) player2phase=2;
    }

    private void placement(){
        //Log.d(TAG, "turn: "+ turn);
        Joints[pressed].setPlayer(turn);
        Joints[pressed].Update();
        if (turn==1) {
            cntplayer1++;
            cntplaced1++;
        }
        if (turn==-2) {
            cntplayer2++;
            cntplaced2++;
        }
        cntplacement++;
        CheckForMill(pressed);
        pressed=-1;
        released=-1;
        sounds.play("placement");
        if (millevent==0) turn=~turn;
    }

    private void MoveThings(){
//.........................................................move.possible.when.there.is no mill.event
        if (millevent==0){
//...........................................................check.we.clicked on.the.appropiate.disk
            if (Joints[pressed].getPlayer()==turn){
//................................................................................do.this.in.phase1
                if (((player1phase==1)||(player2phase==1))&&((released!=-1)&&(pressed!=released))){
                    if (Joints[pressed].checkNeighbour(released)){
                        if (Joints[released].getPlayer()==0){
                            MovePoint();
                            sounds.play("move");
                        }   
                    }
                }
//.................................................................................do.this.in.phase2
                if (((player1phase==2)||(player2phase==2))&&((released!=-1)&&(pressed!=released))){
                    if (Joints[released].getPlayer()==0){
                        MovePoint();
                        sounds.play("jump");
                    }
                }
            }
        }
    }

    private void HandleMills(){
        if ((millevent>0)&&(pressed!=-1)&&(pressed==released)){
            if (Joints[pressed].getPlayer()==~turn){
                if (CheckFreePoint(Joints[pressed].getPlayer())){ 
                    if (!Joints[pressed].checkMillStatus()){
                        RemoveMill();
                        pressed=-1;
                        released=-1;
                    } 
                }
                else {
                    RemoveMill(); 
                    ClearMill(pressed); 
                    pressed=-1;
                    released=-1;
                }
            }
            pressed=-1;
            released=-1;
        } 
    }

    public void ClearMill(int pressed){
        mill1=Joints[pressed].getMill1();
        if ((Joints[mill1[0]].checkMillStatus())&&(Joints[mill1[1]].checkMillStatus())&&
            (Joints[pressed].checkMillStatus())){
            Joints[pressed].clearMill();
            Joints[mill1[0]].clearMill();
            Joints[mill1[1]].clearMill();
        }
        mill2=Joints[pressed].getMill2();
        if ((Joints[mill2[0]].checkMillStatus())&&(Joints[mill2[1]].checkMillStatus())&&
            (Joints[pressed].checkMillStatus())){
            Joints[pressed].clearMill();
            Joints[mill2[0]].clearMill();
            Joints[mill2[1]].clearMill();
        }
    }

    public void CheckForMill(int pressed){
        mill1=Joints[pressed].getMill1();
        if ((Joints[pressed].getPlayer()==Joints[mill1[0]].getPlayer())&&
        (Joints[pressed].getPlayer()==Joints[mill1[1]].getPlayer())){
            Joints[pressed].setMill();
            Joints[mill1[0]].setMill();
            Joints[mill1[1]].setMill();
            sounds.play("mill");;
            millevent++; 
        }
        mill2=Joints[pressed].getMill2();
        if ((Joints[pressed].getPlayer()==Joints[mill2[0]].getPlayer())&&
        (Joints[pressed].getPlayer()==Joints[mill2[1]].getPlayer())){
            Joints[pressed].setMill();
            Joints[mill2[0]].setMill();
            Joints[mill2[1]].setMill();
            sounds.play("mill");;
            millevent++; 
        }
    }

    public void UpdateMouseLocation(int x, int y){
        pressedx = x;
        pressedy=y;
    }

    public void RemoveMill(){
        if (turn==1){
            cntplayer2--;
            cntremoved2++;
        }
        if (turn==-2){
            cntplayer1--;
            cntremoved1++;
        }
        Joints[pressed].clearPlayer();
        Joints[pressed].Update();
        millevent--;
        sounds.play("remove");;
        if(((cntplayer1==2)&&(player1phase==2))||((player2phase==2)&&(cntplayer2==2))) gameover=true;
        else turn=~turn;
    }

    public void MovePoint(){
        Joints[released].setPlayer(Joints[pressed].getPlayer());
        Joints[pressed].clearPlayer();
        Joints[pressed].Update();
        Joints[released].Update();
        ClearMill(pressed);
        CheckForMill(released);
        released=-1;
        pressed=-1;
        if (millevent==0) turn=~turn;
    }

    private boolean CheckFreePoint(int player){
        int i;
        int tmp=0;
        for(i =0; i<24;i++){
            if ((Joints[i].getPlayer()==player)&&(!Joints[i].checkMillStatus())){
                tmp++;
            }
        }
        if (tmp!=0) return true;
        else return false;
    }

}
//--------------------------------------------------------------------------------------------------