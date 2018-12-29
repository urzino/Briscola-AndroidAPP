package it.polimi.ma.group10.trump.view.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.model.persistance.SharedPreferencePersistence;
import it.polimi.ma.group10.trump.model.persistance.Stats;
import it.polimi.ma.group10.trump.view.PieChart;

/**
 * Statistics activity that contains the statistics of the user
 */
public class StatsActivity extends BaseActivity {

    TextView averageTrumps;
    TextView averageLoads;
    TextView averagePoints;
    TextView title;
    TextView noStats;
    PieChart pieChart;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stats stats = Stats.getInstance(getApplicationContext());
        String font = getString(R.string.font);

        if(stats.totalGames()==0){
            setContentView(R.layout.activity_no_stats);
            noStats = (TextView)findViewById(R.id.no_stats);
            noStats.setTypeface(Typeface.createFromAsset(getAssets(), font));
        }
        else{
            setContentView(R.layout.activity_stats);
            title = (TextView) findViewById(R.id.statsTitle);
            averageTrumps = (TextView) findViewById(R.id.average_trumps);
            averageLoads = (TextView) findViewById(R.id.average_loads);
            averagePoints = (TextView) findViewById(R.id.average_points);
            pieChart = (PieChart) findViewById(R.id.pie_chart);

            pieChart.setPercentage(stats.getWins(),stats.getLosses(),stats.getDraws());

            //set the font
            title.setTypeface(Typeface.createFromAsset(getAssets(), font));
            averageTrumps.setTypeface(Typeface.createFromAsset(getAssets(), font));
            averageLoads.setTypeface(Typeface.createFromAsset(getAssets(), font));
            averagePoints.setTypeface(Typeface.createFromAsset(getAssets(), font));


            averageTrumps.setText(getResources().getString(R.string.average_trumps) + stats.trumpsPerGame());
            averageLoads.setText(getResources().getString(R.string.average_loads)  + stats.loadsPerGame());
            averagePoints.setText(getResources().getString(R.string.average_points) + stats.pointsPerGame());

        }

    }
}
