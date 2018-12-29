package it.polimi.ma.group10.trump.view.dialog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import it.polimi.ma.group10.trump.R;


public class YourTurnDialogFragment extends DialogFragment {

    private View thisView;

    public YourTurnDialogFragment() {

    }

    public static YourTurnDialogFragment newInstance() {
        YourTurnDialogFragment frag = new YourTurnDialogFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.dialog_your_turn, container, false);

        this.setCancelable(false);

        Typeface textFont = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.font));
        ((TextView) thisView.findViewById(R.id.tv_your_turn)).setTypeface(textFont);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        return thisView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View dialog = (thisView.findViewById(R.id.ll_dial_your_turn));

        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setDuration(200);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        dialog.startAnimation(scaleAnimation);
    }

    @Override
    public void show(FragmentManager manager, String tag) {

        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.i("FRAGMENT ERROR", "Exception fragment");
        }
    }

    public void callDismiss() {
        if (this.getFragmentManager() != null && this.getActivity() != null) {
            this.dismissAllowingStateLoss();
        }
    }

}
