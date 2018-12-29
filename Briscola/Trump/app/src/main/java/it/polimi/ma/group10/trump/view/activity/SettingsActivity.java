package it.polimi.ma.group10.trump.view.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.view.ImagePagerAdapter;
import it.polimi.ma.group10.trump.view.InfinitePagerAdapter;
import it.polimi.ma.group10.trump.model.persistance.SharedPreferencePersistence;
import it.polimi.ma.group10.trump.model.persistance.enumerations.BoardTheme;
import it.polimi.ma.group10.trump.model.persistance.enumerations.CardTheme;
import it.polimi.ma.group10.trump.service.BackgroundSoundService;
import it.polimi.ma.group10.trump.view.InfiniteViewPager;

public class SettingsActivity extends BaseActivity {

    private final String TAG = "SETTING CHANGED";

    //Arrays containing labels and images of decks and boards
    ArrayList<Integer> mImagesCard = new ArrayList<>();
    ArrayList<Integer> mImagesBoard = new ArrayList<>();
    ArrayList<Integer> mLabelsCard = new ArrayList<>();
    ArrayList<Integer> mLabelsBoard = new ArrayList<>();

    SharedPreferencePersistence persistance = SharedPreferencePersistence.getInstance(SettingsActivity.this);

    InfiniteViewPager cardThemePager,boardThemePager;
    InfinitePagerAdapter cardPagerAdapter, boardPagerAdapter;
    ImageView soundFx, musicBg, diff, hint;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViews();
        Typeface textFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        ((TextView) findViewById(R.id.setting_title)).setTypeface(textFont);
        ((TextView) findViewById(R.id.tv_difficulty)).setTypeface(textFont);
        ((TextView) findViewById(R.id.tv_hint)).setTypeface(textFont);
        ((TextView) findViewById(R.id.tv_soundfx)).setTypeface(textFont);
        ((TextView) findViewById(R.id.tv_music)).setTypeface(textFont);

        for (CardTheme ct: CardTheme.values()){
            mImagesCard.add(ct.getResId());
            mLabelsCard.add(ct.getDescId());
        }
        for (BoardTheme bt: BoardTheme.values()){
            mImagesBoard.add(bt.getResId());
            mLabelsBoard.add(bt.getDescId());
        }

        cardPagerAdapter = new InfinitePagerAdapter(new ImagePagerAdapter(mImagesCard,mLabelsCard));
        cardThemePager.setAdapter(cardPagerAdapter);
        cardThemePager.setPageTransformer(false, new FadePageTransformer());

