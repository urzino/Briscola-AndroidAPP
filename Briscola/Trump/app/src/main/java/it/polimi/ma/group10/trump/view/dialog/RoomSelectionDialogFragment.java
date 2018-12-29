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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.controller.GameController;


public class RoomSelectionDialogFragment extends DialogFragment implements android.view.View.OnClickListener {


    private View thisView;
    private ImageView btn_public, btn_private;
    public int mode;

    public RoomSelectionDialogFragment() {
    }

    public static RoomSelectionDialogFragment newInstance(int mode) {
        RoomSelectionDialogFragment frag = new RoomSelectionDialogFragment();
        frag.mode = mode;
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.dialog_room_selection, container, false);

        btn_public = (ImageView) thisView.findViewById(R.id.btn_public);
        btn_public.setOnClickListener(this);

        btn_private = (ImageView) thisView.findViewById(R.id.btn_private);
        btn_private.setOnClickListener(this);

        Typeface textFont = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.font));
        ((TextView) thisView.findViewById(R.id.tv_select_room)).setTypeface(textFont);


        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //getDialog().getWindow().getAttributes().windowAnimations = R.anim.scale_from_center;

       //getDialog().getWindow().setWindowAnimations(R.style.DialogAnim);



        return thisView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View dialog  = (thisView.findViewById(R.id.ll_dial_room_selection));

        ScaleAnimation scaleAnimation = new ScaleAnimation(0f,1f,0f,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setDuration(200);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        dialog.startAnimation(scaleAnimation);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_public: {
                startMatchPublicRoom();
                break;
            }
            case R.id.btn_private: {
                startMatchPrivateRoom();
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

    private void startMatchPublicRoom() {


        FragmentManager fm = this.getActivity().getSupportFragmentManager();
        final SearchOpponentDialogFragment sarchOpponentDialog = SearchOpponentDialogFragment.newInstance();
        sarchOpponentDialog.show(fm,"tag");
        GameController.getInstance().startRemoteGame(this.getActivity(),"Public",Volley.newRequestQueue(this.getContext()),mode,sarchOpponentDialog,fm);
        this.callDismiss();


    }

    private void startMatchPrivateRoom() {

        FragmentManager fm = this.getActivity().getSupportFragmentManager();
        final SearchOpponentDialogFragment sarchOpponentDialog = SearchOpponentDialogFragment.newInstance();
        sarchOpponentDialog.show(fm,"tag");
        GameController.getInstance().startRemoteGame(this.getActivity(),"Group10",Volley.newRequestQueue(this.getContext()),mode,sarchOpponentDialog,fm);
        this.callDismiss();

    }

    public void callDismiss() {
        if (this.getFragmentManager() != null) {
            this.dismiss();
        }
    }

}
