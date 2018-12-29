package it.polimi.ma.group10.trump.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import it.polimi.ma.group10.trump.model.persistance.SharedPreferencePersistence;
import it.polimi.ma.group10.trump.service.BackgroundSoundService;

/**
 * Base activity class contains a technique able to determine if the application is
 * in a foreground or background state
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected static final String TAG = BaseActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static boolean isAppWentToBg = false;

    public static boolean isWindowFocused = false;

    public static boolean isBackPressed = false;

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart isAppWentToBg " + isAppWentToBg);

        applicationWillEnterForeground();

        super.onStart();
    }

    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            if (SharedPreferencePersistence.getInstance(this).getBackgroundMusic()) {
                Intent svc = new Intent(this, BackgroundSoundService.class);
                Bundle b = new Bundle();
                if(this instanceof GameActivity){
                    b.putInt("songID",1);
                }
                else{
                    b.putInt("songID",0);
                }
                svc.putExtras(b);
                startService(svc);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop ");
        applicationDidEnterBackground();
    }

    /**
     * Understand if the application entered in a background state
     */
    public void applicationDidEnterBackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
            Intent svc = new Intent(this, BackgroundSoundService.class);
            stopService(svc);
            //stop music when background state is reached
        }
    }

    @Override
    public void onBackPressed() {

        if (this instanceof MainActivity) {

        } else {
            isBackPressed = true;
        }

        Log.d(TAG,
                "onBackPressed " + isBackPressed + ""
                        + this.getLocalClassName());
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        isWindowFocused = hasFocus;

        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }

        super.onWindowFocusChanged(hasFocus);
    }


}
