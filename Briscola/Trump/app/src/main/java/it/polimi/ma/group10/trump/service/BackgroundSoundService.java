package it.polimi.ma.group10.trump.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import it.polimi.ma.group10.trump.R;

/**
 * Service class to support background music
 */
public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        //the intent will contain a bundle specifying the song to be played
        switch(intent.getExtras().getInt("songID")){
            case (0):{
                player = MediaPlayer.create(this, R.raw.sound_background);
                break;
            }
            case (1):{
                player = MediaPlayer.create(this, R.raw.sound_background_match);
                break;
            }
            default:{
                player = MediaPlayer.create(this, R.raw.sound_background);
                break;
            }
        }
        player.setLooping(true); // Set looping
        player.setVolume(0.3f,0.3f);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}
