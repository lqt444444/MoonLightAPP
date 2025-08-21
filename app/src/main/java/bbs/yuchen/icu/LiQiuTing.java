package bbs.yuchen.icu;

import android.app.Activity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AlertDialog; // Added
import android.content.*;
import android.graphics.Typeface; // Added
import android.graphics.drawable.GradientDrawable; // Added for rounded buttons
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.content.DialogInterface; // Added
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue; // Added
import android.view.Gravity; // Keep for other dialogs if needed, not directly for new register dialog buttons
import android.view.ViewGroup;
import android.widget.Button; // Added
import android.widget.EditText;
import android.widget.ImageView; // Added
import android.widget.LinearLayout; // Keep for other dialogs if needed
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout; // Added

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
import android.content.ClipboardManager;
import android.content.ClipData;

/**
 * 云注入模块 - 网络验证+公告弹窗 (v2.9)
 * 整合了Moonlight初始化功能
 */
public class LiQiuTing extends Activity {

    public static final String DEFAULT_APP_ID = "a19485011d3233000000242d50cf1f95";
    public static final String PREF_NAME = "cloudinject_config";
    public static final String KEY_SECRET = "saved_secret";
    public static final String KEY_LAST_NOTICE_HASH = "last_notice_hash";
    public static final String KEY_CONFIG_CACHE = "config_cache";

    private static final String TAG = "CloudInject"; // Consistent TAG
    public static final String CONFIG_PATH = "/feature/config";
    public static final String VERIFY_PATH = "/feature/pack/verify";

    private static final boolean cloudInjectEnabled = true;
    private static boolean cloudNoticeEnabled = true;
    private static final Map<String, String> packageAppIdMap = new HashMap<>();
    private static final String globalAppId = DEFAULT_APP_ID;

    private static final String[] DOMESTIC_HOSTS = {"360stop.org", "360mixup.com"};
    private static final String[] INTERNATIONAL_HOSTS = {"checksum.cc", "360stat.org"};

