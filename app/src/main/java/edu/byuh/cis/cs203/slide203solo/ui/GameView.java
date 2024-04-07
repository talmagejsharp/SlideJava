package edu.byuh.cis.cs203.slide203solo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import edu.byuh.cis.cs203.slide203solo.R;
import edu.byuh.cis.cs203.slide203solo.logic.GameBoard;
import edu.byuh.cis.cs203.slide203solo.logic.GameMode;
import edu.byuh.cis.cs203.slide203solo.logic.Player;
import edu.byuh.cis.cs203.slide203solo.util.TickListener;
import edu.byuh.cis.cs203.slide203solo.util.Timer;

/**
 * The GameView class runs the user interface of our app.
 * All drawing is initiated here; all touch events are
 * handled here.
 */
public class GameView extends ImageView implements TickListener {

    private Grid grid;
    private boolean firstRun;
    private GuiButton currentButton;
    private GuiButton[] buttons;
    private List<GuiToken> tokens;
    private GameBoard engine;
    private Timer timer;
    private int xScore, oScore;
    private Paint xScorePaint, oScorePaint;
    private GameMode mode;
    private MediaPlayer soundtrack;

    //In the original Slide game, the computer was X
    //and the human was O, so I've kept that here.
    //But it doesn't really matter.
    private static final Player COMPUTER = Player.X;
    private static final Player HUMAN = Player.O;

    private final String X_NAME = getResources().getString(R.string.x_name);
    private final String O_NAME = getResources().getString(R.string.o_name);

    /**
     * Basic constructor. Initializes all fields that don't
     * directly rely on the screen resolution being known.
     *
     * @param context The Activity class that created the View
     */
    public GameView(Context context) {
        super(context);


//        showWelcomeDialog();
        firstRun = true;
        if(SplashScreen.onePlayer){
            mode = GameMode.ONE_PLAYER;
        }else{
            mode = GameMode.TWO_PLAYER;
        }
        buttons = new GuiButton[10];
        tokens = new ArrayList<GuiToken>();
        engine = new GameBoard();
        timer = Timer.getInstance(Pref.getSpeed(context));
        xScore = oScore = 0;
        xScorePaint = new Paint();
        xScorePaint.setColor(Color.RED);
        oScorePaint = new Paint(xScorePaint);
        xScorePaint.setTextAlign(Paint.Align.LEFT);
        oScorePaint.setTextAlign(Paint.Align.RIGHT);

        if(Pref.soundOn(context)){
            soundtrack = MediaPlayer.create(context,R.raw.seasick_dream);
            soundtrack.start();
            soundtrack.setLooping(true);
        }

    }

    private void pauseMusic(){
        if(soundtrack!=null) {
            soundtrack.pause();
        }
    }
    private void restartMusic(){
        if(soundtrack!=null) {
            soundtrack.start();
        }
    }
    public void justGotBackgrounded(){
        if(soundtrack!=null) {
            pauseMusic();
        }
    }

    public void justGotForegrounded(){
        if(soundtrack!=null) {
            restartMusic();
        }
    }
    public void cleanupBeforeShutdown(){
        if(soundtrack!=null) {
            soundtrack.release();
        }
    }

//    private void showWelcomeDialog() {
//        AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
//        ab.setTitle("SLIDE")
//                .setMessage("Are you playing by yourself or with a friend?")
//                .setPositiveButton("Alone", (dialog, which) -> mode = GameMode.ONE_PLAYER)
//                .setNegativeButton("With a friend", (dialog, which) -> mode = GameMode.TWO_PLAYER)
//                .show();
//    }


    /**
     * Draws the grid, buttons, and tokens.
     *
     * @param c The Canvas object, supplied by the system.
     */
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        //c.drawColor(Color.WHITE);
        if (firstRun) {
            init();
            firstRun = false;
        }

        grid.draw(c);

        for (GuiToken t : tokens) {
            t.draw(c);
        }
        for (GuiButton b : buttons) {
            b.draw(c);
        }

