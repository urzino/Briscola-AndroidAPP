package it.polimi.ma.group10.trump.view.dialog;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.controller.GameController;
import it.polimi.ma.group10.trump.model.game.RemoteGameMaster;


public class SearchOpponentDialogFragment extends DialogFragment implements android.view.View.OnClickListener {

    private View thisView;
    private ImageView iv_magnifier, btn_cancel, iw_cloud;

    public SearchOpponentDialogFragment() {

    }

    public static SearchOpponentDialogFragment newInstance() {
        SearchOpponentDialogFragment frag = new SearchOpponentDialogFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.dialog_search_opponent, container, false);
        btn_cancel = (ImageView) thisView.findViewById(R.id.btn_cancel_search);
        btn_cancel.setOnClickListener(this);
        iv_magnifier = (ImageView) thisView.findViewById(R.id.iv_magnifier);
        iw_cloud = (ImageView) thisView.findViewById(R.id.iv_cloud);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        this.setCancelable(false);

        return thisView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View dialog = (thisView.findViewById(R.id.ll_dial_search_opp));

        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setDuration(200);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        dialog.startAnimation(scaleAnimation);

        ViewTreeObserver viewTreeObserver = dialog.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {


            @Override
            public void onGlobalLayout() {
                final float cloudHeight = iw_cloud.getHeight();

                ValueAnimator animator = ValueAnimator.ofFloat(0, 2);
                animator.setDuration(5000);
                animator.setRepeatCount(Animation.INFINITE);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();
                        // Set translation of your view here. Position can be calculated
                        // out of value. This code should move the view in a half circle.
                        iv_magnifier.setTranslationX((float) (cloudHeight/3.5 * Math.sin(value * Math.PI)));
                        iv_magnifier.setTranslationY((float) (cloudHeight/3.5 * Math.cos(value * Math.PI)));
                    }
                });
                animator.start();
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_search: {
                cancelOpponentSearch();
                break;
            }
        }
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

    private void cancelOpponentSearch() {
        RequestQueue gameQueue = RemoteGameMaster.getInstance().gameQueue;
        String TAG = RemoteGameMaster.getInstance().TAG;
        gameQueue.cancelAll(TAG);
        gameQueue.stop();
        this.callDismiss();
    }

    public void callDismiss() {
        if (this.getFragmentManager() != null) {
            this.dismissAllowingStateLoss();
        }
    }
}
