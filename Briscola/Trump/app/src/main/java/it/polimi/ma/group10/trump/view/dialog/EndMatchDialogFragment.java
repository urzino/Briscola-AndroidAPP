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

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.controller.GameController;
import it.polimi.ma.group10.trump.model.game.GameMaster;
import it.polimi.ma.group10.trump.model.game.PlayerAI;
import it.polimi.ma.group10.trump.model.persistance.SharedPreferencePersistence;
import it.polimi.ma.group10.trump.service.BackgroundSoundService;
import it.polimi.ma.group10.trump.view.activity.GameActivity;
import it.polimi.ma.group10.trump.view.activity.MainActivity;

/**
 * Dialog handling the end of the match
 */
public class EndMatchDialogFragment extends DialogFragment implements android.view.View.OnClickListener {

    public static final String KEY_RESULT = "match_result";
    public static final String KEY_POINTS = "match_points";

    private View thisView;
    private ImageView btn_home, btn_rematch;

    public EndMatchDialogFragment() {
    }

    public static EndMatchDialogFragment newInstance(int victory, int points) {
        EndMatchDialogFragment frag = new EndMatchDialogFragment();
        Bundle args = new Bundle();
        if (victory == 0) {
            args.putInt(KEY_RESULT, R.string.dlg_match_won);
        } else if (victory == 1) {
            args.putInt(KEY_RESULT, R.string.dlg_match_loose);
        } else {
            args.putInt(KEY_RESULT, R.string.dlg_match_draw);
        }

        args.putInt(KEY_POINTS, points);
        System.out.println(args.toString());
        frag.setArguments(args);
        System.out.println(frag.getArguments().toString());
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.dialog_end_match, container, false);

        btn_home = (ImageView) thisView.findViewById(R.id.btn_home);
        btn_home.setOnClickListener(this);

        btn_rematch = (ImageView) thisView.findViewById(R.id.btn_rematch);
        btn_rematch.setOnClickListener(this);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //Dialog cannot be canceled, interaction with user is needed
        this.setCancelable(false);

        if (((GameActivity) this.getActivity()).modality == 1) {
            btn_rematch.setVisibility(View.GONE);
        }

        return thisView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        TextView mMatchResult = (TextView) view.findViewById(R.id.tv_match_result);
        TextView mMatchPoints = (TextView) view.findViewById(R.id.tv_match_points);
        mMatchPoints.setText(String.valueOf(args.getInt(KEY_POINTS)) + " " + getString(R.string.points));
        mMatchResult.setText(getResources().getString(args.getInt(KEY_RESULT)));
        Typeface textFont = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.font));
        mMatchPoints.setTypeface(textFont);
        mMatchResult.setTypeface(textFont);

        View dialog = (thisView.findViewById(R.id.ll_dial_end_match));

        //Setup popup animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f,1f,0f,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setDuration(200);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        dialog.startAnimation(scaleAnimation);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_home: {
                goHome();
                break;
            }
            case R.id.btn_rematch: {
                rematch();
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
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
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

    private void rematch() {

        GameController gameController = GameController.getInstance();

        if (GameMaster.getInstance().getPlayers().get(0) instanceof PlayerAI) {
            gameController.startAiVsAiGame(this.getActivity());
            this.getActivity().finish();
        } else {
            gameController.startSinglePlayerGame(this.getActivity());
            this.getActivity().finish();
        }
    }

    public void callDismiss() {
        if (this.getFragmentManager() != null) {
            this.dismissAllowingStateLoss();
        }
    }

}