        c.drawText(X_NAME + ": " + xScore, 0, xScorePaint.descent() * 3, xScorePaint);
        c.drawText(O_NAME + ": " + oScore, getWidth(), oScorePaint.descent() * 3, oScorePaint);
        //c.drawText("Bro. Draper's Example Project", 0, getHeight() * 0.9f, xScorePaint);
    }

    /**
     * creates the buttons and calculates cellsize.
     */
    private void init() {
        float w = getWidth();
        float h = getHeight();
        float unit = w / 16f;
        float gridX = unit * 2.5f;
        float cellSize = unit * 2.3f;
        float gridY = unit * 9;
        float buttonTop = gridY - cellSize;
        float buttonLeft = gridX - cellSize;
        grid = new Grid(gridX, gridY, cellSize);
        buttons[0] = new GuiButton('1', this, buttonLeft + cellSize * 1, buttonTop, cellSize);
        buttons[1] = new GuiButton('2', this, buttonLeft + cellSize * 2, buttonTop, cellSize);
        buttons[2] = new GuiButton('3', this, buttonLeft + cellSize * 3, buttonTop, cellSize);
        buttons[3] = new GuiButton('4', this, buttonLeft + cellSize * 4, buttonTop, cellSize);
        buttons[4] = new GuiButton('5', this, buttonLeft + cellSize * 5, buttonTop, cellSize);
        buttons[5] = new GuiButton('A', this, buttonLeft, buttonTop + cellSize * 1, cellSize);
        buttons[6] = new GuiButton('B', this, buttonLeft, buttonTop + cellSize * 2, cellSize);
        buttons[7] = new GuiButton('C', this, buttonLeft, buttonTop + cellSize * 3, cellSize);
        buttons[8] = new GuiButton('D', this, buttonLeft, buttonTop + cellSize * 4, cellSize);
        buttons[9] = new GuiButton('E', this, buttonLeft, buttonTop + cellSize * 5, cellSize);
        float scoreFontSize = MainActivity.findThePerfectFontSize(h / 25f);
        xScorePaint.setTextSize(scoreFontSize);
        oScorePaint.setTextSize(scoreFontSize);

        timer.subscribe(this);
    }

    //this would have been easier with a HashMap...
    //not sure why I didn't do it that way instead.
    private GuiButton getButtonByLabel(char label) {
        for (GuiButton b : buttons) {
            if (b.getLabel() == label) {
                return b;
            }
        }
        return null; //this should never happen! :-)
    }

    /**
     * Handles all touch events.
     *
     * @param m an object that contains the (x,y) coordinates
     *          of the touch event (among other things)
     * @return true, always. (Just like the Church!)
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {

        //ignore touch events if the View is not fully initialized
        if (grid == null || firstRun) return true;

        //ignore touch events if there are any "moving" tokens on-screen.
        if (GuiToken.anyMoving()) return true;

        float x = m.getX();
        float y = m.getY();
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            //Main.say("finger down!");
            currentButton = null;
            for (GuiButton b : buttons) {
                if (b.contains(x, y)) {
                    currentButton = b;
                    b.press();
                    break;
                }
            }

            //show a helpful hint if the user taps inside the grid
            if (currentButton == null) {
                Toast t = Toast.makeText(getContext(),
                        getResources().getString(R.string.toast_message),
                        Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();
            }

        } else if (m.getAction() == MotionEvent.ACTION_MOVE) {
            boolean touchingAButton = false;
            for (GuiButton b : buttons) {
                if (b.contains(x, y)) {
                    touchingAButton = true;
                    if (currentButton != null && b != currentButton) {
                        currentButton.release();
                        currentButton = null;
                        break;
                    }
                }
            }
            if (!touchingAButton) {
                unselectAllButtons();
            }
        } else if (m.getAction() == MotionEvent.ACTION_UP) {
            for (GuiButton b : buttons) {
                if (b.contains(x, y)) {
                    if (b == currentButton) {
                        currentButton.release();
                        handleButtonPress();
                    }
                }
            }
            currentButton = null;
        }
        return true;
    }

    /**
     * will submit a move according to the button pressed
     */
    private void handleButtonPress() {
        cleanupFallenTokens();
        char label = currentButton.getLabel();
        GuiToken tok = new GuiToken(engine.getCurrentPlayer(), currentButton, getResources());
        tokens.add(tok);
        timer.subscribe(tok);
        engine.submitMove(label);

        List<GuiToken> neighbors = new ArrayList<GuiToken>();
        neighbors.add(tok);
        if (currentButton.isTopButton()) {
            //we're moving down
            for (char row = 'A'; row <= 'E'; row++) {
                GuiToken tokenInColumn = getTokenAt(row, label);
                if (tokenInColumn != null) {
                    neighbors.add(tokenInColumn);
                } else {
                    break;
                }
            }
            for (GuiToken t : neighbors) {
                t.moveDown();
            }
        } else {
            //we're moving right
            for (char col = '1'; col <= '5'; col++) {
                GuiToken tokenInRow = getTokenAt(label, col);
                if (tokenInRow != null) {
                    neighbors.add(tokenInRow);
                } else {
                    break;
                }
            }
            for (GuiToken t : neighbors) {
                t.moveRight();
            }
        }
    }

    /**
     * Gets the character at a given row
     * @param row
     * @param col
     * @return
     */
    private GuiToken getTokenAt(char row, char col) {
        for (GuiToken gt : tokens) {
            if (gt.matches(row, col)) {
                return gt;
            }
        }
        return null;
    }

    /**
     * releases all the buttons
     */
    private void unselectAllButtons() {
        for (GuiButton b : buttons) {
            b.release();
        }
    }

    /**
     * Defined by the TickListener interface.
     * The GameView responds to
     */
    @Override
    public void onTick() {
        if (!GuiToken.anyMoving()) {
            Player winner = engine.checkForWin();
            if (winner != Player.BLANK) {
                showEndgameDialog(winner);
            } else {
                if (mode == GameMode.ONE_PLAYER
                        && engine.getCurrentPlayer() == COMPUTER) {
                    computerMove();
                }
            }
        }
        invalidate();
    }

    /**
     * starts another thread and asks the engine for the next move
     */
    private void computerMove() {
        post(() -> {
                char move = engine.suggestNextMove();
                currentButton = getButtonByLabel(move);
                handleButtonPress();
            }
        );
    }

