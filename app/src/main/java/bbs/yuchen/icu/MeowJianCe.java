package bbs.yuchen.icu;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MeowJianCe {
    @SuppressWarnings("deprecation")
    static class CheckInternetTask extends AsyncTask<Void, Void, Boolean> {
        private final WeakReference<Activity> activityRef;
        private final Moonlight.InternetCheckCallback callback;

        CheckInternetTask(Activity activity, Moonlight.InternetCheckCallback cb) {
            activityRef = new WeakReference<>(activity);
            callback = cb;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Activity act = activityRef.get();
            if (act == null || act.isFinishing()) return false;

            try {
                String[] endpoints = {
                        "https://www.baidu.com",
                        "https://cn.bing.com",
                        "https://www.google.com",
                        "https://1.1.1.1",
                        "https://8.8.8.8"
                };

                for (String endpoint : endpoints) {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
                        conn.setRequestMethod("HEAD");
                        conn.setConnectTimeout((int) FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ / 2);
                        conn.setReadTimeout((int) FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ / 2);

                        String userAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
                        if (FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_) {
                            userAgent += " (Moonlight-Device: " + Moonlight.getRichDeviceInfo(act) + ")";
                        }
                        conn.setRequestProperty("User-Agent", userAgent);

                        int responseCode = conn.getResponseCode();
                        conn.disconnect();

                        if (responseCode == HttpURLConnection.HTTP_OK ||
                                responseCode == HttpURLConnection.HTTP_NO_CONTENT ||
                                (responseCode >= 200 && responseCode < 400)) {
                            return true;
                        }
                    } catch (IOException e) {
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            Activity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;
            if (callback == null) return;
            if (isConnected) {
                callback.onInternetAvailable();
            } else {
                callback.onInternetUnavailable();
            }
        }
    }
}
