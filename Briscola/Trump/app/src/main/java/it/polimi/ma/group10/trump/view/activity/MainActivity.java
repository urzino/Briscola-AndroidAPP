package it.polimi.ma.group10.trump.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.controller.GameController;
import it.polimi.ma.group10.trump.view.dialog.ExitGameDialogFragment;
import it.polimi.ma.group10.trump.view.dialog.LeaveMatchDialogFragment;
import it.polimi.ma.group10.trump.view.dialog.RoomSelectionDialogFragment;

/**
 * Home activity
 */
public class MainActivity extends BaseActivity {


    private ImageView statsButton, settingsButton, singlePlayerButton, aiVsAiButton, remotePvPButton, remoteAIButton, achievementsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Definition of all the listeners on the buttons

        singlePlayerButton = (ImageView) findViewById(R.id.btn_single_player);
        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameController.getInstance().startSinglePlayerGame(MainActivity.this);
            }
        });

        aiVsAiButton = (ImageView) findViewById(R.id.btn_ai_vs_ai);
        aiVsAiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameController.getInstance().startAiVsAiGame(MainActivity.this);
            }
        });

        remotePvPButton = (ImageView) findViewById(R.id.btn_single_player_online);
        remotePvPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                RoomSelectionDialogFragment roomDialog = RoomSelectionDialogFragment.newInstance(0);
                roomDialog.show(fm, "test");
            }
        });


        remoteAIButton = (ImageView) findViewById(R.id.btn_ai_vs_online);
        remoteAIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                RoomSelectionDialogFragment roomDialog = RoomSelectionDialogFragment.newInstance(1);
                roomDialog.show(fm, "test");
            }
        });


        settingsButton = (ImageView) findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        statsButton = (ImageView) findViewById(R.id.btn_stats);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(i);
            }
        });
        achievementsButton = (ImageView) findViewById(R.id.btn_achievements);
        achievementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AchievementsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        ExitGameDialogFragment exitGameDialogFragment = ExitGameDialogFragment.newInstance();
        exitGameDialogFragment.show(fm, "test");
    }
}
