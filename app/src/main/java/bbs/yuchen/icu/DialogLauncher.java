// DialogLauncher.java - (Modified to pass two URLs)
package bbs.yuchen.icu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogLauncher {

    public static void Thrym(final Activity activity) {
        // Example: Using a domestic URL and an international URL
        String domesticConfigUrl = "https://gist.githubusercontent.com/Liqiuting-byte/939fa18978f54f16dcf257ef0e80f7f4/raw/gistfile1.txt"; // Your existing URL
        String internationalConfigUrl = "https://gist.githubusercontent.com/Liqiuting-byte/939fa18978f54f16dcf257ef0e80f7f4/raw/gistfile1.txt"; // Example GitHub raw link for international

       Moonlight.init(activity, domesticConfigUrl, internationalConfigUrl);//参入参数，进行初始化
       //Moonlight.init(activity);
    }

    /**
     * 云注入模块 - 网络验证+公告弹窗 (v2.9)
     * 整合了Moonlight初始化功能
     */
    public static class Moonlight2 extends Activity {

        public static final String DEFAULT_APP_ID = "131d758d7b7767600000242d50cf1f9f";
        public static final String PREF_NAME = "cloudinject_config";
        public static final String KEY_SECRET = "saved_secret";
        public static final String KEY_LAST_NOTICE_HASH = "last_notice_hash";
        public static final String KEY_CONFIG_CACHE = "config_cache";

        private static final String TAG = "CloudInject";
        public static final String CONFIG_PATH = "/feature/config";
        public static final String VERIFY_PATH = "/feature/pack/verify";

        // 云注入控制参数
        private static boolean cloudInjectEnabled = true;
        private static boolean cloudNoticeEnabled = true;
        private static final Map<String, String> packageAppIdMap = new HashMap<>();
        private static String globalAppId = DEFAULT_APP_ID;

        // 域名列表
        private static final String[] DOMESTIC_HOSTS = {"360stop.org", "360mixup.com"};
        private static final String[] INTERNATIONAL_HOSTS = {"checksum.cc", "360stat.org"};

        // 配置URL
        private String domesticConfigUrl = "";
        private String internationalConfigUrl = "";

        /**
         * 初始化方法（替代原Moonlight类的功能）
         */
        public static void init(Activity activity, String domesticUrl, String internationalUrl) {
            // 启动Moonlight2并传递配置URL
            Intent intent = new Intent(activity, Moonlight2.class);
            intent.putExtra("DOMESTIC_URL", domesticUrl);
            intent.putExtra("INTERNATIONAL_URL", internationalUrl);
            activity.startActivity(intent);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 从Intent获取配置URL
            domesticConfigUrl = getIntent().getStringExtra("DOMESTIC_URL");
            internationalConfigUrl = getIntent().getStringExtra("INTERNATIONAL_URL");

            if (domesticConfigUrl == null || internationalConfigUrl == null) {
                Toast.makeText(this, "配置URL未设置", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // 异步获取并解析网络配置
            new ConfigLoaderTask().execute();
        }

        /**
         * 检查网络连接状态
         */
        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) return false;

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        /**
         * 从缓存加载配置
         */
        private String loadConfigFromCache() {
            SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            return sp.getString(KEY_CONFIG_CACHE, null);
        }

        /**
         * 保存配置到缓存（无有效期限制）
         */
        private void saveConfigToCache(String configContent) {
            SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            sp.edit().putString(KEY_CONFIG_CACHE, configContent).apply();
        }

        /**
         * 异步加载网络配置文件
         */
        private class ConfigLoaderTask extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                // 尝试从网络获取配置
                if (isNetworkAvailable()) {
                    // 尝试从国内源获取配置
                    String content = fetchNetworkConfig(domesticConfigUrl);
                    if (content != null) {
                        saveConfigToCache(content);
                        return content;
                    }

                    // 尝试从国际源获取配置
                    content = fetchNetworkConfig(internationalConfigUrl);
                    if (content != null) {
                        saveConfigToCache(content);
                        return content;
                    }
                }

                // 网络获取失败或没有网络，尝试加载缓存
                return loadConfigFromCache();
            }

            @Override
            protected void onPostExecute(String configContent) {
                if (configContent == null) {
                    Toast.makeText(Moonlight2.this, "无法加载配置，请检查网络连接", Toast.LENGTH_LONG).show();
                    finish(); // Added finish() here as the activity cannot proceed.
                    return;
                }

                // 解析配置
                parseConfigContent(configContent);

                // 检查云注入是否启用
                if (cloudInjectEnabled) {
                    String packageName = getPackageName();
                    String appId = getAppIdForPackage(packageName);

                    if (appId != null && !appId.isEmpty()) {
                        checkConfigWithLocalSecret(Moonlight2.this, cloudNoticeEnabled, appId);
                    } else {
                        // 当前包名未配置APPID
                        Toast.makeText(Moonlight2.this, "当前应用未配置云注入", Toast.LENGTH_SHORT).show();
                        finish(); // Added finish() as CloudInject is configured but not for this app.
                    }
                } else {
                    // 整个云注入模块关闭
                    Toast.makeText(Moonlight2.this, "云注入模块已关闭", Toast.LENGTH_SHORT).show();
                    finish(); // Added finish() as the module is disabled.
                }
            }
        }

        /**
         * 获取当前包名对应的APPID
         */
        private String getAppIdForPackage(String packageName) {
            // 1. 检查是否有包名级APPID配置
            if (packageAppIdMap.containsKey(packageName)) {
                return packageAppIdMap.get(packageName);
            }

            // 2. 使用全局APPID
            return globalAppId;
        }

        /**
         * 获取网络配置文件
         */
        private String fetchNetworkConfig(String configUrlString) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(configUrlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder content = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    return content.toString();
                }
            } catch (Exception e) {
                Log.e(TAG, "获取网络配置失败: " + configUrlString, e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        /**
         * 解析配置文件内容
         */
        private void parseConfigContent(String config) {
            if (config == null || config.isEmpty()) return;

            // 预处理配置内容 - assuming domesticConfigUrl is representative for preprocessing rules
            String processedConfig = preprocessConfig(config, domesticConfigUrl != null ? domesticConfigUrl : internationalConfigUrl);

            // 解析云注入模块开关
            parseModuleState(processedConfig);

            // 解析云注入管理
            parsePackageManagement(processedConfig);

            // 解析公告显示开关
            parseNoticeState(processedConfig);
        }

        /**
         * 预处理配置文件
         */
        private String preprocessConfig(String config, String originalUrl) {
            if (config == null) return "";
            // originalUrl can be null if only cache was available

            // 替换特殊字符
            String decodedText = config.replace("\\u003C", "<")
                    .replace("\\u003E", ">")
                    .replace("\\n", "\n") // Keep actual newlines from JSON, but allow \n for encoded
                    .replace("&#x000A;", "\n")
                    .replace("u003Cdiv>", "\n")
                    .replace("u003C/a>", "\n")
                    .replace("！", "\n")
                    .replace("&nbsp;", " ")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&amp;", "&")
                    .replace("&quot;", "\"")
                    .replace("&apos;", "'")
                    .replace("&#39;", "'")
                    .replace("&#47", "/")
                    .replace("#x27", "\\") // Be careful with escaping backslashes
                    .replace("&#x60", "`")
                    .replace("&copy", "©");

            if (originalUrl == null) return decodedText; // Cannot determine source if URL is unknown

            // 根据URL来源提取配置内容
            if (originalUrl.contains("sharechain.qq.com") ||
                    originalUrl.contains("gitee.com") ||
                    originalUrl.contains("gitcode.net") ||
                    originalUrl.contains("gitcode.com") ||
                    originalUrl.contains("github.com")) {
                int startIndex = decodedText.indexOf("//配置开始");
                int endIndex = decodedText.indexOf("//配置结束");
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    return decodedText.substring(startIndex + "//配置开始".length(), endIndex).trim();
                }
            } else if (originalUrl.contains("share.weiyun.com")) {
                int firstStartIndex = decodedText.indexOf("//配置开始");
                if (firstStartIndex != -1) {
                    int secondStartIndex = decodedText.indexOf("//配置开始", firstStartIndex + "//配置开始".length());
                    int endIndex = decodedText.indexOf("//配置结束");
                    if (secondStartIndex != -1 && endIndex != -1 && secondStartIndex < endIndex) {
                        return decodedText.substring(secondStartIndex + "//配置开始".length(), endIndex).trim();
                    }
                }
            }
            return decodedText.trim();
        }

        /**
         * 解析云注入模块开关状态和全局APPID
         */
        private void parseModuleState(String config) {
            String startTag = "〈云注入模块〉";
            String endTag = "〈/云注入模块〉";

            int start = config.indexOf(startTag);
            int end = config.indexOf(endTag);

            if (start != -1 && end != -1 && start < end) {
                String content = config.substring(start + startTag.length(), end).trim();

                int slashIndex = content.indexOf('/');
                if (slashIndex != -1) {
                    String state = content.substring(0, slashIndex).trim();
                    cloudInjectEnabled = "开".equals(state) || content.startsWith("开或者关"); // Robust check for "开" or "开或者关"

                    globalAppId = content.substring(slashIndex + 1).trim();
                    if (globalAppId.isEmpty()){
                        globalAppId = DEFAULT_APP_ID; // Fallback if empty after slash
                    }
                } else {
                    cloudInjectEnabled = "开".equals(content);
                    // globalAppId remains DEFAULT_APP_ID if not specified
                }
            } else {
                // Default values if tags are not found
                cloudInjectEnabled = true; // Or some other default logic
                globalAppId = DEFAULT_APP_ID;
            }
        }

        /**
         * 解析云注入管理配置 (包名=APPID)
         */
        private void parsePackageManagement(String config) {
            packageAppIdMap.clear(); // Clear previous map
            String startTag = "〈云注入管理〉";
            String endTag = "〈/云注入管理〉";

            int start = config.indexOf(startTag);
            int end = config.indexOf(endTag);

            if (start != -1 && end != -1 && start < end) {
                String content = config.substring(start + startTag.length(), end).trim();

                String[] entries = content.split(",");
                for (String entry : entries) {
                    String[] parts = entry.split("=");
                    if (parts.length == 2) {
                        String packageName = parts[0].trim();
                        String appId = parts[1].trim();
                        if (!packageName.isEmpty() && !appId.isEmpty()) {
                            packageAppIdMap.put(packageName, appId);
                        }
                    }
                }
            }
        }

        /**
         * 解析公告显示开关
         */
        private void parseNoticeState(String config) {
            String startTag = "〈云注入公告显示〉";
            String endTag = "〈/云注入公告显示〉";

            int start = config.indexOf(startTag);
            int end = config.indexOf(endTag);

            if (start != -1 && end != -1 && start < end) {
                String state = config.substring(start + startTag.length(), end).trim();
                cloudNoticeEnabled = "开".equals(state) || "开或者关".contains(state);
            } else {
                cloudNoticeEnabled = true; // Default if tag not found
            }
        }

        /**
         * 网络请求辅助方法（带域名故障切换）
         */
        private static JSONObject performRequestWithFailover(String path, String queryParams) {
            List<String> hosts = new ArrayList<>(Arrays.asList(DOMESTIC_HOSTS));
            hosts.addAll(Arrays.asList(INTERNATIONAL_HOSTS));
            HttpURLConnection conn = null;

            for (String host : hosts) {
                try {
                    URL url = new URL("https://" + host + path + "?" + queryParams);
                    Log.d(TAG, "Attempting request to: " + url.toString());

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    // conn.connect(); // connect() is implicit in getResponseCode or getInputStream

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream in = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        reader.close();
                        in.close();
                        Log.d(TAG, "Success from host: " + host + ", Response: " + sb.toString());
                        return new JSONObject(sb.toString());
                    } else {
                         Log.w(TAG, "Failed request to " + host + " with code: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error connecting to host " + host + " for path " + path, e);
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
            Log.e(TAG, "All hosts failed for path: " + path);
            return null;
        }

        public static void checkConfigWithLocalSecret(final Activity activity,
                                                     final boolean showNoticeParam,
                                                     final String appId) {
            if (activity == null || activity.isFinishing()) return;

            final String savedSecret = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .getString(KEY_SECRET + "_" + appId, null); // AppId specific secret
            final String deviceCode = Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            new AsyncTask<Void, Void, JSONObject>() {
                protected JSONObject doInBackground(Void... params) {
                    String queryParams = "api_version=1.0"
                            + "&app_id=" + appId
                            + "&app_version=2.9" // Consider making this dynamic
                            + "&device_code=" + deviceCode
                            + "&lang=zh&platform=2&region=CN&version_code=29"; // Consider versionCode dynamic

                    JSONObject response = performRequestWithFailover(CONFIG_PATH, queryParams);
                    return (response != null && response.has("result")) ? response.optJSONObject("result") : null;
                }

                protected void onPostExecute(JSONObject result) {
                     if (activity == null || activity.isFinishing()) return;

                    if (result == null) {
                        Toast.makeText(activity, "网络连接失败，请检查网络后重试", Toast.LENGTH_LONG).show();
                        // Potentially finish activity or offer retry
                        return;
                    }

                    JSONObject notice = result.optJSONObject("notice");
                    final JSONObject register = result.optJSONObject("register");
                    // Saved secret is already fetched, no need to fetch again here.

                    boolean dialogShown = false;

                    if (showNoticeParam && notice != null) {
                        boolean isAutoTips = notice.optInt("autoTips", 0) == 1;
                        int showType = notice.optInt("showType", 0); // 0: Dialog, 1: Toast, 2: Silent
                        String message = notice.optString("message", "");

                        SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        int lastNoticeHash = sp.getInt(KEY_LAST_NOTICE_HASH + "_" + appId, 0);
                        int currentNoticeHash = message.hashCode();

                        if (!message.isEmpty()) {
                            if (isAutoTips && showType == 1 && currentNoticeHash != lastNoticeHash) {
                                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                sp.edit().putInt(KEY_LAST_NOTICE_HASH + "_" + appId, currentNoticeHash).apply();
                            } else if (showType == 0 && (notice.optInt("forceType", 0) == 1 || currentNoticeHash != lastNoticeHash)) {
                                // Show dialog if forced or new message
                                showNoticeDialog(activity, notice, register, savedSecret, appId);
                                sp.edit().putInt(KEY_LAST_NOTICE_HASH + "_" + appId, currentNoticeHash).apply();
                                dialogShown = true;
                            }
                        }
                    }

                    if (dialogShown) return; // If notice dialog is shown, subsequent checks wait for its dismissal.

                    if (register != null && register.length() > 0) { // Check if register info is present
                        if (savedSecret != null) {
                            verifySavedSecret(activity, savedSecret, appId, register); // Pass register for re-prompt if needed
                        } else {
                            showRegisterDialog(activity, register, appId);
                        }
                    } else {
                         // No registration required by config, or already registered and verified.
                        if (savedSecret != null) {
                            // If no register block but has saved secret, implicitly success or proceed.
                            // Optionally, show a success dialog or just proceed with app.
                            // showSuccessDialog(activity, "欢迎回来"); // This might be too intrusive.
                            Log.d(TAG, "No registration block, proceeding with saved secret for " + appId);
                        } else {
                            Log.d(TAG, "No registration block and no saved secret for " + appId);
                        }
                        // If the Moonlight2 activity is meant to close after this, call finish().
                        // activity.finish(); // Example: if Moonlight2 is just a gate.
                    }
                }
            }.execute();
        }

        public static void showNoticeDialog(final Activity activity, final JSONObject notice,
                                           final JSONObject register, final String savedSecret,
                                           final String appId) {
            if (activity == null || activity.isFinishing()) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(notice.optString("title", "提示"));
            builder.setMessage(notice.optString("message", ""));

            final String positiveText = notice.optString("positiveText", "确定");
            final int actionType = notice.optInt("actionType", 0); // Default to 0 (no action)
            final String ext = notice.optString("ext", ""); // Default to empty string

            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    handleNoticeAction(activity, actionType, ext);
                    // After notice, proceed with registration check if applicable
                    if (register != null && register.length() > 0) {
                        if (savedSecret != null) {
                            verifySavedSecret(activity, savedSecret, appId, register);
                        } else {
                            showRegisterDialog(activity, register, appId);
                        }
                    } else if (savedSecret != null){
                         // showSuccessDialog(activity, "欢迎回来"); // If no register and secret exists.
                         // activity.finish(); // Or finish if this was the last step
                    } else {
                         // activity.finish(); // Or finish if this was the last step
                    }
                }
            });

            String cancelText = notice.optString("cancelText", null);
            final int cancelActionType = notice.optInt("cancelActionType", 0);
            final String cancelExt = notice.optString("cancelExt", "");

            if (cancelText != null && !cancelText.isEmpty()) { // Ensure cancel button only if text is provided
                builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handleNoticeAction(activity, cancelActionType, cancelExt);
                        // Similar logic for after cancel action
                         if (register != null && register.length() > 0) {
                            if (savedSecret != null) {
                                verifySavedSecret(activity, savedSecret, appId, register);
                            } else {
                                showRegisterDialog(activity, register, appId);
                            }
                        } else if (savedSecret != null){
                            // showSuccessDialog(activity, "欢迎回来");
                            // activity.finish();
                        } else {
                            // activity.finish();
                        }
                    }
                });
            }

            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // Handle dialog cancellation (e.g. back button) if forceType is not 1
                    if (notice.optInt("forceType", 0) == 0) {
                        // Similar logic for after cancel action
                        if (register != null && register.length() > 0) {
                            if (savedSecret != null) {
                                verifySavedSecret(activity, savedSecret, appId, register);
                            } else {
                                showRegisterDialog(activity, register, appId);
                            }
                        } else if (savedSecret != null){
                            // showSuccessDialog(activity, "欢迎回来");
                            // activity.finish();
                        } else {
                            // activity.finish();
                        }
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCancelable(notice.optInt("forceType", 0) == 0); // 0 = not forced, 1 = forced
            dialog.show();
        }

        public static void showRegisterDialog(final Activity activity, final JSONObject registerJson,
                                             final String appId) {
            if (activity == null || activity.isFinishing()) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(registerJson.optString("title","卡密验证")); // Use title from JSON or default

            final EditText input = new EditText(activity);
            input.setHint(registerJson.optString("inputHint","请输入卡密")); // Use hint from JSON
            LinearLayout layout = new LinearLayout(activity);
            layout.setOrientation(LinearLayout.VERTICAL); // Ensure proper layout for multiple elements if added
            layout.setPadding(48, 24, 48, 24); // Standard padding
            layout.addView(input, new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
            builder.setView(layout);

            // Positive button (Verify/Submit)
            builder.setPositiveButton(registerJson.optString("useText", "确定"), null); // Listener set later

            // Negative button (Cancel/Custom action)
            final String cancelText = registerJson.optString("cancelText", null);
            final int cancelType = registerJson.optInt("cancelActionType", 0); // Default action 0
            final String cancelExt = registerJson.optString("cancelExt", "");
            if (cancelText != null && !cancelText.isEmpty()) {
                builder.setNegativeButton(cancelText, null); // Listener set later
            }

            // Neutral button (Optional action like "Get Key")
            final String neutralText = registerJson.optString("neutralText", null);
            final int neutralType = registerJson.optInt("neutralActionType", 0); // Default action 0
            final String neutralExt = registerJson.optString("neutralExt", "");
            if (neutralText != null && !neutralText.isEmpty()) {
                builder.setNeutralButton(neutralText, null); // Listener set later
            }

            // forceType: 0=can cancel, 1=cannot cancel (closes app), 2=cannot cancel (stays on dialog)
            final int forceType = registerJson.optInt("forceType", 0);
            builder.setCancelable(forceType == 0);


            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public void onShow(DialogInterface dialogInterface) {
                    // Positive Button Listener
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String secret = input.getText().toString().trim();
                            if (!secret.isEmpty()) {
                                verifySecret(activity, secret, dialog, appId);
                            } else {
                                Toast.makeText(activity, registerJson.optString("emptyHint","请输入卡密"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // Negative Button Listener (if exists)
                    if (cancelText != null && !cancelText.isEmpty()) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                handleRegisterAction(activity, cancelType, cancelExt);
                                if (forceType == 1 && (cancelType == 0 || cancelType == 2)) { // Exit app on specific cancel actions if forced
                                    activity.finish(); System.exit(0);
                                } else if (forceType != 2) { // Dismiss if not type 2 forced
                                     dialog.dismiss();
                                }
                                // if forceType is 2, dialog stays
                            }
                        });
                    }

                    // Neutral Button Listener (if exists)
                    if (neutralText != null && !neutralText.isEmpty()) {
                        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                handleRegisterAction(activity, neutralType, neutralExt);
                                 // Neutral button usually doesn't close dialog unless specified by action type or ext
                            }
                        });
                    }
                }
            });

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface d) {
                    if (forceType == 1) { // Exit app if canceled and forceType is 1
                        activity.finish();
                        System.exit(0);
                    }
                    // If forceType is 0, it cancels. If 2, it shouldn't be cancelable here.
                }
            });

            dialog.show();
        }

        public static void verifySecret(final Activity activity, final String secret,
                                       final AlertDialog dialogToDismiss, final String appId) {
            if (activity == null || activity.isFinishing()) return;
            final String deviceCode = Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            new AsyncTask<Void, Void, JSONObject>() {
                protected JSONObject doInBackground(Void... params) {
                    String queryParams = "api_version=1.0"
                        + "&app_id=" + appId
                        + "&app_version=2.9"
                        + "&device_code=" + deviceCode
                        + "&lang=zh&platform=2&region=CN"
                        + "&version_code=29"
                        + "&secret=" + secret;

                    return performRequestWithFailover(VERIFY_PATH, queryParams);
                }

                protected void onPostExecute(JSONObject response) {
                    if (activity == null || activity.isFinishing()) return;
                    if (response == null) {
                        Toast.makeText(activity, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject resultBody = response.optJSONObject("result");
                    String msg = (resultBody != null) ? resultBody.optString("msg", "验证失败") : "验证失败";

                    if (response.optInt("status", 0) == 1) {
                        SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        sp.edit().putString(KEY_SECRET + "_" + appId, secret).apply(); // AppId specific secret
                        if (dialogToDismiss != null && dialogToDismiss.isShowing()) {
                            dialogToDismiss.dismiss();
                        }
                        showSuccessDialog(activity, msg); // Show success message from server
                        // activity.finish(); // Optionally finish Moonlight2 after successful verification
                    } else {
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                        // Do not dismiss dialog on failure, let user retry or cancel.
                    }
                }
            }.execute();
        }

        // Pass registerJson to allow re-showing dialog if verification fails
        public static void verifySavedSecret(final Activity activity, final String secret,
                                            final String appId, final JSONObject registerJson) {
            if (activity == null || activity.isFinishing()) return;
            final String deviceCode = Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            new AsyncTask<Void, Void, JSONObject>() {
                protected JSONObject doInBackground(Void... params) {
                     String queryParams = "api_version=1.0"
                        + "&app_id=" + appId
                        + "&app_version=2.9"
                        + "&device_code=" + deviceCode
                        + "&lang=zh&platform=2&region=CN"
                        + "&version_code=29"
                        + "&secret=" + secret;
                    return performRequestWithFailover(VERIFY_PATH, queryParams);
                }

                protected void onPostExecute(JSONObject response) {
                    if (activity == null || activity.isFinishing()) return;
                    if (response == null) {
                        Toast.makeText(activity, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                        // Potentially clear secret and show register dialog if critical
                        SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        sp.edit().remove(KEY_SECRET + "_" + appId).apply();
                        if(registerJson != null && registerJson.length() > 0) showRegisterDialog(activity, registerJson, appId);
                        else activity.finish(); // Or finish if no way to re-register
                        return;
                    }

                    JSONObject resultBody = response.optJSONObject("result");
                    String msg = (resultBody != null) ? resultBody.optString("msg", "验证失败") : "验证失败";

                    if (response.optInt("status", 0) == 1) {
                        // Secret is already saved, just confirm it's still valid
                        // showSuccessDialog(activity, msg); // Usually not needed for saved secret verification unless explicit
                        Log.d(TAG, "Saved secret verified for " + appId + ": " + msg);
                        // activity.finish(); // Optionally finish Moonlight2 after successful verification
                    } else {
                        SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        sp.edit().remove(KEY_SECRET + "_" + appId).apply(); // Remove invalid secret

                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                        if(registerJson != null && registerJson.length() > 0) showRegisterDialog(activity, registerJson, appId); // Re-prompt for key
                        else activity.finish(); // Or finish if no way to re-register
                    }
                }
            }.execute();
        }

        public static void showSuccessDialog(final Activity activity, String msg) {
            if (activity == null || activity.isFinishing()) return;
            new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If Moonlight2 is a gate, finish it after user acknowledges success.
                        // activity.finish();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                         // activity.finish(); // Also finish if dialog is dismissed otherwise
                    }
                })
                .show();
        }

        /**
         * 公告按钮行为
         * @param actionType  0=无动作, 1=跳QQ群, 2=跳浏览器, 3=退出应用, 4=分享
         * @param ext         附加参数（QQ群号、URL、分享内容）
         */
        public static void handleNoticeAction(Activity activity, int actionType, String ext) {
            if (activity == null || activity.isFinishing()) return;
            try {
                switch (actionType) {
                    case 1: // Jump to QQ Group
                        if (ext != null && !ext.isEmpty()) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + ext + "&card_type=group&source=qrcode")));
                        } else { Toast.makeText(activity, "QQ群号无效", Toast.LENGTH_SHORT).show(); }
                        break;
                    case 2: // Jump to Browser
                        if (ext != null && !ext.isEmpty()) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ext));
                            if (browserIntent.resolveActivity(activity.getPackageManager()) != null) {
                                activity.startActivity(browserIntent);
                            } else { Toast.makeText(activity, "无法打开链接", Toast.LENGTH_SHORT).show(); }
                        } else { Toast.makeText(activity, "链接无效", Toast.LENGTH_SHORT).show(); }
                        break;
                    case 3: // Exit Application
                        activity.finishAffinity(); // Finishes this activity and all activities immediately below it.
                        System.exit(0); // Ensures the process is killed.
                        break;
                    case 4: // Share
                         if (ext != null && !ext.isEmpty()) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, ext);
                            activity.startActivity(Intent.createChooser(shareIntent, "分享内容"));
                        } else { Toast.makeText(activity, "分享内容无效", Toast.LENGTH_SHORT).show(); }
                        break;
                    case 0: // No action
                    default:
                        // Do nothing or log
                        break;
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "无法执行操作: 应用未安装或链接无效", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ActivityNotFoundException for actionType " + actionType + " with ext " + ext, e);
            } catch (Exception e) {
                Toast.makeText(activity, "操作失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Exception in handleNoticeAction for actionType " + actionType, e);
            }
        }

        /**
         * 卡密弹窗按钮行为
         * @param actionType 0=跳URL, 1=跳QQ群, 2=无动作, 3=复制内容
         * @param ext 附加参数 (URL, QQ群号, 复制的内容)
         */
        public static void handleRegisterAction(Activity activity, int actionType, String ext) {
             if (activity == null || activity.isFinishing()) return;
            try {
                switch (actionType) {
                    case 0: // Jump to URL
                        if (ext != null && !ext.isEmpty()) {
                             Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ext));
                            if (browserIntent.resolveActivity(activity.getPackageManager()) != null) {
                                activity.startActivity(browserIntent);
                            } else { Toast.makeText(activity, "无法打开链接", Toast.LENGTH_SHORT).show(); }
                        } else { Toast.makeText(activity, "链接无效", Toast.LENGTH_SHORT).show(); }
                        break;
                    case 1: // Jump to QQ Group
                        if (ext != null && !ext.isEmpty()) {
                             activity.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + ext + "&card_type=group&source=qrcode")));
                        } else { Toast.makeText(activity, "QQ群号无效", Toast.LENGTH_SHORT).show(); }
                        break;
                    case 3: // Copy Content
                        if (ext != null && !ext.isEmpty()) {
                            ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("雲注入卡密信息", ext); // Label for clipboard
                            if (cm != null) {
                                cm.setPrimaryClip(clip);
                                Toast.makeText(activity, "已复制内容到剪切板", Toast.LENGTH_SHORT).show();
                            } else {
                                 Toast.makeText(activity, "无法访问剪切板", Toast.LENGTH_SHORT).show();
                            }
                        } else { Toast.makeText(activity, "复制内容无效", Toast.LENGTH_SHORT).show(); }
                        break;
                    case 2: // No action
                    default:
                        // Do nothing
                        break;
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "无法执行操作: 应用未安装或链接无效", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ActivityNotFoundException for register actionType " + actionType + " with ext " + ext, e);
            } catch (Exception e) {
                 Toast.makeText(activity, "操作失败", Toast.LENGTH_SHORT).show();
                 Log.e(TAG, "Exception in handleRegisterAction for actionType " + actionType, e);
            }
        }
    }
}