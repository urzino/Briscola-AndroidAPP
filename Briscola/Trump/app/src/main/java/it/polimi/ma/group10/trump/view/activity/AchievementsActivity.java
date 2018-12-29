package it.polimi.ma.group10.trump.view.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.model.persistance.Stats;
import it.polimi.ma.group10.trump.view.AchievementAdapter;
import it.polimi.ma.group10.trump.view.AchievementRow;

/**
 * Activity that displays the achievement obtained by the user
 */
public class AchievementsActivity extends  BaseActivity {

    ListView listView;
    TextView achTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        achTitle = (TextView) findViewById(R.id.achievements_title);
        achTitle.setTypeface(Typeface.createFromAsset(getAssets(),getString(R.string.font)));

        String[] strings = getResources().getStringArray(R.array.ach_array);

        ArrayList<AchievementRow> data = new ArrayList<>();

        for(int i = 0;i< strings.length;i++){
            ArrayList<Integer> bounds = new ArrayList<>();
            String[] tokens = strings[i].split(",");
            bounds.add(Integer.valueOf(tokens[0]));
            bounds.add(Integer.valueOf(tokens[1]));
            bounds.add(Integer.valueOf(tokens[2]));
            String text = tokens[3];
            data.add(setNumOfStar(text,numOfStars(i,bounds),bounds));
        }

        AchievementAdapter adapter = new AchievementAdapter(this,R.layout.listview_item_row,data);

        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    /**
     * This method determines the number of star associated to each achievement
     * @param index The specified achievement
     * @param bounds
     * @return
     */
    private int numOfStars(int index,ArrayList<Integer> bounds){
        int value;
        Stats stats = Stats.getInstance(this);
        if(index == 0){
            value = stats.getWins();
        }
        else if(index == 1){
            value = stats.getDraws();
        }
        else if(index == 2){
            value = stats.totalGames();
        }
        else if(index == 3){
            value = stats.getTotalPoints();
        }
        else if(index == 4){
            value = stats.getTrumps();
        }
        else if(index == 5){
            value = stats.getLoads();
        }
        else{
            value=0;
        }

        if(value<bounds.get(0)) return 0;
        else if(value>=bounds.get(0) && value<bounds.get(1)) return 1;
        else if(value>=bounds.get(1) && value<bounds.get(2)) return 2;
        else return 3;

    }

    /**
     * Returns the AchievementRow with the specified numbers of stars
     * @param string
     * @param numOfStar
     * @param bounds
     * @return
     */
    private AchievementRow setNumOfStar(String string,int numOfStar,ArrayList<Integer> bounds){
        if(numOfStar==0){
            return new AchievementRow(string,0,bounds);
        }
        else if(numOfStar == 1){
            return new AchievementRow(string,1,bounds);
        }
        else if(numOfStar == 2){
            return new AchievementRow(string, 2,bounds);
        }
        else{
            return new AchievementRow(string, 3,bounds);
        }
    }
}
