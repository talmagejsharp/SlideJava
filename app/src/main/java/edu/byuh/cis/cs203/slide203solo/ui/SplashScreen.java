package edu.byuh.cis.cs203.slide203solo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.EventListener;

import edu.byuh.cis.cs203.slide203solo.R;

public class SplashScreen extends Activity {

    private ImageView iv;
    public static boolean onePlayer;

    /**
     * creates an image view and sets the background of the splash screen to the given image
     * @param b
     */
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        iv = new ImageView(this);
        iv.setImageResource(R.drawable.tropical_spash);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(iv);

    }

    /**
     * This method recieves on touch events, and if they are close to the button it will call a method accordingly.
     * @param m
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent m){
        if(m.getAction()==MotionEvent.ACTION_DOWN){
            Intent i = new Intent(this,MainActivity.class);
            Intent settings = new Intent(this,Pref.class);
            m.getX();
            m.getY();
            Display display = getWindowManager().getDefaultDisplay();
            int w = iv.getWidth();
            int h = iv.getHeight();
            if(m.getX() < w*0.4 && m.getY()>h*0.75){
                onePlayer = true;
                startActivity(i);
            }else if( m.getX() <w*0.4 && m.getY()<h*0.3){
                AlertDialog.Builder about = new AlertDialog.Builder(iv.getContext());
                about.setTitle(getResources().getString(R.string.about_title))
                        .setMessage(getResources().getString(R.string.about))
                        .setCancelable(true)
                        .show();

            }else if(m.getX() >w*0.6 && m.getY() >0.75*h){
                onePlayer = false;
                startActivity(i);
            }else if(m.getX()>w*0.6 && m.getY()<0.3*h){
                startActivity(settings);
            }
        }
        return true;
    }
}
