package it.polimi.ma.group10.trump.view.activity;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.model.persistance.SharedPreferencePersistence;
import it.polimi.ma.group10.trump.service.BackgroundSoundService;

/**
 * Splash screen activity, containing a logo of the developer team
 */
public class StartUpActivity extends AppCompatActivity {

    StartUpActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Handler handler = new Handler();
        MediaPlayer soundStartup = MediaPlayer.create(getApplicationContext(), R.raw.sound_startup);
        soundStartup.start();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (SharedPreferencePersistence.getInstance(StartUpActivity.this).getBackgroundMusic()) {
                    Intent svc = new Intent(self, BackgroundSoundService.class);
                    Bundle b = new Bundle();
                    b.putInt("songID",0);
                    svc.putExtras(b);
                    startService(svc);
                }
                Intent i = new Intent(StartUpActivity.this, MainActivity.class);
                startActivity(i);
                StartUpActivity.this.finish();
            }

        }, 5000);



    }
}
