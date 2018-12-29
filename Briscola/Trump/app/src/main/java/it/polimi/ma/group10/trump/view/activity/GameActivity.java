package it.polimi.ma.group10.trump.view.activity;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.controller.GameController;
import it.polimi.ma.group10.trump.model.game.Card;
import it.polimi.ma.group10.trump.model.game.GameMaster;
import it.polimi.ma.group10.trump.model.game.GameTable;
import it.polimi.ma.group10.trump.model.game.PlayerAI;
import it.polimi.ma.group10.trump.model.game.RemoteGameMaster;
import it.polimi.ma.group10.trump.model.persistance.SharedPreferencePersistence;
import it.polimi.ma.group10.trump.service.BackgroundSoundService;
import it.polimi.ma.group10.trump.view.dialog.EndMatchDialogFragment;
import it.polimi.ma.group10.trump.view.dialog.LeaveMatchDialogFragment;
import it.polimi.ma.group10.trump.view.dialog.RemoteMatchInterruptedDialogFragment;
import it.polimi.ma.group10.trump.view.dialog.YourTurnDialogFragment;

public class GameActivity extends BaseActivity implements View.OnClickListener {

    private ImageView[] playerHand = new ImageView[3];

    private ImageView[] opponentHand = new ImageView[3];

    private ProgressBar progressBarCountdown;

    private ImageView iw_trump, iw_deck;

    private TextView txt_nr_cards_deck;

    private ImageView btn_suggestion;

    static private int[] opCardPlayerdIdx = new int[2];

    public boolean isTimeToPlay = false;

    public boolean suggestionON = true;

    private boolean soundFxON;

    public int modality;

    public CountDownTimer countDownTimer;

    MediaPlayer soundPlayCard, soundFlipCard, soundDistributeCard1, soundDistributeCard2, soundAssignCard, soundTimer, soundWin, soundDraw, soundLose, soundDistribution;

    public Handler gameHandler;

    public Handler UIManagementHandler;

    private ImageView[] tableCards = new ImageView[2];
    private final int PLAYER_TABLE_POSITION = 0;
    private final int OPPONENT_TABLE_POSITION = 1;

    private final GameActivity self = this;

    private long delay_ms = 500;
    private String type = "n";
    private long speedSetting = (long) 2;

    boolean justCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        justCreated = true;

        modality = (int) this.getIntent().getExtras().get("modality");
        findViews();
        Typeface textFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        ((TextView) findViewById(R.id.txt_nr_cards_deck)).setTypeface(textFont);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (justCreated) {
            if (SharedPreferencePersistence.getInstance(this).getBackgroundMusic()) {
                Intent svc = new Intent(this, BackgroundSoundService.class);
                stopService(svc);
                Bundle b = new Bundle();
                b.putInt("songID", 1);
                svc.putExtras(b);
                startService(svc);
            }
            getValuesFromPersistence();
            if (modality == 0) {
                getInitialValuesFromModel();
            } else {
                getInitialValuesFromRemoteModel();
            }

            initializeAutomaticAnimations();

            initializeHandlers();

            loadSounds();

            initializeTimer();

            lockImageViewsToCurrentDimension();
            justCreated = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        this.countDownTimer.cancel();
        this.gameHandler.getLooper().getThread().interrupt();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        isBackPressed = true;

        FragmentManager fm = getSupportFragmentManager();
        LeaveMatchDialogFragment leaveMatchDialog = LeaveMatchDialogFragment.newInstance();
        leaveMatchDialog.show(fm, "test");

    }

    /**
     * This method manages the click on the player cards
     *
     * @param v the view clicked
     */
    @Override
    public void onClick(View v) {
        final GameController gameController = GameController.getInstance();
        switch (v.getId()) {
            case R.id.iw_card0: {
                this.countDownTimer.cancel();
                progressBarCountdown.setVisibility(View.INVISIBLE);
                Log.i("selected card", "0");
                if (!isTimeToPlay) {
                    return;
                }
                isTimeToPlay = false;
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                gameHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (modality == 0) {
                            gameController.playCard(0, playerHand[0], self);
                        } else {
                            gameController.remotePlayCard(0, playerHand[0], self);
                        }
                    }

                });

