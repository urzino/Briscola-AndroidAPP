package it.polimi.ma.group10.trump.model.persistance.enumerations;

import it.polimi.ma.group10.trump.R;


/**
 * Enumeration for the definition of all the available board theme
 */
public enum BoardTheme {
    GREEN(R.drawable.bg_green,R.string.bg_green),
    RED(R.drawable.bg_red,R.string.bg_red),
    WOOD(R.drawable.bg_wood,R.string.bg_wood),
    CLOTH(R.drawable.bg_cloth,R.string.bg_cloth);

    private int resId;
    private int descId;

    BoardTheme(int resId, int descId) {
        this.resId = resId;
        this.descId = descId;
    }

    public int getResId() {
        return resId;
    }

    public int getDescId() {
        return descId;
    }

    /**
     * Gives the enumeration given the resource associated to it
     * @param resId
     * @return BoardTheme
     */
    public static BoardTheme getFromResId(int resId){
        switch (resId){
            case R.drawable.bg_green: {return BoardTheme.GREEN; }
            case R.drawable.bg_wood: {return BoardTheme.WOOD;}
            case R.drawable.bg_cloth: {return BoardTheme.CLOTH;}
            case R.drawable.bg_red: {return BoardTheme.RED;}
            default: return BoardTheme.GREEN;
        }
    }
}
