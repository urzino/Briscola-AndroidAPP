package it.polimi.ma.group10.trump.view;

import java.util.ArrayList;


public class AchievementRow {

    public int firstBound;
    public int secondBound;
    public int thirdBound;
    public int numOfStar;
    public String title;
    public AchievementRow(){
        super();
    }


    public AchievementRow(String title, int numOfStar,ArrayList<Integer> bounds) {
        super();
        this.title = title;
        this.numOfStar = numOfStar;
        this.firstBound = bounds.get(0);
        this.secondBound = bounds.get(1);
        this.thirdBound = bounds.get(2);
    }
}
