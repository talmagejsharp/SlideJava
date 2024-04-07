package edu.byuh.cis.cs203.slide203solo.util;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * This class pumps out "timer" events at
 * regular intervals. Anyone who wants to
 * receive these events can add themselves
 * as "tick listeners".
 */
public class Timer extends Handler {

    private List<TickListener> observers;
    private boolean paused;
    private static Timer singleton;
    private static int delay;

    private Timer(int d) {
        observers = new ArrayList<>();
        delay = d;
    }

    public static Timer getInstance(int d) {
        if (singleton == null) {
            singleton = new Timer(d);
        }
        //System.out.println("Timer speed set to "+d);
        delay = d;
        singleton.restart();
        singleton.handleMessage(null);
        return singleton;
    }

    public void subscribe(TickListener t) {
        observers.add(t);
    }

    public void unsubscribe(TickListener t) {
        observers.remove(t);
    }

    public void deregisterAll() {
        observers.clear();
    }


    public void pause() {
        paused = true;
    }

    public void restart() {
        paused = false;
        //handleMessage(obtainMessage());
    }

    @Override
    public void handleMessage(Message m) {
        if (!paused) {
            for (TickListener t : observers) {
                t.onTick();
            }
        }
        sendMessageDelayed(obtainMessage(), delay);
    }

    public void shutdown() {
        pause();
        deregisterAll();
        removeCallbacksAndMessages(null);
    }
}