//    private void computerMove() {
//        class ComputerTurn extends AsyncTask<Void, Void, Character> {
//            @Override
//            protected Character doInBackground(Void... args) {
//                return engine.suggestNextMove();
//            }
//
//            @Override
//            protected void onPostExecute(Character move) {
//                currentButton = getButtonByLabel(move);
//                handleButtonPress();
//            }
//        }
//        new ComputerTurn().execute();
//    }

    /**
     * Shows the final Dialog after the game is won
     * @param winner
     */
    private void showEndgameDialog(Player winner) {
        timer.pause();
        String message;
        if (winner == Player.O) {
            message = O_NAME + getResources().getString(R.string.win);
            oScore++;
        } else if (winner == Player.X) {
            message = X_NAME + getResources().getString(R.string.win);
            xScore++;
        } else {
            message = getResources().getString(R.string.tie_message);
        }
        AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
        ab.setMessage(message)
                .setTitle(getResources().getString(R.string.endgame_title))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.play_again), (dialog, which) -> reset())
                .setNegativeButton(getResources().getString(R.string.quit), (dialog, which) ->
                        {
                            shutdown();
                            ((Activity) getContext()).finish();
                        })
                .show();
    }

    /**
     * ends the timer
     */
    private void shutdown() {
        timer.shutdown();
    }

    private void reset() {
        engine.clear();
        tokens.clear();
        timer.deregisterAll();
        timer.subscribe(this);
        timer.restart();
    }

    /**
     * deletes all the old tokens
     */
    private void cleanupFallenTokens() {
        //THE OLD WAY
//        List<GuiToken> doomed = new ArrayList<GuiToken>();
//        for (GuiToken t : tokens) {
//            if (t.isInvisible(getHeight())) {
//                doomed.add(t);
//            }
//        }
//        for (GuiToken d : doomed) {
//            tokens.remove(d);
//            timer.unsubscribe(d);
//        }
        //THE NEW WAY
        Predicate<GuiToken> fallen = d -> d.isInvisible(getHeight());
        tokens.stream().filter(fallen).forEach(d -> timer.unsubscribe(d));
        tokens.removeIf(fallen);
    }


}

