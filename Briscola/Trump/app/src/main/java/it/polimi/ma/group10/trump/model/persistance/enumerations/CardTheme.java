package it.polimi.ma.group10.trump.model.persistance.enumerations;

import it.polimi.ma.group10.trump.R;

/**
 * Enumeration for the definition of all the available decks
 */
public enum CardTheme {
    NEAPOLITAN(R.drawable.deck_n_preview,R.string.neapolitan,"n"),
    SPANISH(R.drawable.deck_s_preview,R.string.spanish,"s"),
    FRENCH(R.drawable.deck_f_preview,R.string.french,"f");

    private int resId;
    private int descId;
    private String key;

    CardTheme(int resId, int descId, String key){
        this.resId = resId;
        this.descId = descId;
        this.key = key;
    }

    public int getResId() {
        return resId;
    }

    public int getDescId() {
        return descId;
    }

    public String getKey() {
        return key;
    }

    /**
     * Gives the card theme given the resource associated
     * @param resId
     * @return CardTheme
     */
    public static CardTheme getFromResId(int resId){
        switch (resId){
            case R.drawable.deck_f_preview: {return CardTheme.FRENCH; }
            case R.drawable.deck_n_preview: {return CardTheme.NEAPOLITAN; }
            case R.drawable.deck_s_preview: {return CardTheme.SPANISH;}
            default: return CardTheme.NEAPOLITAN;
        }
    }

    /**
     * Gives the card theme given key
     * @param key
     * @return CardTheme
     */
    public static CardTheme getFromKey(String key){
        switch (key){
            case "f": {return CardTheme.FRENCH; }
            case "n": {return CardTheme.NEAPOLITAN; }
            case "s": {return CardTheme.SPANISH;}
            default: return CardTheme.NEAPOLITAN;
        }
    }


}
