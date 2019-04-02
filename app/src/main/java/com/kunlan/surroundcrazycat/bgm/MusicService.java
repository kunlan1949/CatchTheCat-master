package com.kunlan.surroundcrazycat.bgm;

/**
 * Created by KUNLAN
 * on 2019-04-02
 */
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.surroundcrazycat.R;


@SuppressLint("Registered")
public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
// TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent,int startId){
        super.onStart(intent, startId);

        if(mediaPlayer==null){

// R.raw.abc是资源文件，MP3格式的
            mediaPlayer = MediaPlayer.create(this, R.raw.bg_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

        }
    }


    @Override
    public void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        mediaPlayer.stop();
    }
}
