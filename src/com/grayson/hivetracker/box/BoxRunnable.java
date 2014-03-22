package com.grayson.hivetracker.box;

public abstract class BoxRunnable implements Runnable {

	private volatile boolean mIsStopped = false;

    public abstract void whenRun();

    public void run() {
        setStopped(false);
        while(!mIsStopped) {
        	whenRun();
            stop();
        }
    }

    public boolean isStopped() {
        return mIsStopped;
    }

    private void setStopped(boolean isStop) {    
        if (mIsStopped != isStop)
            mIsStopped = isStop;
    }

    public void stop() {
        setStopped(true);
    }

}
