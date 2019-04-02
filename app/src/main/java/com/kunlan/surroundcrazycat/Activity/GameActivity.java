package com.kunlan.surroundcrazycat.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;

import com.kunlan.surroundcrazycat.View.GameView;
import com.kunlan.surroundcrazycat.bgm.SoundPlayer;
import com.surroundcrazycat.R;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        GameView gameView = new GameView(this);
        setContentView(R.layout.gameview);
        SoundPlayer soundPlayer=new SoundPlayer();
        getSupportActionBar().hide();
    }


}