        boardPagerAdapter = new InfinitePagerAdapter(new ImagePagerAdapter(mImagesBoard,mLabelsBoard));
        boardThemePager.setAdapter(boardPagerAdapter);
        boardThemePager.setPageTransformer(false,new FadePageTransformer());
        cardThemePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                CardTheme theme = CardTheme.getFromResId(mImagesCard.get(cardThemePager.getCurrentItem()));
                persistance.saveCardTheme(theme.getKey());

            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        boardThemePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                int theme = mImagesBoard.get(boardThemePager.getCurrentItem());
                persistance.saveBoardTheme(theme);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        soundFx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(persistance.getSoundFx()){
                    persistance.saveSoundFx(false);
                    Log.i(TAG, "sound FX switched(ON ---> OFF)");
                    soundFx.setImageResource(R.drawable.sound_off);
                } else {
                    persistance.saveSoundFx(true);
                    Log.i(TAG, "sound FX switched(OFF ---> ON)");
                    soundFx.setImageResource(R.drawable.sound_on);
                }
            }
        });

        musicBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(persistance.getBackgroundMusic()){
                    persistance.saveBackgroundMusic(false);
                    Log.i(TAG, "BG music switched(ON ---> OFF)");
                    Intent svc = new Intent(SettingsActivity.this, BackgroundSoundService.class);
                    stopService(svc);
                    musicBg.setImageResource(R.drawable.sound_off);
                } else {
                    persistance.saveBackgroundMusic(true);
                    Log.i(TAG, "BG music switched(OFF ---> ON)");
                    Intent svc = new Intent(SettingsActivity.this, BackgroundSoundService.class);
                    Bundle b = new Bundle();
                    b.putInt("songID", 0);
                    svc.putExtras(b);
                    startService(svc);
                    musicBg.setImageResource(R.drawable.sound_on);
                }
            }
        });

        diff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentDifficulty = persistance.getGameDifficulty();
                changeDifficulty(currentDifficulty +1);
                switch (currentDifficulty){
                    case 0: { diff.setImageResource(R.drawable.diff_medium); break;}
                    case 1: { diff.setImageResource(R.drawable.diff_hard); break;}
                    case 2: { diff.setImageResource(R.drawable.diff_easy); break;}
                }
            }
        });

        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(persistance.getHintPref()){
                    persistance.saveHintSetting(false);
                    hint.setImageResource(R.drawable.hint_off);
                }else {
                    persistance.saveHintSetting(true);
                    hint.setImageResource(R.drawable.hint_on);
                }
            }
        });

        loadCurrentSettings();
    }

    /**
     * Locate the views of the layout
     */
    private void findViews(){
        cardThemePager = (InfiniteViewPager) findViewById(R.id.card_themes);
        boardThemePager = (InfiniteViewPager) findViewById(R.id.board_themes);
        soundFx = (ImageView) findViewById(R.id.btn_soundfx);
        musicBg = (ImageView) findViewById(R.id.btn_bg_music);
        diff = (ImageView) findViewById(R.id.btn_difficulty);
        hint = (ImageView) findViewById(R.id.btn_hint);
    }

    /**
     * Load current settings stored in the shared preferences
     */
    private void loadCurrentSettings(){
        CardTheme currentCardTheme = CardTheme.getFromKey(persistance.getCardTheme());
        cardThemePager.setCurrentItem(mImagesCard.indexOf(currentCardTheme.getResId()));
        BoardTheme currentBoardTheme = BoardTheme.getFromResId(persistance.getBoardTheme());
        boardThemePager.setCurrentItem(mImagesBoard.indexOf(currentBoardTheme.getResId()));
        if(persistance.getSoundFx()){
            Log.i(TAG, "sound FX on");
            soundFx.setImageResource(R.drawable.sound_on);
        }else{
            Log.i(TAG, "sound FX off");
            soundFx.setImageResource(R.drawable.sound_off);
        }
        if(persistance.getBackgroundMusic()){
            Log.i(TAG, "BG music on");
            musicBg.setImageResource(R.drawable.sound_on);
        }else {
            Log.i(TAG, "BG music off");
            musicBg.setImageResource(R.drawable.sound_off);
        }

        if(persistance.getHintPref()){
            hint.setImageResource(R.drawable.hint_on);
        }
        switch (persistance.getGameDifficulty()){
            case 0: {diff.setImageResource(R.drawable.diff_easy); break;}
            case 1: {diff.setImageResource(R.drawable.diff_medium); break;}
            case 2: {diff.setImageResource(R.drawable.diff_hard); break;}
        }
    }

    private void changeDifficulty(int newDifficulty){
        persistance.saveGameDifficulty(newDifficulty % 3);
    }
}


/**
 * Class implementing the PageTransformer in order to add a vanishing effect on the
 * rotation of InfiniteViewPager's elements
 */
class FadePageTransformer implements ViewPager.PageTransformer {
    public void transformPage(View view, float position) {
        if (position < -1 || position > 1) {
            view.setAlpha(0);
        }
        else if (position <= 0 || position <= 1) {
            // Calculate alpha. Position is decimal in [-1,0] or [0,1]
            float alpha = (position <= 0) ? position + 1 : 1 - position;
            view.setAlpha(alpha);
        }
        else if (position == 0) {
            view.setAlpha(1);
        }
    }
}