                break;
            }
            case R.id.iw_card1: {
                Log.i("selected card", "1");
                this.countDownTimer.cancel();
                progressBarCountdown.setVisibility(View.INVISIBLE);
                if (!isTimeToPlay) {
                    return;
                }
                isTimeToPlay = false;
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                gameHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (modality == 0) {
                            gameController.playCard(1, playerHand[1], self);
                        } else {
                            gameController.remotePlayCard(1, playerHand[1], self);
                        }
                    }

                });

                break;

            }
            case R.id.iw_card2: {
                Log.i("selected card", "2");
                this.countDownTimer.cancel();
                progressBarCountdown.setVisibility(View.INVISIBLE);
                if (!isTimeToPlay) {
                    return;
                }

                isTimeToPlay = false;
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                gameHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (modality == 0) {
                            gameController.playCard(2, playerHand[2], self);
                        } else {
                            gameController.remotePlayCard(2, playerHand[2], self);
                        }
                    }

                });

                break;

            }
            case R.id.btn_suggestion: {
                if (!isTimeToPlay) {
                    return;
                }
                isTimeToPlay = false;
                gameHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        gameController.getSuggestion(self);
                    }

                });
                ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setInterpolator(new LinearInterpolator());
                scaleAnimation.setFillEnabled(false);
                scaleAnimation.setDuration(50);
                btn_suggestion.startAnimation(scaleAnimation);
                break;
            }
        }
    }

    /**
     * This method is charged to find all the views coming from game XML
     */
    private void findViews() {
        playerHand[0] = (ImageView) findViewById(R.id.iw_card0);
        playerHand[1] = (ImageView) findViewById(R.id.iw_card1);
        playerHand[2] = (ImageView) findViewById(R.id.iw_card2);
        opponentHand[0] = (ImageView) findViewById(R.id.iw_op_card0);
        opponentHand[1] = (ImageView) findViewById(R.id.iw_op_card1);
        opponentHand[2] = (ImageView) findViewById(R.id.iw_op_card2);
        tableCards[OPPONENT_TABLE_POSITION] = (ImageView) findViewById(R.id.iw_op_card);
        tableCards[PLAYER_TABLE_POSITION] = (ImageView) findViewById(R.id.iw_card_pl);
        iw_trump = (ImageView) findViewById(R.id.iw_trump);
        iw_deck = (ImageView) findViewById(R.id.iw_deck);
        txt_nr_cards_deck = (TextView) findViewById(R.id.txt_nr_cards_deck);
        btn_suggestion = (ImageView) findViewById(R.id.btn_suggestion);
        progressBarCountdown = (ProgressBar) findViewById(R.id.pb_progress_countdown);
    }

    /**
     * This method initializes the coundownTimer for the player move
     */
    void initializeTimer() {
        this.countDownTimer = new CountDownTimer((long) 20000, (long) 100) {
            @Override
            public void onTick(final long millisUntilFinished) {
                if (soundFxON) {
                    soundTimer.start();
                }
                progressBarCountdown.setProgress(((int) millisUntilFinished * (100) / 20000));
                progressBarCountdown.invalidate();
            }

            @Override
            public void onFinish() {
                /*isTimeToPlay = false;
                Log.i("tempo esaurito", "addio");
                FragmentManager fm = getSupportFragmentManager();
                RemoteMatchInterruptedDialogFragment playerLostDialogFragment = RemoteMatchInterruptedDialogFragment.newInstance("You are out of time!");
                playerLostDialogFragment.show(fm, "test");

                if (modality != 0) {
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            RemoteGameMaster.getInstance().endRemoteGame("time's up!");
                        }
                    };
                    gameHandler.post(myRunnable);

                }*/
                progressBarCountdown.setVisibility(View.INVISIBLE);
                isTimeToPlay = false;
                int idx = 0;
                if (playerHand[2].getVisibility() == View.VISIBLE) {
                    idx = ThreadLocalRandom.current().nextInt(0, 3);
                } else if (playerHand[1].getVisibility() == View.VISIBLE) {
                    idx = ThreadLocalRandom.current().nextInt(0, 2);
                }

                final int card = idx;

                gameHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (modality == 0) {
                            GameController.getInstance().playCard(card, playerHand[card], self);
                        } else {
                            GameController.getInstance().remotePlayCard(card, playerHand[card], self);
                        }
                    }

                });
            }
        };
    }

    /**
     * This method is charged to retrieve the initial values of the match
     * from the model that manages the local match
     */
    private void getInitialValuesFromModel() {
        ArrayList<Card> playerCards = GameMaster.getInstance().getPlayers().get(0).getHand();
        ArrayList<Card> opponentCards = GameMaster.getInstance().getPlayers().get(1).getHand();

        int i;
        for (i = 0; i < playerCards.size(); i++) {
            playerHand[i].setImageResource(getResources().getIdentifier("card_" + type + "_" + playerCards.get(i).toString().toLowerCase(), "drawable", this.getPackageName()));
            playerHand[i].setVisibility(View.VISIBLE);
            playerHand[i].setOnClickListener(this);
        }
        for (; i < playerHand.length; i++) {
            playerHand[i].setVisibility(View.GONE);
            playerHand[i].setOnClickListener(this);
        }
        for (i = 0; i < opponentCards.size(); i++) {
            opponentHand[i].setImageResource(getResources().getIdentifier("card_" + type + "_back", "drawable", this.getPackageName()));
            opponentHand[i].setVisibility(View.VISIBLE);
        }
        for (; i < opponentHand.length; i++) {
            opponentHand[i].setImageResource(getResources().getIdentifier("card_" + type + "_back", "drawable", this.getPackageName()));
            opponentHand[i].setVisibility(View.GONE);
        }

        GameTable gameTable = GameTable.getInstance();

        if (gameTable.getDeck().getCards().size() > 0) {
            Card trump = gameTable.getDeck().getCards().get(GameTable.getInstance().getDeck().getCards().size() - 1);

            iw_trump.setImageResource(getResources().getIdentifier("card_" + type + "_" + trump.toString().toLowerCase(), "drawable", this.getPackageName()));
            iw_deck.setImageResource(getResources().getIdentifier("card_" + type + "_back", "drawable", this.getPackageName()));
            iw_trump.setVisibility(View.VISIBLE);
            iw_deck.setVisibility(View.VISIBLE);
        } else {
            iw_trump.setVisibility(View.GONE);
            iw_deck.setVisibility(View.GONE);
        }

        ArrayList<Card> cardsOnTable = gameTable.getCardsOnTable();
        if (cardsOnTable.size() == 0) {
            tableCards[OPPONENT_TABLE_POSITION].setVisibility(View.INVISIBLE);
            tableCards[PLAYER_TABLE_POSITION].setVisibility(View.INVISIBLE);
        } else if (cardsOnTable.size() == 1) {
            if (gameTable.getCurrentPlayer() == 0) {
                tableCards[OPPONENT_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + cardsOnTable.get(0).toString().toLowerCase(), "drawable", this.getPackageName()));
                tableCards[PLAYER_TABLE_POSITION].setVisibility(View.INVISIBLE);
            } else {
                tableCards[PLAYER_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + cardsOnTable.get(0).toString().toLowerCase(), "drawable", this.getPackageName()));
                tableCards[OPPONENT_TABLE_POSITION].setVisibility(View.INVISIBLE);
            }
        } else {
            if (gameTable.getCurrentPlayer() == 0) {
                tableCards[OPPONENT_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + cardsOnTable.get(0).toString().toLowerCase(), "drawable", this.getPackageName()));
                tableCards[PLAYER_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + cardsOnTable.get(1).toString().toLowerCase(), "drawable", this.getPackageName()));
            } else {
                tableCards[OPPONENT_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + cardsOnTable.get(1).toString().toLowerCase(), "drawable", this.getPackageName()));
                tableCards[PLAYER_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + cardsOnTable.get(0).toString().toLowerCase(), "drawable", this.getPackageName()));
            }
        }

        txt_nr_cards_deck.setText(String.valueOf(gameTable.getDeck().getCards().size()));
        if (GameMaster.getInstance().getPlayers().get(0) instanceof PlayerAI || !suggestionON) {
            btn_suggestion.setVisibility(View.INVISIBLE);
        } else if (suggestionON) {
            btn_suggestion.setVisibility(View.VISIBLE);
            btn_suggestion.setOnClickListener(this);
        }
        progressBarCountdown.setVisibility(View.INVISIBLE);

    }

    /**
     * This method is charged to retrieve the initial values of the match
     * from the model that manages the remote game
     */
    private void getInitialValuesFromRemoteModel() {
        RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
        ArrayList<Card> playerCards = remoteGameMaster.getLocalPlayer().getHand();
        int i;
        for (i = 0; i < playerCards.size(); i++) {
            playerHand[i].setImageResource(getResources().getIdentifier("card_" + type + "_" + playerCards.get(i).toString().toLowerCase(), "drawable", this.getPackageName()));
            playerHand[i].setVisibility(View.VISIBLE);
            playerHand[i].setOnClickListener(this);
        }

        for (i = 0; i < opponentHand.length; i++) {
            opponentHand[i].setImageResource(getResources().getIdentifier("card_" + type + "_back", "drawable", this.getPackageName()));
            opponentHand[i].setVisibility(View.VISIBLE);
        }

        tableCards[OPPONENT_TABLE_POSITION].setVisibility(View.INVISIBLE);
        tableCards[PLAYER_TABLE_POSITION].setVisibility(View.INVISIBLE);

        iw_trump.setImageResource(getResources().getIdentifier("card_" + type + "_" + remoteGameMaster.getTrump().toString().toLowerCase(), "drawable", this.getPackageName()));
        iw_trump.setVisibility(View.VISIBLE);

        iw_deck.setImageResource(getResources().getIdentifier("card_" + type + "_back", "drawable", this.getPackageName()));
        iw_deck.setVisibility(View.VISIBLE);

        txt_nr_cards_deck.setText(String.valueOf(remoteGameMaster.getRemainingCardsInDeck()));
        if (remoteGameMaster.getLocalPlayer() instanceof PlayerAI || !suggestionON) {
            btn_suggestion.setVisibility(View.INVISIBLE);
        } else if (suggestionON) {
            btn_suggestion.setVisibility(View.VISIBLE);
            btn_suggestion.setOnClickListener(this);
        }
        progressBarCountdown.setVisibility(View.INVISIBLE);
    }

    /**
     * This method is charged to retrieve the values saved in settings
     */
    private void getValuesFromPersistence() {
        SharedPreferencePersistence persistence = SharedPreferencePersistence.getInstance(self);
        findViewById(R.id.lin_playground).setBackground(getDrawable(persistence.getBoardTheme()));
        type = persistence.getCardTheme();
        soundFxON = persistence.getSoundFx();
        suggestionON = persistence.getHintPref();
    }

    /**
     * This method initialise the automatic animations that will handle
     * the hands of the two players
     */
    private void initializeAutomaticAnimations() {
        opponentHand[opCardPlayerdIdx[0]].setCameraDistance(getResources().getDisplayMetrics().density * 8000);
        ViewGroup mContainerView = (ViewGroup) findViewById(R.id.lin_op_cards);
        LayoutTransition lt = new LayoutTransition();
        lt.setDuration(100 * speedSetting);
        lt.disableTransitionType(LayoutTransition.DISAPPEARING);
        lt.disableTransitionType(LayoutTransition.APPEARING);
        mContainerView.setLayoutTransition(lt);

        mContainerView = (ViewGroup) findViewById(R.id.lin_pl_cards);
        lt = new LayoutTransition();
        lt.setDuration(100 * speedSetting);
        lt.disableTransitionType(LayoutTransition.DISAPPEARING);
        lt.disableTransitionType(LayoutTransition.APPEARING);
        mContainerView.setLayoutTransition(lt);
    }

    /**
     * This method is charged to initialize the handlers necessary to the game,
     * - the one to run the buisness logic
     * - the one to run the user interface
     */
    private void initializeHandlers() {
        HandlerThread GHT = new HandlerThread("gth");
        GHT.start();
        gameHandler = new Handler(GHT.getLooper());
        UIManagementHandler = new Handler(this.getMainLooper());
    }

    /**
     * This voi method is charged to load the sounds that will be used for the current match
     * if the soundFX option is active
     */
    private void loadSounds() {
        if (soundFxON) {
            soundDistribution = MediaPlayer.create(getApplicationContext(), R.raw.sound_start_distribution_cards);
            soundPlayCard = MediaPlayer.create(getApplicationContext(), R.raw.sound_play_card);
            soundFlipCard = MediaPlayer.create(getApplicationContext(), R.raw.sound_flip_card);
            soundDistributeCard1 = MediaPlayer.create(getApplicationContext(), R.raw.sound_distribute_card);
            soundDistributeCard2 = MediaPlayer.create(getApplicationContext(), R.raw.sound_distribute_card);
            soundAssignCard = MediaPlayer.create(getApplicationContext(), R.raw.sound_assign_card);
            soundTimer = MediaPlayer.create(getApplicationContext(), R.raw.sound_timer);
            soundWin = MediaPlayer.create(getApplicationContext(), R.raw.sound_win);
            soundLose = MediaPlayer.create(getApplicationContext(), R.raw.sound_lose);
            soundDraw = MediaPlayer.create(getApplicationContext(), R.raw.sound_tie);
        }
    }

    /**
     * This void method is charged to lock the ImageViews to their optimal dimension chosed at the match startup
     * to avoid auto adaptment; once this lock is done, the match can start;
     */
    private void lockImageViewsToCurrentDimension() {
        ViewTreeObserver viewTreeObserver = (findViewById(R.id.lin_playground)).getViewTreeObserver();
        final float cameraDist = getResources().getDisplayMetrics().density * 100000;
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {


            @Override
            public void onGlobalLayout() {
                int width = playerHand[0].getWidth();
                int height = playerHand[0].getHeight();
                for (ImageView card : playerHand) {
                    card.setMaxWidth(width);
                    card.setMinimumWidth(width);
                    card.setMaxHeight(height);
                    card.setMinimumHeight(width);
                    card.setCameraDistance(cameraDist);
                }
                width = opponentHand[0].getWidth();
                height = opponentHand[0].getHeight();
                for (ImageView card : opponentHand) {
                    card.setMaxWidth(width);
                    card.setMinimumWidth(width);
                    card.setMaxHeight(height);
                    card.setMinimumHeight(width);
                    card.setCameraDistance(cameraDist);
                }
                width = tableCards[PLAYER_TABLE_POSITION].getWidth();
                height = tableCards[PLAYER_TABLE_POSITION].getHeight();
                for (ImageView card : tableCards) {
                    card.setMaxWidth(width);
                    card.setMinimumWidth(width);
                    card.setMaxHeight(height);
                    card.setMinimumHeight(width);
                    card.setCameraDistance(cameraDist);
                }

                width = iw_deck.getWidth();
                height = iw_deck.getHeight();
                iw_deck.setMaxWidth(width);
                iw_deck.setMinimumWidth(width);
                iw_deck.setMaxHeight(height);
                iw_deck.setMinimumHeight(width);

                //width = iw_trump.getWidth();
                //height = iw_trump.getHeight();
                iw_trump.setMaxWidth(width);
                iw_trump.setMinimumWidth(width);
                iw_trump.setMaxHeight(height);
                iw_trump.setMinimumHeight(width);


                iw_trump.setX(iw_deck.getX() - iw_trump.getWidth() / 2.5f); //adjust trump position to make it visible


                (findViewById(R.id.lin_playground)).getViewTreeObserver().removeOnGlobalLayoutListener(this);

                AnimationSet anp0 = createMoveCardAnimation(iw_deck, playerHand[0], false, 0f, 0f, 0f, 0f, 150);
                anp0.setStartOffset(50);
                AnimationSet anp1 = createMoveCardAnimation(iw_deck, playerHand[1], false, 0f, 0f, 0f, 0f, 150);
                anp0.setStartOffset(100);
                AnimationSet anp2 = createMoveCardAnimation(iw_deck, playerHand[2], false, 0f, 0f, 0f, 0f, 150);
                anp0.setStartOffset(150);
                AnimationSet ano0 = createMoveCardAnimation(iw_deck, opponentHand[0], false, 0f, 0f, 0f, 0f, 100);
                ano0.setStartOffset(200);
                AnimationSet ano1 = createMoveCardAnimation(iw_deck, opponentHand[1], false, 0f, 0f, 0f, 0f, 100);
                ano1.setStartOffset(250);
                AnimationSet ano2 = createMoveCardAnimation(iw_deck, opponentHand[2], false, 0f, 0f, 0f, 0f, 100);
                ano2.setStartOffset(300);

                playerHand[0].startAnimation(anp0);
                playerHand[1].startAnimation(anp1);
                playerHand[2].startAnimation(anp2);
                opponentHand[0].startAnimation(ano0);
                opponentHand[1].startAnimation(ano1);
                opponentHand[2].startAnimation(ano2);

                if (soundFxON) {

                    soundDistribution.start();
                }


                gameHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        GameController.getInstance().moveForwardWithTheMatch(self);
                    }

                }, delay_ms + 400 * speedSetting);
            }
        });
    }

    /**
     * this method is charged to play the card selected by the local player
     *
     * @param card           the selected card index
     * @param iw_card        the selected card ImageView
     * @param playedCard     the Card object played
     * @param remainingCards the # of cards remaing in the player hand
     */
    public void playCard(final int card, final ImageView iw_card, final Card playedCard, final int remainingCards) {

        tableCards[PLAYER_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + playedCard.toString().toLowerCase(), "drawable", self.getPackageName()));


        AnimationSet animationSet = createMoveCardAnimation(iw_card, tableCards[PLAYER_TABLE_POSITION], false, 0f, 0f, 0f, 0f, 300);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                iw_card.setVisibility(View.INVISIBLE);
                iw_card.invalidate();

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tableCards[PLAYER_TABLE_POSITION].setVisibility(View.VISIBLE);
                tableCards[PLAYER_TABLE_POSITION].invalidate();

                rearrangePlayerCards(card, remainingCards);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (soundFxON) {
            soundPlayCard.start();
        }

        tableCards[PLAYER_TABLE_POSITION].startAnimation(animationSet);

    }

    /**
     * This method is charged to play the card when the local player is substituted by an AI
     *
     * @param card           the index of the played card
     * @param playedCard     the played card
     * @param remainingCards the number of cards remaining in the player hand
     */
    public void playCardForAI(final int card, final Card playedCard, final int remainingCards) {

        final ImageView iw_card = playerHand[card];

        tableCards[PLAYER_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + playedCard.toString().toLowerCase(), "drawable", self.getPackageName()));

        AnimationSet animationSet = createMoveCardAnimation(iw_card, tableCards[PLAYER_TABLE_POSITION], false, 0f, 0f, 0f, 0f, 300);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iw_card.setVisibility(View.INVISIBLE);
                iw_card.invalidate();

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tableCards[PLAYER_TABLE_POSITION].setVisibility(View.VISIBLE);
                tableCards[PLAYER_TABLE_POSITION].invalidate();

                rearrangePlayerCards(card, remainingCards);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (soundFxON) {
            soundPlayCard.start();
        }
        tableCards[PLAYER_TABLE_POSITION].startAnimation(animationSet);

    }

    /**
     * This method is charged to show the card distribution
     *
     * @param cardForPlayer           the card for the local player
     * @param deckRemainingCards      the number of cards remaining in the deck
     * @param firstPlayerToDistribute the index of the player to start with the distribution
     */
    public void distributeCards(final Card cardForPlayer, final int deckRemainingCards, final int firstPlayerToDistribute) {

        final boolean plRotation = (deckRemainingCards == 0) && (firstPlayerToDistribute == 1);
        final boolean opRotation = (deckRemainingCards == 0) && (firstPlayerToDistribute == 0);

        ImageView plCard = plRotation ? iw_trump : iw_deck;
        ImageView opCard = opRotation ? iw_trump : iw_deck;

        int speedDistribution = 250;
        float rotPivotXPl, rotPivotYPl, rotPivotXOp, rotPivotYOp, transPivotXPl, transPivotYPl, transPivotXOp, transPivotYOp;


        rotPivotXPl = 0f;
        rotPivotYPl = 0f;
        transPivotXPl = 0f;
        transPivotYPl = 0f;

        rotPivotXOp = 0f;
        rotPivotYOp = 0f;
        transPivotXOp = 0f;
        transPivotYOp = 0f;


        if (plRotation) {
            playerHand[2].setImageResource(getResources().getIdentifier("card_" + type + "_" + cardForPlayer.toString().toLowerCase(), "drawable", self.getPackageName()));
        } else {
            playerHand[2].setImageResource(getResources().getIdentifier("card_" + type + "_" + "back", "drawable", self.getPackageName()));
        }

        if (opRotation) {
            opponentHand[opCardPlayerdIdx[0]].setImageDrawable(iw_trump.getDrawable());
        } else if (deckRemainingCards == 0) {
            iw_deck.setVisibility(View.GONE);
        }


        final AnimationSet cardToPlayer = createMoveCardAnimation(plCard, playerHand[2], plRotation, rotPivotXPl, rotPivotYPl, transPivotXPl, transPivotYPl, speedDistribution);
        final AnimationSet cardToOpponent = createMoveCardAnimation(opCard, opponentHand[opCardPlayerdIdx[0]], opRotation, rotPivotXOp, rotPivotYOp, transPivotXOp, transPivotYOp, speedDistribution);


        cardToPlayer.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (soundFxON) {
                    soundDistributeCard2.start();
                }
                playerHand[2].setVisibility(View.VISIBLE);
                playerHand[2].invalidate();
                if (plRotation) {
                    iw_trump.setVisibility(View.INVISIBLE);
                    iw_trump.invalidate();
                } else if (deckRemainingCards == 0) {
                    iw_deck.setVisibility(View.INVISIBLE);
                    iw_deck.invalidate();
                }
            }


            @Override
            public void onAnimationEnd(Animation animation) {
                if (!plRotation) {

                    ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(playerHand[2], "rotationY", 0.0f, 90f);
                    objectAnimator2.setDuration(80 * speedSetting);
                    objectAnimator2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            playerHand[2].setImageResource(getResources().getIdentifier("card_" + type + "_" + cardForPlayer.toString().toLowerCase(), "drawable", self.getPackageName()));
                            playerHand[2].invalidate();

                            ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(playerHand[2], "rotationY", -90f, 0.0f);
                            objectAnimator3.setDuration(80 * speedSetting);
                            objectAnimator3.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (firstPlayerToDistribute == 0) {
                                        opponentHand[opCardPlayerdIdx[0]].startAnimation(cardToOpponent);
                                    } else {

                                        if (deckRemainingCards == 0) {
                                            txt_nr_cards_deck.setVisibility(View.INVISIBLE);
                                            txt_nr_cards_deck.invalidate();
                                        } else {
                                            txt_nr_cards_deck.setText(String.valueOf(deckRemainingCards));
                                            txt_nr_cards_deck.invalidate();
                                        }

                                        gameHandler.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                GameController.getInstance().moveForwardWithTheMatch(self);
                                            }

                                        }, delay_ms);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            objectAnimator3.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    if (soundFxON) {
                        soundFlipCard.start();
                    }

                    objectAnimator2.start();


                    //endif
                } else {


                    if (firstPlayerToDistribute == 0) {
                        opponentHand[opCardPlayerdIdx[0]].startAnimation(cardToOpponent);
                    } else {

                        if (deckRemainingCards == 0) {
                            txt_nr_cards_deck.setVisibility(View.INVISIBLE);
                            txt_nr_cards_deck.invalidate();
                        } else {
                            txt_nr_cards_deck.setText(String.valueOf(deckRemainingCards));
                            txt_nr_cards_deck.invalidate();
                        }

                        gameHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                GameController.getInstance().moveForwardWithTheMatch(self);
                            }

                        }, delay_ms);
                    }
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cardToOpponent.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (soundFxON) {
                    soundDistributeCard1.start();
                }
                opponentHand[opCardPlayerdIdx[0]].setVisibility(View.VISIBLE);
                opponentHand[opCardPlayerdIdx[0]].invalidate();
                if (opRotation) {

                    iw_trump.setVisibility(View.INVISIBLE);
                    iw_trump.invalidate();

                } else if (deckRemainingCards == 0) {
                    iw_deck.setVisibility(View.INVISIBLE);
                    iw_deck.invalidate();
                }


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (opRotation) {
                    ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(opponentHand[opCardPlayerdIdx[0]], "rotationY", 0.0f, 90f);
                    objectAnimator5.setDuration(80 * speedSetting);
                    objectAnimator5.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            opponentHand[opCardPlayerdIdx[0]].setImageResource(getResources().getIdentifier("card_" + type + "_back", "drawable", self.getPackageName()));
                            opponentHand[opCardPlayerdIdx[0]].invalidate();

                            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(opponentHand[opCardPlayerdIdx[0]], "rotationY", -90f, 0.0f);
                            objectAnimator1.setDuration(80 * speedSetting);
                            objectAnimator1.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (firstPlayerToDistribute == 1) {
                                        playerHand[2].startAnimation(cardToPlayer);
                                    } else {

                                        if (deckRemainingCards == 0) {
                                            txt_nr_cards_deck.setVisibility(View.INVISIBLE);
                                            txt_nr_cards_deck.invalidate();
                                        } else {
                                            txt_nr_cards_deck.setText(String.valueOf(deckRemainingCards));
                                            txt_nr_cards_deck.invalidate();
                                        }

                                        gameHandler.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                GameController.getInstance().moveForwardWithTheMatch(self);
                                            }

                                        }, delay_ms);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            objectAnimator1.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    if (soundFxON) {
                        soundFlipCard.start();
                    }
                    objectAnimator5.start();


                } else {
                    //begin else
                    if (firstPlayerToDistribute == 1) {
                        playerHand[2].startAnimation(cardToPlayer);
                    } else {

                        if (deckRemainingCards == 0) {
                            txt_nr_cards_deck.setVisibility(View.INVISIBLE);
                            txt_nr_cards_deck.invalidate();
                        } else {
                            txt_nr_cards_deck.setText(String.valueOf(deckRemainingCards));
                            txt_nr_cards_deck.invalidate();
                        }

                        gameHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                GameController.getInstance().moveForwardWithTheMatch(self);
                            }

                        }, delay_ms);
                    }
                    //end else
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        if (firstPlayerToDistribute == 0) {
            playerHand[2].startAnimation(cardToPlayer);

        } else {
            opponentHand[opCardPlayerdIdx[0]].startAnimation(cardToOpponent);
        }


    }

    /**
     * This method is charged to show the result of the match
     *
     * @param winnerId the winner of the match
     * @param points   the amount of points gained by the local player
     */
    public void declareMatchWinner(int winnerId, int points) {
        if (soundFxON) {
            if (winnerId == 0) {
                soundWin.start();
            } else if (winnerId == 1) {
                soundLose.start();
            } else {
                soundDraw.start();
            }
        }
        btn_suggestion.setVisibility(View.INVISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        EndMatchDialogFragment endMatchDialog = EndMatchDialogFragment.newInstance(winnerId, points);
        endMatchDialog.show(fm, "test");
    }

    /**
     * This method is charged to show the round winner and give to him de cards
     *
     * @param winnerId the winner of the round
     */
    public void assignRoundToWinner(int winnerId) {

        for (ImageView card : tableCards) {
            card.setVisibility(View.INVISIBLE);
            card.invalidate();
        }


        if (soundFxON) {
            soundAssignCard.start();
        }

        if (winnerId == 0) {

            AnimationSet plCardAnimation = createLaunchCardAnimation(tableCards[PLAYER_TABLE_POSITION], playerHand[0], 0, 80);
            AnimationSet opCardAnimation = createLaunchCardAnimation(tableCards[OPPONENT_TABLE_POSITION], playerHand[0], 0, 80);

            tableCards[PLAYER_TABLE_POSITION].startAnimation(plCardAnimation);
            tableCards[OPPONENT_TABLE_POSITION].startAnimation(opCardAnimation);

        } else {

            AnimationSet plCardAnimation = createLaunchCardAnimation(tableCards[PLAYER_TABLE_POSITION], opponentHand[0], 1, 80);
            AnimationSet opCardAnimation = createLaunchCardAnimation(tableCards[OPPONENT_TABLE_POSITION], opponentHand[0], 1, 80);

            tableCards[PLAYER_TABLE_POSITION].startAnimation(plCardAnimation);
            tableCards[OPPONENT_TABLE_POSITION].startAnimation(opCardAnimation);

        }

        gameHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                GameController.getInstance().moveForwardWithTheMatch(self);
            }

        }, delay_ms);

    }

    /**
     * This method is charged to show the played card by the opponent
     *
     * @param playedCard     the actual Card played
     * @param remainingCards the amount of cards remaining in the opponent hand
     */
    public void playCardOpponent(final Card playedCard, final int remainingCards) {

        int cardToHide;

        if (remainingCards == 2) {

            opCardPlayerdIdx[0] = ThreadLocalRandom.current().nextInt(0, 3);
            cardToHide = opCardPlayerdIdx[0];

        } else if (remainingCards == 1) {

            int[] availCards = new int[2];
            if (opCardPlayerdIdx[0] == 0) {
                availCards[0] = 1;
                availCards[1] = 2;
            } else if (opCardPlayerdIdx[0] == 1) {
                availCards[0] = 0;
                availCards[1] = 2;
            } else {
                availCards[0] = 0;
                availCards[1] = 1;
            }


            int idx = ThreadLocalRandom.current().nextInt(0, 2);
            opCardPlayerdIdx[1] = availCards[idx];
            cardToHide = opCardPlayerdIdx[1];

        } else {

            if (opCardPlayerdIdx[0] + opCardPlayerdIdx[1] == 1) {
                cardToHide = 2;
            } else if (opCardPlayerdIdx[0] + opCardPlayerdIdx[1] == 2) {
                cardToHide = 1;
            } else {
                cardToHide = 0;
            }

        }

        final int temp = cardToHide;


        AnimationSet animationSet = this.createMoveCardAnimation(opponentHand[cardToHide], tableCards[OPPONENT_TABLE_POSITION], false, 0f, 0f, 0f, 0f, 300);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                opponentHand[temp].setVisibility(View.INVISIBLE);
                opponentHand[temp].invalidate();
                tableCards[OPPONENT_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + "back", "drawable", self.getPackageName()));
                tableCards[OPPONENT_TABLE_POSITION].setVisibility(View.VISIBLE);
                tableCards[OPPONENT_TABLE_POSITION].invalidate();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tableCards[OPPONENT_TABLE_POSITION], "rotationY", 0.0f, 90f);
                objectAnimator.setDuration(80 * speedSetting);
                objectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tableCards[OPPONENT_TABLE_POSITION].setImageResource(getResources().getIdentifier("card_" + type + "_" + playedCard.toString().toLowerCase(), "drawable", self.getPackageName()));

                        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(tableCards[OPPONENT_TABLE_POSITION], "rotationY", -90f, 0.0f);
                        objectAnimator1.setDuration(80 * speedSetting);
                        objectAnimator1.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (remainingCards == 0) {
                                    opponentHand[temp].setVisibility(View.INVISIBLE);
                                } else {
                                    opponentHand[temp].setVisibility(View.GONE);
                                }

                                opponentHand[temp].invalidate();

                                gameHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        GameController.getInstance().moveForwardWithTheMatch(self);
                                    }

                                }, delay_ms);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        if (soundFxON) {
                            soundFlipCard.start();
                        }

                        objectAnimator1.start();


                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                objectAnimator.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (soundFxON) {
            soundPlayCard.start();
        }

        tableCards[OPPONENT_TABLE_POSITION].startAnimation(animationSet);

    }

    /**
     * This method is charged to rearrange the cards placement in the player hand
     *
     * @param cardPlayed     the last card played
     * @param remainingCards the amount of cards remaining after the move
     */
    private void rearrangePlayerCards(int cardPlayed, int remainingCards) {

        if (remainingCards == 2) {
            if (cardPlayed == 0) { //ok

                playerHand[0].setVisibility(View.INVISIBLE);
                playerHand[0].setImageDrawable(playerHand[1].getDrawable());

                AnimationSet animationSet1 = createMoveCardAnimation(playerHand[1], playerHand[0], false, 0f, 0f, 0f, 0f, 100);

                animationSet1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        playerHand[1].setVisibility(View.INVISIBLE);
                        playerHand[1].invalidate();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        playerHand[0].setVisibility(View.VISIBLE);
                        playerHand[0].invalidate();

                        playerHand[2].setVisibility(View.INVISIBLE);
                        playerHand[1].setImageDrawable(playerHand[2].getDrawable());
                        AnimationSet animationSet2 = createMoveCardAnimation(playerHand[2], playerHand[1], false, 0f, 0f, 0f, 0f, 200);

                        animationSet2.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                playerHand[1].setVisibility(View.VISIBLE);
                                playerHand[1].invalidate();
                                playerHand[2].setVisibility(View.GONE);
                                playerHand[2].invalidate();

                                gameHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        GameController.getInstance().moveForwardWithTheMatch(self);
                                    }

                                }, delay_ms);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        playerHand[1].startAnimation(animationSet2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                playerHand[0].startAnimation(animationSet1);

            } else if (cardPlayed == 1) {//OK

                playerHand[1].setVisibility(View.INVISIBLE);
                playerHand[1].setImageDrawable(playerHand[2].getDrawable());
                playerHand[1].invalidate();

                AnimationSet animationSet = createMoveCardAnimation(playerHand[2], playerHand[1], false, 0f, 0f, 0f, 0f, 100);


                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        playerHand[2].setVisibility(View.INVISIBLE);
                        playerHand[2].invalidate();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        playerHand[1].setVisibility(View.VISIBLE);
                        playerHand[1].invalidate();
                        playerHand[2].setVisibility(View.GONE);
                        playerHand[2].invalidate();

                        gameHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                GameController.getInstance().moveForwardWithTheMatch(self);
                            }

                        }, delay_ms);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                playerHand[1].startAnimation(animationSet);


            } else { // ok
                playerHand[2].setVisibility(View.GONE);
                playerHand[2].invalidate();

                gameHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        GameController.getInstance().moveForwardWithTheMatch(self);
                    }

                }, delay_ms);

            }
        } else if (remainingCards == 1) {
            if (cardPlayed == 0) { //toredo
                playerHand[0].setVisibility(View.INVISIBLE);
                playerHand[0].setImageDrawable(playerHand[1].getDrawable());
                playerHand[0].invalidate();

                AnimationSet animationSet = createMoveCardAnimation(playerHand[1], playerHand[0], false, 0f, 0f, 0f, 0f, 100);


                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        playerHand[1].setVisibility(View.INVISIBLE);
                        playerHand[1].invalidate();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        playerHand[0].setVisibility(View.VISIBLE);
                        playerHand[0].invalidate();
                        playerHand[1].setVisibility(View.GONE);
                        playerHand[1].invalidate();

                        gameHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                GameController.getInstance().moveForwardWithTheMatch(self);
                            }

                        }, delay_ms);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                playerHand[0].startAnimation(animationSet);
            } else { //ok
                playerHand[1].setVisibility(View.GONE);
                playerHand[1].invalidate();


                gameHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        GameController.getInstance().moveForwardWithTheMatch(self);
                    }

                }, delay_ms);
            }
        } else {//ok
            playerHand[0].setVisibility(View.INVISIBLE);
            playerHand[0].invalidate();

            gameHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    GameController.getInstance().moveForwardWithTheMatch(self);
                }

            }, delay_ms);
        }


    }

    /**
     * This method creates the animation set that will show a card movement
     *
     * @param source           the animation starting place
     * @param destination      the animation ending place
     * @param rotate           if is required to rotate (trump distribution case)
     * @param rotationPivotX
     * @param rotationPivotY
     * @param translatioPivotX
     * @param translatioPivotY
     * @param duration         the animation duration
     * @return the AnimationSet created
     */
    private AnimationSet createMoveCardAnimation(ImageView source, ImageView destination, boolean rotate, float rotationPivotX, float rotationPivotY, float translatioPivotX, float translatioPivotY, int duration) {

        int[] prevLoc = new int[2];
        source.getLocationOnScreen(prevLoc);
        int[] nextLoc = new int[2];
        destination.getLocationOnScreen(nextLoc);
        float prevWidth = source.getMeasuredWidth();
        float prevHeight = source.getMeasuredHeight();
        float nextWidth = destination.getMeasuredWidth();
        float nextHeight = destination.getMeasuredHeight();

        float startingScaleWidth = (prevWidth) / nextWidth;
        float startingScaleHeight = (prevHeight) / nextHeight;
        TranslateAnimation translateAnimation = new TranslateAnimation(prevLoc[0] - nextLoc[0], 0, prevLoc[1] - nextLoc[1], 0);

        ScaleAnimation scaleAnimation = new ScaleAnimation(startingScaleWidth, 1f, startingScaleHeight, 1f, Animation.RELATIVE_TO_SELF, translatioPivotX, Animation.RELATIVE_TO_SELF, translatioPivotY);

        AnimationSet animationSet = new AnimationSet(true);

        animationSet.setRepeatMode(0);
        animationSet.setDuration(duration * speedSetting);
        animationSet.setFillEnabled(true);


        if (rotate) {
            RotateAnimation rotateAnimation = new RotateAnimation(-90, 0, Animation.RELATIVE_TO_SELF, rotationPivotX, Animation.RELATIVE_TO_SELF, rotationPivotY);
            animationSet.addAnimation(rotateAnimation);
        }

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);


        return animationSet;
    }

    /**
     * This method creates the animation set for the card assignment after a round ending
     *
     * @param source             the card to distribute
     * @param targetDimenstionIW the reference imageview to get the dimension of the final image after the animation
     * @param targetPlayer       the player to which launch the card
     * @param duration           the animation duration
     * @return the created AnimationSet
     */
    private AnimationSet createLaunchCardAnimation(ImageView source, ImageView targetDimenstionIW, int targetPlayer, int duration) {

        int[] prevLoc = new int[2];
        source.getLocationOnScreen(prevLoc);
        int prevWidth = source.getMeasuredWidth();
        int prevHeight = source.getMeasuredHeight();
        int nextWidth = targetDimenstionIW.getMeasuredWidth();
        int nextHeight = targetDimenstionIW.getMeasuredHeight();

        float endingScaleWidth = (float) (nextWidth) / (float) (prevWidth);
        float endingScaleHeight = (float) nextHeight / (float) (prevHeight);

        int toXDelta;
        int toYDelta;
        int scale;

        if (targetPlayer == 1) {
            scale = 2;
            toYDelta = -prevLoc[1] - nextHeight - prevHeight;
        } else {
            scale = 3;
            toYDelta = prevLoc[1] + nextHeight + prevHeight + (findViewById(R.id.lin_playground)).getHeight();
        }

        int midCardPos = prevLoc[0] + (prevWidth / 2);

        int midFieldPos = (findViewById(R.id.lin_playground)).getWidth() / 2;

        toXDelta = (midCardPos > midFieldPos) ? (midCardPos - midFieldPos) : (midFieldPos - midCardPos);


        TranslateAnimation translateAnimation = new TranslateAnimation(0, toXDelta, 0, toYDelta);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, endingScaleWidth, 1f, endingScaleHeight, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setRepeatMode(0);
        animationSet.setDuration(duration * speedSetting * scale);

        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(scaleAnimation);

        return animationSet;
    }

    /**
     * This method is charged to show the dialog that notifies the real local player that is his turn
     */
    public void showYourTurnDialog() {
        /*FragmentManager fm = getSupportFragmentManager();
        final YourTurnDialogFragment yourTurnDialog = YourTurnDialogFragment.newInstance();
        yourTurnDialog.show(fm, "test");

        UIManagementHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                yourTurnDialog.callDismiss();*/
                startPlayTimer();
            /*}
        }, 800);*/

    }

    /**
     * This method is charged to show to the local player
     * the suggestion for the next move
     *
     * @param cardIndex the index of the suggested card
     */
    public void showSuggestion(int cardIndex) {

        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setInterpolator(new BounceInterpolator());
        scaleAnimation.setFillEnabled(false);
        scaleAnimation.setDuration(300);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isTimeToPlay = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        playerHand[cardIndex].startAnimation(scaleAnimation);

    }

    /**
     * This method is charged to start the countound timer
     * related to the player move time limit
     */
    private void startPlayTimer() {
        if (!this.isFinishing() || !this.isDestroyed()) {
            this.countDownTimer.start();
            this.progressBarCountdown.setVisibility(View.VISIBLE);
            this.progressBarCountdown.setMax(100);
            this.progressBarCountdown.setProgress(this.progressBarCountdown.getMax());
            this.progressBarCountdown.setInterpolator(new LinearInterpolator());

        }
    }

}
