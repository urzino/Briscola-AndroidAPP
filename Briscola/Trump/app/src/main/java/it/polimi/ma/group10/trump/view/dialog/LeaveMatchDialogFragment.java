package it.polimi.ma.group10.trump.view.dialog;

import android.content.Intent;
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
import it.polimi.ma.group10.trump.model.game.GameMaster;
import it.polimi.ma.group10.trump.model.game.PlayerAI;
import it.polimi.ma.group10.trump.model.game.RemoteGameMaster;
import it.polimi.ma.group10.trump.model.persistance.SharedPreferencePersistence;
import it.polimi.ma.group10.trump.service.BackgroundSoundService;
import it.polimi.ma.group10.trump.view.activity.GameActivity;
import it.polimi.ma.group10.trump.view.activity.MainActivity;


public class LeaveMatchDialogFragment extends DialogFragment implements android.view.View.OnClickListener {

    public static final String KEY_RESULT = "match_result";
    public static final String KEY_POINTS = "match_points";

    private View thisView;
    private ImageView btn_leave, btn_continue;

    public LeaveMatchDialogFragment() {
    }

    public static LeaveMatchDialogFragment newInstance() {
        LeaveMatchDialogFragment frag = new LeaveMatchDialogFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.dialog_leave_match, container, false);

        btn_leave = (ImageView) thisView.findViewById(R.id.btn_leave);
        btn_leave.setOnClickListener(this);

        btn_continue = (ImageView) thisView.findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(this);

        Typeface textFont = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.font));
        ((TextView) thisView.findViewById(R.id.tv_leave_match)).setTypeface(textFont);

        this.setCancelable(true);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        return thisView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View dialog = (thisView.findViewById(R.id.ll_dial_leave_match));

        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setDuration(200);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        dialog.startAnimation(scaleAnimation);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_leave: {
                goHome();
                break;
            }
            case R.id.btn_continue: {
                this.callDismiss();
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

    private void goHome() {
        Log.i("bob", "bob");

        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        final GameActivity gameActivity = (GameActivity) this.getActivity();
        if (gameActivity.modality != 0) {
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    RemoteGameMaster.getInstance().endRemoteGame("abandon");
                }
            };
            gameActivity.gameHandler.post(myRunnable);
        }

        ((GameActivity) this.getActivity()).countDownTimer.cancel();
        //((GameActivity) this.getActivity()).gameHandler.getLooper().getThread().interrupt();
        startActivity(intent);
        if (SharedPreferencePersistence.getInstance(this.getContext()).getBackgroundMusic()) {
            Intent svc = new Intent(this.getContext(), BackgroundSoundService.class);
            this.getActivity().stopService(svc);
            Bundle b = new Bundle();
            b.putInt("songID", 0);
            svc.putExtras(b);
            this.getActivity().startService(svc);
        }

        this.getActivity().finish();

    }

    public void callDismiss() {
        if (this.getFragmentManager() != null) {
            this.dismissAllowingStateLoss();
        }
    }

}
