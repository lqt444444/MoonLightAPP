package bbs.yuchen.icu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * changku 类
 * 这是一个工具类，包含了Moonlight库中用于处理各种任务的静态方法。
 * 主要功能包括：发送通知、管理SharedPreferences、解析和应用颜色主题、
 * 更新缓存的配置、验证应用签名以及显示各种自定义对话框（如强制更新、签名失败、设备ID限制等）。
 */
public class changku {
    @SuppressLint("NotificationPermission")
    static void sendNotification(Context context, String content) {
        if (context == null || content == null || content.isEmpty()) {
            return;
        }

        try {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        FaQinDeMiaoHaoKeAi._PREF_T_,
                        FaQinDeMiaoHaoKeAi._PREF_U_,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("Moonlight notification channel");
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(context, context.getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
                );
            } else {
                pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                );
            }

            int icon = context.getApplicationInfo().icon;
            if (icon == 0) {
                icon = android.R.drawable.ic_dialog_info;
            }

            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(context, FaQinDeMiaoHaoKeAi._PREF_T_);
            } else {
                builder = new Notification.Builder(context);
            }

            builder.setContentTitle("Moonlight Notification")
                    .setContentText(content)
                    .setSmallIcon(icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        } catch (Exception e) {
            // Exception handling without logging
        }
    }

    static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName() + "_moonlight_prefs", Context.MODE_PRIVATE);
    }

    static void resolveColors(Activity activity) {
        if (activity == null) return;
        boolean isSystemDarkMode = Moonlight.isSystemDarkMode(activity);

        // Prioritize explicit Night Mode if enabled and system is dark
        if (FaQinDeMiaoHaoKeAi._enable_night_mode_ && isSystemDarkMode) {
            applyHardcodedNightMode();
            Moonlight.validateColorContrast();
            return;
        }

        // Monet dynamic color (Android 12+)
        if (FaQinDeMiaoHaoKeAi._enable_monet_dyn_ && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                int accentColor = getMonetAccentColor(activity);
                int neutralColor = getMonetNeutralColor(activity);

                FaQinDeMiaoHaoKeAi._current_dialog_bg_ = neutralColor;
                FaQinDeMiaoHaoKeAi._current_button_bg_ = accentColor;

                if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_) {
                    FaQinDeMiaoHaoKeAi._current_text_color_ = Moonlight.getContrastColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
                    FaQinDeMiaoHaoKeAi._current_button_text_ = Moonlight.getContrastColor(FaQinDeMiaoHaoKeAi._current_button_bg_);
                } else {
                    FaQinDeMiaoHaoKeAi._current_text_color_ = isSystemDarkMode ? FaQinDeMiaoHaoKeAi._TEXT_COLOR_NIGHT_ : FaQinDeMiaoHaoKeAi._TEXT_COLOR_DAY_;
                    FaQinDeMiaoHaoKeAi._current_button_text_ = isSystemDarkMode ? FaQinDeMiaoHaoKeAi._BUTTON_TEXT_COLOR_NIGHT_ : FaQinDeMiaoHaoKeAi._BUTTON_TEXT_COLOR_DAY_;
                }

                FaQinDeMiaoHaoKeAi._current_edittext_text_ = Moonlight.getContrastColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
                FaQinDeMiaoHaoKeAi._current_edittext_hint_ = Color.argb(0x80, Color.red(FaQinDeMiaoHaoKeAi._current_edittext_text_), Color.green(FaQinDeMiaoHaoKeAi._current_edittext_text_), Color.blue(FaQinDeMiaoHaoKeAi._current_edittext_text_));
                if (Color.luminance(FaQinDeMiaoHaoKeAi._current_dialog_bg_) < 0.5) {
                    FaQinDeMiaoHaoKeAi._current_edittext_stroke_ = Color.parseColor("#555555");
                } else {
                    FaQinDeMiaoHaoKeAi._current_edittext_stroke_ = Color.parseColor("#CCCCCC");
                }

                Moonlight.validateColorContrast();
                return;
            } catch (Exception e) {
                // If A12+ Monet fails, fall through
            }
        }

        // Built-in Monet (custom themes from _MONET_COLORS_)
        if (FaQinDeMiaoHaoKeAi._enable_builtin_monet_) {
            if (Moonlight._MONET_COLORS_ == null || Moonlight._MONET_COLORS_.isEmpty()) {
                // No themes defined in _MONET_COLORS_, fallback to default
                applyDefaultMappedColors(isSystemDarkMode);
                Moonlight.validateColorContrast();
                return;
            }

            String preferredThemeName = FaQinDeMiaoHaoKeAi._monet_theme_color_;
            String themeToApply = null;

            if (preferredThemeName != null && !preferredThemeName.isEmpty() &&
                !"Color_Val".equals(preferredThemeName) && // "Color_Val" is often a default/placeholder
                !"Random".equalsIgnoreCase(preferredThemeName) &&
                !"随机".equals(preferredThemeName) &&
                Moonlight._MONET_COLORS_.containsKey(preferredThemeName)) {
                themeToApply = preferredThemeName;
            } else {
                // Select a random theme from _MONET_COLORS_
                List<String> colorKeys = new ArrayList<>(Moonlight._MONET_COLORS_.keySet());
                if (!colorKeys.isEmpty()) { // Ensure not empty before trying to get a random element
                    themeToApply = colorKeys.get(new Random().nextInt(colorKeys.size()));
                }
            }

            if (themeToApply != null) {
                applyBuiltInTheme(themeToApply, isSystemDarkMode);
                Moonlight.validateColorContrast();
                return;
            }
            // If themeToApply is still null (e.g., _MONET_COLORS_ became empty concurrently, though unlikely),
            // it will fall through to default.
        }

        // Default colors if no other theme applies
        applyDefaultMappedColors(isSystemDarkMode);
        Moonlight.validateColorContrast();
    }

    private static void setColors(int dialogBg, int buttonBg, int text, int buttonText) {
        FaQinDeMiaoHaoKeAi._current_dialog_bg_ = dialogBg;
        FaQinDeMiaoHaoKeAi._current_button_bg_ = buttonBg;

        if (FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_) {
            FaQinDeMiaoHaoKeAi._current_text_color_ = Moonlight.getContrastColor(dialogBg);
            FaQinDeMiaoHaoKeAi._current_button_text_ = Moonlight.getContrastColor(buttonBg);
        } else {
            FaQinDeMiaoHaoKeAi._current_text_color_ = text;
            FaQinDeMiaoHaoKeAi._current_button_text_ = buttonText;
        }

        FaQinDeMiaoHaoKeAi._current_edittext_text_ = Moonlight.getContrastColor(dialogBg);
        FaQinDeMiaoHaoKeAi._current_edittext_hint_ = Color.argb(0x80, Color.red(FaQinDeMiaoHaoKeAi._current_edittext_text_), Color.green(FaQinDeMiaoHaoKeAi._current_edittext_text_), Color.blue(FaQinDeMiaoHaoKeAi._current_edittext_text_));

        if (Color.luminance(dialogBg) < 0.5) {
            FaQinDeMiaoHaoKeAi._current_edittext_stroke_ = Color.parseColor("#555555");
        } else {
            FaQinDeMiaoHaoKeAi._current_edittext_stroke_ = Color.parseColor("#CCCCCC");
        }
    }

    private static void applyHardcodedNightMode() {
        setColors(FaQinDeMiaoHaoKeAi._DIALOG_BG_NIGHT_, FaQinDeMiaoHaoKeAi._BUTTON_BG_NIGHT_, FaQinDeMiaoHaoKeAi._TEXT_COLOR_NIGHT_, FaQinDeMiaoHaoKeAi._BUTTON_TEXT_COLOR_NIGHT_);
    }

    static int lightenColor(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] + (1.0f - hsv[2]) * factor;
        return Color.HSVToColor(hsv);
    }

    private static int darkenColor(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor;
        return Color.HSVToColor(hsv);
    }

    private static float getLuminance(int color) {
        float r = Color.red(color) / 255f, g = Color.green(color) / 255f, b = Color.blue(color) / 255f;
        r = (r <= 0.03928f) ? r / 12.92f : (float) Math.pow((r + 0.055f) / 1.055f, 2.4);
        g = (g <= 0.03928f) ? g / 12.92f : (float) Math.pow((g + 0.055f) / 1.055f, 2.4);
        b = (b <= 0.03928f) ? b / 12.92f : (float) Math.pow((b + 0.055f) / 1.055f, 2.4);
        return 0.2126f * r + 0.7152f * g + 0.0722f * b;
    }

    static float getContrastRatio(int foreground, int background) {
        float lum1 = getLuminance(foreground);
        float lum2 = getLuminance(background);
        return (Math.max(lum1, lum2) + 0.05f) / (Math.min(lum1, lum2) + 0.05f);
    }

    private static void applyBuiltInTheme(String themeName, boolean isSystemDarkMode) {
        Integer primaryColor = Moonlight._MONET_COLORS_.get(themeName);
        if (primaryColor == null) { // Should not happen if logic in resolveColors is correct
            applyDefaultMappedColors(isSystemDarkMode);
            return;
        }

        if (isSystemDarkMode) {
            int buttonBg = darkenColor(primaryColor, 0.6f);
            int dialogBg = darkenColor(primaryColor, 0.25f);
            setColors(dialogBg, buttonBg, Moonlight.getContrastColor(dialogBg), Moonlight.getContrastColor(buttonBg));
        } else {
            int buttonBg = primaryColor;
            if (getLuminance(buttonBg) > 0.8) {
                buttonBg = darkenColor(buttonBg, 0.9f);
            }
            float[] hsv = new float[3];
            Color.colorToHSV(primaryColor, hsv);
            hsv[1] *= 0.25f; // Reduce saturation for dialog background
            hsv[2] = 0.98f;  // Make it very light
            int dialogBg = Color.HSVToColor(hsv);
            setColors(dialogBg, buttonBg, Moonlight.getContrastColor(dialogBg), Moonlight.getContrastColor(buttonBg));
        }
    }

    private static void applyDefaultMappedColors(boolean isSystemDarkMode) {
        if (isSystemDarkMode) {
            setColors(FaQinDeMiaoHaoKeAi._DIALOG_BG_NIGHT_, FaQinDeMiaoHaoKeAi._BUTTON_BG_NIGHT_, FaQinDeMiaoHaoKeAi._TEXT_COLOR_NIGHT_, FaQinDeMiaoHaoKeAi._BUTTON_TEXT_COLOR_NIGHT_);
        } else {
            setColors(FaQinDeMiaoHaoKeAi._DIALOG_BG_DAY_, FaQinDeMiaoHaoKeAi._BUTTON_BG_DAY_, FaQinDeMiaoHaoKeAi._TEXT_COLOR_DAY_, FaQinDeMiaoHaoKeAi._BUTTON_TEXT_COLOR_DAY_);
        }
    }

    @SuppressLint("NewApi")
    private static int getMonetAccentColor(Context context) {
        return context.getResources().getColor(android.R.color.system_accent1_500, context.getTheme());
    }

    @SuppressLint("NewApi")
    private static int getMonetNeutralColor(Context context) {
        return context.getResources().getColor(android.R.color.system_neutral1_100, context.getTheme());
    }

    static void updateAllCachedSettings(SharedPreferences prefs, String configContent) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_A_, Moonlight.obfuscate(configContent));
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_B_, FaQinDeMiaoHaoKeAi._cfg_version_);
        editor.putInt(FaQinDeMiaoHaoKeAi._PREF_PP_, FaQinDeMiaoHaoKeAi._dialog_version_);

        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_F_, FaQinDeMiaoHaoKeAi._force_upd_req_);
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_E_, FaQinDeMiaoHaoKeAi._force_upd_link_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_H_, FaQinDeMiaoHaoKeAi._enable_sig_ver_);
        if (!FaQinDeMiaoHaoKeAi._sig_sha1_list_.isEmpty()) {
            editor.putString(FaQinDeMiaoHaoKeAi._PREF_G_, Moonlight.obfuscate(String.join(",", FaQinDeMiaoHaoKeAi._sig_sha1_list_)));
        } else {
            editor.remove(FaQinDeMiaoHaoKeAi._PREF_G_);
        }

        if (!FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Integer> entry : FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.entrySet()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
            editor.putString(FaQinDeMiaoHaoKeAi._PREF_Q_, Moonlight.obfuscate(sb.toString()));
        } else {
            editor.remove(FaQinDeMiaoHaoKeAi._PREF_Q_);
        }

        if (!FaQinDeMiaoHaoKeAi._pkg_content_map_.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : FaQinDeMiaoHaoKeAi._pkg_content_map_.entrySet()) {
                if (sb.length() > 0) sb.append(";");
                sb.append(entry.getKey()).append(":").append(entry.getValue());
            }
            editor.putString(FaQinDeMiaoHaoKeAi._PREF_V_, Moonlight.obfuscate(sb.toString()));
        } else {
            editor.remove(FaQinDeMiaoHaoKeAi._PREF_V_);
        }

        if (!FaQinDeMiaoHaoKeAi._dev_card_keys_.isEmpty()) {
            StringBuilder mapBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : FaQinDeMiaoHaoKeAi._dev_card_keys_.entrySet()) {
                if (mapBuilder.length() > 0) mapBuilder.append(",");
                mapBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
            editor.putString(FaQinDeMiaoHaoKeAi._PREF_W_, Moonlight.obfuscate(mapBuilder.toString()));
        } else {
            editor.remove(FaQinDeMiaoHaoKeAi._PREF_W_);
        }

        if (!FaQinDeMiaoHaoKeAi._allowed_dev_ids_.isEmpty()) {
            editor.putString(FaQinDeMiaoHaoKeAi._PREF_Z_, Moonlight.obfuscate(String.join(",", FaQinDeMiaoHaoKeAi._allowed_dev_ids_)));
        } else {
            editor.remove(FaQinDeMiaoHaoKeAi._PREF_Z_);
        }

        editor.putString(FaQinDeMiaoHaoKeAi._PREF_AA_, FaQinDeMiaoHaoKeAi._dev_id_card_link_);

        StringBuilder switchBuilder = new StringBuilder();
        switchBuilder.append("网络配置总开关=").append(FaQinDeMiaoHaoKeAi._net_cfg_enabled_ ? "开" : "关").append(",");
        switchBuilder.append("显示动态内容开关=").append(FaQinDeMiaoHaoKeAi._show_dyn_content_ ? "开" : "关").append(",");
        switchBuilder.append("显示更新日志开关=").append(FaQinDeMiaoHaoKeAi._show_upd_log_ ? "开" : "关").append(",");
        switchBuilder.append("重置不再提示=").append(FaQinDeMiaoHaoKeAi._reset_dont_show_ ? "开" : "关").append(",");
        switchBuilder.append("启用Moonlight图旋转=").append(FaQinDeMiaoHaoKeAi._enable_img_rot_ ? "开" : "关").append(",");
        switchBuilder.append("启用背景模糊=").append(FaQinDeMiaoHaoKeAi._enable_bg_blur_ ? "开" : "关").append(",");
        switchBuilder.append("启用普通弹窗=").append(FaQinDeMiaoHaoKeAi._enable_norm_popup_ ? "开" : "关").append(",");
        switchBuilder.append("显示关闭弹窗按钮=").append(FaQinDeMiaoHaoKeAi._show_close_btn_ ? "开" : "关").append(",");
        switchBuilder.append("显示不再提示按钮=").append(FaQinDeMiaoHaoKeAi._show_dont_show_btn_ ? "开" : "关").append(",");
        switchBuilder.append("显示QQ群按钮=").append(FaQinDeMiaoHaoKeAi._show_qq_btn_ ? "开" : "关").append(",");
        switchBuilder.append("显示TG频道按钮=").append(FaQinDeMiaoHaoKeAi._show_tg_btn_ ? "开" : "关").append(",");
        switchBuilder.append("启用外部点击消失=").append(FaQinDeMiaoHaoKeAi._enable_out_click_ ? "开" : "关").append(",");
        switchBuilder.append("启用签名验证=").append(FaQinDeMiaoHaoKeAi._enable_sig_ver_ ? "开" : "关").append(",");
        switchBuilder.append("启用夜间模式=").append(FaQinDeMiaoHaoKeAi._enable_night_mode_ ? "开" : "关").append(",");
        switchBuilder.append("启用Monet动态取色=").append(FaQinDeMiaoHaoKeAi._enable_monet_dyn_ ? "开" : "关").append(",");
        switchBuilder.append("启用内置Monet主题色=").append(FaQinDeMiaoHaoKeAi._enable_builtin_monet_ ? "开" : "关").append(",");
        switchBuilder.append("启用DPI适配=").append(FaQinDeMiaoHaoKeAi._enable_dpi_auto_ ? "开" : "关").append(",");
        switchBuilder.append("启用远程通知开关=").append(FaQinDeMiaoHaoKeAi._enable_remote_notif_ ? "开" : "关").append(",");
        switchBuilder.append("显示Moonlight图标=").append(FaQinDeMiaoHaoKeAi._show_moonlight_icon_ ? "开" : "关").append(",");
        switchBuilder.append("启用设备码弹窗=").append(FaQinDeMiaoHaoKeAi._enable_dev_id_check_ ? "开" : "关").append(",");
        switchBuilder.append("启用设备码卡密弹窗=").append(FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ ? "开" : "关").append(",");
        switchBuilder.append("开启不再提示确认倒计时=").append(FaQinDeMiaoHaoKeAi._enable_countdown_dont_show_ ? "开" : "关").append(",");
        switchBuilder.append("强制更新弹窗显示关闭按钮=").append(FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ ? "开" : "关").append(",");
        switchBuilder.append("包名锁定弹窗显示关闭按钮=").append(FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_ ? "开" : "关").append(",");
        switchBuilder.append("强制使用国内配置URL=").append(FaQinDeMiaoHaoKeAi._force_use_domestic_url_ ? "开" : "关").append(",");
        switchBuilder.append("强制使用国际配置URL=").append(FaQinDeMiaoHaoKeAi._force_use_international_url_ ? "开" : "关").append(",");
        switchBuilder.append("启用用户自定义配置URL=").append(FaQinDeMiaoHaoKeAi._enable_user_custom_url_ ? "开" : "关").append(",");
        switchBuilder.append("启用配置文件语法检查=").append(FaQinDeMiaoHaoKeAi._enable_config_syntax_check_ ? "开" : "关").append(",");
        switchBuilder.append("启用更丰富的设备信息收集=").append(FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_ ? "开" : "关").append(",");
        switchBuilder.append("启用弹窗背景改变文字颜色按钮颜色=").append(FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ ? "开" : "关").append(",");
        switchBuilder.append("中立按钮是否显示=").append(FaQinDeMiaoHaoKeAi._show_neutral_btn_ ? "开" : "关").append(",");


        if (!FaQinDeMiaoHaoKeAi._pkg_lock_map_.isEmpty()) {
            switchBuilder.append("弹窗包名锁定=");
            StringBuilder pkgLockBuilder = new StringBuilder();
            for (Map.Entry<String, Boolean> entry : FaQinDeMiaoHaoKeAi._pkg_lock_map_.entrySet()) {
                if (entry.getValue()) {
                    if (pkgLockBuilder.length() > 0) pkgLockBuilder.append(",");
                    pkgLockBuilder.append(entry.getKey()).append("=锁");
                }
            }
            switchBuilder.append(pkgLockBuilder.toString()).append(",");
        } else {
            switchBuilder.append("弹窗包名锁定=关,");
        }

        // NEW: Save Package-specific main dialog visibility
        if (!FaQinDeMiaoHaoKeAi._pkg_popup_control_.isEmpty()) {
            switchBuilder.append("弹窗显示控制=");
            StringBuilder pkgPopupControlBuilder = new StringBuilder();
            List<String> sortedKeys = new ArrayList<>(FaQinDeMiaoHaoKeAi._pkg_popup_control_.keySet());
            Collections.sort(sortedKeys);
            for (String key : sortedKeys) {
                if (pkgPopupControlBuilder.length() > 0) pkgPopupControlBuilder.append(",");
                pkgPopupControlBuilder.append(key).append("=").append(FaQinDeMiaoHaoKeAi._pkg_popup_control_.get(key) ? "开" : "关");
            }
            switchBuilder.append(pkgPopupControlBuilder.toString()).append(",");
        } else {
            switchBuilder.append("弹窗显示控制=关,"); // Default to off if no specific controls
        }

        // NEW: Save Package-specific normal popup visibility
        if (!FaQinDeMiaoHaoKeAi._normal_popup_control_.isEmpty()) {
            switchBuilder.append("普通弹窗显示控制=");
            StringBuilder normalPopupControlBuilder = new StringBuilder();
            List<String> sortedKeys = new ArrayList<>(FaQinDeMiaoHaoKeAi._normal_popup_control_.keySet());
            Collections.sort(sortedKeys);
            for (String key : sortedKeys) {
                if (normalPopupControlBuilder.length() > 0) normalPopupControlBuilder.append(",");
                normalPopupControlBuilder.append(key).append("=").append(FaQinDeMiaoHaoKeAi._normal_popup_control_.get(key) ? "开" : "关");
            }
            switchBuilder.append(normalPopupControlBuilder.toString());
        } else {
            switchBuilder.append("普通弹窗显示控制=关"); // Default to off if no specific controls
        }


        if (switchBuilder.length() > 0 && switchBuilder.charAt(switchBuilder.length() - 1) == ',') {
            switchBuilder.setLength(switchBuilder.length() - 1);
        }
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_BB_, Moonlight.obfuscate(switchBuilder.toString()));


        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("弹窗版本=").append(FaQinDeMiaoHaoKeAi._dialog_version_).append(",");
        textBuilder.append("莫奈主题颜色=").append(FaQinDeMiaoHaoKeAi._monet_theme_color_).append(",");
        textBuilder.append("更新日志=").append(FaQinDeMiaoHaoKeAi._upd_log_).append(",");
        textBuilder.append("更新日志英文=").append(FaQinDeMiaoHaoKeAi._upd_log_eng_).append(",");
        textBuilder.append("强制更新内容=").append(FaQinDeMiaoHaoKeAi._force_upd_content_).append(",");
        textBuilder.append("强制更新内容英文=").append(FaQinDeMiaoHaoKeAi._force_upd_content_eng_).append(",");
        textBuilder.append("弹窗标题英文=").append(FaQinDeMiaoHaoKeAi._popup_title_eng_).append(",");
        textBuilder.append("强制更新链接=").append(FaQinDeMiaoHaoKeAi._force_upd_link_).append(",");
        textBuilder.append("远程通知内容=").append(FaQinDeMiaoHaoKeAi._remote_notif_content_).append(",");
        textBuilder.append("设备码卡密链接=").append(FaQinDeMiaoHaoKeAi._dev_id_card_link_).append(",");
        textBuilder.append("弹窗包名锁定内容=").append(FaQinDeMiaoHaoKeAi._pkg_lock_content_).append(",");
        textBuilder.append("弹窗包名锁定链接=").append(FaQinDeMiaoHaoKeAi._pkg_lock_link_).append(",");
        textBuilder.append("用户自定义配置URL列表=").append(FaQinDeMiaoHaoKeAi._user_custom_urls_).append(",");
        textBuilder.append("配置加载超时时间=").append(FaQinDeMiaoHaoKeAi._config_load_timeout_ms_).append(",");
        textBuilder.append("中立按钮内容=").append(FaQinDeMiaoHaoKeAi._neutral_btn_content_);

        editor.putString(FaQinDeMiaoHaoKeAi._PREF_CC_, Moonlight.obfuscate(textBuilder.toString()));

        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_I_, FaQinDeMiaoHaoKeAi._enable_night_mode_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_J_, FaQinDeMiaoHaoKeAi._enable_monet_dyn_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_K_, FaQinDeMiaoHaoKeAi._enable_builtin_monet_);
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_L_, FaQinDeMiaoHaoKeAi._monet_theme_color_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_O_, FaQinDeMiaoHaoKeAi._enable_dpi_auto_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_R_, FaQinDeMiaoHaoKeAi._enable_remote_notif_);
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_S_, FaQinDeMiaoHaoKeAi._remote_notif_content_);

        editor.putString("cached_update_log_zh", FaQinDeMiaoHaoKeAi._upd_log_);
        editor.putString("cached_update_log_en", FaQinDeMiaoHaoKeAi._upd_log_eng_);
        editor.putString("cached_force_update_msg_zh", FaQinDeMiaoHaoKeAi._force_upd_content_);
        editor.putString("cached_force_update_msg_en", FaQinDeMiaoHaoKeAi._force_upd_content_eng_);

        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_DD_, FaQinDeMiaoHaoKeAi._show_moonlight_icon_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_EE_, FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_FF_, FaQinDeMiaoHaoKeAi._enable_countdown_dont_show_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_Y_, FaQinDeMiaoHaoKeAi._enable_dev_id_check_);
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_HH_, FaQinDeMiaoHaoKeAi._pkg_lock_content_);
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_II_, FaQinDeMiaoHaoKeAi._pkg_lock_link_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_JJ_, FaQinDeMiaoHaoKeAi._show_force_update_close_btn_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_KK_, FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_LL_, FaQinDeMiaoHaoKeAi._force_use_domestic_url_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_MM_, FaQinDeMiaoHaoKeAi._force_use_international_url_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_NN_, FaQinDeMiaoHaoKeAi._enable_user_custom_url_);
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_OO_, FaQinDeMiaoHaoKeAi._user_custom_urls_);
        editor.putLong(FaQinDeMiaoHaoKeAi._PREF_QQ_, FaQinDeMiaoHaoKeAi._config_load_timeout_ms_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_RR_, FaQinDeMiaoHaoKeAi._enable_config_syntax_check_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_SS_, FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_NEW_DYNAMIC_TEXT_COLORS_, FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_);
        editor.putBoolean(FaQinDeMiaoHaoKeAi._PREF_NEUTRAL_BTN_SWITCH_, FaQinDeMiaoHaoKeAi._show_neutral_btn_);
        editor.putString(FaQinDeMiaoHaoKeAi._PREF_NEUTRAL_BTN_CONTENT_, FaQinDeMiaoHaoKeAi._neutral_btn_content_);


        if (!FaQinDeMiaoHaoKeAi._pkg_lock_map_.isEmpty()) {
            StringBuilder pkgLockMapString = new StringBuilder();
            for (Map.Entry<String, Boolean> entry : FaQinDeMiaoHaoKeAi._pkg_lock_map_.entrySet()) {
                if (entry.getValue()) {
                    if (pkgLockMapString.length() > 0) pkgLockMapString.append(",");
                    pkgLockMapString.append(entry.getKey()).append("=锁");
                }
            }
            editor.putString(FaQinDeMiaoHaoKeAi._PREF_GG_, Moonlight.obfuscate(pkgLockMapString.toString()));
        } else {
            editor.remove(FaQinDeMiaoHaoKeAi._PREF_GG_);
        }

        // NEW: Save Package-specific main dialog visibility
        if (!FaQinDeMiaoHaoKeAi._pkg_popup_control_.isEmpty()) {
            StringBuilder pkgPopupControlBuilder = new StringBuilder();
            List<String> sortedKeys = new ArrayList<>(FaQinDeMiaoHaoKeAi._pkg_popup_control_.keySet());
            Collections.sort(sortedKeys);
            for (String key : sortedKeys) {
                if (pkgPopupControlBuilder.length() > 0) pkgPopupControlBuilder.append(",");
                pkgPopupControlBuilder.append(key).append("=").append(FaQinDeMiaoHaoKeAi._pkg_popup_control_.get(key) ? "开" : "关");
            }
            editor.putString(FaQinDeMiaoHaoKeAi._PREF_P_, Moonlight.obfuscate(pkgPopupControlBuilder.toString()));
        } else {
            editor.remove(FaQinDeMiaoHaoKeAi._PREF_P_);
        }

        // NEW: Save Package-specific normal popup visibility
        if (!FaQinDeMiaoHaoKeAi._normal_popup_control_.isEmpty()) {
            StringBuilder normalPopupControlBuilder = new StringBuilder();
            List<String> sortedKeys = new ArrayList<>(FaQinDeMiaoHaoKeAi._normal_popup_control_.keySet());
            Collections.sort(sortedKeys);
            for (String key : sortedKeys) {
                if (normalPopupControlBuilder.length() > 0) normalPopupControlBuilder.append(",");
                normalPopupControlBuilder.append(key).append("=").append(FaQinDeMiaoHaoKeAi._normal_popup_control_.get(key) ? "开" : "关");
            }
            editor.putString(FaQinDeMiaoHaoKeAi._PREF_NEW_NORMAL_POPUP_CONTROL_, Moonlight.obfuscate(normalPopupControlBuilder.toString()));
        } else {
            editor.remove(FaQinDeMiaoHaoKeAi._PREF_NEW_NORMAL_POPUP_CONTROL_);
        }

        editor.apply();
    }

    @SuppressWarnings("deprecation")
    static boolean verifyAppSignature(Activity activity) {
        if (!FaQinDeMiaoHaoKeAi._enable_sig_ver_) return true;
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                byte[] digest = md.digest();
                StringBuilder hexString = new StringBuilder();
                for (byte b : digest) {
                    hexString.append(String.format("%02X:", b));
                }
                String appSignature = hexString.substring(0, hexString.length() - 1);
                for (String expectedSignature : FaQinDeMiaoHaoKeAi._sig_sha1_list_) {
                    if (expectedSignature.equalsIgnoreCase(appSignature)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void validateCardKey(Activity activity, EditText cardKeyInput) {
        String enteredCardKey = cardKeyInput.getText().toString().trim();
        String currentDeviceId = Moonlight.getDeviceId(activity);
        boolean isKeyValid = false;
        if (FaQinDeMiaoHaoKeAi._dev_card_keys_.containsKey(currentDeviceId)) {
            String expectedKeyForDevice = FaQinDeMiaoHaoKeAi._dev_card_keys_.get(currentDeviceId);
            if (expectedKeyForDevice != null && expectedKeyForDevice.equals(enteredCardKey)) {
                isKeyValid = true;
            }
        } else {
            if (FaQinDeMiaoHaoKeAi._dev_card_keys_.containsValue(enteredCardKey)) {
                isKeyValid = true;
            }
        }
        if (isKeyValid) {
            if (FaQinDeMiaoHaoKeAi._card_key_timer_ != null) {
                FaQinDeMiaoHaoKeAi._card_key_timer_.cancel();
                FaQinDeMiaoHaoKeAi._card_key_timer_ = null;
            }
            showToast(activity, MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "卡密验证成功！" : "Card key validation successful!");
            getSharedPreferences(activity).edit().putBoolean(FaQinDeMiaoHaoKeAi._PREF_X_ + currentDeviceId, true).apply();
            FaQinDeMiaoHaoKeAi._s_main_dialog_.dismiss();
            Moonlight.performHighestPriorityChecks(activity);
        } else {
            showToast(activity, MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "卡密错误，请重试！" : "Incorrect card key, please try again!");
        }
    }

    public static void copyDeviceId(Activity activity, String deviceId) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Device ID", deviceId));
        showToast(activity, MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "设备码已复制" : "Device ID copied to clipboard");
    }

    public static void getCardKey(Activity activity) {
        try {
            String urlToOpen = FaQinDeMiaoHaoKeAi._dev_id_card_link_;
            if (urlToOpen == null || urlToOpen.isEmpty()) urlToOpen = FaQinDeMiaoHaoKeAi._force_upd_link_;
            if (urlToOpen == null || urlToOpen.isEmpty()) urlToOpen = FaQinDeMiaoHaoKeAi._tg_channel_link_;
            if (urlToOpen != null && !urlToOpen.isEmpty()) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(convertToRawUrl(extractUrlFromHref(urlToOpen)))));
            } else {
                showToast(activity, MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "联系链接未配置" : "Contact link not configured");
            }
        } catch (Exception e) {
            showToast(activity, MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "打开链接失败" : "Failed to open link");
        }
    }

    public static void startCardKeyCountdownTimer(final Activity activity, final TextView countdownTextView, final AlertDialog dialog) {
        if (FaQinDeMiaoHaoKeAi._card_key_timer_ != null) {
            FaQinDeMiaoHaoKeAi._card_key_timer_.cancel();
        }

        FaQinDeMiaoHaoKeAi._card_key_timer_ = new CountDownTimer(FaQinDeMiaoHaoKeAi._CARD_KEY_CD_, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                countdownTextView.setText(MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "崩溃倒计时: " + seconds + " 秒" : "Crash Countdown: " + seconds + " seconds");
            }

            public void onFinish() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Moonlight.cleanupBeforeExit(activity);
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "未激活卡密，应用即将退出！" : "Card key not activated, app will exit!", Toast.LENGTH_LONG).show();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        throw new RuntimeException("Card key not activated, app crash initiated.");
                    }, 1000);
                });
            }
        }.start();
    }

    public static void startDeviceIdCountdown(final Activity activity, final TextView countdownTextView) {
        if (FaQinDeMiaoHaoKeAi._dev_id_timer_ != null) {
            FaQinDeMiaoHaoKeAi._dev_id_timer_.cancel();
        }

        FaQinDeMiaoHaoKeAi._dev_id_timer_ = new CountDownTimer(FaQinDeMiaoHaoKeAi._DEV_ID_CRASH_CD_ * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                if (countdownTextView != null) {
                    countdownTextView.setText(MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "应用将在 " + seconds + " 秒后关闭" : "App will close in " + seconds + " seconds");
                }
            }

            @Override
            public void onFinish() {
                if (countdownTextView != null) {
                    countdownTextView.setText(MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "应用即将关闭..." : "App is closing...");
                }
                Moonlight.cleanupBeforeExit(activity);
                activity.finish();
                throw new RuntimeException("Device verification failed, app closed.");
            }
        }.start();
    }

    static String extractUrlFromHref(String input) {
        if (input == null || input.isEmpty()) return "";
        if (input.contains("href=\"")) {
            int start = input.indexOf("href=\"") + 6;
            int end = input.indexOf("\"", start);
            if (end > start) return input.substring(start, end);
        }
        return input;
    }

    static String convertToRawUrl(String url) {//处理链接
        if (url == null || url.isEmpty()) return url;
        if (url.contains("gitee.com") && url.contains("/blob/")) return url.replace("/blob/", "/raw/");
        if (url.contains("github.com") && url.contains("/blob/"))
            return url.replace("github.com", "raw.githubusercontent.com").replace("/blob/", "/");
        if (url.contains("gitcode.com") && url.contains("/blob/")) return url.replace("/blob/", "/raw/");
        if (url.contains("gitcode.net") && url.contains("/blob/")) return url.replace("/blob/", "/raw/");
        if (url.contains("share.weiyun.com") && !url.endsWith(".txt")) return url + "&raw=1";
        return url;
    }

    static void showToast(Activity activity, String message) {
        if (activity != null && !activity.isFinishing()) {
            Moonlight._main_handler_.post(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
        }
    }

    static void resetNetworkConfigsToDefault() {
        FaQinDeMiaoHaoKeAi._dialog_version_ = 2;
        FaQinDeMiaoHaoKeAi._dyn_content_ = "Content1";
        FaQinDeMiaoHaoKeAi._eng_content_ = "Content2";
        FaQinDeMiaoHaoKeAi._force_upd_link_ = "";
        FaQinDeMiaoHaoKeAi._tg_channel_link_ = "https://t.me/YuChen_3";// tg群
        FaQinDeMiaoHaoKeAi._qq_group_num_ = "3421917199";
        FaQinDeMiaoHaoKeAi._popup_title_ = "Title1";
        FaQinDeMiaoHaoKeAi._popup_title_eng_ = "Title2";
        FaQinDeMiaoHaoKeAi._force_upd_content_ = "UpdateMsg1";
        FaQinDeMiaoHaoKeAi._force_upd_content_eng_ = "UpdateMsg2";
        FaQinDeMiaoHaoKeAi._upd_log_ = "Log1";
        FaQinDeMiaoHaoKeAi._upd_log_eng_ = "Log2";
        FaQinDeMiaoHaoKeAi._cfg_version_ = "1.0";
        FaQinDeMiaoHaoKeAi._sig_sha1_list_ = new ArrayList<>(FaQinDeMiaoHaoKeAi._DEF_SIG_LIST_);
        FaQinDeMiaoHaoKeAi._pkg_ver_cfg_ = new HashMap<>();
        FaQinDeMiaoHaoKeAi._pkg_content_map_ = new HashMap<>();
        FaQinDeMiaoHaoKeAi._dev_card_keys_ = new HashMap<>();
        FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = false;
        FaQinDeMiaoHaoKeAi._allowed_dev_ids_ = new ArrayList<>();
        FaQinDeMiaoHaoKeAi._dev_id_card_link_ = "";
        FaQinDeMiaoHaoKeAi._force_upd_req_ = false;
        FaQinDeMiaoHaoKeAi._net_cfg_enabled_ = true;
        FaQinDeMiaoHaoKeAi._show_dyn_content_ = true;
        FaQinDeMiaoHaoKeAi._show_upd_log_ = true;
        FaQinDeMiaoHaoKeAi._reset_dont_show_ = false;
        FaQinDeMiaoHaoKeAi._enable_img_rot_ = true;
        FaQinDeMiaoHaoKeAi._enable_bg_blur_ = true;
        FaQinDeMiaoHaoKeAi._enable_norm_popup_ = true;
        FaQinDeMiaoHaoKeAi._show_close_btn_ = true;
        FaQinDeMiaoHaoKeAi._show_dont_show_btn_ = true;
        FaQinDeMiaoHaoKeAi._show_qq_btn_ = true;
        FaQinDeMiaoHaoKeAi._show_tg_btn_ = true;
        FaQinDeMiaoHaoKeAi._enable_out_click_ = true;
        FaQinDeMiaoHaoKeAi._enable_sig_ver_ = false;
        FaQinDeMiaoHaoKeAi._enable_night_mode_ = false;
        FaQinDeMiaoHaoKeAi._enable_monet_dyn_ = true;
        FaQinDeMiaoHaoKeAi._enable_builtin_monet_ = true;
        FaQinDeMiaoHaoKeAi._monet_theme_color_ = "Color_Val"; // Default placeholder
        FaQinDeMiaoHaoKeAi._enable_dpi_auto_ = true;
        FaQinDeMiaoHaoKeAi._enable_remote_notif_ = false;
        FaQinDeMiaoHaoKeAi._remote_notif_content_ = "";
        FaQinDeMiaoHaoKeAi._show_moonlight_icon_ = true;
        FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = false;
        FaQinDeMiaoHaoKeAi._enable_countdown_dont_show_ = true;
        FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ = false;
        FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_ = false;
        FaQinDeMiaoHaoKeAi._force_use_domestic_url_ = false;
        FaQinDeMiaoHaoKeAi._force_use_international_url_ = false;
        FaQinDeMiaoHaoKeAi._enable_user_custom_url_ = false;
        FaQinDeMiaoHaoKeAi._user_custom_urls_ = "";
        FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ = FaQinDeMiaoHaoKeAi._DEF_CONFIG_LOAD_TIMEOUT_;
        FaQinDeMiaoHaoKeAi._enable_config_syntax_check_ = false;
        FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_ = false;
        FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ = true;
        FaQinDeMiaoHaoKeAi._show_neutral_btn_ = false;
        FaQinDeMiaoHaoKeAi._neutral_btn_content_ = "";
        FaQinDeMiaoHaoKeAi._pkg_lock_map_ = new HashMap<>();
        FaQinDeMiaoHaoKeAi._pkg_lock_content_ = "";
        FaQinDeMiaoHaoKeAi._pkg_lock_link_ = "";
        FaQinDeMiaoHaoKeAi._pkg_popup_control_ = new HashMap<>();
        FaQinDeMiaoHaoKeAi._normal_popup_control_ = new HashMap<>();
    }

    static void applyBackgroundBlur(Activity activity) {
        if (!FaQinDeMiaoHaoKeAi._enable_bg_blur_ || Build.VERSION.SDK_INT < Build.VERSION_CODES.S || activity.isFinishing()) return;
        try {
            activity.getWindow().getDecorView().setRenderEffect(RenderEffect.createBlurEffect(25, 25, Shader.TileMode.CLAMP));
            FaQinDeMiaoHaoKeAi._is_blur_applied_ = true;
        } catch (Exception e) {
        }
    }

    static void removeBackgroundBlur(Activity activity) {
        if (!FaQinDeMiaoHaoKeAi._is_blur_applied_ || Build.VERSION.SDK_INT < Build.VERSION_CODES.S || activity == null || activity.isFinishing()) return;
        try {
            activity.getWindow().getDecorView().setRenderEffect(null);
            FaQinDeMiaoHaoKeAi._is_blur_applied_ = false;
        } catch (Exception e) {
        }
    }

    /**
     * 检查屏幕类型（平板电脑或手机）并缓存结果。
     * <p>
     * 此方法会确定设备屏幕宽度是否达到平板电脑的阈值（600dp）。
     * 结果会存储在 SharedPreferences 中，以避免重复检查。
     * 同时，它会更新静态变量 {@code sharezhifu._is_large_screen_}。
     *
     * @param activity 用于获取资源和屏幕指标的 Activity 实例。
     * @param prefs    用于存储和检索屏幕类型检查结果的 SharedPreferences 实例。
     */
    static void checkAndCacheScreenType(Activity activity, SharedPreferences prefs) {//检查屏幕类型
        boolean alreadyChecked = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_N_, false);
        if (alreadyChecked) {
            return;
        }

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        boolean isTablet = dpWidth >= 600;
        FaQinDeMiaoHaoKeAi._is_large_screen_ = isTablet;

        prefs.edit()
                .putBoolean(FaQinDeMiaoHaoKeAi._PREF_M_, isTablet)
                .putBoolean(FaQinDeMiaoHaoKeAi._PREF_N_, true)
                .apply();
    }

    static Drawable createButtonBackground(int buttonColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(70f);
        gd.setColor(buttonColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(adjustColorBrightness(buttonColor, 0.8f)), gd, null);
        }
        return gd;
    }

    private static int adjustColorBrightness(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor;
        return Color.HSVToColor(hsv);
    }
}
