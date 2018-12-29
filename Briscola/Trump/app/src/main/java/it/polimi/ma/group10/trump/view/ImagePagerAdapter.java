package it.polimi.ma.group10.trump.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.ma.group10.trump.R;

/**
 * Custom pager adapter for scrolling lists
 */
public class ImagePagerAdapter extends PagerAdapter {

    ArrayList<Integer> mImages;
    ArrayList<Integer> mDescriptions;

    public ImagePagerAdapter(ArrayList<Integer> mImages, ArrayList<Integer> mDescriptions) {
        this.mImages = mImages;
        this.mDescriptions = mDescriptions;
    }


    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)container.getContext
                ().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.settings_pager, null);

        ImageView im=(ImageView) layout.findViewById(R.id.iv_setting_img);
        im.setImageResource(mImages.get(position));

        TextView txt=(TextView) layout.findViewById(R.id.tv_description);
        txt.setText(mDescriptions.get(position));
        Context containerCtx = container.getContext();
        Typeface textFont = Typeface.createFromAsset(containerCtx.getAssets(), containerCtx.getString(R.string.font));
        txt.setTypeface(textFont);
        container.addView(layout, 0);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
