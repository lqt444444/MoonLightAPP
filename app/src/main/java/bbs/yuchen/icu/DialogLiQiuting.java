package bbs.yuchen.icu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class DialogLiQiuting {

    /**
     * Displays a force update dialog to the user.
     * <p>
     * This method constructs and shows an AlertDialog that informs the user about a mandatory update.
     * The dialog's appearance and content are dynamically adjusted based on the device's screen size,
     * orientation, and language settings.
     * </p>
     * <p>
     * Key features of the dialog:
     * <ul>
     *     <li>Prevents display if the activity is null, finishing, or another main dialog is already showing.</li>
     *     <li>Applies a background blur effect if enabled.</li>
     *     <li>Determines and applies appropriate colors based on the current theme.</li>
     *     <li>Localizes text content (e.g., "版本更新" or "Update Required") based on the device language.</li>
     *     <li>Adapts layout (horizontal or vertical) for large screens or landscape orientation.</li>
     *     <li>Optionally displays a Moonlight icon.</li>
     *     <li>Displays the update title and content. The content can include clickable links.</li>
     *     <li>Optionally displays an update log.</li>
     *     <li>Provides an "Update Now" button that attempts to open the update link (configured in {@code sharezhifu._force_upd_link_}).
     *         Shows a toast message if the link is not configured or fails to open.</li>
     *     <li>Optionally provides a "Close" (or "暂不更新") button to dismiss the dialog.</li>
     *     <li>Provides an "Exit App" button to close the application.</li>
     * </ul>
     * </p>
     * <p>
     * The dialog is stored in {@code sharezhifu._s_main_dialog_} and configured with common dialog properties
     * before being shown.
     * </p>
     *
     * @param activity The current Activity context in which to display the dialog.
     *                 If null or finishing, the dialog will not be shown.
     */
    public static void showForceUpdateDialog(final Activity activity) {//更新弹窗
        if (activity == null || activity.isFinishing() || (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null && FaQinDeMiaoHaoKeAi._s_main_dialog_.isShowing())) {
            return;
        }
        if (FaQinDeMiaoHaoKeAi._enable_bg_blur_) {
            changku.applyBackgroundBlur(activity);
        }
        changku.resolveColors(activity);
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        FaQinDeMiaoHaoKeAi._is_large_screen_ = dpWidth >= 600;
        int orientation = activity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean useHorizontalLayout = FaQinDeMiaoHaoKeAi._is_large_screen_ || isLandscape;

        ScrollView sv = new ScrollView(activity);
        LinearLayout rootLayout = new LinearLayout(activity);

        if (useHorizontalLayout) {
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            rootLayout.setPadding(40, 40, 40, 40);
            rootLayout.setGravity(Gravity.CENTER);

            LinearLayout leftContent = new LinearLayout(activity);
            leftContent.setOrientation(LinearLayout.VERTICAL);
            leftContent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80)));
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                leftContent.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setText(isChinese ? "版本更新" : "Update Required");
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setPadding(0, 20, 0, 0);
            leftContent.addView(title);
            TextView content = new TextView(activity);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            Moonlight.applyClickableLinks(activity, content, isChinese ? FaQinDeMiaoHaoKeAi._force_upd_content_ : FaQinDeMiaoHaoKeAi._force_upd_content_eng_);
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(0, 20, 0, 0);
            leftContent.addView(content);
            if (FaQinDeMiaoHaoKeAi._show_upd_log_) {
                TextView updateLogView = new TextView(activity);
                updateLogView.setTextSize(14);
                updateLogView.setText((isChinese ? "更新日志：\n" : "Update Log:\n") + (isChinese ? FaQinDeMiaoHaoKeAi._upd_log_ : FaQinDeMiaoHaoKeAi._upd_log_eng_));
                updateLogView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
                updateLogView.setPadding(0, 20, 0, 0);
                leftContent.addView(updateLogView);
            }

            LinearLayout rightButtons = new LinearLayout(activity);
            rightButtons.setOrientation(LinearLayout.VERTICAL);
            rightButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rightButtons.setGravity(Gravity.CENTER);

            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "立即更新" : "Update Now", v -> {
                try {
                    String urlToOpen = changku.convertToRawUrl(changku.extractUrlFromHref(FaQinDeMiaoHaoKeAi._force_upd_link_));
                    if (urlToOpen != null && !urlToOpen.isEmpty()) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen)));
                    } else {
                        changku.showToast(activity, isChinese ? "更新链接未配置" : "Update link not configured");
                    }
                } catch (Exception e) {
                    changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
                }
            }, true);
            if (FaQinDeMiaoHaoKeAi._show_force_update_close_btn_) {
                MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "暂不更新" : "Close", v -> {
                    if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) MaoMaoShiYiGeXiaoSaoHuo.animateDialogDismiss(FaQinDeMiaoHaoKeAi._s_main_dialog_);
                }, true);
            }
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
            }, true);

            rootLayout.addView(leftContent);
            rootLayout.addView(rightButtons);
        } else {
            rootLayout.setOrientation(LinearLayout.VERTICAL);
            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(250, 250);
                ip.gravity = Gravity.CENTER_HORIZONTAL;
                ip.setMargins(0, 40, 0, 0);
                iv.setLayoutParams(ip);
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                rootLayout.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setGravity(Gravity.CENTER);
            title.setText(isChinese ? "版本更新" : "Update Required");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setTextSize(25);
            rootLayout.addView(title);
            View spacer1 = new View(activity);
            spacer1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80));
            rootLayout.addView(spacer1);
            TextView content = new TextView(activity);
            content.setTextSize(18);
            Moonlight.applyClickableLinks(activity, content, isChinese ? FaQinDeMiaoHaoKeAi._force_upd_content_ : FaQinDeMiaoHaoKeAi._force_upd_content_eng_);
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(100, 50, 100, 50);
            rootLayout.addView(content);
            if (FaQinDeMiaoHaoKeAi._show_upd_log_) {
                TextView updateLogView = new TextView(activity);
                updateLogView.setTextSize(16);
                updateLogView.setText((isChinese ? "更新日志：\n" : "Update Log:\n") + (isChinese ? FaQinDeMiaoHaoKeAi._upd_log_ : FaQinDeMiaoHaoKeAi._upd_log_eng_));
                updateLogView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
                updateLogView.setPadding(100, 20, 100, 50);
                rootLayout.addView(updateLogView);
            }
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "立即更新" : "Update Now", v -> {
                try {
                    String urlToOpen = changku.convertToRawUrl(changku.extractUrlFromHref(FaQinDeMiaoHaoKeAi._force_upd_link_));
                    if (urlToOpen != null && !urlToOpen.isEmpty()) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen)));
                    } else {
                        changku.showToast(activity, isChinese ? "更新链接未配置" : "Update link not configured");
                    }
                } catch (Exception e) {
                    changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
                }
            }, false);
            if (FaQinDeMiaoHaoKeAi._show_force_update_close_btn_) {
                MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "暂不更新" : "Close", v -> {
                    if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) MaoMaoShiYiGeXiaoSaoHuo.animateDialogDismiss(FaQinDeMiaoHaoKeAi._s_main_dialog_);
                }, false);
            }
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
            }, false);
            View spacer2 = new View(activity);
            spacer2.setLayoutParams(new LinearLayout.LayoutParams(1, 40));
            rootLayout.addView(spacer2);
        }

        sv.addView(rootLayout);
        FaQinDeMiaoHaoKeAi._s_main_dialog_ = builder.create();
        MaoMaoShiYiGeXiaoSaoHuo.setupCommonDialogProperties(activity, FaQinDeMiaoHaoKeAi._s_main_dialog_, sv, false);
    }

    /**
     * Displays a dialog indicating that the application's signature verification has failed.
     *
     * <p>This dialog informs the user that the app's integrity cannot be confirmed,
     * suggesting it might be a modified or unofficial version. It provides options to
     * download the official version or exit the application.
     *
     * <p>The dialog's appearance and layout adapt based on screen size (large screen/tablet
     * vs. phone) and orientation (landscape vs. portrait). It also considers whether
     * to show a specific icon (Moonlight icon) and applies background blur if enabled.
     *
     * <p>Key features:
     * <ul>
     *     <li>Displays a clear message about signature verification failure.</li>
     *     <li>Offers a button to "Download Official" version, which attempts to open
     *         a configured update link.</li>
     *     <li>Offers a button to "Exit App".</li>
     *     <li>Localizes text to Chinese or English based on system settings.</li>
     *     <li>Adjusts layout for optimal viewing on different screen sizes and orientations.</li>
     *     <li>Prevents multiple instances of this dialog from showing simultaneously.</li>
     *     <li>Returns early if the provided activity is null, finishing, or if another
     *         main dialog from {@code sharezhifu} is already showing.</li>
     * </ul>
     *
     * @param activity The {@link Activity} context in which to display the dialog.
     *                 If null or finishing, the dialog will not be shown.
     */
    public static void showSignatureFailureDialog(final Activity activity) {//签名校验弹窗
        if (activity == null || activity.isFinishing() || (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null && FaQinDeMiaoHaoKeAi._s_main_dialog_.isShowing())) {
            return;
        }
        if (FaQinDeMiaoHaoKeAi._enable_bg_blur_) {
            changku.applyBackgroundBlur(activity);
        }
        changku.resolveColors(activity);
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

        // --- Layout Logic ---
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        FaQinDeMiaoHaoKeAi._is_large_screen_ = dpWidth >= 600;
        int orientation = activity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean useHorizontalLayout = FaQinDeMiaoHaoKeAi._is_large_screen_ || isLandscape;

        ScrollView sv = new ScrollView(activity);
        LinearLayout rootLayout = new LinearLayout(activity);

        if (useHorizontalLayout) {
            // Landscape / Tablet Layout
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            rootLayout.setPadding(40, 40, 40, 40);
            rootLayout.setGravity(Gravity.CENTER);

            LinearLayout leftContent = new LinearLayout(activity);
            leftContent.setOrientation(LinearLayout.VERTICAL);
            leftContent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80)));
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                leftContent.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setText(isChinese ? "签名验证失败" : "Signature Verification Failed");
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setPadding(0, 20, 0, 0);
            leftContent.addView(title);

            TextView content = new TextView(activity);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            Moonlight.applyClickableLinks(activity, content, isChinese ? "应用签名验证失败，请下载官方正版！" : "App signature verification failed, please download the official version!");
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(0, 20, 0, 0);
            leftContent.addView(content);

            LinearLayout rightButtons = new LinearLayout(activity);
            rightButtons.setOrientation(LinearLayout.VERTICAL);
            rightButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rightButtons.setGravity(Gravity.CENTER);

            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "下载正版" : "Download Official", v -> {
                try {
                    String urlToOpen = changku.convertToRawUrl(changku.extractUrlFromHref(FaQinDeMiaoHaoKeAi._force_upd_link_));
                    if (urlToOpen != null && !urlToOpen.isEmpty()) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen)));
                    } else {
                        changku.showToast(activity, isChinese ? "更新链接未配置" : "Update link not configured");
                    }
                } catch (Exception e) {
                    changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
                }
            }, true);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
            }, true);

            rootLayout.addView(leftContent);
            rootLayout.addView(rightButtons);
        } else {
            // Portrait Layout
            rootLayout.setOrientation(LinearLayout.VERTICAL);
            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(250, 250);
                ip.gravity = Gravity.CENTER_HORIZONTAL;
                ip.setMargins(0, 40, 0, 0);
                iv.setLayoutParams(ip);
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                rootLayout.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setGravity(Gravity.CENTER);
            title.setText(isChinese ? "签名验证失败" : "Signature Verification Failed");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setTextSize(25);
            rootLayout.addView(title);
            View spacer1 = new View(activity);
            spacer1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80));
            rootLayout.addView(spacer1);
            TextView content = new TextView(activity);
            content.setTextSize(18);
            Moonlight.applyClickableLinks(activity, content, isChinese ? "应用签名验证失败，请下载官方正版！" : "App signature verification failed, please download the official version!");
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(100, 50, 100, 50);
            rootLayout.addView(content);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "下载正版" : "Download Official", v -> {
                try {
                    String urlToOpen = changku.convertToRawUrl(changku.extractUrlFromHref(FaQinDeMiaoHaoKeAi._force_upd_link_));
                    if (urlToOpen != null && !urlToOpen.isEmpty()) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen)));
                    } else {
                        changku.showToast(activity, isChinese ? "更新链接未配置" : "Update link not configured");
                    }
                } catch (Exception e) {
                    changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
                }
            }, false);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
            }, false);
            View spacer2 = new View(activity);
            spacer2.setLayoutParams(new LinearLayout.LayoutParams(1, 40));
            rootLayout.addView(spacer2);
        }
        sv.addView(rootLayout);
        FaQinDeMiaoHaoKeAi._s_main_dialog_ = builder.create();
        MaoMaoShiYiGeXiaoSaoHuo.setupCommonDialogProperties(activity, FaQinDeMiaoHaoKeAi._s_main_dialog_, sv, false);
    }

    /**
     * Displays a dialog indicating that the application is locked.
     * <p>
     * This dialog informs the user that the current version of the application is locked
     * and provides options to visit a link (if provided), close the dialog (if enabled),
     * or exit the application. The dialog's appearance and layout adapt based on screen size
     * and orientation.
     * <p>
     * The method first checks if the activity is valid and if another dialog is already showing.
     * It then applies background blur (if enabled) and resolves colors for the dialog.
     * The layout can be either horizontal (for large screens or landscape orientation)
     * or vertical.
     * <p>
     * If a {@code link} is provided, a "Visit Link" button will be shown, allowing the user
     * to open the URL in a browser.
     * If {@code _show_package_lock_close_btn_} is true, a "Close" button will be available
     * to dismiss the dialog.
     * An "Exit App" button is always present, which will terminate the application.
     * <p>
     * The content message displayed in the dialog can be customized via the {@code contentMessage}
     * parameter. If it's empty, a default message (localized to Chinese or English) will be used.
     * Any URLs within the content message will be made clickable.
     *
     * @param activity       The current Activity context.
     * @param contentMessage The custom message to display in the dialog. If empty, a default message is used.
     * @param link           An optional URL to provide as a "Visit Link" option. Can be null or empty.
     */
    public static void showPackageLockedDialog(final Activity activity, final String contentMessage, final String link) {
        if (activity == null || activity.isFinishing() || (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null && FaQinDeMiaoHaoKeAi._s_main_dialog_.isShowing())) {
            return;
        }
        if (FaQinDeMiaoHaoKeAi._enable_bg_blur_) {
            changku.applyBackgroundBlur(activity);
        }
        changku.resolveColors(activity);
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        FaQinDeMiaoHaoKeAi._is_large_screen_ = dpWidth >= 600;
        int orientation = activity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean useHorizontalLayout = FaQinDeMiaoHaoKeAi._is_large_screen_ || isLandscape;

        ScrollView sv = new ScrollView(activity);
        LinearLayout rootLayout = new LinearLayout(activity);

        if (useHorizontalLayout) {
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            rootLayout.setPadding(40, 40, 40, 40);
            rootLayout.setGravity(Gravity.CENTER);

            LinearLayout leftContent = new LinearLayout(activity);
            leftContent.setOrientation(LinearLayout.VERTICAL);
            leftContent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80)));
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                leftContent.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setText(isChinese ? "应用已被锁定" : "Application Locked");
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setPadding(0, 20, 0, 0);
            leftContent.addView(title);
            TextView content = new TextView(activity);
            String defaultLockedContent = isChinese ? "此应用版本已锁定，请下载最新官方版本或联系管理员获取支持。" : "This application version is locked. Please download the latest official version or contact the administrator for support.";
            content.setText(contentMessage.isEmpty() ? defaultLockedContent : contentMessage);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(0, 20, 0, 0);
            Moonlight.applyClickableLinks(activity, content, content.getText().toString());
            leftContent.addView(content);

            LinearLayout rightButtons = new LinearLayout(activity);
            rightButtons.setOrientation(LinearLayout.VERTICAL);
            rightButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rightButtons.setGravity(Gravity.CENTER);

            if (link != null && !link.isEmpty()) {
                MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "访问链接" : "Visit Link", v -> {
                    try {
                        String urlToOpen = changku.convertToRawUrl(changku.extractUrlFromHref(link));
                        if (urlToOpen != null && !urlToOpen.isEmpty()) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen)));
                        } else {
                            changku.showToast(activity, isChinese ? "链接未配置" : "Link not configured");
                        }
                    } catch (Exception e) {
                        changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
                    }
                }, true);
            }
            if (FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_) {
                MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "关闭弹窗" : "Close", v -> {
                    if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) MaoMaoShiYiGeXiaoSaoHuo.animateDialogDismiss(FaQinDeMiaoHaoKeAi._s_main_dialog_);
                }, true);
            }
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
                throw new RuntimeException("Application locked, forced exit.");
            }, true);

            rootLayout.addView(leftContent);
            rootLayout.addView(rightButtons);
        } else {
            rootLayout.setOrientation(LinearLayout.VERTICAL);
            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(250, 250);
                ip.gravity = Gravity.CENTER_HORIZONTAL;
                ip.setMargins(0, 40, 0, 0);
                iv.setLayoutParams(ip);
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                rootLayout.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setGravity(Gravity.CENTER);
            title.setText(isChinese ? "应用已被锁定" : "Application Locked");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setTextSize(25);
            rootLayout.addView(title);
            View spacer1 = new View(activity);
            spacer1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80));
            rootLayout.addView(spacer1);
            TextView content = new TextView(activity);
            String defaultLockedContent = isChinese ? "此应用版本已锁定，请下载最新官方版本或联系管理员获取支持。" : "This application version is locked. Please download the latest official version or contact the administrator for support.";
            content.setText(contentMessage.isEmpty() ? defaultLockedContent : contentMessage);
            content.setTextSize(18);
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setGravity(Gravity.CENTER);
            content.setPadding(100, 50, 100, 50);
            Moonlight.applyClickableLinks(activity, content, content.getText().toString());
            rootLayout.addView(content);
            if (link != null && !link.isEmpty()) {
                MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "访问链接" : "Visit Link", v -> {
                    try {
                        String urlToOpen = changku.convertToRawUrl(changku.extractUrlFromHref(link));
                        if (urlToOpen != null && !urlToOpen.isEmpty()) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen)));
                        } else {
                            changku.showToast(activity, isChinese ? "链接未配置" : "Link not configured");
                        }
                    } catch (Exception e) {
                        changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
                    }
                }, false);
            }
            if (FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_) {
                MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "关闭弹窗" : "Close", v -> {
                    if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) MaoMaoShiYiGeXiaoSaoHuo.animateDialogDismiss(FaQinDeMiaoHaoKeAi._s_main_dialog_);
                }, false);
            }
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
                throw new RuntimeException("Application locked, forced exit.");
            }, false);
            View spacer2 = new View(activity);
            spacer2.setLayoutParams(new LinearLayout.LayoutParams(1, 40));
            rootLayout.addView(spacer2);
        }

        sv.addView(rootLayout);
        FaQinDeMiaoHaoKeAi._s_main_dialog_ = builder.create();
        MaoMaoShiYiGeXiaoSaoHuo.setupCommonDialogProperties(activity, FaQinDeMiaoHaoKeAi._s_main_dialog_, sv, false);
    }

    /**
     * Displays a dialog indicating that the device is restricted from using the application.
     *
     * <p>This dialog informs the user that their device is not authorized and provides
     * options to copy the device ID, contact the administrator, or exit the application.
     * It also displays a countdown timer before automatically closing the application.
     *
     * <p>The dialog's layout adapts based on screen size and orientation (large screens or
     * landscape mode will use a horizontal layout, otherwise a vertical layout is used).
     *
     * <p>Before showing this dialog, it ensures that any existing Moonlight dialogs are
     * dismissed and cleaned up to maintain a clean UI state.
     * It also applies a background blur if enabled in the application settings.
     *
     * <p>Key features of this dialog:
     * <ul>
     *     <li>Displays the device ID.
     *     <li>Provides a button to copy the device ID to the clipboard.
     *     <li>Provides a button to open a link to get a key or contact the administrator.
     *          The link is determined by the following order of preference:
     *          {@code sharezhifu._dev_id_card_link_}, {@code sharezhifu._force_upd_link_},
     *          {@code sharezhifu._tg_channel_link_}.
     *     <li>Provides a button to exit the application.
     *     <li>Shows a countdown timer (controlled by {@link changku#startDeviceIdCountdown})
     *         after which the application will likely be closed.
     *     <li>Handles localization for Chinese and non-Chinese languages.
     *     <li>Optionally displays a Moonlight icon.
     * </ul>
     *
     * <p>Important: This method should be called on the UI thread.
     *
     * @param activity The {@link Activity} context in which to display the dialog.
     *                 If the activity is null or finishing, the dialog will not be shown.
     */
    public static void showDeviceIdRestrictedDialog(final Activity activity) {
        // [FIX] Call the centralized cleanup method in Moonlight to ensure a clean state.
        Moonlight.dismissAndCleanupAllDialogs();

        if (activity == null || activity.isFinishing()) {
            return; // Don't show a dialog for a dying activity
        }

        if (FaQinDeMiaoHaoKeAi._enable_bg_blur_) {
            changku.applyBackgroundBlur(activity);
        }
        changku.resolveColors(activity);
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);
        final String deviceId = Moonlight.getDeviceId(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        FaQinDeMiaoHaoKeAi._is_large_screen_ = dpWidth >= 600;
        int orientation = activity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean useHorizontalLayout = FaQinDeMiaoHaoKeAi._is_large_screen_ || isLandscape;

        ScrollView sv = new ScrollView(activity);
        LinearLayout rootLayout = new LinearLayout(activity);

        if (useHorizontalLayout) {
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            rootLayout.setPadding(40, 40, 40, 40);
            rootLayout.setGravity(Gravity.CENTER);

            LinearLayout leftContent = new LinearLayout(activity);
            leftContent.setOrientation(LinearLayout.VERTICAL);
            leftContent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80)));
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                leftContent.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setText(isChinese ? "设备限制" : "Device Restricted");
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setPadding(0, 20, 0, 0);
            leftContent.addView(title);
            TextView content = new TextView(activity);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            Moonlight.applyClickableLinks(activity, content, isChinese ? "您的设备码: " + deviceId + "\n该设备未被授权使用此应用。" : "Your Device ID: " + deviceId + "\nThis device is not authorized to use this application.");
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(0, 20, 0, 0);
            leftContent.addView(content);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_ = new TextView(activity);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_.setTextSize(14);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_.setTextColor(Color.RED);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_.setPadding(0, 10, 0, 0);
            leftContent.addView(FaQinDeMiaoHaoKeAi._dev_id_timer_text_);

            LinearLayout rightButtons = new LinearLayout(activity);
            rightButtons.setOrientation(LinearLayout.VERTICAL);
            rightButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rightButtons.setGravity(Gravity.CENTER);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "复制设备码" : "Copy Device ID", v -> {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Device ID", deviceId);
                clipboard.setPrimaryClip(clip);
                changku.showToast(activity, isChinese ? "设备码已复制" : "Device ID copied to clipboard");
            }, true);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "获取卡密/联系管理员" : "Get Key/Contact Admin", v -> {
                try {
                    String urlToOpen = FaQinDeMiaoHaoKeAi._dev_id_card_link_;
                    if (urlToOpen == null || urlToOpen.isEmpty()) urlToOpen = FaQinDeMiaoHaoKeAi._force_upd_link_;
                    if (urlToOpen == null || urlToOpen.isEmpty()) urlToOpen = FaQinDeMiaoHaoKeAi._tg_channel_link_;
                    if (urlToOpen != null && !urlToOpen.isEmpty()) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(changku.convertToRawUrl(changku.extractUrlFromHref(urlToOpen)))));
                    } else {
                        changku.showToast(activity, isChinese ? "联系链接未配置" : "Contact link not configured");
                    }
                } catch (Exception e) {
                    changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
                }
            }, true);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
            }, true);

            rootLayout.addView(leftContent);
            rootLayout.addView(rightButtons);
        } else {
            rootLayout.setOrientation(LinearLayout.VERTICAL);
            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(250, 250);
                ip.gravity = Gravity.CENTER_HORIZONTAL;
                ip.setMargins(0, 40, 0, 0);
                iv.setLayoutParams(ip);
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                rootLayout.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setGravity(Gravity.CENTER);
            title.setText(isChinese ? "设备限制" : "Device Restricted");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setTextSize(25);
            rootLayout.addView(title);
            View spacer1 = new View(activity);
            spacer1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80));
            rootLayout.addView(spacer1);
            TextView content = new TextView(activity);
            content.setTextSize(18);
            Moonlight.applyClickableLinks(activity, content, isChinese ? "您的设备码: " + deviceId + "\n该设备未被授权使用此应用。" : "Your Device ID: " + deviceId + "\nThis device is not authorized to use this application.");
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(100, 0, 100, 50);
            rootLayout.addView(content);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_ = new TextView(activity);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_.setTextSize(16);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_.setTextColor(Color.RED);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_.setGravity(Gravity.CENTER);
            FaQinDeMiaoHaoKeAi._dev_id_timer_text_.setPadding(100, MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10), 100, MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20));
            rootLayout.addView(FaQinDeMiaoHaoKeAi._dev_id_timer_text_);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "复制设备码" : "Copy Device ID", v -> {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Device ID", deviceId);
                clipboard.setPrimaryClip(clip);
                changku.showToast(activity, isChinese ? "设备码已复制" : "Device ID copied to clipboard");
            }, false);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "获取卡密/联系管理员" : "Get Key/Contact Admin", v -> {
                try {
                    String urlToOpen = FaQinDeMiaoHaoKeAi._dev_id_card_link_;
                    if (urlToOpen == null || urlToOpen.isEmpty()) urlToOpen = FaQinDeMiaoHaoKeAi._force_upd_link_;
                    if (urlToOpen == null || urlToOpen.isEmpty()) urlToOpen = FaQinDeMiaoHaoKeAi._tg_channel_link_;
                    if (urlToOpen != null && !urlToOpen.isEmpty()) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(changku.convertToRawUrl(changku.extractUrlFromHref(urlToOpen)))));
                    } else {
                        changku.showToast(activity, isChinese ? "联系链接未配置" : "Contact link not configured");
                    }
                } catch (Exception e) {
                    changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
                }
            }, false);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
            }, false);
            View spacer2 = new View(activity);
            spacer2.setLayoutParams(new LinearLayout.LayoutParams(1, 40));
            rootLayout.addView(spacer2);
        }
        sv.addView(rootLayout);

        FaQinDeMiaoHaoKeAi._s_main_dialog_ = builder.create();
        FaQinDeMiaoHaoKeAi._s_activity_ref_ = new WeakReference<>(activity);

        FaQinDeMiaoHaoKeAi._s_main_dialog_.setOnDismissListener(dialogInterface -> {
            // The centralized cleanup handles the main resources.
            // We only need to ensure the blur is removed from the correct activity instance.
            if (activity != null && !activity.isFinishing()) {
                changku.removeBackgroundBlur(activity);
            }
        });

        MaoMaoShiYiGeXiaoSaoHuo.setupCommonDialogProperties(activity, FaQinDeMiaoHaoKeAi._s_main_dialog_, sv, false);
        changku.startDeviceIdCountdown(activity, FaQinDeMiaoHaoKeAi._dev_id_timer_text_);
        FaQinDeMiaoHaoKeAi._s_main_dialog_.show();
    }

    /**
     * Displays a dialog for card key validation.
     * <p>
     * This method constructs and shows an AlertDialog that prompts the user to enter a card key
     * for validation. The dialog's appearance and content are dynamically adjusted based on the
     * device's screen size, orientation, and language settings. It also displays the current
     * device ID and provides options to copy it or get a card key.
     * </p>
     * <p>
     * Before displaying this dialog, it ensures any existing Moonlight dialogs are dismissed
     * and cleaned up. It also applies a background blur effect if enabled.
     * </p>
     * <p>
     * Key features of the dialog:
     * <ul>
     *     <li>Prevents display if the activity is null or finishing.</li>
     *     <li>Localizes text content (e.g., "卡密验证" or "Card Key Validation") based on the device language.</li>
     *     <li>Adapts layout (horizontal or vertical) for large screens or landscape orientation.</li>
     *     <li>Optionally displays a Moonlight icon.</li>
     *     <li>Displays the current device ID with an option to copy it.</li>
     *     <li>Includes an input field for the card key.</li>
     *     <li>Provides a "Validate Card Key" button which triggers {@link changku#validateCardKey(Activity, EditText)}.</li>
     *     <li>Provides a "Copy Device ID" button which triggers {@link changku#copyDeviceId(Activity, String)}.</li>
     *     <li>Provides a "Get Key/Contact Admin" button which triggers {@link changku#getCardKey(Activity)}.</li>
     *     <li>Provides an "Exit App" button to close the application.</li>
     *     <li>Shows a countdown timer (controlled by {@link changku#startCardKeyCountdownTimer}) indicating the time
     *         remaining for card key input, after which the dialog might be dismissed or another action taken.</li>
     * </ul>
     * </p>
     * <p>
     * The dialog instance is stored in {@code sharezhifu._s_main_dialog_} and its associated activity
     * in {@code sharezhifu._s_activity_ref_}. Common dialog properties are set up before showing.
     */
    public static void showCardKeyInputDialog(final Activity activity) {
        // [FIX] Call the centralized cleanup method in Moonlight to ensure a clean state.
        Moonlight.dismissAndCleanupAllDialogs();

        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (FaQinDeMiaoHaoKeAi._enable_bg_blur_) {
            changku.applyBackgroundBlur(activity);
        }
        changku.resolveColors(activity);
        final boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);
        final String currentDeviceId = Moonlight.getDeviceId(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        FaQinDeMiaoHaoKeAi._is_large_screen_ = dpWidth >= 600;
        int orientation = activity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean useHorizontalLayout = FaQinDeMiaoHaoKeAi._is_large_screen_ || isLandscape;

        ScrollView sv = new ScrollView(activity);
        LinearLayout rootLayout = new LinearLayout(activity);
        final EditText cardKeyInput = new EditText(activity);
        final TextView cardKeyCountdownLocalTextView = new TextView(activity);

        if (useHorizontalLayout) {
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            rootLayout.setPadding(40, 40, 40, 40);
            rootLayout.setGravity(Gravity.CENTER);

            LinearLayout leftContent = new LinearLayout(activity);
            leftContent.setOrientation(LinearLayout.VERTICAL);
            leftContent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80)));
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                leftContent.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setText(isChinese ? "卡密验证" : "Card Key Validation");
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setPadding(0, 20, 0, 0);
            leftContent.addView(title);
            TextView deviceIdTextView = new TextView(activity);
            deviceIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            Moonlight.applyClickableLinks(activity, deviceIdTextView, isChinese ? "您的设备码: " + currentDeviceId : "Your Device ID: " + currentDeviceId);
            deviceIdTextView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            deviceIdTextView.setPadding(0, 20, 0, 0);
            leftContent.addView(deviceIdTextView);
            cardKeyCountdownLocalTextView.setTextSize(14);
            cardKeyCountdownLocalTextView.setTextColor(Color.RED);
            cardKeyCountdownLocalTextView.setPadding(0, 10, 0, 0);
            leftContent.addView(cardKeyCountdownLocalTextView);
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(-1, -2);
            etParams.setMargins(0, 10, 20, 10);
            cardKeyInput.setLayoutParams(etParams);
            cardKeyInput.setHint(isChinese ? "请输入卡密" : "Enter Card Key");
            cardKeyInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            Moonlight.setupEditTextStyle(activity, cardKeyInput);
            leftContent.addView(cardKeyInput);

            LinearLayout rightButtons = new LinearLayout(activity);
            rightButtons.setOrientation(LinearLayout.VERTICAL);
            rightButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rightButtons.setGravity(Gravity.CENTER);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "验证卡密" : "Validate Card Key", v -> changku.validateCardKey(activity, cardKeyInput), true);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "复制设备码" : "Copy Device ID", v -> changku.copyDeviceId(activity, currentDeviceId), true);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "获取卡密/联系管理员" : "Get Key/Contact Admin", v -> changku.getCardKey(activity), true);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
            }, true);

            rootLayout.addView(leftContent);
            rootLayout.addView(rightButtons);
        } else {
            rootLayout.setOrientation(LinearLayout.VERTICAL);
            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(250, 250);
                ip.gravity = Gravity.CENTER_HORIZONTAL;
                ip.setMargins(0, 40, 0, 0);
                iv.setLayoutParams(ip);
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                rootLayout.addView(iv);
            }
            TextView title = new TextView(activity);
            title.setGravity(Gravity.CENTER);
            title.setText(isChinese ? "卡密验证" : "Card Key Validation");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setTextSize(25);
            rootLayout.addView(title);
            View spacer1 = new View(activity);
            spacer1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80));
            rootLayout.addView(spacer1);
            TextView deviceIdTextView = new TextView(activity);
            deviceIdTextView.setTextSize(18);
            Moonlight.applyClickableLinks(activity, deviceIdTextView, isChinese ? "您的设备码: " + currentDeviceId : "Your Device ID: " + currentDeviceId);
            deviceIdTextView.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            deviceIdTextView.setPadding(100, 0, 100, 50);
            rootLayout.addView(deviceIdTextView);
            cardKeyCountdownLocalTextView.setTextSize(16);
            cardKeyCountdownLocalTextView.setTextColor(Color.RED);
            cardKeyCountdownLocalTextView.setGravity(Gravity.CENTER);
            cardKeyCountdownLocalTextView.setPadding(100, MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 10), 100, MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 20));
            rootLayout.addView(cardKeyCountdownLocalTextView);
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(-1, -2);
            etParams.setMargins(90, 10, 90, 10);
            cardKeyInput.setLayoutParams(etParams);
            cardKeyInput.setHint(isChinese ? "请输入卡密" : "Enter Card Key");
            cardKeyInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            Moonlight.setupEditTextStyle(activity, cardKeyInput);
            rootLayout.addView(cardKeyInput);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "验证卡密" : "Validate Card Key", v -> changku.validateCardKey(activity, cardKeyInput), false);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "复制设备码" : "Copy Device ID", v -> changku.copyDeviceId(activity, currentDeviceId), false);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "获取卡密/联系管理员" : "Get Key/Contact Admin", v -> changku.getCardKey(activity), false);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "退出应用" : "Exit App", v -> {
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
            }, false);
            View spacer2 = new View(activity);
            spacer2.setLayoutParams(new LinearLayout.LayoutParams(1, 40));
            rootLayout.addView(spacer2);
        }

        sv.addView(rootLayout);

        FaQinDeMiaoHaoKeAi._s_main_dialog_ = builder.create();
        FaQinDeMiaoHaoKeAi._s_activity_ref_ = new WeakReference<>(activity);

        FaQinDeMiaoHaoKeAi._s_main_dialog_.setOnDismissListener(dialogInterface -> {
            // The centralized cleanup handles the main resources.
            // We only need to ensure the blur is removed from the correct activity instance.
            if (activity != null && !activity.isFinishing()) {
                changku.removeBackgroundBlur(activity);
            }
        });

        MaoMaoShiYiGeXiaoSaoHuo.setupCommonDialogProperties(activity, FaQinDeMiaoHaoKeAi._s_main_dialog_, sv, false);
        changku.startCardKeyCountdownTimer(activity, cardKeyCountdownLocalTextView, FaQinDeMiaoHaoKeAi._s_main_dialog_);
        FaQinDeMiaoHaoKeAi._s_main_dialog_.show();
    }

    public static void originalPopupLogic(final Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        FaQinDeMiaoHaoKeAi._current_dialog_type_ = FaQinDeMiaoHaoKeAi.DialogType.NORMAL_POPUP;

        String currentPackageName = activity.getPackageName();
        if (FaQinDeMiaoHaoKeAi._normal_popup_control_.containsKey(currentPackageName) && !FaQinDeMiaoHaoKeAi._normal_popup_control_.get(currentPackageName)) {
            Moonlight.dismissAndCleanupAllDialogs();
            return;
        }


        final SharedPreferences prefs = changku.getSharedPreferences(activity);
        try {
            String storedVersion = prefs.getString("dialogVer", "default");
            if (prefs.getBoolean("0", false) && !FaQinDeMiaoHaoKeAi._reset_dont_show_ && storedVersion.equals(FaQinDeMiaoHaoKeAi._cfg_version_)) {
                Moonlight.dismissAndCleanupAllDialogs();
                return;
            }
            if (prefs.getBoolean("0", false) && !storedVersion.equals(FaQinDeMiaoHaoKeAi._cfg_version_)) {
                prefs.edit().remove("0").apply();
            }
        } catch (Exception e) {
            // Log error or handle
        }
        createDialog(activity, prefs);
    }

    public static void createDialog(final Activity activity, final SharedPreferences prefs) {
        Activity currentActivity = (FaQinDeMiaoHaoKeAi._s_activity_ref_ != null) ? FaQinDeMiaoHaoKeAi._s_activity_ref_.get() : null;
        if (currentActivity == null || currentActivity.isFinishing()) {
            Moonlight.dismissAndCleanupAllDialogs();
            return;
        }
        if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null && FaQinDeMiaoHaoKeAi._s_main_dialog_.isShowing()) {
            return;
        }
        if (FaQinDeMiaoHaoKeAi._enable_bg_blur_) {
            changku.applyBackgroundBlur(currentActivity);
        }
        changku.resolveColors(currentActivity);

        if (FaQinDeMiaoHaoKeAi._dialog_version_ == 2) {
            createDialogStyle2(currentActivity, prefs);
        } else {
            createDialogStyle1(currentActivity, prefs);
        }
    }

    static void addButtonStyle1(LinearLayout layout, Activity activity, String text, View.OnClickListener listener, boolean compactLayout) {
        LinearLayout.LayoutParams lp;
        if (compactLayout) {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 10, 20, 10);
        } else {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(90, 10, 90, 10);
        }
        Button button = new Button(activity);
        button.setLayoutParams(lp);
        button.setText(text);
        button.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
        button.setBackground(changku.createButtonBackground(FaQinDeMiaoHaoKeAi._current_button_bg_));
        if (compactLayout) {
            button.setMinWidth(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 120));
        }
        button.setOnClickListener(v -> MaoMaoShiYiGeXiaoSaoHuo.animateButtonPress(v, () -> listener.onClick(v)));
        layout.addView(button);
    }

    // --- Dialog Style 2 (Themed Material Layout) ---
    static void createDialogStyle2(final Activity activity, final SharedPreferences prefs) {
        final boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        FaQinDeMiaoHaoKeAi._is_large_screen_ = dpWidth >= 600;

        int orientation = activity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean useHorizontalLayout = FaQinDeMiaoHaoKeAi._is_large_screen_ || isLandscape;

        ScrollView sv = new ScrollView(activity);
        LinearLayout rootLayout = new LinearLayout(activity);

        if (useHorizontalLayout) {
            // ===== Landscape / Tablet Layout (Style 2) =====
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            rootLayout.setPadding(40, 40, 40, 40);
            rootLayout.setGravity(Gravity.CENTER);

            // Left side
            LinearLayout leftContent = new LinearLayout(activity);
            leftContent.setOrientation(LinearLayout.VERTICAL);
            leftContent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            LinearLayout imageTitleContainer = new LinearLayout(activity);
            imageTitleContainer.setOrientation(LinearLayout.HORIZONTAL);
            imageTitleContainer.setGravity(Gravity.CENTER_VERTICAL);

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80)));
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                imageTitleContainer.addView(iv);
            }

            TextView title = new TextView(activity);
            title.setText(isChinese ? FaQinDeMiaoHaoKeAi._popup_title_ : FaQinDeMiaoHaoKeAi._popup_title_eng_);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setPadding(20, 0, 0, 0);
            imageTitleContainer.addView(title);
            leftContent.addView(imageTitleContainer);

            TextView content = new TextView(activity);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            Moonlight.applyClickableLinks(activity, content, FaQinDeMiaoHaoKeAi._show_dyn_content_ ? (isChinese ? FaQinDeMiaoHaoKeAi._dyn_content_ : FaQinDeMiaoHaoKeAi._eng_content_) : (isChinese ? "欢迎使用" : "Welcome"));
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(0, 20, 0, 0);
            leftContent.addView(content);

            // Right side
            LinearLayout rightButtons = new LinearLayout(activity);
            rightButtons.setOrientation(LinearLayout.VERTICAL);
            rightButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rightButtons.setGravity(Gravity.CENTER);

            if (FaQinDeMiaoHaoKeAi._show_close_btn_) MaoMaoShiYiGeXiaoSaoHuo.addButtonStyle2(rightButtons, activity, isChinese ? "关闭弹窗" : "Close", v -> { if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) MaoMaoShiYiGeXiaoSaoHuo.animateDialogDismiss(FaQinDeMiaoHaoKeAi._s_main_dialog_); }, true);
            if (FaQinDeMiaoHaoKeAi._show_tg_btn_) MaoMaoShiYiGeXiaoSaoHuo.addButtonStyle2(rightButtons, activity, isChinese ? "加入TG频道" : "Join Telegram", v -> MaoMaoShiYiGeXiaoSaoHuo.openTelegram(activity), true);
            if (FaQinDeMiaoHaoKeAi._show_qq_btn_) MaoMaoShiYiGeXiaoSaoHuo.addButtonStyle2(rightButtons, activity, isChinese ? "加入QQ群" : "Join QQ Group", v -> MaoMaoShiYiGeXiaoSaoHuo.openQQGroup(activity), true);
            if (FaQinDeMiaoHaoKeAi._show_dont_show_btn_) MaoMaoShiYiGeXiaoSaoHuo.addButtonStyle2(rightButtons, activity, isChinese ? "不再提示" : "Don't Show Again", v -> MaoMaoShiYiGeXiaoSaoHuo.showDontShowAgainDialog(activity, prefs), true);

            rootLayout.addView(leftContent);
            rootLayout.addView(rightButtons);
        } else {
            // ===== Portrait Layout (Original Style 2) =====
            rootLayout.setOrientation(LinearLayout.VERTICAL);

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(250, 250);
                ip.gravity = Gravity.CENTER_HORIZONTAL;
                ip.setMargins(0, 40, 0, 0);
                iv.setLayoutParams(ip);
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                rootLayout.addView(iv);
            }

            TextView title = new TextView(activity);
            title.setGravity(Gravity.CENTER);
            title.setText(isChinese ? FaQinDeMiaoHaoKeAi._popup_title_ : FaQinDeMiaoHaoKeAi._popup_title_eng_);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setTextSize(25);
            rootLayout.addView(title);

            View spacer1 = new View(activity);
            spacer1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80));
            rootLayout.addView(spacer1);

            TextView content = new TextView(activity);
            content.setTextSize(18);
            Moonlight.applyClickableLinks(activity, content, FaQinDeMiaoHaoKeAi._show_dyn_content_ ? (isChinese ? FaQinDeMiaoHaoKeAi._dyn_content_ : FaQinDeMiaoHaoKeAi._eng_content_) : (isChinese ? "欢迎使用" : "Welcome"));
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(100, 50, 100, 50);
            rootLayout.addView(content);

            if (FaQinDeMiaoHaoKeAi._show_close_btn_) MaoMaoShiYiGeXiaoSaoHuo.addButtonStyle2(rootLayout, activity, isChinese ? "关闭弹窗" : "Close", v -> { if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) MaoMaoShiYiGeXiaoSaoHuo.animateDialogDismiss(FaQinDeMiaoHaoKeAi._s_main_dialog_); }, false);
            if (FaQinDeMiaoHaoKeAi._show_tg_btn_) MaoMaoShiYiGeXiaoSaoHuo.addButtonStyle2(rootLayout, activity, isChinese ? "加入TG频道" : "Join Telegram", v -> MaoMaoShiYiGeXiaoSaoHuo.openTelegram(activity), false);
            if (FaQinDeMiaoHaoKeAi._show_qq_btn_) MaoMaoShiYiGeXiaoSaoHuo.addButtonStyle2(rootLayout, activity, isChinese ? "加入QQ群" : "Join QQ Group", v -> MaoMaoShiYiGeXiaoSaoHuo.openQQGroup(activity), false);
            if (FaQinDeMiaoHaoKeAi._show_dont_show_btn_) MaoMaoShiYiGeXiaoSaoHuo.addButtonStyle2(rootLayout, activity, isChinese ? "不再提示" : "Don't Show Again", v -> MaoMaoShiYiGeXiaoSaoHuo.showDontShowAgainDialog(activity, prefs), false);

            View spacer2 = new View(activity);
            spacer2.setLayoutParams(new LinearLayout.LayoutParams(1, 80));
            rootLayout.addView(spacer2);
        }

        sv.addView(rootLayout);
        FaQinDeMiaoHaoKeAi._s_main_dialog_ = builder.create();
        MaoMaoShiYiGeXiaoSaoHuo.setupCommonDialogProperties(activity, FaQinDeMiaoHaoKeAi._s_main_dialog_, sv, FaQinDeMiaoHaoKeAi._enable_out_click_);
    }

    /**
     * Creates and configures a dialog with a simple layout (Style 1).
     * This dialog style adapts its layout (portrait or landscape/tablet) based on screen size and orientation.
     * It can display an icon, title, content text, and several action buttons.
     * The content and button labels are localized based on the device's language settings (Chinese or English).
     *
     * <p><b>Layout Details:</b></p>
     * <ul>
     *   <li><b>Portrait Mode:</b>
     *     <ul>
     *       <li>Icon (if enabled) centered at the top.</li>
     *       <li>Title centered below the icon.</li>
     *       <li>Content text below the title, with padding.</li>
     *       <li>Action buttons stacked vertically at the bottom.</li>
     *     </ul>
     *   </li>
     *   <li><b>Landscape/Tablet Mode:</b>
     *     <ul>
     *       <li><b>Left Side:</b>
     *         <ul>
     *           <li>Icon (if enabled) and Title displayed horizontally.</li>
     *           <li>Content text below the icon and title.</li>
     *         </ul>
     *       </li>
     *       <li><b>Right Side:</b>
     *         <ul>
     *           <li>Action buttons stacked vertically.</li>
     *         </ul>
     *       </li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p><b>Configurable Elements (via {@code sharezhifu} and {@code peizhi} classes):</b></p>
     * <ul>
     *   <li>{@code sharezhifu._show_moonlight_icon_}: Whether to show the Moonlight icon.</li>
     *   <li>{@code sharezhifu._popup_title_} / {@code sharezhifu._popup_title_eng_}: Dialog title text.</li>
     *   <li>{@code sharezhifu._current_text_color_}: Text color for title and content.</li>
     *   <li>{@code sharezhifu._show_dyn_content_}: Whether to use dynamic content.</li>
     *   <li>{@code sharezhifu._dyn_content_} / {@code sharezhifu._eng_content_}: Dynamic content text.</li>
     */ // --- Dialog Style 1 (Simple Layout) ---
    static void createDialogStyle1(final Activity activity, final SharedPreferences prefs) {
        final boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        FaQinDeMiaoHaoKeAi._is_large_screen_ = dpWidth >= 600;

        int orientation = activity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean useHorizontalLayout = FaQinDeMiaoHaoKeAi._is_large_screen_ || isLandscape;

        ScrollView sv = new ScrollView(activity);
        LinearLayout rootLayout = new LinearLayout(activity);

        if (useHorizontalLayout) {
            // ===== Landscape / Tablet Layout =====
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
            rootLayout.setPadding(40, 40, 40, 40);
            rootLayout.setGravity(Gravity.CENTER);

            // Left side
            LinearLayout leftContent = new LinearLayout(activity);
            leftContent.setOrientation(LinearLayout.VERTICAL);
            leftContent.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            LinearLayout imageTitleContainer = new LinearLayout(activity);
            imageTitleContainer.setOrientation(LinearLayout.HORIZONTAL);
            imageTitleContainer.setGravity(Gravity.CENTER_VERTICAL);

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(new LinearLayout.LayoutParams(MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80), MaoMaoShiYiGeXiaoSaoHuo.dpToPx_int(activity, 80)));
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                imageTitleContainer.addView(iv);
            }

            TextView title = new TextView(activity);
            title.setText(isChinese ? FaQinDeMiaoHaoKeAi._popup_title_ : FaQinDeMiaoHaoKeAi._popup_title_eng_);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setPadding(20, 0, 0, 0);
            imageTitleContainer.addView(title);
            leftContent.addView(imageTitleContainer);

            TextView content = new TextView(activity);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            Moonlight.applyClickableLinks(activity, content, FaQinDeMiaoHaoKeAi._show_dyn_content_ ? (isChinese ? FaQinDeMiaoHaoKeAi._dyn_content_ : FaQinDeMiaoHaoKeAi._eng_content_) : (isChinese ? "欢迎使用" : "Welcome"));
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(0, 20, 0, 0);
            leftContent.addView(content);

            // Right side
            LinearLayout rightButtons = new LinearLayout(activity);
            rightButtons.setOrientation(LinearLayout.VERTICAL);
            rightButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rightButtons.setGravity(Gravity.CENTER);

            if (FaQinDeMiaoHaoKeAi._show_close_btn_) addButtonStyle1(rightButtons, activity, isChinese ? "关闭弹窗" : "Close", v -> { if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) MaoMaoShiYiGeXiaoSaoHuo.animateDialogDismiss(FaQinDeMiaoHaoKeAi._s_main_dialog_); }, true);
            if (FaQinDeMiaoHaoKeAi._show_tg_btn_) addButtonStyle1(rightButtons, activity, isChinese ? "加入TG频道" : "Join Telegram", v -> MaoMaoShiYiGeXiaoSaoHuo.openTelegram(activity), true);
            if (FaQinDeMiaoHaoKeAi._show_qq_btn_) addButtonStyle1(rightButtons, activity, isChinese ? "加入QQ群" : "Join QQ Group", v -> MaoMaoShiYiGeXiaoSaoHuo.openQQGroup(activity), true);
            if (FaQinDeMiaoHaoKeAi._show_dont_show_btn_) addButtonStyle1(rightButtons, activity, isChinese ? "不再提示" : "Don't Show Again", v -> MaoMaoShiYiGeXiaoSaoHuo.showDontShowAgainDialog(activity, prefs), true);

            rootLayout.addView(leftContent);
            rootLayout.addView(rightButtons);
        } else {
            // ===== Portrait Layout =====
            rootLayout.setOrientation(LinearLayout.VERTICAL);

            if (FaQinDeMiaoHaoKeAi._show_moonlight_icon_) {
                ImageView iv = new ImageView(activity);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(250, 250);
                ip.gravity = Gravity.CENTER_HORIZONTAL;
                ip.setMargins(0, 40, 0, 0);
                iv.setLayoutParams(ip);
                MaoMaoShiYiGeXiaoSaoHuo.setupMoonlightIcon(activity, iv);
                rootLayout.addView(iv);
            }

            TextView title = new TextView(activity);
            title.setGravity(Gravity.CENTER);
            title.setText(isChinese ? FaQinDeMiaoHaoKeAi._popup_title_ : FaQinDeMiaoHaoKeAi._popup_title_eng_);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setTextSize(25);
            rootLayout.addView(title);

            View spacer1 = new View(activity);
            spacer1.setLayoutParams(new LinearLayout.LayoutParams(-2, 80));
            rootLayout.addView(spacer1);

            TextView content = new TextView(activity);
            content.setTextSize(18);
            Moonlight.applyClickableLinks(activity, content, FaQinDeMiaoHaoKeAi._show_dyn_content_ ? (isChinese ? FaQinDeMiaoHaoKeAi._dyn_content_ : FaQinDeMiaoHaoKeAi._eng_content_) : (isChinese ? "欢迎使用" : "Welcome"));
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(100, 50, 100, 50);
            rootLayout.addView(content);

            if (FaQinDeMiaoHaoKeAi._show_close_btn_) addButtonStyle1(rootLayout, activity, isChinese ? "关闭弹窗" : "Close", v -> { if (FaQinDeMiaoHaoKeAi._s_main_dialog_ != null) MaoMaoShiYiGeXiaoSaoHuo.animateDialogDismiss(FaQinDeMiaoHaoKeAi._s_main_dialog_); }, false);
            if (FaQinDeMiaoHaoKeAi._show_tg_btn_) addButtonStyle1(rootLayout, activity, isChinese ? "加入TG频道" : "Join Telegram", v -> MaoMaoShiYiGeXiaoSaoHuo.openTelegram(activity), false);
            if (FaQinDeMiaoHaoKeAi._show_qq_btn_) addButtonStyle1(rootLayout, activity, isChinese ? "加入QQ群" : "Join QQ Group", v -> MaoMaoShiYiGeXiaoSaoHuo.openQQGroup(activity), false);
            if (FaQinDeMiaoHaoKeAi._show_dont_show_btn_) addButtonStyle1(rootLayout, activity, isChinese ? "不再提示" : "Don't Show Again", v -> MaoMaoShiYiGeXiaoSaoHuo.showDontShowAgainDialog(activity, prefs), false);

            View spacer2 = new View(activity);
            spacer2.setLayoutParams(new LinearLayout.LayoutParams(1, 40));
            rootLayout.addView(spacer2);
        }

        sv.addView(rootLayout);
        FaQinDeMiaoHaoKeAi._s_main_dialog_ = builder.create();
        MaoMaoShiYiGeXiaoSaoHuo.setupCommonDialogProperties(activity, FaQinDeMiaoHaoKeAi._s_main_dialog_, sv, FaQinDeMiaoHaoKeAi._enable_out_click_);
    }
}
