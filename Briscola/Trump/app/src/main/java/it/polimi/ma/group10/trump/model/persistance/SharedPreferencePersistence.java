package it.polimi.ma.group10.trump.model.persistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

import it.polimi.ma.group10.trump.R;

/**
 * Class which handle the persistance of the application through
 * shared preferences
 */
public class SharedPreferencePersistence {

    /**
     * Applicaton context
     */
    private Context mContext;

    /**
     * loggin tag
     */
    private final String TAG_SETTING = "setting.persistance";
    private final String TAG_STATS = "stats.persistance";

    /**
     * shared preferences file name
     */
    private final static String SETTINGS_PREF_NAME = "app_settings";
    private final static String STATS_PREF_NAME = "player_stats";
    private final static int PRIVATE_MODE = 0;

    /**
     * Singleton instance
     */
    private static SharedPreferencePersistence ourInstance = null;

    private SharedPreferences preferences;

    /**
     * Key associated to possible statistics in the game
     */
    public enum  SettingsKey{
        BOARD_THEME,
        CARD_THEME,
        SOUND_FX,
        BACKGROUND_MUSIC,
        GAME_DIFFICULTY,
        MOVE_HINT
    }

    /**
     * Key associated to possible statistics of the game
     */
    public enum StatsKey{
        WINS,
        LOSSES,
        DRAWS,
        TOTAL_POINTS,
        TRUMPS,
        LOADS
    }


    public static SharedPreferencePersistence getInstance(Context mContext) {
        if (ourInstance == null)
            ourInstance = new SharedPreferencePersistence(mContext);
        return ourInstance;
    }


    /**
     * Constructor function
     * @param mContext:  the application context
     */
    private SharedPreferencePersistence(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * Save in shared preferences the selected card theme
     * @param cardTheme the theme to be saved
     */
    public void saveCardTheme(String cardTheme) {
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME, PRIVATE_MODE);
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SettingsKey.CARD_THEME.name(), cardTheme);
            editor.apply();
            Log.d(TAG_SETTING, "card theme saved: " + cardTheme);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save in shared preferences the selected board theme
     * @param boardTheme the theme to be saved
     */
    public void saveBoardTheme(int boardTheme) {
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME, PRIVATE_MODE);
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(SettingsKey.BOARD_THEME.name(), boardTheme);
            editor.apply();
            Log.d(TAG_SETTING, "board theme saved: " + boardTheme);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save in shared preferences the preference about soundFX
     * @param soundfx the sound preference to be stored
     */
    public void saveSoundFx(boolean soundfx){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME, PRIVATE_MODE);
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SettingsKey.SOUND_FX.name(), soundfx);
            editor.apply();
            Log.d(TAG_SETTING, "Saved sound fx: " + soundfx);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Save in shared preferences the preference about background music
     * @param bgMusic the music preference to be stored
     */
    public void saveBackgroundMusic(boolean bgMusic){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME, PRIVATE_MODE);
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SettingsKey.BACKGROUND_MUSIC.name(), bgMusic);
            editor.apply();
            Log.d(TAG_SETTING, "Saved bg audio: " + bgMusic);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Save in shared preferences new statistics
     * @param stats the statistics to be stored
     */
    public void saveStatistic(Stats stats){
        preferences = mContext.getSharedPreferences(STATS_PREF_NAME, PRIVATE_MODE);
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(StatsKey.WINS.toString(), stats.getWins());
            Log.d(TAG_STATS, StatsKey.WINS+ "saved : " + stats.getWins());
            editor.putInt(StatsKey.LOSSES.toString(), stats.getLosses());
            Log.d(TAG_STATS, StatsKey.LOSSES+ "saved : " + stats.getLosses());
            editor.putInt(StatsKey.DRAWS.toString(), stats.getDraws());
            Log.d(TAG_STATS, StatsKey.DRAWS+ "saved : " + stats.getDraws());
            editor.putInt(StatsKey.TOTAL_POINTS.toString(), stats.getTotalPoints());
            Log.d(TAG_STATS, StatsKey.TOTAL_POINTS+ "saved : " + stats.getTotalPoints());
            editor.putInt(StatsKey.TRUMPS.toString(), stats.getTrumps());
            Log.d(TAG_STATS, StatsKey.TRUMPS+ "saved : " + stats.getTrumps());
            editor.putInt(StatsKey.LOADS.toString(), stats.getLoads());
            Log.d(TAG_STATS, StatsKey.LOADS+ "saved : " + stats.getLoads());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save in shared preferences the preference about game difficulty
     * @param difficulty the dififculty to be stored
     */
    public void saveGameDifficulty(int difficulty){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME, PRIVATE_MODE);
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(SettingsKey.GAME_DIFFICULTY.name(), difficulty);
            editor.apply();
            Log.d(TAG_SETTING, "Saved game dificulty: " + difficulty);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveHintSetting(boolean pref){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME, PRIVATE_MODE);

        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SettingsKey.MOVE_HINT.name(), pref);
            editor.apply();
            Log.d(TAG_SETTING, "Saved hint: " + pref);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get the current statistics stored in the preferences
     * @return the arraylist of statistics
     */
    public ArrayList<Integer> getStatistics(){
        ArrayList<Integer> stats = new ArrayList<Integer>();
        preferences = mContext.getSharedPreferences(STATS_PREF_NAME,PRIVATE_MODE);
        stats.add(preferences.getInt(StatsKey.WINS.toString(),0));
        stats.add(preferences.getInt(StatsKey.LOSSES.toString(),0));
        stats.add(preferences.getInt(StatsKey.DRAWS.toString(),0));
        stats.add(preferences.getInt(StatsKey.TRUMPS.toString(),0));
        stats.add(preferences.getInt(StatsKey.LOADS.toString(),0));
        stats.add(preferences.getInt(StatsKey.TOTAL_POINTS.toString(),0));
        return stats;
    }

    /**
     * get stored board theme
     * @return the resource representing the board theme
     */
    public int getBoardTheme(){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME, PRIVATE_MODE);
        return preferences.getInt(SettingsKey.BOARD_THEME.name(), R.drawable.bg_green);}

    /**
     * get the stored car theme
     * @return the string representing the stored theme
     */
    public String getCardTheme(){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME,PRIVATE_MODE);
        return preferences.getString(SettingsKey.CARD_THEME.name(), "n");}

    /**
     * get the stored preference about sound fx
     * @return the boolean preference
     */
    public boolean getSoundFx(){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME,PRIVATE_MODE);
        return preferences.getBoolean(SettingsKey.SOUND_FX.name(), true);
    }

    /**
     * get the stored preference about background music
     * @return the boolen preference
     */
    public boolean getBackgroundMusic(){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME,PRIVATE_MODE);
        return preferences.getBoolean(SettingsKey.BACKGROUND_MUSIC.name(), true);
    }

    /**
     * get the stored preference about game difficulty
     * @return the game difficulty
     */
    public int getGameDifficulty(){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME,PRIVATE_MODE);
        return preferences.getInt(SettingsKey.GAME_DIFFICULTY.name(), 1);
    }

    /**
     * get the current preference about hints
     * @return the boolean preference
     */
    public boolean getHintPref(){
        preferences = mContext.getSharedPreferences(SETTINGS_PREF_NAME,PRIVATE_MODE);
        return preferences.getBoolean(SettingsKey.MOVE_HINT.name(), false);
    }

}
