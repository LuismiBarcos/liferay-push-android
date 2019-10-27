/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.push;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;
import com.liferay.mobile.push.util.GoogleServices;
import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public abstract class PushNotificationsReceiver extends BroadcastReceiver {

	public static int JOB_ID = 10001;

	public abstract String getServiceClassName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String className = getServiceClassName();

		try {
			Class clazz = Class.forName(className);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				ComponentName serviceComponent = new ComponentName(context, clazz);
				JobScheduler jobScheduler =
					(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
				JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
				builder.setMinimumLatency(1000);
				builder.setOverrideDeadline(3000);

				PersistableBundle persistableBundle = new PersistableBundle();

				JSONObject pushNotification = _googleService.getPushNotification(context, intent);

				persistableBundle.putString("pushNotification", pushNotification.toString());

				builder.setExtras(persistableBundle);

				jobScheduler.schedule(builder.build());
			} else {
				PushLegacyNotificationsService.enqueueWork(context, clazz, JOB_ID, intent);
			}
		} catch (Exception e) {
			Log.e(PushNotificationsReceiver.class.getName(), "PushReceiver failed", e);
			throw new IllegalArgumentException("Not found service of class " + className);
		}
	}

	private GoogleServices _googleService = new GoogleServices();
}