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

//---------------------------------------------------------------------------------------------------------------------
public class SmartJointFactory {

    private int[] circlesx = new int[24];  //........................................................joints.x.coordinate
    private int[] circlesy = new int[24];  //.......................................................joints.y.coordinates
    private int circledia = 56;        //................................................................joints.diameter

    private static final int[][] MillForEveryone = new int[][] //egyes pontokezekben a malmokban szerepelnek
    { { 1, 2, 9, 21 }, { 0, 2, 4, 7 }, { 1, 0, 14, 23 }, { 4, 5, 10, 18 }, { 3, 5, 1, 7 }, { 3, 4, 13, 20 },
            { 7, 8, 11, 15 }, { 6, 8, 1, 4 }, { 6, 7, 12, 17 }, { 0, 21, 10, 11 }, { 9, 11, 3, 18 }, { 6, 15, 9, 10 },
            { 13, 14, 8, 17 }, { 12, 14, 5, 20 }, { 12, 13, 2, 23 }, { 6, 11, 16, 17 }, { 15, 17, 19, 22 },
            { 15, 16, 8, 12 }, { 19, 20, 3, 10 }, { 18, 20, 16, 22 }, { 18, 19, 5, 13 }, { 22, 23, 0, 9 },
            { 21, 23, 16, 19 }, { 21, 22, 2, 14 } };

    private static final int[][] NeighbourForEveryone = new int[][] //egyes pontoknak ők a szomszádaik
    { { -1, 1, 9, -1 }, { -1, 2, 4, 0 }, { -1, -1, 14, 1 }, { -1, 4, 10, -1 }, { 1, 5, 7, 3 }, { -1, -1, 13, 4 },
            { -1, 7, 11, -1 }, { 4, 8, -1, 6 }, { -1, -1, 12, 7 }, { 0, 10, 21, -1 }, { 3, 11, 18, 9 },
            { 6, -1, 15, 10 }, { 8, 13, 17, -1 }, { 5, 14, 20, 12 }, { 2, -1, 23, 13 }, { 11, 16, -1, -1 },
            { -1, 17, 19, 15 }, { 12, -1, -1, 16 }, { 10, 19, -1, -1 }, { 16, 20, 22, 18 }, { 13, -1, -1, 19 },
            { 9, 22, -1, -1 }, { 19, 23, -1, 21 }, { 14, -1, -1, 22 } };

    public PlayerSprite[] Joints = new PlayerSprite[24];   // 24 joint
//---------------------------------------------------------------------------------------------------------------------

    public SmartJointFactory(Context context) {
        Imageset imgset = new Imageset(context);
        Bitmap[] imageset = imgset.getImageSet("imageset");
        circledia = Pd2px.pd2px((int) circledia);
        this.GenerateCoordinates();

        for (int i = 0; i < 24; i++) {//..........................................................create.and.inic.joints
            Joints[i] = new PlayerSprite(circlesx[i], circlesy[i], circledia, imageset, i);
            Joints[i].setMill1(MillForEveryone[i][0], MillForEveryone[i][1]);
            Joints[i].setMill2(MillForEveryone[i][2], MillForEveryone[i][3]);
            Joints[i].setNeighbours(NeighbourForEveryone[i][0], NeighbourForEveryone[i][1], NeighbourForEveryone[i][2],
                    NeighbourForEveryone[i][3]);
        }
    }

    private void GenerateCoordinates() {//....................................this.was.inspirated.by.evil.voices.i.think
        circlesy[1] = Pd2px.pd2px((int) (GamePanel.scale * circledia / 2));

        circlesx[1] = GamePanel.PWIDTH / 2;
        circlesx[0] = GamePanel.PWIDTH / 2 - (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7);
        circlesx[9] = GamePanel.PWIDTH / 2 - GamePanel.PHEIGHT / 2
                + Pd2px.pd2px((int) (GamePanel.scale * circledia / 2));
        circlesx[14] = GamePanel.PWIDTH / 2 + GamePanel.PHEIGHT / 2
                - Pd2px.pd2px((int) (GamePanel.scale * circledia / 2));
        circlesx[3] = GamePanel.PWIDTH / 2 - (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7 * 0.735);
        circlesx[10] = circlesx[9] + Pd2px.pd2px((int) (GamePanel.scale * 65));
        circlesx[6] = GamePanel.PWIDTH / 2 - (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7 * 0.72 * 0.65);
        circlesx[11] = circlesx[10] + Pd2px.pd2px((int) (GamePanel.scale * 65));
        circlesx[8] = GamePanel.PWIDTH / 2 + (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7 * 0.72 * 0.65);
        circlesx[13] = circlesx[14] - Pd2px.pd2px((int) (GamePanel.scale * 65));
        circlesx[12] = circlesx[13] - Pd2px.pd2px((int) (GamePanel.scale * 65));
        circlesx[5] = GamePanel.PWIDTH / 2 + (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7 * 0.735);
        circlesx[2] = GamePanel.PWIDTH / 2 + (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7);
        circlesx[21] = circlesx[0];
        circlesx[18] = circlesx[3];
        circlesx[15] = circlesx[6];
        circlesx[4] = circlesx[1];
        circlesx[7] = circlesx[1];
        circlesx[16] = circlesx[1];
        circlesx[19] = circlesx[1];
        circlesx[22] = circlesx[1];
        circlesx[17] = circlesx[8];
        circlesx[20] = circlesx[5];
        circlesx[23] = circlesx[2];

        circlesy[22] = GamePanel.PHEIGHT - circledia / 2;
        circlesy[0] = GamePanel.PHEIGHT / 2 - (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7);
        circlesy[19] = circlesy[22] - Pd2px.pd2px((int) (GamePanel.scale * 65));
        circlesy[16] = circlesy[19] - Pd2px.pd2px((int) (GamePanel.scale * 65));
        circlesy[9] = GamePanel.PHEIGHT / 2;
        circlesy[4] = circlesy[1] + Pd2px.pd2px((int) (GamePanel.scale * 65));
        circlesy[3] = GamePanel.PHEIGHT / 2 - (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7 * 0.735);
        circlesy[15] = GamePanel.PHEIGHT / 2 + (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7 * 0.72 * 0.65);
        circlesy[7] = circlesy[4] + Pd2px.pd2px((int) (GamePanel.scale * 65));
        circlesy[6] = GamePanel.PHEIGHT / 2 - (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7 * 0.72 * 0.65);
        circlesy[18] = GamePanel.PHEIGHT / 2 + (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7 * 0.735);
        circlesy[21] = GamePanel.PHEIGHT / 2 + (int) ((GamePanel.PHEIGHT / 2 - circlesy[1]) * 0.7);
        circlesy[2] = circlesy[0];
        circlesy[8] = circlesy[6];
        circlesy[10] = circlesy[9];
        circlesy[11] = circlesy[9];
        circlesy[12] = circlesy[9];
        circlesy[13] = circlesy[9];
        circlesy[14] = circlesy[9];
        circlesy[2] = circlesy[0];
        circlesy[5] = circlesy[3];
        circlesy[17] = circlesy[15];
        circlesy[20] = circlesy[18];
        circlesy[23] = circlesy[21];
    }
}
//----------------------------------------------------------------------------------------------------------------------