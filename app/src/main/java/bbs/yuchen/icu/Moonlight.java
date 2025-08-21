
package bbs.yuchen.icu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Moonlight {


    // Animation Interpolators
    public static final Interpolator _STD_DECEL_ = new DecelerateInterpolator(); // 标准减速插值器
    public static final Interpolator _EMPH_INTERP_ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? // 强调插值器
            new PathInterpolator(0.2f, 0f, 0f, 1f) : new AccelerateDecelerateInterpolator();

    // Built-in Monet Colors
    public static final Map<String, Integer> _MONET_COLORS_ = new HashMap<String, Integer>() {{ // Monet颜色映射 可以直接调用
        put("Red", Color.parseColor("#F44336"));
        put("Pink", Color.parseColor("#E91E63"));
        put("Purple", Color.parseColor("#9C27B0"));
        put("DeepPurple", Color.parseColor("#673AB7"));
        put("Indigo", Color.parseColor("#3F51B5"));
        put("Blue", Color.parseColor("#2196F3"));
        put("LightBlue", Color.parseColor("#03A9F4"));
        put("Cyan", Color.parseColor("#00BCD4"));
        put("Teal", Color.parseColor("#009688"));
        put("Green", Color.parseColor("#4CAF50"));
        put("LightGreen", Color.parseColor("#8BC34A"));
        put("Lime", Color.parseColor("#CDDC39"));
        put("Yellow", Color.parseColor("#FFEB3B"));
        put("Amber", Color.parseColor("#FFC107"));
        put("Orange", Color.parseColor("#FF9800"));
        put("DeepOrange", Color.parseColor("#FF5722"));
        put("Brown", Color.parseColor("#795548"));
        put("Grey", Color.parseColor("#9E9E9E"));
        put("BlueGrey", Color.parseColor("#607D8B"));
        put("PastelBlue", Color.parseColor("#A7C7E7"));
        put("PastelGreen", Color.parseColor("#C1E1C1"));
        put("PastelPink", Color.parseColor("#FFD1DC"));
        put("PastelPurple", Color.parseColor("#C8A2C8"));
        put("PastelYellow", Color.parseColor("#FFFD96"));
        put("PastelOrange", Color.parseColor("#FFD8A9"));
        put("PastelRed", Color.parseColor("#FFB6B9"));
        put("PastelTeal", Color.parseColor("#99D5C9"));
        put("SkyBlue", Color.parseColor("#87CEEB"));
        put("OceanBlue", Color.parseColor("#4F97A3"));
        put("ForestGreen", Color.parseColor("#228B22"));
        put("MossGreen", Color.parseColor("#8A9A5B"));
        put("SunsetOrange", Color.parseColor("#FF6B35"));
        put("SunflowerYellow", Color.parseColor("#FFDA03"));
        put("Lavender", Color.parseColor("#E6E6FA"));
        put("WarmGrey", Color.parseColor("#A9A9A9"));
        put("Sakura", Color.parseColor("#FF9CA8"));
        put("Matcha", Color.parseColor("#A5D6A7"));
        put("MidnightBlue", Color.parseColor("#2C3E50"));
        put("Coral", Color.parseColor("#FF7F50"));
        put("Turquoise", Color.parseColor("#40E0D0"));
        put("Lilac", Color.parseColor("#C8A2C8"));
        put("Gold", Color.parseColor("#FFD700"));
        put("Silver", Color.parseColor("#C0C0C0"));
        put("DarkSlate", Color.parseColor("#2F4F4F"));
        put("Navy", Color.parseColor("#001F3F"));
        put("Burgundy", Color.parseColor("#800020"));
        put("DarkTeal", Color.parseColor("#006D5B"));
        put("DeepPlum", Color.parseColor("#673147"));
        put("Charcoal", Color.parseColor("#36454F"));
    }};

    // Obfuscation key
    private static final String _OBF_KEY_ = "MoonlightSecureKey123!"; // 混淆密钥

    public static Handler _main_handler_; // 主线程Handler

    // Obfuscation method
    public static String obfuscate(String input) {
        if (input == null) return null;
        try {
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = _OBF_KEY_.getBytes(StandardCharsets.UTF_8);
            byte[] outputBytes = new byte[inputBytes.length];
            for (int i = 0; i < inputBytes.length; i++) {
                outputBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
            }
            return Base64.encodeToString(outputBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            return input;
        }
    }

    // Deobfuscation method
    public static String deobfuscate(String input) {
        if (input == null) return null;
        try {
            byte[] inputBytes = Base64.decode(input, Base64.DEFAULT);
            byte[] keyBytes = _OBF_KEY_.getBytes(StandardCharsets.UTF_8);
            byte[] outputBytes = new byte[inputBytes.length];
            for (int i = 0; i < inputBytes.length; i++) {
                outputBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
            }
            return new String(outputBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return input;
        }
    }

    /**
     * [START] CENTRALIZED CLEANUP SOLUTION
     * This is the single, authoritative method for cleaning up any dialogs and their resources.
     */
    public static void dismissAndCleanupAllDialogs() {
        dismissAndCleanupAllDialogs(false); // Default to not keeping state
    }

    public static void dismissAndCleanupAllDialogs(boolean keepState) {
        Activity activity = null;
        if (FaQinDeMiaoHaoKeAi._s_activity_ref_ != null) {
            activity = FaQinDeMiaoHaoKeAi._s_activity_ref_.get();
        }

        if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) {
            if (FaQinDeMiaoHaoKeAi._s_main_dialog_.isShowing()) {
                try {
                    FaQinDeMiaoHaoKeAi._s_main_dialog_.dismiss();
                } catch (Exception e) {
                    // Ignored
                }
            }
            FaQinDeMiaoHaoKeAi._s_main_dialog_ = null;
        }

        if (activity != null && !activity.isFinishing()) {
            changku.removeBackgroundBlur(activity);
        }

        if (!keepState) {
            FaQinDeMiaoHaoKeAi._current_dialog_type_ = FaQinDeMiaoHaoKeAi.DialogType.NONE;
            FaQinDeMiaoHaoKeAi._s_activity_ref_ = null;
        }
    }
    // [END] CENTRALIZED CLEANUP SOLUTION

    public static void init(Activity activity, String domesticUrl, String internationalUrl) {//之前两个参数作为链接
        // 1. 确保主线程 Handler 已初始化
        if (_main_handler_ == null) {
            // 如果 _main_handler_（可能是一个用于在主线程执行操作的静态 Handler）尚未创建，
            // 则创建一个新的 Handler，并关联到主线程的 Looper。
            // 这确保了任何通过此 Handler 发送的任务都会在 UI 线程上执行。
            _main_handler_ = new Handler(Looper.getMainLooper());
        }

        // 2. 输入参数校验
        if (activity == null || (domesticUrl == null || domesticUrl.trim().isEmpty()) && (internationalUrl == null || internationalUrl.trim().isEmpty())) {
            // 检查传入的参数：
            // - activity 是否为 null。
            // - domesticUrl 是否为 null 或去除前后空格后是否为空字符串。
            // - internationalUrl 是否为 null 或去除前后空格后是否为空字符串。
            // 如果 activity 为 null，或者 domesticUrl 和 internationalUrl *同时*都无效，
            // 则方法直接返回，不执行后续的初始化逻辑。
            // 这是一个卫语句，用于防止无效参数导致后续代码出错。
            return;
        }

        // 3. 检测是否为配置变更（例如屏幕旋转）
        // isConfigurationChange 用于判断当前的 Activity 实例是否因为配置变更（如屏幕旋转）而发生了改变。
        boolean isConfigurationChange =
                FaQinDeMiaoHaoKeAi._s_activity_ref_ != null &&                 // 之前是否保存过 Activity 的引用？
                        FaQinDeMiaoHaoKeAi._s_activity_ref_.get() != null &&          // 之前保存的 Activity 引用是否仍然有效（未被垃圾回收）？
                        FaQinDeMiaoHaoKeAi._s_activity_ref_.get() != activity;        // 之前保存的 Activity 实例是否与当前传入的 Activity 实例不同？

        // 4. 根据是否为配置变更来清理对话框
        if (isConfigurationChange) {
            // 如果是配置变更（例如屏幕旋转导致 Activity 重建）：
            // 调用 dismissAndCleanupAllDialogs(true)。
            // 这里的 true 参数可能表示“保留状态”。这意味着在关闭所有对话框的同时，
            // 应该保留当前对话框的类型（例如存储在 sharezhifu._current_dialog_type_ 中），
            // 以便在 Activity 重建后能够恢复显示之前的对话框。
            dismissAndCleanupAllDialogs(true);
        } else {
            // 如果不是配置变更（例如首次初始化或 Activity 未发生变化）：
            // 调用 dismissAndCleanupAllDialogs(false)。
            // 这里的 false 参数可能表示“不保留状态”或“重置状态”。
            // 这意味着关闭所有对话框，并且可能将当前对话框类型重置为 NONE。
            dismissAndCleanupAllDialogs(true);
        }

        // 5. 开始实际的初始化过程
        // 不论是否为配置变更，最后都会调用 startInitialization 方法，
        // 并将 Activity 和两个 URL 字符串传递给它。
        // 这个方法包含了核心的初始化逻辑，例如加载配置、检查更新、发起网络请求等。
        startInitialization(activity, domesticUrl, internationalUrl);
    }


    /**
     * Starts the core initialization process for the Moonlight SDK.
     * This method handles:
     * 1. Basic validation of the provided Activity.
     * 2. Storing a WeakReference to the Activity and the configuration URLs.
     * 3. Resetting network configurations to default and loading any cached settings from SharedPreferences.
     *    This includes screen type and general cached settings.
     * 4. Checking for package-specific popup control. If popups are disabled for the current
     *    application package, it dismisses any existing dialogs and returns.
     * 5. Loading the main configuration:
     *    - It attempts to load a cached configuration string from SharedPreferences.
     *    - If found, it deobfuscates and parses this configuration.
     *    - It then checks if a force update is required based on this configuration.
     *    - Finally, it resolves colors based on the loaded (or default) configuration.
     * 6. Conditionally loading configuration from the network:
     *    - If network configuration loading is enabled ({@code sharezhifu._net_cfg_enabled_ == true}):
     *        - It first checks for internet connectivity.
     *        - If internet is available, it attempts to load the configuration from the
     *          {@code domesticUrl} or {@code internationalUrl}.
     *            - On successful load: Parses the new config, updates cache, resolves colors,
     *              checks for force updates again, and then performs highest priority checks.
     *            - On failure: If no cached config exists, shows an offline dialog. Otherwise,
     *              proceeds with highest priority checks using the cached config.
     *        - If internet is unavailable: If no cached config exists, shows an offline dialog.
     *          Otherwise, proceeds with highest priority checks using the cached config.
     *    - If network configuration loading is disabled:
     *        - It directly proceeds to perform the highest priority checks (using default
     *          or cached configuration).
     *
     * This method orchestrates both local cache loading and optional network fetching
     * to ensure the application has the most relevant configuration.
     *
     * @param activity The current Activity context. Used for UI operations and accessing SharedPreferences.
     */
    private static void startInitialization(final Activity activity, final String domesticUrl, final String internationalUrl) {
        // 1. 基本的有效性检查
        if (activity == null || activity.isFinishing()) {
            // 如果传入的 Activity 为空，或者 Activity 正在结束 (isFinishing)，
            // 则直接返回，不执行后续初始化。这是为了避免在无效的 Activity 上操作。
            return;
        }

        // 2. 保存 Activity 引用和配置 URL
        // 使用 WeakReference (弱引用) 保存 Activity 实例，有助于防止内存泄漏。
        // 当 Activity 没有其他强引用时，垃圾回收器可以回收它。
        FaQinDeMiaoHaoKeAi._s_activity_ref_ = new WeakReference<>(activity);
        // 保存国内和国际的配置 URL，这些 URL 稍后可能用于从网络获取配置。
        FaQinDeMiaoHaoKeAi._cfg_dom_url_ = domesticUrl;
        FaQinDeMiaoHaoKeAi._cfg_intl_url_ = internationalUrl;

        // 3. 重置网络配置并加载本地缓存
        // 在加载缓存或网络配置之前，重置所有网络配置为默认值。
        changku.resetNetworkConfigsToDefault();
        // 获取 SharedPreferences 实例，用于读取和存储应用的持久化键值对数据。
        final SharedPreferences prefs = changku.getSharedPreferences(activity);

        // 检查并缓存屏幕类型 (例如手机、平板)，可能存储到 SharedPreferences 中。
        changku.checkAndCacheScreenType(activity, prefs);

        // 从 SharedPreferences 加载所有已缓存的设置，然后定义到字符
        wenbenpeizi.loadAllCachedSettings(prefs);

        // 4. 特定包名的弹窗控制
        String currentPackageName = activity.getPackageName(); // 获取当前应用的包名
        // 检查 sharezhifu._pkg_popup_control_ (一个 Map) 中是否包含当前包名的配置，
        // 并且该配置值为 false (表示禁止弹窗)。
        if (FaQinDeMiaoHaoKeAi._pkg_popup_control_.containsKey(currentPackageName) && !FaQinDeMiaoHaoKeAi._pkg_popup_control_.get(currentPackageName)) {
            // 如果禁止弹窗，则关闭所有已显示的对话框并返回，不执行后续可能显示 UI 的初始化。
            dismissAndCleanupAllDialogs();
            return;
        }

        // 5. 加载已缓存的主要配置
        // 从 SharedPreferences 中尝试读取之前缓存的配置字符串，键为 sharezhifu._PREF_A_。
        final String cachedConfig = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_A_, null);

        if (cachedConfig != null && !cachedConfig.isEmpty()) {
            // 如果缓存的配置存在且不为空：
            // 对缓存的配置字符串进行反混淆操作。这表明配置可能以混淆的方式存储。
            FaQinDeMiaoHaoKeAi._full_config_string_ = deobfuscate(cachedConfig);
            // 解析反混淆后的配置字符串。第三个参数为 null，可能表示此配置来源于本地缓存。
            QQstart.parseConfig(activity, FaQinDeMiaoHaoKeAi._full_config_string_, null);
            // 根据解析的配置检查应用版本，判断是否需要强制更新。
            QQstart.checkAppVersionForForceUpdate(activity);
        }
        // 根据已加载的配置 (缓存的或默认的) 解析颜色。
        changku.resolveColors(activity);

        // 6. 条件性地从网络加载配置
        if (FaQinDeMiaoHaoKeAi._net_cfg_enabled_) { // 检查是否启用了网络配置加载功能
            // 如果启用了网络配置加载：
            // 创建并执行一个 CheckInternetTask (可能是 AsyncTask 或类似的后台任务) 来检查网络连接。
            new MeowJianCe.CheckInternetTask(activity, new InternetCheckCallback() {
                @Override
                public void onInternetAvailable() { // 网络可用时的回调
                    // 创建并执行一个 LoadConfigTask (后台任务) 从网络加载配置。
                    new LoadConfigTask(activity, domesticUrl, internationalUrl, new ConfigLoadCallback() {
                        @Override
                        public void onConfigLoaded(String configContent, String loadedFromUrl) { // 配置成功从网络加载时的回调
                            // 将从网络获取的配置作为当前完整配置。
                            FaQinDeMiaoHaoKeAi._full_config_string_ = configContent;
                            // 解析新的网络配置。
                            QQstart.parseConfig(activity, configContent, loadedFromUrl);
                            // 将新的网络配置更新到 SharedPreferences 缓存中。
                            changku.updateAllCachedSettings(prefs, configContent);
                            // 根据新的网络配置重新解析颜色。
                            changku.resolveColors(activity);
                            // 根据新的网络配置再次检查是否需要强制更新。
                            QQstart.checkAppVersionForForceUpdate(activity);
                            // 执行最高优先级的检查。
                            performHighestPriorityChecks(activity);
                        }

                        @Override
                        public void onConfigFailed() { // 从网络加载配置失败时的回调
                            if (cachedConfig == null || cachedConfig.isEmpty()) {
                                // 如果网络加载失败，并且本地也没有缓存配置，则显示一个初始离线对话框。
                                QQstart.showInitialOfflineDialog(activity);
                            } else {
                                // 如果网络加载失败，但本地有缓存配置，则使用缓存配置执行最高优先级检查。
                                performHighestPriorityChecks(activity);
                            }
                        }
                    }).execute();
                }

                /**
                 * Callback method invoked when internet connectivity is unavailable.
                 *
                 * This method handles the scenario where the application cannot connect to the
                 * internet to fetch the latest configuration.
                 *
                 * If there is no cached configuration available locally (i.e., {@code cachedConfig}
                 * is null or empty), it means the app has neither online nor offline configuration.
                 * In this case, it displays an initial offline dialog to the user, informing them
                 * about the lack of connectivity and potentially offering guidance.
                 *
                 * If a cached configuration *is* available locally, even though the internet is
                 * down, the application will proceed to use this cached data. It calls
                 * {@link #performHighestPriorityChecks(Activity)} which will use the existing
                 * cached settings to perform critical checks like force updates, signature verification, etc.
                 * This ensures the app can still function with the last known good configuration
                 * if possible.
                 *
                 * @see QQstart#showInitialOfflineDialog(Activity)
                 * @see #performHighestPriorityChecks(Activity)
                 */
                @Override
                public void onInternetUnavailable() { // 网络不可用时的回调
                    if (cachedConfig == null || cachedConfig.isEmpty()) {
                        // 如果网络不可用，并且本地也没有缓存配置，则显示初始离线对话框。
                        QQstart.showInitialOfflineDialog(activity);
                    } else {
                        // 如果网络不可用，但本地有缓存配置，则使用缓存配置执行最高优先级检查。
                        performHighestPriorityChecks(activity);
                    }
                }
            }).execute();
        } else { // 如果禁用了网络配置加载功能
            // 直接执行最高优先级的检查 (可能使用默认配置或已加载的缓存配置)。
            performHighestPriorityChecks(activity);
        }
    }

    /**
     * Performs a series of checks in a specific priority order and displays the
     * corresponding dialog or takes action if a check is met.
     *
     * <p>The priority order for checks is as follows:
     * <ol>
     *     <li><b>Restore Previous Dialog:</b> If {@code sharezhifu._current_dialog_type_} indicates a dialog was
     *         previously shown (e.g., due to a configuration change like screen rotation), that dialog is
     *         restored and shown again. No further checks are performed.</li>
     *     <li><b>Force Update:</b> If {@code sharezhifu._force_upd_req_} is true, a force update dialog is shown.
     *         Sets {@code sharezhifu._current_dialog_type_} to {@code FORCE_UPDATE}.</li>
     *     <li><b>Signature Verification:</b> If {@code sharezhifu._enable_sig_ver_} is true and the app's signature
     *         is invalid (as determined by {@code changku.verifyAppSignature(activity)}),
     *         a signature failure dialog is shown. Sets {@code sharezhifu._current_dialog_type_} to {@code SIGNATURE_FAILURE}.</li>
     *     <li><b>Package Lock:</b> If the current application's package name is present in {@code sharezhifu._pkg_lock_map_}
     *         and its value is true, a package locked dialog is shown with content and link from
     *         {@code sharezhifu._pkg_lock_content_} and {@code sharezhifu._pkg_lock_link_}.
     *         Sets {@code sharezhifu._current_dialog_type_} to {@code PACKAGE_LOCKED}.</li>
     *     <li><b>Device ID Check:</b> If {@code sharezhifu._enable_dev_id_check_} is true, and
     *         {@code sharezhifu._allowed_dev_ids_} is not null, not empty, and does not contain the current
     *         device ID (obtained via {@code getDeviceId(activity)}), a device ID restricted dialog is shown.
     *         Sets {@code sharezhifu._current_dialog_type_} to {@code DEVICE_ID_RESTRICTED}.</li>
     */
    public static void performHighestPriorityChecks(final Activity activity) {
        if (activity == null || activity.isFinishing()) {
            dismissAndCleanupAllDialogs();
            return;
        }
        String currentDeviceId = getDeviceId(activity);
        SharedPreferences prefs = changku.getSharedPreferences(activity);
        boolean isCardKeyValidated = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_X_ + currentDeviceId, false);
        String currentPackageName = activity.getPackageName();

        if (FaQinDeMiaoHaoKeAi._pkg_popup_control_.containsKey(currentPackageName) && !FaQinDeMiaoHaoKeAi._pkg_popup_control_.get(currentPackageName)) {
            dismissAndCleanupAllDialogs();
            return;
        }

        // Restore dialog state if necessary
        if (FaQinDeMiaoHaoKeAi._current_dialog_type_ != FaQinDeMiaoHaoKeAi.DialogType.NONE) {
            switch (FaQinDeMiaoHaoKeAi._current_dialog_type_) {
                case FORCE_UPDATE:
                    DialogLiQiuting.showForceUpdateDialog(activity);
                    return;
                case SIGNATURE_FAILURE:
                    DialogLiQiuting.showSignatureFailureDialog(activity);
                    return;
                case PACKAGE_LOCKED:
                    DialogLiQiuting.showPackageLockedDialog(activity, FaQinDeMiaoHaoKeAi._pkg_lock_content_, FaQinDeMiaoHaoKeAi._pkg_lock_link_);
                    return;
                case DEVICE_ID_RESTRICTED:
                    DialogLiQiuting.showDeviceIdRestrictedDialog(activity);
                    return;
                case CARD_KEY_INPUT:
                    DialogLiQiuting.showCardKeyInputDialog(activity);
                    return;
                case NORMAL_POPUP:
                    DialogLiQiuting.originalPopupLogic(activity);
                    return;
            }
        }
        if (FaQinDeMiaoHaoKeAi._force_upd_req_) {
            FaQinDeMiaoHaoKeAi._current_dialog_type_ = FaQinDeMiaoHaoKeAi.DialogType.FORCE_UPDATE;
            DialogLiQiuting.showForceUpdateDialog(activity);
            return;
        }

        if (FaQinDeMiaoHaoKeAi._enable_sig_ver_ && !changku.verifyAppSignature(activity)) {
            FaQinDeMiaoHaoKeAi._current_dialog_type_ = FaQinDeMiaoHaoKeAi.DialogType.SIGNATURE_FAILURE;
            DialogLiQiuting.showSignatureFailureDialog(activity);
            return;
        }

        if (FaQinDeMiaoHaoKeAi._pkg_lock_map_.containsKey(currentPackageName) && FaQinDeMiaoHaoKeAi._pkg_lock_map_.get(currentPackageName)) {
            FaQinDeMiaoHaoKeAi._current_dialog_type_ = FaQinDeMiaoHaoKeAi.DialogType.PACKAGE_LOCKED;
            DialogLiQiuting.showPackageLockedDialog(activity, FaQinDeMiaoHaoKeAi._pkg_lock_content_, FaQinDeMiaoHaoKeAi._pkg_lock_link_);
            return;
        }

        if (FaQinDeMiaoHaoKeAi._enable_dev_id_check_) {
            if (FaQinDeMiaoHaoKeAi._allowed_dev_ids_ != null && !FaQinDeMiaoHaoKeAi._allowed_dev_ids_.isEmpty()) {
                if (!FaQinDeMiaoHaoKeAi._allowed_dev_ids_.contains(currentDeviceId)) {
                    FaQinDeMiaoHaoKeAi._current_dialog_type_ = FaQinDeMiaoHaoKeAi.DialogType.DEVICE_ID_RESTRICTED;
                    DialogLiQiuting.showDeviceIdRestrictedDialog(activity);
                    return;
                }
            }
        }

        if (FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ && !isCardKeyValidated) {
            FaQinDeMiaoHaoKeAi._current_dialog_type_ = FaQinDeMiaoHaoKeAi.DialogType.CARD_KEY_INPUT;
            DialogLiQiuting.showCardKeyInputDialog(activity);
            return;
        }

        if (FaQinDeMiaoHaoKeAi._enable_remote_notif_ && !FaQinDeMiaoHaoKeAi._remote_notif_content_.isEmpty()) {
            changku.sendNotification(activity, FaQinDeMiaoHaoKeAi._remote_notif_content_);
        }

        boolean showNormalPopupForPackage = true;
        if (FaQinDeMiaoHaoKeAi._normal_popup_control_.containsKey(currentPackageName)) {
            showNormalPopupForPackage = FaQinDeMiaoHaoKeAi._normal_popup_control_.get(currentPackageName);
        }

        if (FaQinDeMiaoHaoKeAi._enable_norm_popup_ && showNormalPopupForPackage) {
            DialogLiQiuting.originalPopupLogic(activity);
        } else {
            dismissAndCleanupAllDialogs();
        }
    }


    public static int getContrastColor(int backgroundColor) {
        return Color.luminance(backgroundColor) > 0.5 ? Color.BLACK : Color.WHITE;
    }

    /**
     * Validates and adjusts the color contrast of various UI elements within the dialog.
     * This method checks the contrast ratio between text/element colors and their respective
     * background colors. If the contrast is below the recommended accessibility guidelines,
     * it attempts to adjust the foreground color to a more suitable one (typically black or white
     * based on the background luminance) or lightens it.
     *
     * <p>The specific elements and their contrast ratio thresholds are:
     * <ul>
     *     <li>Dialog text color against dialog background: Threshold 4.5.
     *         If below, {@code _current_text_color_} is adjusted.</li>
     *     <li>Button text color against button background: Threshold 3.0.
     *         If below, {@code _current_button_text_} is adjusted.</li>
     *     <li>EditText text color against dialog background: Threshold 4.5.
     *         If below, {@code _current_edittext_text_} is adjusted.</li>
     *     <li>EditText hint color against dialog background: Threshold 3.0.
     *         If below, {@code _current_edittext_hint_} is lightened relative to the EditText text color.</li>
     *     <li>EditText stroke color against dialog background: Threshold 3.0.
     *         If below, {@code _current_edittext_stroke_} is adjusted.</li>
     * </ul>
     *
     * <p>This process is controlled by {@code sharezhifu._enable_dynamic_text_colors_}.
     * However, the current implementation performs the checks regardless of this flag's value.
     * This might be an oversight or intended behavior where the flag controls other aspects
     * of dynamic color adjustment not covered here.
     *
     * @see #getContrastColor(int)
     * @see changku#getContrastRatio(int, int)
     * @see changku#lightenColor(int, float)
     */
    public static void validateColorContrast() {
        if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_) {
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_text_color_, FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 4.5) {
                FaQinDeMiaoHaoKeAi._current_text_color_ = getContrastColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
            }
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_button_text_, FaQinDeMiaoHaoKeAi._current_button_bg_) < 3.0) {
                FaQinDeMiaoHaoKeAi._current_button_text_ = getContrastColor(FaQinDeMiaoHaoKeAi._current_button_bg_);
            }
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_edittext_text_, FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 4.5) {
                FaQinDeMiaoHaoKeAi._current_edittext_text_ = getContrastColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
            }
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_edittext_hint_, FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 3.0) {
                FaQinDeMiaoHaoKeAi._current_edittext_hint_ = changku.lightenColor(FaQinDeMiaoHaoKeAi._current_edittext_text_, 0.5f);
            }
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_edittext_stroke_, FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 3.0) {
                FaQinDeMiaoHaoKeAi._current_edittext_stroke_ = getContrastColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
            }
        } else {
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_text_color_, FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 4.5) {
                FaQinDeMiaoHaoKeAi._current_text_color_ = getContrastColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
            }
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_button_text_, FaQinDeMiaoHaoKeAi._current_button_bg_) < 3.0) {
                FaQinDeMiaoHaoKeAi._current_button_text_ = getContrastColor(FaQinDeMiaoHaoKeAi._current_button_bg_);
            }
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_edittext_text_, FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 4.5) {
                FaQinDeMiaoHaoKeAi._current_edittext_text_ = getContrastColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
            }
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_edittext_hint_, FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 3.0) {
                FaQinDeMiaoHaoKeAi._current_edittext_hint_ = changku.lightenColor(FaQinDeMiaoHaoKeAi._current_edittext_text_, 0.5f);
            }
            if (changku.getContrastRatio(FaQinDeMiaoHaoKeAi._current_edittext_stroke_, FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 3.0) {
                FaQinDeMiaoHaoKeAi._current_edittext_stroke_ = getContrastColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
            }
        }
    }


    /**
     * Parses a configuration string containing various switch settings and updates
     * the corresponding static boolean flags in the {@code sharezhifu} class.
     *
     * <p>The configuration string is expected to be a comma-separated list of key-value pairs,
     * where each pair is in the format "key=value". The keys are specific strings
     * (e.g., "网络配置总开关", "显示动态内容开关") and the values are either "开" (on) or
     * any other string (interpreted as off).
     *
     * <p>For example, a valid config string might be:
     * "网络配置总开关=开,显示动态内容开关=关,启用背景模糊=开"
     *
     * <p>This method iterates through each key-value pair, trims whitespace, and
     * sets the appropriate boolean flag in {@code sharezhifu} based on the key and
     * whether the value is "开".
     *
     * <p>The following keys are recognized and mapped to their respective flags:
     * <ul>
     *   <li>"网络配置总开关" -> {@code sharezhifu._net_cfg_enabled_}</li>
     *   <li>"显示动态内容开关" -> {@code sharezhifu._show_dyn_content_}</li>
     *   <li>"显示更新日志开关" -> {@code sharezhifu._show_upd_log_}</li>
     *   <li>"重置不再提示" -> {@code sharezhifu._reset_dont_show_}</li>
     *   <li>"启用Moonlight图旋转" -> {@code sharezhifu._enable_img_rot_}</li>
     *   <li>"启用背景模糊" -> {@code sharezhifu._enable_bg_blur_}</li>
     *   <li>"启用普通弹窗" -> {@code sharezhifu._enable_norm_popup_}</li>
     *   <li>"显示关闭弹窗按钮" -> {@code sharezhifu._show_close_btn_}</li>
     *   <li>"显示不再提示按钮" -> {@code sharezhifu._show_dont_show_btn_}</li>
     */
    public static void parseSwitchConfigs(String config) {
        String[] parts = config.split(",");
        for (String part : parts) {
            String[] kv = part.split("=");
            if (kv.length == 2) {
                String key = kv[0].trim();
                boolean value = "开".equals(kv[1].trim());

                switch (key) {
                    case "网络配置总开关":
                        FaQinDeMiaoHaoKeAi._net_cfg_enabled_ = value;
                        break;
                    case "显示动态内容开关":
                        FaQinDeMiaoHaoKeAi._show_dyn_content_ = value;
                        break;
                    case "显示更新日志开关":
                        FaQinDeMiaoHaoKeAi._show_upd_log_ = value;
                        break;
                    case "重置不再提示":
                        FaQinDeMiaoHaoKeAi._reset_dont_show_ = value;
                        break;
                    case "启用Moonlight图旋转":
                        FaQinDeMiaoHaoKeAi._enable_img_rot_ = value;
                        break;
                    case "启用背景模糊":
                        FaQinDeMiaoHaoKeAi._enable_bg_blur_ = value;
                        break;
                    case "启用普通弹窗":
                        FaQinDeMiaoHaoKeAi._enable_norm_popup_ = value;
                        break;
                    case "显示关闭弹窗按钮":
                        FaQinDeMiaoHaoKeAi._show_close_btn_ = value;
                        break;
                    case "显示不再提示按钮":
                        FaQinDeMiaoHaoKeAi._show_dont_show_btn_ = value;
                        break;
                    case "显示QQ群按钮":
                        FaQinDeMiaoHaoKeAi._show_qq_btn_ = value;
                        break;
                    case "显示TG频道按钮":
                        FaQinDeMiaoHaoKeAi._show_tg_btn_ = value;
                        break;
                    case "启用外部点击消失":
                        FaQinDeMiaoHaoKeAi._enable_out_click_ = value;
                        break;
                    case "启用签名验证":
                        FaQinDeMiaoHaoKeAi._enable_sig_ver_ = value;
                        break;
                    case "启用夜间模式":
                        FaQinDeMiaoHaoKeAi._enable_night_mode_ = value;
                        break;
                    case "启用Monet动态取色":
                        FaQinDeMiaoHaoKeAi._enable_monet_dyn_ = value;
                        break;
                    case "启用内置Monet主题色":
                        FaQinDeMiaoHaoKeAi._enable_builtin_monet_ = value;
                        break;
                    case "启用DPI适配":
                        FaQinDeMiaoHaoKeAi._enable_dpi_auto_ = value;
                        break;
                    case "启用远程通知开关":
                        FaQinDeMiaoHaoKeAi._enable_remote_notif_ = value;
                        break;
                    case "显示Moonlight图标":
                        FaQinDeMiaoHaoKeAi._show_moonlight_icon_ = value;
                        break;
                    case "启用设备码弹窗":
                        FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = value;
                        break;
                    case "启用设备码卡密弹窗":
                        FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = value;
                        break;
                    case "开启不再提示确认倒计时":
                        FaQinDeMiaoHaoKeAi._enable_countdown_dont_show_ = value;
                        break;
                    case "强制更新弹窗显示关闭按钮":
                        FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ = value;
                        break;
                    case "包名锁定弹窗显示关闭按钮":
                        FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_ = value;
                        break;
                    case "强制使用国内配置URL":
                        FaQinDeMiaoHaoKeAi._force_use_domestic_url_ = value;
                        break;
                    case "强制使用国际配置URL":
                        FaQinDeMiaoHaoKeAi._force_use_international_url_ = value;
                        break;
                    case "启用用户自定义配置URL":
                        FaQinDeMiaoHaoKeAi._enable_user_custom_url_ = value;
                        break;
                    case "启用配置文件语法检查":
                        FaQinDeMiaoHaoKeAi._enable_config_syntax_check_ = value;
                        break;
                    case "启用更丰富的设备信息收集":
                        FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_ = value;
                        break;
                    case "启用弹窗背景改变文字颜色按钮颜色":
                        FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ = value;
                        break;
                    case "中立按钮是否显示":
                        FaQinDeMiaoHaoKeAi._show_neutral_btn_ = value;
                        break;
                }
            }
        }
    }

    // --- [START] NEW DIALOG LAYOUT SECTION ---
    // This section contains the new, combined layout logic.


    // --- [END] REPLACEMENT UNIFIED DIALOG LAYOUT SECTION ---

    // cleanupBeforeExit is now effectively an alias for dismissAndCleanupAllDialogs
    // if the intent is general cleanup. For specific exit scenarios, it might differ.
    // For now, let's assume general cleanup is sufficient.
    public static void cleanupBeforeExit(Activity activityContext) {
        dismissAndCleanupAllDialogs();
    }

    public static boolean isSystemDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }


    /**
     * Finds HTTP/HTTPS URLs within a given message string, makes them clickable,
     * and sets the modified text on the provided TextView.
     *
     * <p>The method performs the following steps:
     * <ol>
     *   <li>Decodes HTML entities in the message.</li>
     *   <li>Removes all HTML tags from the message.</li>
     *   <li>Replaces multiple newlines with a single newline and trims whitespace.</li>
     *   <li>Creates a {@link SpannableString} from the cleaned message.</li>
     *   <li>Uses a regular expression to find all valid HTTP and HTTPS URLs.</li>
     *   <li>For each found URL:
     *     <ul>
     *       <li>Creates a {@link ClickableSpan}.</li>
     *       <li>When clicked, the span attempts to open the URL using an {@link Intent#ACTION_VIEW}.
     *           A toast message is shown indicating "Opening link..." or its Chinese equivalent.</li>
     *       <li>If opening the link fails, a toast message "Failed to open link" or its Chinese equivalent is shown.</li>
     *       <li>The clickable span is styled with the color #FF2196F3 and without an underline.</li>
     *       <li>The span is applied to the corresponding part of the {@code SpannableString}.</li>
     *     </ul>
     *   </li>
     *   <li>Sets the resulting {@code SpannableString} to the {@code textView}.</li>
     *   <li>Makes the links clickable by setting {@link LinkMovementMethod} on the {@code textView}.</li>
     * </ol>
     *
     * @param context The Context used to start the activity for opening the URL and to show toasts.
     * @param textView The TextView where the clickable links will be applied.
     * @param message The input string message that may contain URLs.
     */
    public static void applyClickableLinks(Context context, TextView textView, String message) {
        String cleanMessage = MaoMaoShiYiGeXiaoSaoHuo.decodeHtmlEntities(message);
        cleanMessage = cleanMessage.replaceAll("<[^>]*>", "");
        cleanMessage = cleanMessage.replaceAll("[\r\n]+", "\n").trim();


        SpannableString spannableMessage = new SpannableString(cleanMessage);
        Pattern urlPattern = Pattern.compile(
                "(https?://[\\w\\-\\.]+\\.[a-z]{2,}(?:/[^\\s\"'<]*)?)" ,
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = urlPattern.matcher(cleanMessage);
        while (matcher.find()) {
            final String cleanUrl = matcher.group(1);

            if (cleanUrl == null || cleanUrl.isEmpty() || (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://"))) {
                continue;
            }

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(cleanUrl));
                        context.startActivity(intent);
                        if (context instanceof Activity) {
                           changku.showToast((Activity) context, MaoMaoShiYiGeXiaoSaoHuo.isChinese(context) ? "正在打开链接..." : "Opening link...");
                        }
                    } catch (Exception e) {
                         if (context instanceof Activity) {
                            changku.showToast((Activity) context, MaoMaoShiYiGeXiaoSaoHuo.isChinese(context) ? "打开链接失败" : "Failed to open link");
                         }
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(Color.parseColor("#FF2196F3"));
                    ds.setUnderlineText(false);
                }
            };

            spannableMessage.setSpan(clickableSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(spannableMessage);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static String getDeviceId(Context context) {
        if (context == null) return "unknown_device_id"; // Prevent crash
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getRichDeviceInfo(Context context) {
        if (context == null) return "UnknownDevice";
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        String deviceInfo = String.format(
                "Model:%s;OS:%s;API:%d;DPI:%d;Density:%.2f;Screen:%dx%d",
                Build.MODEL,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                dm.densityDpi,
                dm.density,
                dm.widthPixels,
                dm.heightPixels
        );
        return deviceInfo;
    }


    public static void setupEditTextStyle(Activity activity, EditText editText) {
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setShape(GradientDrawable.RECTANGLE);
        backgroundDrawable.setCornerRadius(MaoMaoShiYiGeXiaoSaoHuo.dpToPx(activity, 10));

        backgroundDrawable.setStroke(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 1.5f), FaQinDeMiaoHaoKeAi._current_edittext_stroke_);

        if (Color.luminance(FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 0.5) {
            backgroundDrawable.setColor(Color.argb(0x22, 0xFF, 0xFF, 0xFF));
        } else {
            backgroundDrawable.setColor(Color.argb(0x22, 0x00, 0x00, 0x00));
        }

        editText.setBackground(backgroundDrawable);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        editText.setTextColor(FaQinDeMiaoHaoKeAi._current_edittext_text_);
        editText.setHintTextColor(FaQinDeMiaoHaoKeAi._current_edittext_hint_);
        editText.setPadding(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 15), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 15), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10));
    }


    public static WindowManager.LayoutParams getDialogWindowAttributes(Activity activity, AlertDialog dialog) {
        WindowManager.LayoutParams windowAttributes = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            windowAttributes.copyFrom(dialog.getWindow().getAttributes());
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        // int screenHeight = displayMetrics.heightPixels; // Not used

        int orientation = activity.getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE || FaQinDeMiaoHaoKeAi._is_large_screen_) { // If in landscape or it's a large screen (tablet)
            windowAttributes.width = (int) (screenWidth * 0.85f); // 85% of landscape width
            // windowAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT; // Already default
        } else { // Portrait for phones
            windowAttributes.width = (int) (screenWidth * 0.9f); // 90% of portrait width
            // windowAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT; // Already default
        }
        // Height is WRAP_CONTENT by default in AlertDialogs unless overridden,
        // so explicitly setting it here might be redundant if that's the desired behavior.
        // If specific height control is needed, it would be set here.
        // For now, let's assume WRAP_CONTENT is fine.
        windowAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT;


        windowAttributes.gravity = Gravity.CENTER;

        return windowAttributes;
    }


    interface ConfigLoadCallback {
        void onConfigLoaded(String configContent, String loadedFromUrl);

        void onConfigFailed();
    }

    interface InternetCheckCallback {
        void onInternetAvailable();

        void onInternetUnavailable();
    }

    @SuppressWarnings("deprecation")
    static class LoadConfigTask extends AsyncTask<Void, Void, String> {
        private final WeakReference<Activity> activityRef;
        private final ConfigLoadCallback callback;
        private final String domesticUrl;
        private final String internationalUrl;
        private String loadedFromUrl = null;

        LoadConfigTask(Activity activity, String domesticUrl, String internationalUrl, ConfigLoadCallback cb) {
            activityRef = new WeakReference<>(activity);
            callback = cb;
            this.domesticUrl = domesticUrl;
            this.internationalUrl = internationalUrl;
        }

        private String fetchConfig(String urlString) {
            if (urlString == null || urlString.trim().isEmpty()) {
                return null;
            }
            try {
                URL url = new URL(changku.convertToRawUrl(urlString));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout((int) FaQinDeMiaoHaoKeAi._config_load_timeout_ms_);
                conn.setReadTimeout((int) FaQinDeMiaoHaoKeAi._config_load_timeout_ms_);

                String userAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
                if (FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_) {
                    Activity activity = activityRef.get();
                    if (activity != null && !activity.isFinishing()) { // Check activity state
                        userAgent += " (Moonlight-Device: " + getRichDeviceInfo(activity) + ")";
                    }
                }
                conn.setRequestProperty("User-Agent", userAgent);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        String content = sb.toString();
                        // Basic validation: Check for JSON structure or custom tags
                        try {
                            new JSONObject(content); // Try to parse as JSON
                            return content;
                        } catch (JSONException e) {
                            // If not JSON, check for custom tags as a fallback
                            if (content.contains("〈配置版本〉") && content.contains("〈/配置版本〉")) {
                                return content;
                            } else {
                                // Log.e("Moonlight", "Config content is not valid JSON and lacks custom tags: " + urlString);
                                return null; // Invalid content
                            }
                        }
                    }
                } else {
                    // Log.e("Moonlight", "HTTP error fetching config: " + responseCode + " from " + urlString);
                    return null;
                }
            } catch (Exception e) {
                 // Log.e("Moonlight", "Exception fetching config from " + urlString, e);
                return null;
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            Activity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return null;

            String configContent = null;
            List<String> urlsToTry = new ArrayList<>();

            // Prioritize user custom URLs
            if (FaQinDeMiaoHaoKeAi._enable_user_custom_url_ && FaQinDeMiaoHaoKeAi._user_custom_urls_ != null && !FaQinDeMiaoHaoKeAi._user_custom_urls_.isEmpty()) {
                String[] customUrls = FaQinDeMiaoHaoKeAi._user_custom_urls_.split(",");
                for (String url : customUrls) {
                    if (url != null && !url.trim().isEmpty()) {
                        urlsToTry.add(url.trim());
                    }
                }
            }

            // Add domestic/international based on forcing flags and if not already added
            String finalDomesticUrl = (domesticUrl != null) ? domesticUrl.trim() : "";
            String finalInternationalUrl = (internationalUrl != null) ? internationalUrl.trim() : "";

            if (FaQinDeMiaoHaoKeAi._force_use_domestic_url_ && !finalDomesticUrl.isEmpty() && !urlsToTry.contains(finalDomesticUrl)) {
                urlsToTry.add(finalDomesticUrl);
            }
            if (FaQinDeMiaoHaoKeAi._force_use_international_url_ && !finalInternationalUrl.isEmpty() && !urlsToTry.contains(finalInternationalUrl)) {
                urlsToTry.add(finalInternationalUrl);
            }

            // Add normal priority if not forced and not already present
            if (!finalDomesticUrl.isEmpty() && !FaQinDeMiaoHaoKeAi._force_use_international_url_ && !urlsToTry.contains(finalDomesticUrl)) {
                urlsToTry.add(finalDomesticUrl);
            }
            if (!finalInternationalUrl.isEmpty() && !FaQinDeMiaoHaoKeAi._force_use_domestic_url_ && !urlsToTry.contains(finalInternationalUrl)) {
                urlsToTry.add(finalInternationalUrl);
            }


            for (String url : urlsToTry) {
                if (url.trim().isEmpty()) continue;
                configContent = fetchConfig(url);
                if (configContent != null) {
                    loadedFromUrl = url;
                    return configContent;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String config) {
            Activity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;

            if (callback != null) {
                if (config != null && !config.isEmpty()) {
                    callback.onConfigLoaded(config, loadedFromUrl);
                } else {
                    callback.onConfigFailed();
                }
            }
        }
    }
}
