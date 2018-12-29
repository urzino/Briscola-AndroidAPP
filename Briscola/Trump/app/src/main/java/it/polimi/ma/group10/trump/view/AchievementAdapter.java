package it.polimi.ma.group10.trump.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.ma.group10.trump.R;


/**
 * This class is used to update dynamically the achievement listview
 */
public class AchievementAdapter extends ArrayAdapter<AchievementRow> {

    Context context;
    int layoutResourceId;
    ArrayList<AchievementRow> data = null;

    public AchievementAdapter(Context context, int layoutResourceId, ArrayList<AchievementRow> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    /**
     * This method return the view associated to a single row of the listview
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AchievementHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AchievementHolder();
            holder.firstStar = (ImageView) row.findViewById(R.id.firstStar);
            holder.secondStar = (ImageView) row.findViewById(R.id.secondStar);
            holder.thirdStar = (ImageView) row.findViewById(R.id.thirdStar);
            holder.firstStarText = (TextView) row.findViewById(R.id.firstStarText);
            holder.secondStarText = (TextView) row.findViewById(R.id.secondStarText);
            holder.thirdStarText = (TextView) row.findViewById(R.id.thirdStarText);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            //set the font
            String font = getContext().getString(R.string.font);
            holder.txtTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(),font));
            holder.firstStarText.setTypeface(Typeface.createFromAsset(getContext().getAssets(),font));
            holder.secondStarText.setTypeface(Typeface.createFromAsset(getContext().getAssets(),font));
            holder.thirdStarText.setTypeface(Typeface.createFromAsset(getContext().getAssets(),font));

            row.setTag(holder);
        }
        else
        {
            holder = (AchievementHolder)row.getTag();
        }

        AchievementRow achievement = data.get(position);
        holder.txtTitle.setText(achievement.title);
        holder.firstStarText.setText(Integer.toString(achievement.firstBound));
        holder.secondStarText.setText(Integer.toString(achievement.secondBound));
        holder.thirdStarText.setText(Integer.toString(achievement.thirdBound));
        if(achievement.numOfStar == 0){
            holder.firstStar.setAlpha(0.4f);
            holder.secondStar.setAlpha(0.4f);
            holder.thirdStar.setAlpha(0.4f);
        }
        else if(achievement.numOfStar == 1){
            holder.firstStar.setAlpha(1f);
            holder.secondStar.setAlpha(0.4f);
            holder.thirdStar.setAlpha(0.4f);
        }
        else if(achievement.numOfStar == 2){
            holder.firstStar.setAlpha(1f);
            holder.secondStar.setAlpha(1f);
            holder.thirdStar.setAlpha(0.4f);
        }
        else{
            holder.firstStar.setAlpha(1f);
            holder.secondStar.setAlpha(1f);
            holder.thirdStar.setAlpha(1f);
        }
        return row;
    }

    static class AchievementHolder
    {
        ImageView firstStar;
        ImageView secondStar;
        ImageView thirdStar;
        TextView firstStarText;
        TextView secondStarText;
        TextView thirdStarText;
        TextView txtTitle;
    }
}

