package bbs.yuchen.icu;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MaoMaoShiYiGeXiaoSaoHuo {

    public static void addButtonStyle2(LinearLayout layout, Activity activity, String text, View.OnClickListener listener, boolean compactLayout) {
        LinearLayout.LayoutParams lp;
        if (compactLayout) {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 5, 20, 5);
        } else {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            lp.setMargins(90, 0, 90, 10);
        }

        Button button = new Button(activity);
        button.setLayoutParams(lp);
        button.setText(text);
        button.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
        button.setBackground(changku.createButtonBackground(FaQinDeMiaoHaoKeAi._current_button_bg_));
        if (compactLayout) {
            button.setMinWidth(dpToPx_int(activity, 120));
        }
        button.setOnClickListener(v -> animateButtonPress(v, () -> listener.onClick(v)));
        layout.addView(button);
    }

    public static void addButton(LinearLayout layout, Activity activity, String text, View.OnClickListener listener, boolean compactLayout) {
        if (FaQinDeMiaoHaoKeAi._dialog_version_ == 2) {
            addButtonStyle2(layout, activity, text, listener, compactLayout);
        } else {
            DialogLiQiuting.addButtonStyle1(layout, activity, text, listener, compactLayout);
        }
    }

    public static void setupCommonDialogProperties(Activity activity, AlertDialog dialog, View view, boolean isCancelable) {
        dialog.setCancelable(isCancelable);
        dialog.setView(view);
        dialog.setOnDismissListener(d -> {
            // Centralized cleanup is now the sole responsibility of this call.
            Moonlight.dismissAndCleanupAllDialogs();
        });
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dpToPx(activity, 16));
        gd.setColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(gd);
            dialog.getWindow().setAttributes(Moonlight.getDialogWindowAttributes(activity, dialog));
        }

        animateDialogShow(dialog);
        dialog.show();
    }

    public static void setupMoonlightIcon(Activity activity, ImageView iv) {
        Bitmap bmp = loadOptimizedMoonlightBitmap(activity);
        if (bmp != null) {
            Bitmap circleBmp = createCircularBitmap(bmp);
            iv.setImageBitmap(circleBmp);
            if (FaQinDeMiaoHaoKeAi._enable_img_rot_) {
                startImageRotationAnimation(iv);
            }
        }
    }

    static void openQQGroup(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?card_type=group&uin=" + FaQinDeMiaoHaoKeAi._qq_group_num_)));
        } catch (Exception e) {
            changku.showToast(activity, isChinese(activity) ? "无法跳转到QQ" : "Unable to open QQ");
        }
    }

    static void openTelegram(Activity activity) {
        try {
            String channelLink = FaQinDeMiaoHaoKeAi._tg_channel_link_;
            if (channelLink != null && !channelLink.isEmpty()) {
                if (channelLink.startsWith("https://t.me/")) {
                    String tgAppLink = channelLink.replace("https://t.me/", "tg://resolve?domain=");
                    try {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tgAppLink)));
                        return;
                    } catch (ActivityNotFoundException e) {
                        // Fallback to browser
                        Log.w("peizhi", "Telegram app not found, falling back to browser for: " + channelLink, e);
                    }
                }
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(channelLink)));
            } else {
                Toast.makeText(activity, isChinese(activity) ? "TG链接未配置" : "TG link not configured", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            changku.showToast(activity, isChinese(activity) ? "打开TG失败" : "Failed to open Telegram");
            Log.e("peizhi", "Failed to open Telegram", e);
        }
    }

    static void showDontShowAgainDialog(final Activity activity, final SharedPreferences prefs) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        final boolean isChinese = isChinese(activity);
        // Before showing this utility dialog, ensure any main dialog is dismissed to avoid overlap issues.
        // This is a local cleanup for this specific helper dialog flow.
        // The original empty if statement regarding sharezhifu._s_main_dialog_ has been removed.

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView message = new TextView(activity);
        message.setText(isChinese ? "是否他妈的不想要我了？" : "Don't show this dialog again?");
        message.setTextSize(18);
        message.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
        message.setGravity(Gravity.CENTER);
        layout.addView(message);

        LinearLayout buttonLayout = new LinearLayout(activity);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setPadding(0, 30, 0, 0);

        Button negativeButton = new Button(activity);
        negativeButton.setText(isChinese ? "否" : "No");
        negativeButton.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
        negativeButton.setBackground(changku.createButtonBackground(FaQinDeMiaoHaoKeAi._current_button_bg_));
        LinearLayout.LayoutParams negativeParams = new LinearLayout.LayoutParams(0, -2);
        negativeParams.weight = 1;
        negativeParams.setMarginEnd(10);
        negativeButton.setLayoutParams(negativeParams);

        final Button positiveButton = new Button(activity);
        positiveButton.setTextColor(FaQinDeMiaoHaoKeAi._current_button_text_);
        positiveButton.setBackground(changku.createButtonBackground(FaQinDeMiaoHaoKeAi._current_button_bg_));
        LinearLayout.LayoutParams positiveParams = new LinearLayout.LayoutParams(0, -2);
        positiveParams.weight = 1;
        positiveParams.setMarginStart(10);
        positiveButton.setLayoutParams(positiveParams);

        if (FaQinDeMiaoHaoKeAi._enable_countdown_dont_show_) {
            positiveButton.setEnabled(false);
            new CountDownTimer(FaQinDeMiaoHaoKeAi._DONT_SHOW_AGAIN_CD_, 1000) {
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    positiveButton.setText(isChinese ? "是 (" + seconds + ")" : "Yes (" + seconds + ")");
                }

                public void onFinish() {
                    positiveButton.setText(isChinese ? "是" : "Yes");
                    positiveButton.setEnabled(true);
                }
            }.start();
        } else {
            positiveButton.setText(isChinese ? "是" : "Yes");
            positiveButton.setEnabled(true);
        }


        buttonLayout.addView(negativeButton);
        buttonLayout.addView(positiveButton);
        layout.addView(buttonLayout);
        final AlertDialog dialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog)
                .setView(layout)
                .create();
        GradientDrawable dialogBackground = new GradientDrawable();
        dialogBackground.setCornerRadius(dpToPx(activity, 16));
        dialogBackground.setColor(FaQinDeMiaoHaoKeAi._current_dialog_bg_);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(dialogBackground);
            dialog.getWindow().setAttributes(Moonlight.getDialogWindowAttributes(activity, dialog));
        }

        negativeButton.setOnClickListener(v -> animateButtonPress(v, () -> {
            vibrateOnClick(activity);
            dialog.dismiss();
        }));
        positiveButton.setOnClickListener(v -> animateButtonPress(v, () -> {
            vibrateOnClick(activity);
            try {
                prefs.edit()
                        .putString("dialogVer", FaQinDeMiaoHaoKeAi._cfg_version_)
                        .putBoolean("0", true)
                        .apply();
            } catch (Exception e) {
                changku.showToast(activity, isChinese ? "设置失败：" + e.getMessage() : "Failed to set: " + e.getMessage());
                Log.e("peizhi", "Failed to save 'Don''t show again' preferences.", e);
            }
            dialog.dismiss();
        }));
        dialog.show();
    }

    public static boolean isChinese(Context context) {
        return context.getResources().getConfiguration().getLocales().get(0).getLanguage().equals("zh");
    }

    public static void animateButtonPress(View view, final Runnable action) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).setInterpolator(Moonlight._STD_DECEL_)
                .withEndAction(() -> {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(120).setInterpolator(Moonlight._EMPH_INTERP_).start();
                    if (action != null) {
                        action.run();
                    }
                }).start();
    }

    private static void animateDialogShow(AlertDialog dialog) {
        if (dialog == null || dialog.getWindow() == null) return;
        View view = dialog.getWindow().getDecorView();
        view.setAlpha(0f);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        view.setTranslationY(dpToPx(dialog.getContext(), 50));
        view.animate().alpha(1f).scaleX(1f).scaleY(1f).translationY(0f).setDuration(300).setInterpolator(Moonlight._EMPH_INTERP_).start();
    }

    public static void animateDialogDismiss(final AlertDialog dialog) {
        if (dialog == null || dialog.getWindow() == null || !dialog.isShowing()) return;
        dialog.getWindow().getDecorView().animate().alpha(0f).scaleX(0.9f).scaleY(0.9f).setDuration(250)
                .setInterpolator(Moonlight._STD_DECEL_).withEndAction(() -> {
                    if (dialog.isShowing()) {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            Log.w("peizhi", "Exception during dialog dismiss animation end action.", e);
                        }
                    }
                }).start();
    }

    static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx_int(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private static void vibrateOnClick(Context context) {
        try {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(15);
                }
            }
        } catch (Exception e) {
            Log.e("peizhi", "Vibration failed", e);
        }
    }

    private static Bitmap loadOptimizedMoonlightBitmap(Activity activity) {
        try (InputStream is = activity.getAssets().open("Moonlight")) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, opt);
            opt.inSampleSize = calculateInSampleSize(opt); // Removed reqW and reqH
            opt.inJustDecodeBounds = false;
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try (InputStream is2 = activity.getAssets().open("Moonlight")) {
                return BitmapFactory.decodeStream(is2, null, opt);
            }
        } catch (IOException e) {
            Log.e("peizhi", "Failed to load optimized Moonlight bitmap.", e);
            return null;
        }
    }

    private static Bitmap createCircularBitmap(Bitmap source) {
        if (source == null) return null;

        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2f, height / 2f, Math.min(width, height) / 2f, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        if (source != output && !source.isRecycled()) {
            source.recycle();
        }
        return output;
    }

    private static void startImageRotationAnimation(ImageView imageView) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotation.setDuration(5000);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();
    }

    private static int calculateInSampleSize(BitmapFactory.Options opt) { // Removed reqW and reqH
        final int reqW = 250; // Inlined constant
        final int reqH = 250; // Inlined constant
        int h = opt.outHeight;
        int w = opt.outWidth;
        int size = 1;
        if (h > reqH || w > reqW) {
            final int halfH = h / 2;
            final int halfW = w / 2;
            while ((halfH / size) >= reqH && (halfW / size) >= reqW) {
                size *= 2;
            }
        }
        return size;
    }

    static void parseConfigJson(String config) throws JSONException {
        JSONObject jsonConfig = new JSONObject(config);

        if (jsonConfig.has("弹窗版本")) FaQinDeMiaoHaoKeAi._dialog_version_ = jsonConfig.optInt("弹窗版本", 2);
        if (jsonConfig.has("内容")) FaQinDeMiaoHaoKeAi._dyn_content_ = jsonConfig.getString("内容");
        if (jsonConfig.has("英文内容")) FaQinDeMiaoHaoKeAi._eng_content_ = jsonConfig.getString("英文内容");
        if (jsonConfig.has("更新链接")) FaQinDeMiaoHaoKeAi._force_upd_link_ = jsonConfig.getString("更新链接");

        if (jsonConfig.has("TG频道")) {
            String tgChannelConfig = jsonConfig.getString("TG频道");
            if (!tgChannelConfig.isEmpty()) {
                if (!tgChannelConfig.startsWith("https://") && !tgChannelConfig.startsWith("tg://")) {
                    if (tgChannelConfig.startsWith("@")) {
                        FaQinDeMiaoHaoKeAi._tg_channel_link_ = "https://t.me/" + tgChannelConfig.substring(1);
                    } else {
                        FaQinDeMiaoHaoKeAi._tg_channel_link_ = "https://t.me/" + tgChannelConfig;
                    }
                } else {
                    FaQinDeMiaoHaoKeAi._tg_channel_link_ = tgChannelConfig;
                }
            } else {
                FaQinDeMiaoHaoKeAi._tg_channel_link_ = "https://t.me/channel"; // Default or fallback
            }
        }

        if (jsonConfig.has("QQ群")) FaQinDeMiaoHaoKeAi._qq_group_num_ = jsonConfig.getString("QQ群");
        if (jsonConfig.has("弹窗标题")) FaQinDeMiaoHaoKeAi._popup_title_ = jsonConfig.getString("弹窗标题");
        if (jsonConfig.has("弹窗标题英文")) FaQinDeMiaoHaoKeAi._popup_title_eng_ = jsonConfig.getString("弹窗标题英文");
        if (jsonConfig.has("强制更新内容")) FaQinDeMiaoHaoKeAi._force_upd_content_ = jsonConfig.getString("强制更新内容");
        if (jsonConfig.has("强制更新内容英文")) FaQinDeMiaoHaoKeAi._force_upd_content_eng_ = jsonConfig.getString("强制更新内容英文");
        if (jsonConfig.has("更新日志")) FaQinDeMiaoHaoKeAi._upd_log_ = jsonConfig.getString("更新日志");
        if (jsonConfig.has("更新日志英文")) FaQinDeMiaoHaoKeAi._upd_log_eng_ = jsonConfig.getString("更新日志英文");
        if (jsonConfig.has("配置版本")) FaQinDeMiaoHaoKeAi._cfg_version_ = jsonConfig.getString("配置版本");
        if (jsonConfig.has("远程通知内容")) FaQinDeMiaoHaoKeAi._remote_notif_content_ = jsonConfig.getString("远程通知内容");

        if (jsonConfig.has("签名SHA1")) {
            String sigs = jsonConfig.getString("签名SHA1");
            if (!sigs.isEmpty()) {
                FaQinDeMiaoHaoKeAi._sig_sha1_list_ = new ArrayList<>(Arrays.asList(sigs.split(",")));
            } else {
                FaQinDeMiaoHaoKeAi._sig_sha1_list_ = new ArrayList<>(FaQinDeMiaoHaoKeAi._DEF_SIG_LIST_);
            }
        }

        if (jsonConfig.has("包名版本")) {
            String pkgVer = jsonConfig.getString("包名版本");
            if (!pkgVer.isEmpty()) {
                FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.clear();
                for (String pair : pkgVer.split(",")) {
                    String[] kv = pair.split("="); // Assuming format key=value,key2=value2
                    if (kv.length == 2) {
                        try {
                            FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.put(kv[0].trim(), Integer.parseInt(kv[1].trim()));
                        } catch (NumberFormatException e) {
                            Log.e("peizhi", "Error parsing package version from config: " + pair, e);
                        }
                    }
                }
            } else {
                 FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.clear(); // Clear if empty or null
            }
        }

        if (jsonConfig.has("包名内容")) {
            String pkgContent = jsonConfig.getString("包名内容");
            if (!pkgContent.isEmpty()) {
                FaQinDeMiaoHaoKeAi._pkg_content_map_.clear();
                for (String pair : pkgContent.split(";")) {
                    String[] kv = pair.split(":", 2);
                    if (kv.length == 2) {
                        FaQinDeMiaoHaoKeAi._pkg_content_map_.put(kv[0].trim(), kv[1].trim());
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._pkg_content_map_.clear();
            }
        }

        if (jsonConfig.has("设备码卡密")) {
            String deviceCardKeysConfig = jsonConfig.getString("设备码卡密");
            if (!deviceCardKeysConfig.isEmpty()) {
                FaQinDeMiaoHaoKeAi._dev_card_keys_.clear();
                for (String pair : deviceCardKeysConfig.split(",")) {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2) {
                        FaQinDeMiaoHaoKeAi._dev_card_keys_.put(kv[0].trim(), kv[1].trim());
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._dev_card_keys_.clear();
            }
        }

        if (jsonConfig.has("启用设备码弹窗")) {
            FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = "开".equals(jsonConfig.getString("启用设备码弹窗"));
        }
        if (jsonConfig.has("设备码列表")) {
            String allowedDeviceIdsConfig = jsonConfig.getString("设备码列表");
            if (!allowedDeviceIdsConfig.isEmpty()) {
                FaQinDeMiaoHaoKeAi._allowed_dev_ids_ = new ArrayList<>(Arrays.asList(allowedDeviceIdsConfig.split(",")));
            } else {
                FaQinDeMiaoHaoKeAi._allowed_dev_ids_ = new ArrayList<>();
            }
        }

        if (jsonConfig.has("设备码卡密链接")) {
            FaQinDeMiaoHaoKeAi._dev_id_card_link_ = jsonConfig.getString("设备码卡密链接");
        }

        if (jsonConfig.has("弹窗包名锁定")) {
            String packageLockConfig = jsonConfig.getString("弹窗包名锁定");
            if (!packageLockConfig.isEmpty()) {
                FaQinDeMiaoHaoKeAi._pkg_lock_map_.clear();
                for (String pair : packageLockConfig.split(",")) {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2 && "锁".equals(kv[1].trim())) {
                        FaQinDeMiaoHaoKeAi._pkg_lock_map_.put(kv[0].trim(), true);
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._pkg_lock_map_.clear();
            }
        }
        if (jsonConfig.has("弹窗包名锁定内容")) {
            FaQinDeMiaoHaoKeAi._pkg_lock_content_ = jsonConfig.getString("弹窗包名锁定内容");
        }
        if (jsonConfig.has("弹窗包名锁定链接")) {
            FaQinDeMiaoHaoKeAi._pkg_lock_link_ = jsonConfig.getString("弹窗包名锁定链接");
        }

        if (jsonConfig.has("弹窗显示控制")) {
            String pkgPopupControlJson = jsonConfig.getString("弹窗显示控制");
            if (!pkgPopupControlJson.isEmpty()) {
                FaQinDeMiaoHaoKeAi._pkg_popup_control_.clear();
                for (String pair : pkgPopupControlJson.split(",")) {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2) {
                        FaQinDeMiaoHaoKeAi._pkg_popup_control_.put(kv[0].trim(), "开".equals(kv[1].trim()));
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._pkg_popup_control_.clear();
            }
        }

        if (jsonConfig.has("普通弹窗显示控制")) {
            String normalPopupControlJson = jsonConfig.getString("普通弹窗显示控制");
            if (!normalPopupControlJson.isEmpty()) {
                FaQinDeMiaoHaoKeAi._normal_popup_control_.clear();
                for (String pair : normalPopupControlJson.split(",")) {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2) {
                        FaQinDeMiaoHaoKeAi._normal_popup_control_.put(kv[0].trim(), "开".equals(kv[1].trim()));
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._normal_popup_control_.clear();
            }
        }

        if (jsonConfig.has("网络配置总开关")) FaQinDeMiaoHaoKeAi._net_cfg_enabled_ = !"关".equals(jsonConfig.getString("网络配置总开关"));
        if (jsonConfig.has("显示动态内容开关")) FaQinDeMiaoHaoKeAi._show_dyn_content_ = !"关".equals(jsonConfig.getString("显示动态内容开关"));
        if (jsonConfig.has("显示更新日志开关")) FaQinDeMiaoHaoKeAi._show_upd_log_ = !"关".equals(jsonConfig.getString("显示更新日志开关"));
        if (jsonConfig.has("重置不再提示")) FaQinDeMiaoHaoKeAi._reset_dont_show_ = "开".equals(jsonConfig.getString("重置不再提示"));
        if (jsonConfig.has("启用Moonlight图旋转")) FaQinDeMiaoHaoKeAi._enable_img_rot_ = !"关".equals(jsonConfig.getString("启用Moonlight图旋转"));
        if (jsonConfig.has("启用背景模糊")) FaQinDeMiaoHaoKeAi._enable_bg_blur_ = !"关".equals(jsonConfig.getString("启用背景模糊"));
        if (jsonConfig.has("启用普通弹窗")) FaQinDeMiaoHaoKeAi._enable_norm_popup_ = !"关".equals(jsonConfig.getString("启用普通弹窗"));
        if (jsonConfig.has("显示关闭弹窗按钮")) FaQinDeMiaoHaoKeAi._show_close_btn_ = !"关".equals(jsonConfig.getString("显示关闭弹窗按钮"));
        if (jsonConfig.has("显示不再提示按钮")) FaQinDeMiaoHaoKeAi._show_dont_show_btn_ = !"关".equals(jsonConfig.getString("显示不再提示按钮"));
        if (jsonConfig.has("显示QQ群按钮")) FaQinDeMiaoHaoKeAi._show_qq_btn_ = !"关".equals(jsonConfig.getString("显示QQ群按钮"));
        if (jsonConfig.has("显示TG频道按钮")) FaQinDeMiaoHaoKeAi._show_tg_btn_ = !"关".equals(jsonConfig.getString("显示TG频道按钮"));
        if (jsonConfig.has("启用外部点击消失")) FaQinDeMiaoHaoKeAi._enable_out_click_ = !"关".equals(jsonConfig.getString("启用外部点击消失"));
        if (jsonConfig.has("启用签名验证")) FaQinDeMiaoHaoKeAi._enable_sig_ver_ = "开".equals(jsonConfig.getString("启用签名验证"));
        if (jsonConfig.has("启用夜间模式")) FaQinDeMiaoHaoKeAi._enable_night_mode_ = "开".equals(jsonConfig.getString("启用夜间模式"));
        if (jsonConfig.has("启用Monet动态取色")) FaQinDeMiaoHaoKeAi._enable_monet_dyn_ = "开".equals(jsonConfig.getString("启用Monet动态取色"));
        if (jsonConfig.has("启用内置Monet主题色")) FaQinDeMiaoHaoKeAi._enable_builtin_monet_ = "开".equals(jsonConfig.getString("启用内置Monet主题色"));
        if (jsonConfig.has("莫奈主题颜色")) FaQinDeMiaoHaoKeAi._monet_theme_color_ = jsonConfig.getString("莫奈主题颜色");
        if (jsonConfig.has("启用DPI适配")) FaQinDeMiaoHaoKeAi._enable_dpi_auto_ = !"关".equals(jsonConfig.getString("启用DPI适配"));
        if (jsonConfig.has("启用远程通知开关")) FaQinDeMiaoHaoKeAi._enable_remote_notif_ = "开".equals(jsonConfig.getString("启用远程通知开关"));

        if (jsonConfig.has("显示Moonlight图标")) FaQinDeMiaoHaoKeAi._show_moonlight_icon_ = !"关".equals(jsonConfig.getString("显示Moonlight图标"));
        if (jsonConfig.has("启用设备码卡密弹窗")) FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = "开".equals(jsonConfig.getString("启用设备码卡密弹窗"));
        if (jsonConfig.has("开启不再提示确认倒计时")) FaQinDeMiaoHaoKeAi._enable_countdown_dont_show_ = !"关".equals(jsonConfig.getString("开启不再提示确认倒计时"));
        if (jsonConfig.has("强制更新弹窗显示关闭按钮")) FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ = "开".equals(jsonConfig.getString("强制更新弹窗显示关闭按钮"));
        if (jsonConfig.has("包名锁定弹窗显示关闭按钮")) FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_ = "开".equals(jsonConfig.getString("包名锁定弹窗显示关闭按钮"));
        if (jsonConfig.has("强制使用国内配置URL")) FaQinDeMiaoHaoKeAi._force_use_domestic_url_ = "开".equals(jsonConfig.getString("强制使用国内配置URL"));
        if (jsonConfig.has("强制使用国际配置URL")) FaQinDeMiaoHaoKeAi._force_use_international_url_ = "开".equals(jsonConfig.getString("强制使用国际配置URL"));
        if (jsonConfig.has("启用用户自定义配置URL")) FaQinDeMiaoHaoKeAi._enable_user_custom_url_ = "开".equals(jsonConfig.getString("启用用户自定义配置URL"));
        if (jsonConfig.has("用户自定义配置URL列表")) FaQinDeMiaoHaoKeAi._user_custom_urls_ = jsonConfig.getString("用户自定义配置URL列表");
        if (jsonConfig.has("配置加载超时时间")) {
            try {
                FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ = Long.parseLong(jsonConfig.getString("配置加载超时时间"));
            } catch (NumberFormatException e) {
                FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ = FaQinDeMiaoHaoKeAi._DEF_CONFIG_LOAD_TIMEOUT_;
                Log.e("peizhi", "Error parsing config load timeout from JSON", e);
            }
        }
        if (jsonConfig.has("启用配置文件语法检查")) FaQinDeMiaoHaoKeAi._enable_config_syntax_check_ = "开".equals(jsonConfig.getString("启用配置文件语法检查"));
        if (jsonConfig.has("启用更丰富的设备信息收集")) FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_ = "开".equals(jsonConfig.getString("启用更丰富的设备信息收集"));

        if (jsonConfig.has("启用弹窗背景改变文字颜色按钮颜色")) {
            FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ = "开".equals(jsonConfig.getString("启用弹窗背景改变文字颜色按钮颜色"));
        }
        if (jsonConfig.has("中立按钮是否显示")) {
            FaQinDeMiaoHaoKeAi._show_neutral_btn_ = "开".equals(jsonConfig.getString("中立按钮是否显示"));
        }
        if (jsonConfig.has("中立按钮内容")) {
            FaQinDeMiaoHaoKeAi._neutral_btn_content_ = jsonConfig.getString("中立按钮内容");
        }
    }

    static void parseConfigTxt(String config) { // Removed unused Activity parameter
        String cleanConfig = config
                .replace("</p><p>", "\n")
                .replace("<br />", "\n")
                .replace("<br/>", "\n")
                .replace("<br>", "\n")
                .replace("</div><div>", "\n")
                .replace("<p><br></p>", "\n")
                .replace("amp;", "");

        cleanConfig = decodeHtmlEntities(cleanConfig);
        cleanConfig = cleanConfig.replaceAll("<[^>]*>", "");
        cleanConfig = cleanConfig.replaceAll("[\r\n]+", "\n").trim();

        FaQinDeMiaoHaoKeAi._full_config_string_ = cleanConfig;

        FaQinDeMiaoHaoKeAi._dialog_version_ = Integer.parseInt(extractContentBetweenMarkers("弹窗版本", "2"));
        FaQinDeMiaoHaoKeAi._dyn_content_ = extractContentBetweenMarkers("内容", "Content1");
        FaQinDeMiaoHaoKeAi._eng_content_ = extractContentBetweenMarkers("英文内容", "Content2");
        FaQinDeMiaoHaoKeAi._force_upd_link_ = extractContentBetweenMarkers("更新链接", "");

        String tgChannelConfig = extractContentBetweenMarkers("TG频道", null);
        if (tgChannelConfig != null && !tgChannelConfig.isEmpty()) {
            if (!tgChannelConfig.startsWith("https://") && !tgChannelConfig.startsWith("tg://")) {
                if (tgChannelConfig.startsWith("@")) {
                    FaQinDeMiaoHaoKeAi._tg_channel_link_ = "https://t.me/" + tgChannelConfig.substring(1);
                } else {
                    FaQinDeMiaoHaoKeAi._tg_channel_link_ = "https://t.me/" + tgChannelConfig;
                }
            } else {
                // If it already starts with https:// or tg://, use it as is, or default if it's just "https://t.me/"
                 if (tgChannelConfig.equals("https://t.me/") || tgChannelConfig.equals("tg://resolve?domain=")){ // Example check for empty useful part
                    FaQinDeMiaoHaoKeAi._tg_channel_link_ = "https://t.me/channel"; // Default or fallback
                 } else {
                    FaQinDeMiaoHaoKeAi._tg_channel_link_ = tgChannelConfig;
                 }
            }
        } else {
             FaQinDeMiaoHaoKeAi._tg_channel_link_ = "https://t.me/channel"; // Default if null or empty from extract
        }

        FaQinDeMiaoHaoKeAi._qq_group_num_ = extractContentBetweenMarkers("QQ群", "");
        FaQinDeMiaoHaoKeAi._popup_title_ = extractContentBetweenMarkers("弹窗标题", "Title1");
        FaQinDeMiaoHaoKeAi._popup_title_eng_ = extractContentBetweenMarkers("弹窗标题英文", "Title2");
        FaQinDeMiaoHaoKeAi._force_upd_content_ = extractContentBetweenMarkers("强制更新内容", "UpdateMsg1");
        FaQinDeMiaoHaoKeAi._force_upd_content_eng_ = extractContentBetweenMarkers("强制更新内容英文", "UpdateMsg2");
        FaQinDeMiaoHaoKeAi._upd_log_ = extractContentBetweenMarkers("更新日志", "Log1");
        FaQinDeMiaoHaoKeAi._upd_log_eng_ = extractContentBetweenMarkers("更新日志英文", "Log2");
        FaQinDeMiaoHaoKeAi._cfg_version_ = extractContentBetweenMarkers("配置版本", "1.0");
        FaQinDeMiaoHaoKeAi._remote_notif_content_ = extractContentBetweenMarkers("远程通知内容", "");

        String sigs = extractContentBetweenMarkers("签名SHA1", null);
        if (sigs != null && !sigs.isEmpty()) {
            FaQinDeMiaoHaoKeAi._sig_sha1_list_ = new ArrayList<>(Arrays.asList(sigs.split(",")));
        } else {
            FaQinDeMiaoHaoKeAi._sig_sha1_list_ = new ArrayList<>(FaQinDeMiaoHaoKeAi._DEF_SIG_LIST_);
        }

        String pkgVer = extractContentBetweenMarkers("包名版本", null);
        if (pkgVer != null && !pkgVer.isEmpty()) {
            FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.clear();
            for (String pair : pkgVer.split(",")) {
                String[] kv = pair.split("="); // Assuming format key=value,key2=value2
                if (kv.length == 2) {
                    try {
                        FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.put(kv[0].trim(), Integer.parseInt(kv[1].trim()));
                    } catch (NumberFormatException e) {
                        Log.e("peizhi", "Error parsing package version from TXT config: " + pair, e);
                    }
                }
            }
        } else {
            FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.clear();
        }

        String pkgContent = extractContentBetweenMarkers("包名内容", null);
        if (pkgContent != null && !pkgContent.isEmpty()) {
            FaQinDeMiaoHaoKeAi._pkg_content_map_.clear();
            for (String pair : pkgContent.split(";")) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    FaQinDeMiaoHaoKeAi._pkg_content_map_.put(kv[0].trim(), kv[1].trim());
                }
            }
        } else {
            FaQinDeMiaoHaoKeAi._pkg_content_map_.clear();
        }

        String deviceCardKeysConfig = extractContentBetweenMarkers("设备码卡密", null);
        if (deviceCardKeysConfig != null && !deviceCardKeysConfig.isEmpty()) {
            FaQinDeMiaoHaoKeAi._dev_card_keys_.clear();
            for (String pair : deviceCardKeysConfig.split(",")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    FaQinDeMiaoHaoKeAi._dev_card_keys_.put(kv[0].trim(), kv[1].trim());
                }
            }
        } else {
            FaQinDeMiaoHaoKeAi._dev_card_keys_.clear();
        }

        FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = "开".equals(extractContentBetweenMarkers("启用设备码弹窗", "关"));
        String allowedDeviceIdsConfig = extractContentBetweenMarkers("设备码列表", null);
        if (allowedDeviceIdsConfig != null && !allowedDeviceIdsConfig.isEmpty()) {
            FaQinDeMiaoHaoKeAi._allowed_dev_ids_ = new ArrayList<>(Arrays.asList(allowedDeviceIdsConfig.split(",")));
        } else {
            FaQinDeMiaoHaoKeAi._allowed_dev_ids_ = new ArrayList<>();
        }

        FaQinDeMiaoHaoKeAi._dev_id_card_link_ = extractContentBetweenMarkers("设备码卡密链接", "");

        String packageLockConfig = extractContentBetweenMarkers("弹窗包名锁定", null);
        if (packageLockConfig != null && !packageLockConfig.isEmpty()) {
            FaQinDeMiaoHaoKeAi._pkg_lock_map_.clear();
            for (String pair : packageLockConfig.split(",")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2 && "锁".equals(kv[1].trim())) {
                    FaQinDeMiaoHaoKeAi._pkg_lock_map_.put(kv[0].trim(), true);
                }
            }
        } else {
            FaQinDeMiaoHaoKeAi._pkg_lock_map_.clear();
        }
        FaQinDeMiaoHaoKeAi._pkg_lock_content_ = extractContentBetweenMarkers("弹窗包名锁定内容", "");
        FaQinDeMiaoHaoKeAi._pkg_lock_link_ = extractContentBetweenMarkers("弹窗包名锁定链接", "");

        String pkgPopupControlTxt = extractContentBetweenMarkers("弹窗显示控制", null);
        if (pkgPopupControlTxt != null && !pkgPopupControlTxt.isEmpty()) {
            FaQinDeMiaoHaoKeAi._pkg_popup_control_.clear();
            for (String pair : pkgPopupControlTxt.split(",")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    FaQinDeMiaoHaoKeAi._pkg_popup_control_.put(kv[0].trim(), "开".equals(kv[1].trim()));
                }
            }
        } else {
            FaQinDeMiaoHaoKeAi._pkg_popup_control_.clear();
        }

        String normalPopupControlTxt = extractContentBetweenMarkers("普通弹窗显示控制", null);
        if (normalPopupControlTxt != null && !normalPopupControlTxt.isEmpty()) {
            FaQinDeMiaoHaoKeAi._normal_popup_control_.clear();
            for (String pair : normalPopupControlTxt.split(",")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    FaQinDeMiaoHaoKeAi._normal_popup_control_.put(kv[0].trim(), "开".equals(kv[1].trim()));
                }
            }
        } else {
            FaQinDeMiaoHaoKeAi._normal_popup_control_.clear();
        }

        FaQinDeMiaoHaoKeAi._net_cfg_enabled_ = !"关".equals(extractContentBetweenMarkers("网络配置总开关", "开"));
        FaQinDeMiaoHaoKeAi._show_dyn_content_ = !"关".equals(extractContentBetweenMarkers("显示动态内容开关", "开"));
        FaQinDeMiaoHaoKeAi._show_upd_log_ = !"关".equals(extractContentBetweenMarkers("显示更新日志开关", "开"));
        FaQinDeMiaoHaoKeAi._reset_dont_show_ = "开".equals(extractContentBetweenMarkers("重置不再提示", "关"));
        FaQinDeMiaoHaoKeAi._enable_img_rot_ = !"关".equals(extractContentBetweenMarkers("启用Moonlight图旋转", "开"));
        FaQinDeMiaoHaoKeAi._enable_bg_blur_ = !"关".equals(extractContentBetweenMarkers("启用背景模糊", "开"));
        FaQinDeMiaoHaoKeAi._enable_norm_popup_ = !"关".equals(extractContentBetweenMarkers("启用普通弹窗", "开"));
        FaQinDeMiaoHaoKeAi._show_close_btn_ = !"关".equals(extractContentBetweenMarkers("显示关闭弹窗按钮", "开"));
        FaQinDeMiaoHaoKeAi._show_dont_show_btn_ = !"关".equals(extractContentBetweenMarkers("显示不再提示按钮", "开"));
        FaQinDeMiaoHaoKeAi._show_qq_btn_ = !"关".equals(extractContentBetweenMarkers("显示QQ群按钮", "开"));
        FaQinDeMiaoHaoKeAi._show_tg_btn_ = !"关".equals(extractContentBetweenMarkers("显示TG频道按钮", "开"));
        FaQinDeMiaoHaoKeAi._enable_out_click_ = !"关".equals(extractContentBetweenMarkers("启用外部点击消失", "开"));
        FaQinDeMiaoHaoKeAi._enable_sig_ver_ = "开".equals(extractContentBetweenMarkers("启用签名验证", "关"));
        FaQinDeMiaoHaoKeAi._enable_night_mode_ = "开".equals(extractContentBetweenMarkers("启用夜间模式", "关"));
        FaQinDeMiaoHaoKeAi._enable_monet_dyn_ = "开".equals(extractContentBetweenMarkers("启用Monet动态取色", "开"));
        FaQinDeMiaoHaoKeAi._enable_builtin_monet_ = "开".equals(extractContentBetweenMarkers("启用内置Monet主题色", "开"));
        FaQinDeMiaoHaoKeAi._monet_theme_color_ = extractContentBetweenMarkers("莫奈主题颜色", "Color_Val");
        FaQinDeMiaoHaoKeAi._enable_dpi_auto_ = !"关".equals(extractContentBetweenMarkers("启用DPI适配", "开"));
        FaQinDeMiaoHaoKeAi._enable_remote_notif_ = "开".equals(extractContentBetweenMarkers("启用远程通知开关", "关"));
        FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = "开".equals(extractContentBetweenMarkers("启用设备码卡密弹窗", "关"));

        FaQinDeMiaoHaoKeAi._show_moonlight_icon_ = !"关".equals(extractContentBetweenMarkers("显示Moonlight图标", "开"));
        FaQinDeMiaoHaoKeAi._enable_countdown_dont_show_ = !"关".equals(extractContentBetweenMarkers("开启不再提示确认倒计时", "开"));
        FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ = "开".equals(extractContentBetweenMarkers("强制更新弹窗显示关闭按钮", "关"));
        FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_ = "开".equals(extractContentBetweenMarkers("包名锁定弹窗显示关闭按钮", "关"));
        FaQinDeMiaoHaoKeAi._force_use_domestic_url_ = "开".equals(extractContentBetweenMarkers("强制使用国内配置URL", "关"));
        FaQinDeMiaoHaoKeAi._force_use_international_url_ = "开".equals(extractContentBetweenMarkers("强制使用国际配置URL", "关"));
        FaQinDeMiaoHaoKeAi._enable_user_custom_url_ = "开".equals(extractContentBetweenMarkers("启用用户自定义配置URL", "关"));
        FaQinDeMiaoHaoKeAi._user_custom_urls_ = extractContentBetweenMarkers("用户自定义配置URL列表", "");
        FaQinDeMiaoHaoKeAi._enable_config_syntax_check_ = "开".equals(extractContentBetweenMarkers("启用配置文件语法检查", "关"));
        FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_ = "开".equals(extractContentBetweenMarkers("启用更丰富的设备信息收集", "关"));

        try {
            FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ = Long.parseLong(extractContentBetweenMarkers("配置加载超时时间", String.valueOf(FaQinDeMiaoHaoKeAi._DEF_CONFIG_LOAD_TIMEOUT_)));
        } catch (NumberFormatException e) {
            FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ = FaQinDeMiaoHaoKeAi._DEF_CONFIG_LOAD_TIMEOUT_;
            Log.e("peizhi", "Error parsing config load timeout from TXT config", e);
        }

        FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ = "开".equals(extractContentBetweenMarkers("启用弹窗背景改变文字颜色按钮颜色", "开"));
        FaQinDeMiaoHaoKeAi._show_neutral_btn_ = "开".equals(extractContentBetweenMarkers("中立按钮是否显示", "关"));
        FaQinDeMiaoHaoKeAi._neutral_btn_content_ = extractContentBetweenMarkers("中立按钮内容", "");
    }

    static String extractContentBetweenMarkers(String tagName, String defaultValue) {
        if (FaQinDeMiaoHaoKeAi._full_config_string_ == null || FaQinDeMiaoHaoKeAi._full_config_string_.isEmpty() || tagName == null || tagName.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            String startTag = "〈" + tagName + "〉";
            String endTag = "〈/" + tagName + "〉";

            int startIndex = FaQinDeMiaoHaoKeAi._full_config_string_.indexOf(startTag);
            if (startIndex == -1) {
                return defaultValue;
            }

            int endIndex = FaQinDeMiaoHaoKeAi._full_config_string_.indexOf(endTag, startIndex + startTag.length());
            if (endIndex == -1) {
                return defaultValue;
            }

            return FaQinDeMiaoHaoKeAi._full_config_string_.substring(startIndex + startTag.length(), endIndex).trim();

        } catch (Exception e) {
            Log.w("peizhi", "Error extracting content between markers for tag: " + tagName, e);
            return defaultValue;
        }
    }

    static String decodeHtmlEntities(String text) {
        if (text == null) return "";
        // Removed redundant decodedText variable
        return text.replace("\\u003C", "<")
                .replace("\\u003E", ">")
                .replace("\\n", "\n")
                .replace("&#x000A;", "\n")
                .replace("u003Cdiv>", "\n") // These might be overly aggressive if "u003Cdiv>" appears in actual content
                .replace("u003C/a>", "\n")  // Same as above
                .replace("！", "\n") // This replaces all exclamation marks with newlines, which might not be intended.

                .replace("&nbsp;", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&") // Should be first or carefully placed if other entities produce ampersands
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&#39;", "'")
                .replace("&#47;", "/")
                .replace("#x27;", "'") // Corrected: added semicolon
                .replace("&#x60;", "`")
                .replace("&copy;", "©"); // Corrected: added semicolon
    }
}
