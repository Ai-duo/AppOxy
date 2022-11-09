package com.kd.appoxy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kd.appoxy.MainActivity;


public class BootCompletedReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
      Intent activity = new Intent(context, MainActivity.class);
      activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(activity);
	}
}