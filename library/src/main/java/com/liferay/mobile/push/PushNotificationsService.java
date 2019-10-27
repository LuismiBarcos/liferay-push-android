package com.liferay.mobile.push;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;
import com.liferay.mobile.push.bus.BusUtil;
import com.liferay.mobile.push.util.GoogleServices;
import org.json.JSONObject;

public class PushNotificationsService extends JobService implements Push.OnPushNotification {

	@Override
	public boolean onStartJob(JobParameters jobParameters) {
		try {
			PersistableBundle persistableBundle = jobParameters.getExtras();

			String pushNotification = persistableBundle.getString("pushNotification");

			JSONObject jsonObject = new JSONObject(pushNotification);

			BusUtil.post(jsonObject);
			onPushNotification(jsonObject);
		} catch (Exception e) {
			BusUtil.post(e);
		}
		return false;
	}

	@Override
	public boolean onStopJob(JobParameters params) {
		return false;
	}

	@Override
	public void onPushNotification(JSONObject pushNotification) {
	}

	public void setGoogleServices(GoogleServices googleServices) {
		_googleService = googleServices;
	}

	private GoogleServices _googleService = new GoogleServices();
}
