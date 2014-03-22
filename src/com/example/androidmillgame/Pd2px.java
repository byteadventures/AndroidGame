package com.example.androidmillgame;

public class Pd2px {

    public static int pd2px(int pd) {
        float tmp = pd;
        return (int) (tmp * (GamePanel.dpi / 160.0f));
    }
}
//----------------------------------------------------------------------------------------------------------------------