    private final String domesticConfigUrl = "https://gist.githubusercontent.com/Liqiuting-byte/939fa18978f54f16dcf257ef0e80f7f4/raw/gistfile1.txt";
    private final String internationalConfigUrl = "https://gist.githubusercontent.com/Liqiuting-byte/939fa18978f54f16dcf257ef0e80f7f4/raw/gistfile1.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ConfigLoaderTask(this).execute();
    }

    private void startMainActivityAndFinish() {
        Log.d(TAG, "Navigating to MainActivity...");//调试代码，臭猫记得生产环境删掉
        Intent intent = new Intent(LiQiuTing.this, MainActivity.class);//创建意图
        startActivity(intent);
        finish();
    }
    
    private static void safeStartMainActivityAndFinish(Activity activity) {
        if (activity == null) return;
        if (activity instanceof LiQiuTing) {
            ((LiQiuTing) activity).startMainActivityAndFinish();
        } else {
            Log.w(TAG, "Attempted to start MainActivity from a non-Moonlight2 activity: " + activity.getClass().getSimpleName() + ". Finishing current activity.");
            activity.finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    private String loadConfigFromCache() {
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sp.getString(KEY_CONFIG_CACHE, null);
    }

    private void saveConfigToCache(String configContent) {
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        sp.edit().putString(KEY_CONFIG_CACHE, configContent).apply();
    }

    private static class ConfigLoaderTask extends AsyncTask<Void, Void, String> {
        private final Activity activity;

        public ConfigLoaderTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Cast activity to Moonlight2 only if it's guaranteed to be so. Safer to pass needed methods to constructor.
            // For now, assuming activity is Moonlight2 for direct method calls based on original code structure.
            LiQiuTing moonlightActivity = (LiQiuTing) activity;
            if (moonlightActivity.isNetworkAvailable()) {
                String content = moonlightActivity.fetchNetworkConfig(moonlightActivity.domesticConfigUrl);
                if (content != null) {
                    moonlightActivity.saveConfigToCache(content);
                    return content;
                }
                content = moonlightActivity.fetchNetworkConfig(moonlightActivity.internationalConfigUrl);
                if (content != null) {
                    moonlightActivity.saveConfigToCache(content);
                    return content;
                }
            }
            return moonlightActivity.loadConfigFromCache();
        }

        @Override
        protected void onPostExecute(String configContent) {
            if (activity == null || activity.isFinishing()) {
                return;
            }

            LiQiuTing moonlightActivity = (LiQiuTing) activity;

            if (configContent == null) {
                Toast.makeText(activity, "无法加载配置，请检查网络连接", Toast.LENGTH_LONG).show();
                safeStartMainActivityAndFinish(activity); // Uses static method
                return;
            }
            moonlightActivity.parseConfigContent(configContent);
            // Access cloudInjectEnabled from Moonlight2. This assumes cloudInjectEnabled is static.
            if (LiQiuTing.cloudInjectEnabled) {
                String packageName = activity.getPackageName();
                String appId = moonlightActivity.getAppIdForPackage(packageName);
                if (appId != null && !appId.isEmpty()) {
                    LiQiuTing.checkConfigWithLocalSecret(activity, LiQiuTing.cloudNoticeEnabled, appId); // Access static field
                } else {
                    Toast.makeText(activity, "当前应用未配置云注入", Toast.LENGTH_SHORT).show();
                    safeStartMainActivityAndFinish(activity);  // Uses static method
                }
            } else {
                Toast.makeText(activity, "云注入模块已关闭", Toast.LENGTH_SHORT).show();
                safeStartMainActivityAndFinish(activity); // Uses static method
            }
        }
    }

    private String getAppIdForPackage(String packageName) {
        if (packageAppIdMap.containsKey(packageName)) {
            return packageAppIdMap.get(packageName);
        }
        return globalAppId;
    }

    private String fetchNetworkConfig(String configUrl) {
        try {
            URL url = new URL(configUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(" ");
                }
                reader.close();
                return content.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "获取网络配置失败: " + configUrl, e);
        }
        return null;
    }

    private void parseConfigContent(String config) {
        if (config == null || config.isEmpty()) return;
        String processedConfig = preprocessConfig(config, domesticConfigUrl);
        parseModuleState(processedConfig);
        WoYaoCaoSiMiaoMiao(processedConfig);
        parseNoticeState(processedConfig);
    }

    private void parseModuleState(String processedConfig) {
    }

    private String preprocessConfig(String config, String originalUrl) {
        if (config == null) return "";
        if (originalUrl == null) return config;
        String decodedText = config.replace("<", "<")
                .replace(">", ">")
                .replace("\n", "\n")
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
                .replace("#x27", "\\")
                .replace("&#x60;", "`")
                .replace("&copy", "©");
        if (originalUrl.contains("sharechain.qq.com") ||
                originalUrl.contains("gitee.com") ||
                originalUrl.contains("gitcode.net") ||
                originalUrl.contains("gitcode.com") ||
                originalUrl.contains("github.com")) {
            int startIndex = decodedText.indexOf("//配置开始");
            int endIndex = decodedText.indexOf("//配置结束");
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                return decodedText.substring(startIndex + "//配置开始".length(), endIndex);
            }
        } else if (originalUrl.contains("share.weiyun.com")) {
            int firstStartIndex = decodedText.indexOf("//配置开始");
            if (firstStartIndex != -1) {
                int secondStartIndex = decodedText.indexOf("//配置开始", firstStartIndex + "//配置开始".length());
                int endIndex = decodedText.indexOf("//配置结束");
                if (secondStartIndex != -1 && endIndex != -1 && secondStartIndex < endIndex) {
                    return decodedText.substring(secondStartIndex + "//配置开始".length(), endIndex);
                }
            }
        }
        return decodedText;
    }


    private void WoYaoCaoSiMiaoMiao(String config) {
        String startTag = "〈云注入管理〉";
        String endTag = "〈/云注入管理〉";
        int start = config.indexOf(startTag);
        int end = config.indexOf(endTag);
        if (start != -1 && end != -1) {
            String content = config.substring(start + startTag.length(), end).trim();
            String[] entries = content.split(",");
            for (String entry : entries) {
                if (entry.contains("=")) {
                    String[] parts = entry.split("=");                    if (parts.length == 2) {
                        String packageName = parts[0].trim();
                        String appId = parts[1].trim();
                        packageAppIdMap.put(packageName, appId);
                    }
                }
            }
        }
    }

    private void parseNoticeState(String config) {
        String startTag = "〈云注入公告显示〉";
        String endTag = "〈/云注入公告显示〉";
        int start = config.indexOf(startTag);
        int end = config.indexOf(endTag);
        if (start != -1 && end != -1) {
            String state = config.substring(start + startTag.length(), end).trim();
            cloudNoticeEnabled = "开".equals(state) || "开或者关".contains(state);
        }
    }

    private static JSONObject performRequestWithFailover(String path, String queryParams) {
        List<String> hosts = new ArrayList<>(Arrays.asList(DOMESTIC_HOSTS));
        hosts.addAll(Arrays.asList(INTERNATIONAL_HOSTS));
        for (String host : hosts) {
            try {
                URL url = new URL("https://" + host + path + "?" + queryParams);
                Log.d(TAG, "Attempting request to: " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = conn.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        sb.append(new String(buffer, 0, len));
                    }
                    in.close();
                    Log.d(TAG, "Success from host: " + host);
                    return new JSONObject(sb.toString());
                } else {
                    Log.w(TAG, "Failed request to " + host + " with code: " + responseCode);
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Error connecting to host " + host, e);
            }
        }
        Log.e(TAG, "All hosts failed for path: " + path);
        return null;
    }

    public static void checkConfigWithLocalSecret(final Activity activity,
                                                  final boolean showNotice,
                                                  final String appId) {
        final String deviceCode = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                String queryParams = "api_version=1.0"
                        + "&app_id=" + appId
                        + "&app_version=2.9"
                        + "&device_code=" + deviceCode
                        + "&lang=zh&platform=2&region=CN&version_code=29";
                JSONObject response = performRequestWithFailover(CONFIG_PATH, queryParams);
                return (response != null) ? response.optJSONObject("result") : null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (activity == null || activity.isFinishing()) {
                    return;
                }

                if (result == null) {
                    Toast.makeText(activity, "网络连接失败，请检查网络后重试", Toast.LENGTH_LONG).show();
                    safeStartMainActivityAndFinish(activity);
                    return;
                }
                JSONObject notice = result.optJSONObject("notice");
                final JSONObject register = result.optJSONObject("register");
                final JSONObject update = result.optJSONObject("update"); // Get update object

                final String savedSecret = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                        .getString(KEY_SECRET, null);
                boolean dialogShown = false;

                // Check for force update first
                if (update != null && update.optInt("forceType", 0) == 1) {
                    showForceUpdateDialog(activity, update);
                    dialogShown = true;
                }
                
                if (dialogShown) return;

                if (showNotice && notice != null) {
                    boolean isAutoTips = notice.optInt("autoTips", 0) == 1;
                    int showType = notice.optInt("showType", 0);
                    String message = notice.optString("message", "");
                    SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    int lastNoticeHash = sp.getInt(KEY_LAST_NOTICE_HASH, 0);
                    int currentNoticeHash = message.hashCode();
                    if (isAutoTips && showType == 1 && !message.isEmpty() && currentNoticeHash != lastNoticeHash) {
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                        sp.edit().putInt(KEY_LAST_NOTICE_HASH, currentNoticeHash).apply();
                        safeStartMainActivityAndFinish(activity);
                        dialogShown = true;
                    } else if (showType != 1) {
                        showNoticeDialog(activity, notice, register, savedSecret, appId, update); // Pass update
                        dialogShown = true;
                    }
                }
                if (dialogShown) return;
                processNextStepAfterDialogs(activity, register, savedSecret, appId, update);
            }
        }.execute();
    }

    private static void processNextStepAfterDialogs(final Activity activity, final JSONObject register, final String savedSecret, final String appId, final JSONObject update) {
        boolean dialogShown = false;
        if (update != null && update.optInt("forceType", 0) != 1) { // Non-forced update
             if (update.optInt("showType", 0) != 0) { // Only show if showType is not 0 (e.g. 1 for dialog)
                showForceUpdateDialog(activity, update);
                dialogShown = true; // Assume update dialog handles continuation or exit
                return; // Stop further processing if non-forced update dialog is shown
            }
        }

        if (register != null) {
            if (savedSecret != null) {
                verifySavedSecret(activity, savedSecret, appId);
            } else {
                showRegisterDialog(activity, register, appId);
            }
        } else {
            if (savedSecret != null) {
                safeStartMainActivityAndFinish(activity);
                 // Success dialog itself handles finish via safeStartMainActivityAndFinish
            } else {
                safeStartMainActivityAndFinish(activity);
            }
        }
    }
    
    /**
     * Displays a notice dialog to the user.
     * The dialog's content (title, message, button texts, actions) is configured by the
     * {@code notice} JSONObject. It can include a positive button and an optional negative button.
     *
     * <p>Button clicks can trigger actions like opening a URL, joining a QQ group, or exiting the app,
     * as defined by {@code actionType} and {@code ext} in the {@code notice} object.
     * After an action is handled (or if the dialog is dismissed/canceled), it proceeds to the
     * next step in the application flow (e.g., update check, registration, or main activity)
     * by calling {@link #processNextStepAfterDialogs(Activity, JSONObject, String, String, JSONObject)}.
     *
     * <p>The dialog's appearance (icon, text colors, button colors) can be customized
     * through static fields in the {@code sharezhifu} and {@code peizhi} classes.
     * The dialog can be made non-cancelable based on the {@code forceType} in the {@code notice} object.
     *
     * @param activity The current activity context.
     * @param notice A JSONObject containing configuration for the notice dialog. Expected keys include:
     *               <ul>
     *                 <li>"title": (Optional) The dialog title. Defaults to "提示".
     *                 <li>"message": (Optional) The main message content of the dialog.
     *                 <li>"positiveText": (Optional) Text for the positive button. Defaults to "确定".
     *                 <li>"actionType": (Optional) Integer defining the action for the positive button.
     *                 <li>"ext": (Optional) String data for the positive button's action (e.g., URL).
     *                 <li>"cancelText": (Optional) Text for the negative button. If null, the button is not shown.
     *                 <li>"cancelActionType": (Optional) Integer defining the action for the negative button.
     *                 <li>"cancelExt": (Optional) String data for the negative button's action.
     *                 <li>"forceType": (Optional) Integer, if not 0, makes the dialog non-cancelable.
     *               </ul>
     */ // Modified to accept 'update' for sequential dialog logic
    public static void showNoticeDialog(final Activity activity, final JSONObject notice,
                                        final JSONObject register, final String savedSecret,
                                        final String appId, final JSONObject update) { 
        changku.resolveColors(activity);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        
        LinearLayout dialogLayout = new LinearLayout(activity);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20));

        if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
            ImageView iconView = new ImageView(activity);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80));
            iconParams.gravity = Gravity.CENTER_HORIZONTAL;
            iconParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 16);
            iconView.setLayoutParams(iconParams);
            MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iconView);
            dialogLayout.addView(iconView);
        }

        TextView titleView = new TextView(activity);
        titleView.setText(notice.optString("title", "提示"));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) {
            titleView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        }
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.gravity = Gravity.CENTER_HORIZONTAL;
        titleParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10);
        dialogLayout.addView(titleView, titleParams);

        TextView messageView = new TextView(activity);
        messageView.setText(notice.optString("message", ""));
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
         if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) {
            messageView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        }
        // Consider peizhi.applyClickableLinks(activity, messageView, notice.optString("message", "")); if available
        dialogLayout.addView(messageView);
        
        builder.setView(dialogLayout);


        final String positiveText = notice.optString("positiveText", "确定");
        final int actionType = notice.optInt("actionType", 0);
        final String ext = notice.optString("ext", null);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            handleNoticeAction(activity, actionType, ext);
            dialog.dismiss();
            processNextStepAfterDialogs(activity, register, savedSecret, appId, update);
        });

        String cancelText = notice.optString("cancelText", null);
        final int cancelType = notice.optInt("cancelActionType", 0);
        final String cancelExt = notice.optString("cancelExt", null);
        if (cancelText != null) { 
            builder.setNegativeButton(cancelText, (dialog, which) -> {
                handleNoticeAction(activity, cancelType, cancelExt);
                dialog.dismiss();
                processNextStepAfterDialogs(activity, register, savedSecret, appId, update);
            });
        }
        
        builder.setOnCancelListener(dialog -> processNextStepAfterDialogs(activity, register, savedSecret, appId, update));

        AlertDialog dialog = builder.create();
        dialog.setCancelable(notice.optInt("forceType", 0) == 0);

        if (dialog.getWindow() != null) {
            // dialog.getWindow().setBackgroundDrawable(peizhi.getDialogBackground(activity));
        }
        
        dialog.setOnShowListener(di -> {
            Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveBtn != null) {
                if (FaQinDeMiaoHaoKeAi._current_button_text_ != 0) {
                    positiveBtn.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
                }
                if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ && FaQinDeMiaoHaoKeAi._current_button_bg_ != 0) {
                    GradientDrawable gradientDrawable = new GradientDrawable(); // 创建一个GradientDrawable用于绘制圆角背景
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE); // 设置形状为矩形
                    gradientDrawable.setColor(FaQinDeMiaoHaoKeAi._current_button_bg_); // 设置背景颜色
                    gradientDrawable.setCornerRadius((float) MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 70f)); // 设置圆角半径为70dp
                    positiveBtn.setBackground(gradientDrawable); // 将圆角背景应用到“确定”按钮
                }
            }
            Button negativeBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (negativeBtn != null) {
                if (FaQinDeMiaoHaoKeAi._current_button_text_ != 0) {
                    negativeBtn.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
                }
                if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ && FaQinDeMiaoHaoKeAi._current_button_bg_ != 0) {
                    GradientDrawable gradientDrawable = new GradientDrawable(); // 创建一个GradientDrawable用于绘制圆角背景
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE); // 设置形状为矩形
                    gradientDrawable.setColor(FaQinDeMiaoHaoKeAi._current_button_bg_); // 设置背景颜色
                    gradientDrawable.setCornerRadius((float) MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 70f)); // 设置圆角半径为70dp
                    negativeBtn.setBackground(gradientDrawable); // 将圆角背景应用到“取消”按钮
                }
            }
        });
        dialog.show();
    }

    /**
     * Displays a dialog for the user to enter a registration key (card secret).
     * The dialog includes a title, an input field for the key, and up to three buttons
     * (Positive, Negative, Neutral) whose text and actions are configurable via the
     * {@code register} JSON object.
     *
     * <p>The positive button is always present and triggers the key verification process.
     * The negative and neutral buttons are optional and can perform actions like opening a URL,
     * joining a QQ group, or copying text to the clipboard, before navigating to the main activity.
     *
     * <p>The dialog's appearance (icon, text colors, button colors) can be customized
     * through static fields in the {@code sharezhifu} and {@code peizhi} classes.
     *
     * <p>If the user cancels the dialog (e.g., by pressing the back button), it will attempt
     * to start the main activity and finish the current one.
     *
     * @param activity The current activity context, used for creating UI elements and accessing resources.
     * @param register A JSONObject containing configuration for the dialog, such as title, hint text,
     *                 button texts, and action types/data for negative/neutral buttons. Expected keys include:
     *                 <ul>
     *                   <li>"title": (Optional) The dialog title. Defaults to "卡密验证" or "Card Key Validation".
     *                   <li>"hint": (Optional) The hint text for the input field. Defaults to "请输入卡密" or "Enter Key".
     *                   <li>"useText": (Optional) The text for the positive button. Defaults to "确定" or "OK".
     *                   <li>"cancelText": (Optional) The text for the negative button. If null, the button is not shown.
     *                   <li>"cancelActionType": (Optional) Integer defining the action for the negative button.
     *                   <li>"cancelExt": (Optional) String data for the negative button's action (e.g., URL).
     *                   <li>"neutralText": (Optional) The text for the neutral button. If null, the button is not shown.
     *                 </ul>
     */
    public static void showRegisterDialog(final Activity activity, final JSONObject register, final String appId) { // 定义一个静态方法，用于显示注册（卡密验证）对话框
        changku.resolveColors(activity); // 调用changku类的方法，解析或初始化颜色配置
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity); // 创建一个Material风格的对话框构建器
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity); // 判断当前系统语言是否为中文

        // Explicitly set null for default buttons to prevent them from showing
        builder.setPositiveButton(null, null); // 显式地将默认的“确定”按钮设为null，防止显示多余的默认按钮
        builder.setNegativeButton(null, null); // 显式地将默认的“取消”按钮设为null，防止显示多余的默认按钮
        builder.setNeutralButton(null, null); // 显式地将默认的“中立”按钮设为null，防止显示多余的默认按钮

        LinearLayout dialogLayout = new LinearLayout(activity); // 创建一个线性布局作为对话框的主要内容视图
        dialogLayout.setOrientation(LinearLayout.VERTICAL); // 设置线性布局的方向为垂直，使其子视图上下排列
        dialogLayout.setPadding(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20)); // 设置对话框内容的内边距（左上右下各20dp）

        if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) { // 如果配置显示月光图标
            ImageView iconView = new ImageView(activity); // 创建一个ImageView用于显示图标
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80)); // 设置图标的布局参数：宽80dp，高80dp
            iconParams.gravity = Gravity.CENTER_HORIZONTAL; // 设置图标在布局中水平居中
            iconParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 16); // 设置图标的下边距为16dp
            iconView.setLayoutParams(iconParams); // 将布局参数应用到图标视图
            MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iconView); // 调用peizhi类的方法，设置月光图标的图片资源
            dialogLayout.addView(iconView); // 将图标视图添加到对话框主布局中
        }

        TextView titleView = new TextView(activity); // 创建一个TextView用于显示对话框标题
        titleView.setText(register.optString("title", isChinese ? "卡密验证" : "Card Key Validation")); // 设置标题文本，如果JSONObject中没有则根据语言默认显示“卡密验证”或“Card Key Validation”
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22); // 设置标题文本大小为22sp
        titleView.setTypeface(Typeface.DEFAULT_BOLD); // 设置标题字体为粗体
        if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) { // 如果当前文本颜色配置不为0（即已设置颜色）
            titleView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_); // 设置标题文本颜色
        }
        titleView.setGravity(Gravity.CENTER_HORIZONTAL); // 设置标题文本水平居中
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // 设置标题的布局参数：宽匹配父容器，高包裹内容
        titleParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10); // 设置标题的下边距为10dp
        dialogLayout.addView(titleView, titleParams); // 将标题视图添加到对话框主布局中

        FrameLayout inputFrame = new FrameLayout(activity); // 创建一个FrameLayout用于包裹输入框，提供额外的布局灵活性（虽然这里效果不明显）
        final EditText input = new EditText(activity); // 创建一个EditText用于用户输入卡密
        input.setHint(register.optString("hint", isChinese ? "请输入卡密，超死猫猫" : "Enter Key")); // 设置输入框的提示文本，如果JSONObject中没有则根据语言默认显示“请输入卡密”或“Enter Key”
        if (FaQinDeMiaoHaoKeAi._current_edittext_text_ != 0) { // 如果当前EditText文本颜色配置不为0
            input.setTextColor(FaQinDeMiaoHaoKeAi._current_edittext_text_); // 设置输入框文本颜色
        }
        if (FaQinDeMiaoHaoKeAi._current_edittext_hint_ != 0) { // 如果当前EditText提示文本颜色配置不为0
            input.setHintTextColor(FaQinDeMiaoHaoKeAi._current_edittext_hint_); // 设置输入框提示文本颜色
        }
        FrameLayout.LayoutParams inputParams = new FrameLayout.LayoutParams( // 设置输入框的布局参数
                ViewGroup.LayoutParams.MATCH_PARENT, // 宽匹配父容器
                ViewGroup.LayoutParams.WRAP_CONTENT); // 高包裹内容
        int margin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 8f); // 将8dp转换为像素作为边距
        inputParams.setMargins(margin, margin, margin, margin); // 设置输入框的四周外边距
        input.setLayoutParams(inputParams); // 将布局参数应用到输入框视图
        inputFrame.addView(input); // 将输入框添加到其包裹布局中
        dialogLayout.addView(inputFrame); // 将包裹输入框的布局添加到对话框主布局中

        // Create a LinearLayout for buttons to ensure vertical stacking and full width
        LinearLayout buttonContainer = new LinearLayout(activity); // 创建一个线性布局用于放置所有按钮
        buttonContainer.setOrientation(LinearLayout.VERTICAL); // 设置按钮布局的方向为垂直，使其内部按钮上下排列
        // Add some top margin to separate buttons from input field
        LinearLayout.LayoutParams buttonContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮容器的布局参数：宽匹配父容器，高包裹内容
        buttonContainerParams.topMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20); // 设置按钮容器的上边距为20dp，与输入框分开
        dialogLayout.addView(buttonContainer, buttonContainerParams); // 将按钮容器添加到对话框主布局中

        builder.setView(dialogLayout); // 将自定义的dialogLayout设置为对话框的内容视图
        builder.setCancelable(false); // 设置对话框不可通过点击外部或按返回键取消，强制用户通过按钮交互

        final AlertDialog dialog = builder.create(); // 使用构建器创建最终的AlertDialog实例

        // Common LayoutParams for buttons to ensure full width and vertical stacking
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams( // 创建按钮的通用布局参数
                ViewGroup.LayoutParams.MATCH_PARENT, // 宽匹配父容器（横向拉伸）
                ViewGroup.LayoutParams.WRAP_CONTENT); // 高包裹内容
        int horizontalMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20); // 计算水平边距（左90dp，右90dp），实现“横着”居中效果
        int verticalMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10); // 计算垂直边距（按钮之间间隔）
        btnParams.setMargins(horizontalMargin, verticalMargin / 2, horizontalMargin, verticalMargin / 2); // 设置按钮的四周外边距

        // Positive Button (确定/OK)
        Button positiveButton = new Button(activity); // 创建“确定”按钮
        positiveButton.setText(register.optString("useText", isChinese ? "确定" : "OK")); // 设置“确定”按钮的文本，如果JSONObject中没有则根据语言默认显示“确定”或“OK”
        positiveButton.setLayoutParams(btnParams); // 将通用布局参数应用到“确定”按钮
        if (FaQinDeMiaoHaoKeAi._current_button_text_ != 0) { // 如果当前按钮文本颜色配置不为0
            positiveButton.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_); // 设置“确定”按钮的文本颜色
        }
        if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ && FaQinDeMiaoHaoKeAi._current_button_bg_ != 0) { // 如果启用动态文本颜色且当前按钮背景颜色配置不为0
            GradientDrawable gradientDrawable = new GradientDrawable(); // 创建一个GradientDrawable用于绘制圆角背景
            gradientDrawable.setShape(GradientDrawable.RECTANGLE); // 设置形状为矩形
            gradientDrawable.setColor(FaQinDeMiaoHaoKeAi._current_button_bg_); // 设置背景颜色
            gradientDrawable.setCornerRadius((float) MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 70f)); // 设置圆角半径为8dp
            positiveButton.setBackground(gradientDrawable); // 将圆角背景应用到“确定”按钮
        }
        positiveButton.setOnClickListener(v -> { // 设置“确定”按钮的点击监听器
            String secret = input.getText().toString().trim(); // 获取输入框中的卡密，并去除首尾空格
            if (!secret.isEmpty()) { // 如果卡密不为空
                verifySecret(activity, secret, dialog, appId); // 调用verifySecret方法验证卡密，并传入对话框实例以便验证完成后关闭
            } else { // 如果卡密为空
                Toast.makeText(activity, isChinese ? "请输入卡密" : "Please enter key", Toast.LENGTH_SHORT).show(); // 弹出提示，要求输入卡密
            }
        });
        buttonContainer.addView(positiveButton); // 将“确定”按钮添加到按钮容器中

        // Negative Button (取消/Exit - if applicable)

        // No need for setOnShowListener to style buttons, as they are styled on creation. // 不需要setOnShowListener来设置按钮样式，因为它们在创建时就已经设置好了。
        // Removed setOnCancelListener as it might conflict with specific button actions. // 移除了setOnCancelListener，因为它可能与特定的按钮动作冲突。
        // The logic for safely starting MainActivity if dialog is canceled is now only in specific button actions. // 如果对话框被取消，安全启动MainActivity的逻辑现在只存在于特定的按钮动作中。

        if (dialog.getWindow() != null) { // 如果对话框的窗口存在
            // dialog.getWindow().setBackgroundDrawable(peizhi.getDialogBackground(activity)); // 这行代码被注释掉了，用于设置对话框窗口的背景（如果需要的话）
        }
        dialog.show(); // 显示对话框
    }
    
    public static void showForceUpdateDialog(final Activity activity, final JSONObject update) {
        changku.resolveColors(activity);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);

        LinearLayout dialogLayout = new LinearLayout(activity);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20));

        if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
            ImageView iconView = new ImageView(activity);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80));
            iconParams.gravity = Gravity.CENTER_HORIZONTAL;
            iconParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 16);
            iconView.setLayoutParams(iconParams);
            MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iconView);
            dialogLayout.addView(iconView);
        }

        TextView titleView = new TextView(activity);
        titleView.setText(update.optString("title", isChinese ? "版本更新" : "Version Update"));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) {
            titleView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        }
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10);
        dialogLayout.addView(titleView, titleParams);

        TextView messageView = new TextView(activity);
        messageView.setText(update.optString("content", ""));
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) {
            messageView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        }
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 16);
        dialogLayout.addView(messageView, messageParams);
        
        // Create a LinearLayout for buttons to ensure vertical stacking and full width
        LinearLayout buttonContainer = new LinearLayout(activity);
        buttonContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams buttonContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonContainerParams.topMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20);
        dialogLayout.addView(buttonContainer, buttonContainerParams);

        final String positiveText = update.optString("positiveText", isChinese ? "立即更新" : "Update Now");
        final String downloadUrl = update.optString("downloadUrl", null);
        
        Button positiveButton = new Button(activity);
        positiveButton.setText(positiveText);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int btnMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10); // Example margin
        btnParams.setMargins(btnMargin, btnMargin / 2, btnMargin, btnMargin / 2);
        positiveButton.setLayoutParams(btnParams);
        if (FaQinDeMiaoHaoKeAi._current_button_text_ != 0) {
            positiveButton.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
        }
        if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ && FaQinDeMiaoHaoKeAi._current_button_bg_ != 0) {
            GradientDrawable gradientDrawable = new GradientDrawable(); // 创建一个GradientDrawable用于绘制圆角背景
            gradientDrawable.setShape(GradientDrawable.RECTANGLE); // 设置形状为矩形
            gradientDrawable.setColor(FaQinDeMiaoHaoKeAi._current_button_bg_); // 设置背景颜色
            gradientDrawable.setCornerRadius((float) MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 70f)); // 设置圆角半径为70dp
            positiveButton.setBackground(gradientDrawable); // 将圆角背景应用到“确定”按钮
        }
        positiveButton.setOnClickListener(v -> {
            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                activity.startActivity(intent);
            }
            if (update.optInt("forceType", 0) == 1) { // 强制更新，退出应用
                 if (activity != null) activity.finishAffinity(); 
                 System.exit(0);
            } else {
                // Only dismiss if not force update and no other action takes over
                ((AlertDialog) ((ViewGroup)v.getParent().getParent()).getTag()).dismiss(); // Dismiss the dialog if it's not a force update and no exit
            }
        });
        buttonContainer.addView(positiveButton);

        final String cancelText = update.optString("cancelText", null);
        if (cancelText != null && update.optInt("forceType", 0) != 1) { // 非强制更新才显示取消按钮
            Button negativeButton = new Button(activity);
            negativeButton.setText(cancelText);
            negativeButton.setLayoutParams(btnParams);
            if (FaQinDeMiaoHaoKeAi._current_button_text_ != 0) {
                negativeButton.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
            }
            if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ && FaQinDeMiaoHaoKeAi._current_button_bg_ != 0) {
                GradientDrawable gradientDrawable = new GradientDrawable(); // 创建一个GradientDrawable用于绘制圆角背景
                gradientDrawable.setShape(GradientDrawable.RECTANGLE); // 设置形状为矩形
                gradientDrawable.setColor(FaQinDeMiaoHaoKeAi._current_button_bg_); // 设置背景颜色
                gradientDrawable.setCornerRadius((float) MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 70f)); // 设置圆角半径为70dp
                negativeButton.setBackground(gradientDrawable); // 将圆角背景应用到“取消”按钮
            }
            negativeButton.setOnClickListener(v -> ((AlertDialog) ((ViewGroup)v.getParent().getParent()).getTag()).dismiss());
            buttonContainer.addView(negativeButton);
        }
        
        builder.setView(dialogLayout);
        builder.setCancelable(update.optInt("forceType", 0) != 1); // 强制更新时不可取消

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // dialog.getWindow().setBackgroundDrawable(peizhi.getDialogBackground(activity));
        }
        // Store dialog in tag for access from click listeners if needed, due to removal of builder.setButton
        dialogLayout.setTag(dialog);
        dialog.show();
    }

    public static void showSignatureFailureDialog(final Activity activity) {
        changku.resolveColors(activity);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);

        LinearLayout dialogLayout = new LinearLayout(activity);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20));

        if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
            ImageView iconView = new ImageView(activity);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80));
            iconParams.gravity = Gravity.CENTER_HORIZONTAL;
            iconParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 16);
            iconView.setLayoutParams(iconParams);
            MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iconView);
            dialogLayout.addView(iconView);
        }

        TextView titleView = new TextView(activity);
        titleView.setText(isChinese ? "签名校验失败" : "Signature Verification Failed");
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) {
            titleView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        }
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10);
        dialogLayout.addView(titleView, titleParams);

        TextView messageView = new TextView(activity);
        messageView.setText(isChinese ? "应用完整性校验失败，请从官方渠道下载应用。" : "App integrity check failed. Please download the app from official channels.");
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) {
            messageView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        }
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 16);
        dialogLayout.addView(messageView, messageParams);

        // Create a LinearLayout for buttons to ensure vertical stacking and full width
        LinearLayout buttonContainer = new LinearLayout(activity);
        buttonContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams buttonContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonContainerParams.topMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20);
        dialogLayout.addView(buttonContainer, buttonContainerParams);
        
        Button positiveButton = new Button(activity);
        positiveButton.setText(isChinese ? "退出" : "Exit");
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int btnMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10); // Example margin
        btnParams.setMargins(btnMargin, btnMargin / 2, btnMargin, btnMargin / 2);
        positiveButton.setLayoutParams(btnParams);
        if (FaQinDeMiaoHaoKeAi._current_button_text_ != 0) {
            positiveButton.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
        }
        if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ && FaQinDeMiaoHaoKeAi._current_button_bg_ != 0) {
            GradientDrawable gradientDrawable = new GradientDrawable(); // 创建一个GradientDrawable用于绘制圆角背景
            gradientDrawable.setShape(GradientDrawable.RECTANGLE); // 设置形状为矩形
            gradientDrawable.setColor(FaQinDeMiaoHaoKeAi._current_button_bg_); // 设置背景颜色
            gradientDrawable.setCornerRadius((float) MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 70f)); // 设置圆角半径为70dp
            positiveButton.setBackground(gradientDrawable); // 将圆角背景应用到“确定”按钮
        }
        positiveButton.setOnClickListener(v -> {
            if (activity != null) activity.finishAffinity(); 
            System.exit(0);
        });
        buttonContainer.addView(positiveButton);

        builder.setView(dialogLayout);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // dialog.getWindow().setBackgroundDrawable(peizhi.getDialogBackground(activity));
        }
        dialog.show();
    }


    public static void verifySecret(final Activity activity, final String secret,// 检查卡密
                                    final AlertDialog dialogToDismiss, 
                                    final String appId) {
        final String deviceCode = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
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

            @Override
            protected void onPostExecute(JSONObject response) {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                if (response == null) {
                    Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
                    return; 
                }
                if (response.optInt("status", 0) == 1) {
                    SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    sp.edit().putString(KEY_SECRET, secret).apply();
                    if (dialogToDismiss != null && dialogToDismiss.isShowing()) dialogToDismiss.dismiss();
                    JSONObject resultObj = response.optJSONObject("result");
                    String msg = (resultObj != null) ? resultObj.optString("msg", "验证成功") : "验证成功";
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    safeStartMainActivityAndFinish(activity);//Li注   安全跳转到main1

                    // Changed to Toast
                    // Do NOT call showSuccessDialog here to prevent auto-transition to MainActivity
                } else {
                    JSONObject resultObj = response.optJSONObject("result");
                    String msg = (resultObj != null) ? resultObj.optString("msg", "验证失败") : "验证失败";
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    public static void verifySavedSecret(final Activity activity, final String secret,
                                         final String appId) {
        final String deviceCode = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
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

            @Override
            protected void onPostExecute(JSONObject response) {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                if (response == null) {
                    Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
                    safeStartMainActivityAndFinish(activity);
                    return;
                }
                if (response.optInt("status", 0) == 1) {
                    SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    sp.edit().putString(KEY_SECRET, secret).apply();
                    JSONObject resultObj = response.optJSONObject("result");
                    String msg = (resultObj != null) ? resultObj.optString("msg", "验证成功") : "验证成功";
                    showSuccessDialog(activity, msg); 
                } else {
                    SharedPreferences sp = activity.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    sp.edit().remove(KEY_SECRET).apply();
                    JSONObject resultObj = response.optJSONObject("result");
                    String msg = (resultObj != null) ? resultObj.optString("msg", "卡密无效，请重新验证") : "卡密无效，请重新验证";
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    JSONObject emptyRegisterForRetry = new JSONObject();
                    try {
                         emptyRegisterForRetry.put("title", MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "卡密验证" : "Card Key Validation");
                        emptyRegisterForRetry.put("hint", MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "请输入卡密" : "Enter Key");
                        emptyRegisterForRetry.put("useText", MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "确定" : "OK");
                        // Add cancelText for explicit Exit option during re-validation
                        emptyRegisterForRetry.put("cancelText", MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "退出" : "Exit");
                    } catch (org.json.JSONException e) { Log.e(TAG, "Error creating JSON for retry dialog", e); }
                    showRegisterDialog(activity, emptyRegisterForRetry, appId);
                }
            }
        }.execute();
    }

    public static void showSuccessDialog(Activity activity, String msg) {
        changku.resolveColors(activity);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);

        LinearLayout dialogLayout = new LinearLayout(activity);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20));

        if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
            ImageView iconView = new ImageView(activity);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80));
            iconParams.gravity = Gravity.CENTER_HORIZONTAL;
            iconParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 16);
            iconView.setLayoutParams(iconParams);
            MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iconView);
            dialogLayout.addView(iconView);
        }
        
        TextView titleView = new TextView(activity);
        titleView.setText(isChinese ? "提示" : "Notice");
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) {
            titleView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        }
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10);
        dialogLayout.addView(titleView, titleParams);

        TextView messageView = new TextView(activity);
        messageView.setText(msg);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        if (FaQinDeMiaoHaoKeAi._current_text_color_ != 0) {
            messageView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        }
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageParams.bottomMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 16);
        dialogLayout.addView(messageView, messageParams);

        // Create a LinearLayout for buttons to ensure vertical stacking and full width
        LinearLayout buttonContainer = new LinearLayout(activity);
        buttonContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams buttonContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonContainerParams.topMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20);
        dialogLayout.addView(buttonContainer, buttonContainerParams);

        Button positiveButton = new Button(activity);
        positiveButton.setText(isChinese ? "确定" : "OK");
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int btnMargin = MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10); // Example margin
        btnParams.setMargins(btnMargin, btnMargin / 2, btnMargin, btnMargin / 2);
        positiveButton.setLayoutParams(btnParams);
        if (FaQinDeMiaoHaoKeAi._current_button_text_ != 0) {
            positiveButton.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
        }
        if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ && FaQinDeMiaoHaoKeAi._current_button_bg_ != 0) {
            GradientDrawable gradientDrawable = new GradientDrawable(); // 创建一个GradientDrawable用于绘制圆角背景
            gradientDrawable.setShape(GradientDrawable.RECTANGLE); // 设置形状为矩形
            gradientDrawable.setColor(FaQinDeMiaoHaoKeAi._current_button_bg_); // 设置背景颜色
            gradientDrawable.setCornerRadius((float) MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 70f)); // 设置圆角半径为70dp
            positiveButton.setBackground(gradientDrawable); // 将圆角背景应用到“确定”按钮
        }
        positiveButton.setOnClickListener(v ->
            safeStartMainActivityAndFinish(activity)
        );
        Toast.makeText(activity, "正在调教不听话的小懒猫.... 请主人稍等", Toast.LENGTH_LONG).show();
        buttonContainer.addView(positiveButton);
        
        builder.setView(dialogLayout);
        
        final AlertDialog dialog = builder.create();
        
        if (dialog.getWindow() != null) {
           // dialog.getWindow().setBackgroundDrawable(peizhi.getDialogBackground(activity)); // This line is now commented out
        }
        dialog.show();
    }

    public static void handleNoticeAction(Activity activity, int actionType, String ext) {
        try {
            if (actionType == 1 && ext != null) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + ext + "&card_type=group&source=qrcode")));
            }
            else if (actionType == 2 && ext != null) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ext)));
            }
            else if (actionType == 3) {
                 if (activity != null) activity.finishAffinity(); 
                 System.exit(0);
            }
            else if (actionType == 4 && ext != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, ext);
                activity.startActivity(Intent.createChooser(shareIntent, "分享内容"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in handleNoticeAction", e);
        }
    }

    public static void handleRegisterAction(Activity activity, int actionType, String ext) {
        try {
            if (actionType == 0 && ext != null) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ext)));
            }
            else if (actionType == 1 && ext != null) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + ext + "&card_type=group&source=qrcode")));
            }
            else if (actionType == 3 && ext != null) {
                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copy", ext);
                if (cm != null) {
                    cm.setPrimaryClip(clip);
                    Toast.makeText(activity, "已复制内容到剪切板", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "复制失败", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in handleRegisterAction", e);
        }
    }
}