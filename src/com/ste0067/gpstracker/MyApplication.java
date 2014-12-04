package com.ste0067.gpstracker;

import android.app.Application;

public class MyApplication extends Application {

	private boolean timerRun;

	public boolean getTimerRun() {
		return timerRun;
	}

	public void setTimerRun(boolean timerRun) {
		this.timerRun = timerRun;
	}
}