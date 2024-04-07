package edu.byuh.cis.cs203.slide203solo.ui;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;

/**
 * Our "main" class. Everything starts here.
 */
public class MainActivity extends Activity {

    private GameView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gv = new GameView(this);
        setContentView(gv);
    }

    /**
     * finds the font size dependant on the size of the screen
     * @param dim
     * @return
     */
    public static float findThePerfectFontSize(float dim) {
        float fontSize = 1;
        Paint p = new Paint();
        p.setTextSize(fontSize);
        float lowerThreshold = dim;
        while (true) {
            float asc = -p.getFontMetrics().ascent;
            if (asc > lowerThreshold) {
                break;
            }
            fontSize++;
            p.setTextSize(fontSize);
        }
        return fontSize;
    }
    @Override
    /**
     * When the activity is paused it will call justGotBackGrounded
     */
    public void onPause(){
        super.onPause();
        gv.justGotBackgrounded();
    }

    /**
     * When the activity is resumed it will call justGotForegrounded
     */
    @Override
    public void onResume(){
        super.onResume();
        gv.justGotForegrounded();
    }

    /**
     * When the activity ends it will call cleanupBeforeShutdown
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        gv.cleanupBeforeShutdown();
    }


}