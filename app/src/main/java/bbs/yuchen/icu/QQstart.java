package bbs.yuchen.icu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QQstart {

    public static void showInitialOfflineDialog(final Activity activity) {
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
            title.setText(isChinese ? "网络连接错误" : "Network Error");
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setPadding(0, 20, 0, 0);
            leftContent.addView(title);
            TextView content = new TextView(activity);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            Moonlight.applyClickableLinks(activity, content, isChinese ? "首次启动需要网络连接来获取配置，请连接网络后重试。" : "A network connection is required on first launch to fetch configuration. Please connect and try again.");
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(0, 20, 0, 0);
            leftContent.addView(content);

            LinearLayout rightButtons = new LinearLayout(activity);
            rightButtons.setOrientation(LinearLayout.VERTICAL);
            rightButtons.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rightButtons.setGravity(Gravity.CENTER);

            MaoMaoShiYiGeXiaoSaoHuo.addButton(rightButtons, activity, isChinese ? "确定并退出" : "OK & Exit", v -> {
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
            title.setText(isChinese ? "网络连接错误" : "Network Error");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            title.setTextSize(25);
            rootLayout.addView(title);
            View spacer1 = new View(activity);
            spacer1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80));
            rootLayout.addView(spacer1);
            TextView content = new TextView(activity);
            content.setTextSize(18);
            Moonlight.applyClickableLinks(activity, content, isChinese ? "首次启动需要网络连接来获取配置，请连接网络后重试。" : "A network connection is required on first launch to fetch configuration. Please connect and try again.");
            content.setTextColor(FaQinDeMiaoHaoKeAi._current_text_color_);
            content.setPadding(100, 50, 100, 50);
            rootLayout.addView(content);
            MaoMaoShiYiGeXiaoSaoHuo.addButton(rootLayout, activity, isChinese ? "确定并退出" : "OK & Exit", v -> {
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

    static void parseConfig(Activity activity, String config, String originalUrl) {
        if (config == null || config.isEmpty()) {
            return;
        }

        String processedConfig = config;
        if (originalUrl != null) {
            if (originalUrl.contains("sharechain.qq.com") ||
                    originalUrl.contains("gitee.com") ||
                    originalUrl.contains("gitcode.net") ||
                    originalUrl.contains("gitcode.com") ||
                    originalUrl.contains("github.com")) {
                int startIndex = processedConfig.indexOf("//配置开始");
                int endIndex = processedConfig.indexOf("//配置结束");
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    processedConfig = processedConfig.substring(startIndex + "//配置开始".length(), endIndex);
                }
            } else if (originalUrl.contains("share.weiyun.com")) {
                int firstStartIndex = processedConfig.indexOf("//配置开始");
                if (firstStartIndex != -1) {
                    int secondStartIndex = processedConfig.indexOf("//配置开始", firstStartIndex + "//配置开始".length());
                    int endIndex = processedConfig.indexOf("//配置结束");
                    if (secondStartIndex != -1 && endIndex != -1 && secondStartIndex < endIndex) {
                        processedConfig = processedConfig.substring(secondStartIndex + "//配置开始".length(), endIndex);
                    }
                }
            }
        }

        processedConfig = processedConfig.replaceAll("\\r\\n", " ").replaceAll("\\n", " ");

        boolean jsonParsedSuccessfully = false;
        try {
            MaoMaoShiYiGeXiaoSaoHuo.parseConfigJson(processedConfig);
            jsonParsedSuccessfully = true;
        } catch (JSONException e) {
        }

        if (!jsonParsedSuccessfully) {
            MaoMaoShiYiGeXiaoSaoHuo.parseConfigTxt(processedConfig);
        }

        if (FaQinDeMiaoHaoKeAi._enable_config_syntax_check_) {
            performConfigSyntaxCheck(processedConfig, jsonParsedSuccessfully);
        }
    }

    private static void performConfigSyntaxCheck(String configContent, boolean isJsonParsed) {
        boolean isValid = true;
        List<String> missingFields = new ArrayList<>();

        String[] criticalFields = {
                "配置版本", "内容", "英文内容", "更新链接", "强制更新内容", "强制更新内容英文"
        };

        for (String field : criticalFields) {
            if (isJsonParsed) {
                try {
                    JSONObject jsonConfig = new JSONObject(configContent);
                    if (!jsonConfig.has(field) || jsonConfig.getString(field).isEmpty()) {
                        missingFields.add(field);
                        isValid = false;
                    }
                } catch (JSONException e) {
                    isValid = false;
                    break;
                }
            } else {
                String content = MaoMaoShiYiGeXiaoSaoHuo.extractContentBetweenMarkers(field, null);
                if (content == null || content.isEmpty()) {
                    missingFields.add(field);
                    isValid = false;
                }
            }
        }

        if (!isValid) {
        } else {
        }
    }

    @SuppressWarnings("deprecation")
    static void checkAppVersionForForceUpdate(Activity activity) {
        if (activity == null || activity.isFinishing() || FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.isEmpty()) {
            FaQinDeMiaoHaoKeAi._force_upd_req_ = false;
            return;
        }

        try {
            String pkgName = activity.getPackageName();
            if (FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.containsKey(pkgName)) {
                int requiredVersion = FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.get(pkgName);
                PackageInfo pkgInfo = activity.getPackageManager().getPackageInfo(pkgName, 0);
                int currentVersion = pkgInfo.versionCode;

                if (currentVersion < requiredVersion) {
                    FaQinDeMiaoHaoKeAi._force_upd_req_ = true;
                } else {
                    FaQinDeMiaoHaoKeAi._force_upd_req_ = false;
                }
            } else {
                FaQinDeMiaoHaoKeAi._force_upd_req_ = false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            FaQinDeMiaoHaoKeAi._force_upd_req_ = false;
        }
    }

    private static void handleNeutralButtonAction(Activity activity, String actionContent) {
        if (activity == null || actionContent == null || actionContent.isEmpty()) {
            changku.showToast(activity, MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "中立按钮链接未配置" : "Neutral button link not configured");
            return;
        }

        String[] parts = actionContent.split("=", 2);
        if (parts.length != 2) {
            changku.showToast(activity, MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity) ? "中立按钮配置格式错误" : "Neutral button config format error");
            return;
        }

        String type = parts[0].trim();
        String target = parts[1].trim();
        boolean isChinese = MaoMaoShiYiGeXiaoSaoHuo.isChinese(activity);

        try {
            switch (type) {
                case "网站":
                    String urlToOpen = changku.convertToRawUrl(changku.extractUrlFromHref(target));
                    if (!urlToOpen.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));
                        activity.startActivity(intent);
                        changku.showToast(activity, isChinese ? "正在打开网站..." : "Opening website...");
                    } else {
                        changku.showToast(activity, isChinese ? "网站链接无效" : "Invalid website link");
                    }
                    break;
                case "QQ":
                    openQQ(activity, "聊天" + target);
                    changku.showToast(activity, isChinese ? "正在打开QQ聊天..." : "Opening QQ chat...");
                    break;
                case "QQ群":
                    openQQ(activity, "群" + target);
                    changku.showToast(activity, isChinese ? "正在打开QQ群..." : "Opening QQ group...");
                    break;
                default:
                    changku.showToast(activity, isChinese ? "未知的中立按钮类型" : "Unknown neutral button type");
                    break;
            }
        } catch (ActivityNotFoundException e) {
            changku.showToast(activity, isChinese ? "未安装QQ或无法打开链接" : "QQ not installed or failed to open link");
        } catch (Exception e) {
            changku.showToast(activity, isChinese ? "打开链接失败" : "Failed to open link");
        }
    }

    private static void openQQ(Activity activity, String action) {
        try {
            String qq = action.substring(action.indexOf("=") + 1).trim();

            if (action.startsWith("个人")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("mqqapi://card/show_pslcard?src_type=internal&uin=" + qq));
                activity.startActivity(intent);
            } else if (action.startsWith("群")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("mqqapi://card/show_pslcard?src_type=internal&uin=" + qq + "&card_type=group"));
                activity.startActivity(intent);
            } else if (action.startsWith("聊天")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + qq));
                activity.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + qq));
                activity.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            changku.showToast(activity, activity.getResources().getConfiguration().getLocales().get(0).getLanguage().equals("zh") ? "未安装QQ" : "QQ not installed");
        } catch (Exception e) {
            changku.showToast(activity, activity.getResources().getConfiguration().getLocales().get(0).getLanguage().equals("zh") ? "QQ操作失败: " + e.getMessage() : "QQ operation failed: " + e.getMessage());
        }
    }
}
