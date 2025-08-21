package bbs.yuchen.icu;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;

// wenbenpeizi 类用于加载和解析文本类型的配置信息
public class wenbenpeizi {

    /**
     * 从 SharedPreferences 加载所有缓存的设置到 sharezhifu 类的静态字段中。
     * <p>
     * 此方法会读取 SharedPreferences 中的各项配置，包括：
     * <ul>
     *     <li>通用弹窗版本号 ({@link FaQinDeMiaoHaoKeAi#_PREF_PP_})</li>
     *     <li>是否需要强制更新 ({@link FaQinDeMiaoHaoKeAi#_PREF_F_})</li>
     *     <li>强制更新的链接 ({@link FaQinDeMiaoHaoKeAi#_PREF_E_})</li>
     *     <li>是否启用签名验证 ({@link FaQinDeMiaoHaoKeAi#_PREF_H_})</li>
     *     <li>签名验证的 SHA1 列表 ({@link FaQinDeMiaoHaoKeAi#_PREF_G_}) - 解密后存储</li>
     *     <li>包版本配置 ({@link FaQinDeMiaoHaoKeAi#_PREF_Q_}) - 解密后存储为 Map</li>
     *     <li>包内容映射 ({@link FaQinDeMiaoHaoKeAi#_PREF_V_}) - 解密后存储为 Map</li>
     *     <li>设备卡密配置 ({@link FaQinDeMiaoHaoKeAi#_PREF_W_}) - 解密后存储为 Map</li>
     *     <li>是否启用设备ID检查 ({@link FaQinDeMiaoHaoKeAi#_PREF_Y_})</li>
     *     <li>允许的设备 ID 列表 ({@link FaQinDeMiaoHaoKeAi#_PREF_Z_}) - 解密后存储</li>
     *     <li>设备 ID 卡密相关的跳转链接 ({@link FaQinDeMiaoHaoKeAi#_PREF_AA_})</li>
     *     <li>开关类型的配置 ({@link FaQinDeMiaoHaoKeAi#_PREF_BB_}) - 解密后通过 {@link Moonlight#parseSwitchConfigs(String)} 解析</li>
     *     <li>文本类型的配置 ({@link FaQinDeMiaoHaoKeAi#_PREF_CC_}) - 解密后通过 {@link #parseTextConfigs(String)} 解析</li>
     *     <li>夜间模式开关 ({@link FaQinDeMiaoHaoKeAi#_PREF_I_})</li>
     *     <li>Monet 动态取色开关 ({@link FaQinDeMiaoHaoKeAi#_PREF_J_})</li>
     *     <li>内置 Monet 开关 ({@link FaQinDeMiaoHaoKeAi#_PREF_K_})</li>
     */ // 从 SharedPreferences 加载所有缓存的设置到 sharezhifu 类的静态字段中
    static void loadAllCachedSettings(SharedPreferences prefs) {
        // 加载通用弹窗版本号
        FaQinDeMiaoHaoKeAi._dialog_version_ = prefs.getInt(FaQinDeMiaoHaoKeAi._PREF_PP_, 2);
        // 加载是否需要强制更新的设置
        FaQinDeMiaoHaoKeAi._force_upd_req_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_F_, false);
        // 加载强制更新的链接
        FaQinDeMiaoHaoKeAi._force_upd_link_ = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_E_, "");
        // 加载是否启用签名验证的设置
        FaQinDeMiaoHaoKeAi._enable_sig_ver_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_H_, false);
        // 加载签名验证的 SHA1 列表
        String sha1ListStr = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_G_, null);
        if (sha1ListStr != null) {
            sha1ListStr = Moonlight.deobfuscate(sha1ListStr); // 解密
            if (sha1ListStr != null && !sha1ListStr.isEmpty()) {
                FaQinDeMiaoHaoKeAi._sig_sha1_list_ = new ArrayList<>(Arrays.asList(sha1ListStr.split(",")));
            } else {
                // 如果解密后为空或解密失败，则使用默认列表
                FaQinDeMiaoHaoKeAi._sig_sha1_list_ = new ArrayList<>(FaQinDeMiaoHaoKeAi._DEF_SIG_LIST_);
            }
        } else {
            // 如果 SharedPreferences 中没有，则使用默认列表
            FaQinDeMiaoHaoKeAi._sig_sha1_list_ = new ArrayList<>(FaQinDeMiaoHaoKeAi._DEF_SIG_LIST_);
        }

        // 加载包版本配置
        String pkgVerStr = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_Q_, null);
        if (pkgVerStr != null) {
            pkgVerStr = Moonlight.deobfuscate(pkgVerStr); // 解密
            if (pkgVerStr != null && !pkgVerStr.isEmpty()) {
                FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.clear();
                for (String pair : pkgVerStr.split(",")) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2) {
                        try {
                            FaQinDeMiaoHaoKeAi._pkg_ver_cfg_.put(kv[0].trim(), Integer.parseInt(kv[1].trim()));
                        } catch (NumberFormatException e) {
                            // 解析失败则忽略
                        }
                    }
                }
            }
        }

        // 加载包内容映射 (使用 ; 和 : 分隔符)
        String pkgContentStr = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_V_, null);
        if (pkgContentStr != null) {
            pkgContentStr = Moonlight.deobfuscate(pkgContentStr); // 解密
            if (pkgContentStr != null && !pkgContentStr.isEmpty()) {
                FaQinDeMiaoHaoKeAi._pkg_content_map_.clear();
                for (String pair : pkgContentStr.split(";")) {
                    String[] kv = pair.split(":", 2); // 按第一个 : 分割
                    if (kv.length == 2) {
                        FaQinDeMiaoHaoKeAi._pkg_content_map_.put(kv[0].trim(), kv[1].trim());
                    }
                }
            }
        }

        // 加载设备卡密配置
        String deviceCardKeysConfigStr = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_W_, null);
        if (deviceCardKeysConfigStr != null) {
            deviceCardKeysConfigStr = Moonlight.deobfuscate(deviceCardKeysConfigStr); // 解密
            if (deviceCardKeysConfigStr != null && !deviceCardKeysConfigStr.isEmpty()) {
                FaQinDeMiaoHaoKeAi._dev_card_keys_.clear();
                for (String pair : deviceCardKeysConfigStr.split(",")) {
                    String[] kv = pair.split("=", 2); // 按第一个 = 分割
                    if (kv.length == 2) {
                        FaQinDeMiaoHaoKeAi._dev_card_keys_.put(kv[0].trim(), kv[1].trim());
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._dev_card_keys_.clear(); // 解密后为空则清空
            }
        } else {
            FaQinDeMiaoHaoKeAi._dev_card_keys_.clear(); // SharedPreferences 中没有则清空
        }

        // 加载允许的设备 ID 列表
        FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_Y_, false); // 是否启用设备ID检查
        String allowedDeviceIdsStr = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_Z_, null);
        if (allowedDeviceIdsStr != null) {
            allowedDeviceIdsStr = Moonlight.deobfuscate(allowedDeviceIdsStr); // 解密
            if (allowedDeviceIdsStr != null && !allowedDeviceIdsStr.isEmpty()) {
                FaQinDeMiaoHaoKeAi._allowed_dev_ids_ = new ArrayList<>(Arrays.asList(allowedDeviceIdsStr.split(",")));
            } else {
                FaQinDeMiaoHaoKeAi._allowed_dev_ids_ = new ArrayList<>(); // 解密后为空则设置为空列表
            }
        } else {
            FaQinDeMiaoHaoKeAi._allowed_dev_ids_ = new ArrayList<>(); // SharedPreferences 中没有则设置为空列表
        }

        // 加载设备 ID 卡密相关的跳转链接
        FaQinDeMiaoHaoKeAi._dev_id_card_link_ = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_AA_, "");

        // 加载开关类型的配置
        String switchConfigs = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_BB_, null);
        if (switchConfigs != null) {
            switchConfigs = Moonlight.deobfuscate(switchConfigs); // 解密
            if (switchConfigs != null && !switchConfigs.isEmpty()) {
                Moonlight.parseSwitchConfigs(switchConfigs); // 调用 Moonlight 类的方法解析开关配置
            }
        }

        // 加载文本类型的配置
        String textConfigs = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_CC_, null);
        if (textConfigs != null) {
            textConfigs = Moonlight.deobfuscate(textConfigs); // 解密
            if (textConfigs != null && !textConfigs.isEmpty()) {
                parseTextConfigs(textConfigs); // 调用本类的方法解析文本配置
            }
        }

        // 加载特性和颜色开关设置
        FaQinDeMiaoHaoKeAi._enable_night_mode_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_I_, false);       // 夜间模式开关
        FaQinDeMiaoHaoKeAi._enable_monet_dyn_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_J_, true);        // Monet 动态取色开关
        FaQinDeMiaoHaoKeAi._enable_builtin_monet_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_K_, true);  // 内置 Monet 开关
        FaQinDeMiaoHaoKeAi._monet_theme_color_ = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_L_, "Blue");    // Monet 主题颜色 (来自配置)
        FaQinDeMiaoHaoKeAi._enable_dpi_auto_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_O_, true);        // DPI 自动调整开关
        FaQinDeMiaoHaoKeAi._is_large_screen_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_M_, false);       // 是否为大屏幕设备

        // 加载远程通知设置
        FaQinDeMiaoHaoKeAi._enable_remote_notif_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_R_, false); // 远程通知开关
        FaQinDeMiaoHaoKeAi._remote_notif_content_ = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_S_, "");   // 远程通知内容

        // 加载新的布尔类型设置
        FaQinDeMiaoHaoKeAi._show_moonlight_icon_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_DD_, true);               // 是否显示 Moonlight 图标
        FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_EE_, false);         // 是否启用设备卡密检查
        FaQinDeMiaoHaoKeAi._enable_countdown_dont_show_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_FF_, true);       // 是否启用倒计时不再显示功能
        FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_JJ_, false);      // 强制更新弹窗是否显示关闭按钮
        FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_KK_, false);     // 包名锁定弹窗是否显示关闭按钮
        FaQinDeMiaoHaoKeAi._force_use_domestic_url_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_LL_, false);        // 是否强制使用国内链接
        FaQinDeMiaoHaoKeAi._force_use_international_url_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_MM_, false);  // 是否强制使用国际链接
        FaQinDeMiaoHaoKeAi._enable_user_custom_url_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_NN_, false);        // 是否启用用户自定义链接
        FaQinDeMiaoHaoKeAi._user_custom_urls_ = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_OO_, "");                  // 用户自定义链接列表
        FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ = prefs.getLong(FaQinDeMiaoHaoKeAi._PREF_QQ_, FaQinDeMiaoHaoKeAi._DEF_CONFIG_LOAD_TIMEOUT_); // 配置加载超时时间
        FaQinDeMiaoHaoKeAi._enable_config_syntax_check_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_RR_, false);    // 是否启用配置语法检查
        FaQinDeMiaoHaoKeAi._enable_rich_device_info_collection_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_SS_, false); // 是否启用丰富的设备信息收集

        // 新增：加载动态文本颜色设置
        FaQinDeMiaoHaoKeAi._enable_dynamic_text_colors_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_NEW_DYNAMIC_TEXT_COLORS_, true);

        // 新增：加载中立按钮设置
        FaQinDeMiaoHaoKeAi._show_neutral_btn_ = prefs.getBoolean(FaQinDeMiaoHaoKeAi._PREF_NEUTRAL_BTN_SWITCH_, false); // 是否显示中立按钮
        FaQinDeMiaoHaoKeAi._neutral_btn_content_ = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_NEUTRAL_BTN_CONTENT_, ""); // 中立按钮内容

        // 加载包名锁定配置
        String packageLockMapStr = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_GG_, null);
        if (packageLockMapStr != null) {
            packageLockMapStr = Moonlight.deobfuscate(packageLockMapStr); // 解密
            if (packageLockMapStr != null && !packageLockMapStr.isEmpty()) {
                FaQinDeMiaoHaoKeAi._pkg_lock_map_.clear();
                for (String pair : packageLockMapStr.split(",")) {
                    String[] kv = pair.split("=", 2); // 按第一个 = 分割
                    if (kv.length == 2 && "锁".equals(kv[1].trim())) { // 如果值为 "锁"
                        FaQinDeMiaoHaoKeAi._pkg_lock_map_.put(kv[0].trim(), true);
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._pkg_lock_map_.clear(); // 解密后为空则清空
            }
        } else {
            FaQinDeMiaoHaoKeAi._pkg_lock_map_.clear(); // SharedPreferences 中没有则清空
        }
        FaQinDeMiaoHaoKeAi._pkg_lock_content_ = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_HH_, ""); // 包名锁定弹窗内容
        FaQinDeMiaoHaoKeAi._pkg_lock_link_ = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_II_, "");   // 包名锁定弹窗链接

        // 新增：加载特定包名弹窗控制
        String pkgPopupControlStr = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_P_, null);
        if (pkgPopupControlStr != null) {
            pkgPopupControlStr = Moonlight.deobfuscate(pkgPopupControlStr); // 解密
            if (pkgPopupControlStr != null && !pkgPopupControlStr.isEmpty()) {
                FaQinDeMiaoHaoKeAi._pkg_popup_control_.clear();
                for (String pair : pkgPopupControlStr.split(",")) {
                    String[] kv = pair.split("=", 2); // 按第一个 = 分割
                    if (kv.length == 2) {
                        FaQinDeMiaoHaoKeAi._pkg_popup_control_.put(kv[0].trim(), "开".equals(kv[1].trim())); // 如果值为 "开"
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._pkg_popup_control_.clear(); // 解密后为空则清空
            }
        } else {
            FaQinDeMiaoHaoKeAi._pkg_popup_control_.clear(); // SharedPreferences 中没有则清空
        }

        // 新增：加载普通弹窗控制
        String normalPopupControlStr = prefs.getString(FaQinDeMiaoHaoKeAi._PREF_NEW_NORMAL_POPUP_CONTROL_, null);
        if (normalPopupControlStr != null) {
            normalPopupControlStr = Moonlight.deobfuscate(normalPopupControlStr); // 解密
            if (normalPopupControlStr != null && !normalPopupControlStr.isEmpty()) {
                FaQinDeMiaoHaoKeAi._normal_popup_control_.clear();
                for (String pair : normalPopupControlStr.split(",")) {
                    String[] kv = pair.split("=", 2); // 按第一个 = 分割
                    if (kv.length == 2) {
                        FaQinDeMiaoHaoKeAi._normal_popup_control_.put(kv[0].trim(), "开".equals(kv[1].trim())); // 如果值为 "开"
                    }
                }
            } else {
                FaQinDeMiaoHaoKeAi._normal_popup_control_.clear(); // 解密后为空则清空
            }
        } else {
            FaQinDeMiaoHaoKeAi._normal_popup_control_.clear(); // SharedPreferences 中没有则清空
        }
    }

    // 解析文本配置字符串 (例如 "key1=value1,key2=value2")
    private static void parseTextConfigs(String config) {
        String[] parts = config.split(","); // 按逗号分割成键值对字符串
        for (String part : parts) {
            String[] kv = part.split("=", 2); // 按第一个等号分割成键和值
            if (kv.length == 2) {
                String key = kv[0].trim();   // 获取键
                String value = kv[1].trim(); // 获取值

                switch (key) {
                    case "弹窗版本":
                        try {
                            FaQinDeMiaoHaoKeAi._dialog_version_ = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            FaQinDeMiaoHaoKeAi._dialog_version_ = 2; // 解析失败则默认为 2
                        }
                        break;
                    case "莫奈主题颜色":
                        FaQinDeMiaoHaoKeAi._monet_theme_color_ = value;
                        break;
                    case "更新日志":
                        FaQinDeMiaoHaoKeAi._upd_log_ = value;
                        break;
                    case "更新日志英文":
                        FaQinDeMiaoHaoKeAi._upd_log_eng_ = value;
                        break;
                    case "强制更新内容":
                        FaQinDeMiaoHaoKeAi._force_upd_content_ = value;
                        break;
                    case "强制更新内容英文":
                        FaQinDeMiaoHaoKeAi._force_upd_content_eng_ = value;
                        break;
                    case "弹窗标题英文":
                        FaQinDeMiaoHaoKeAi._popup_title_eng_ = value;
                        break;
                    case "强制更新链接":
                        FaQinDeMiaoHaoKeAi._force_upd_link_ = value;
                        break;
                    case "远程通知内容":
                        FaQinDeMiaoHaoKeAi._remote_notif_content_ = value;
                        break;
                    case "设备码卡密链接":
                        FaQinDeMiaoHaoKeAi._dev_id_card_link_ = value;
                        break;
                    case "弹窗包名锁定内容":
                        FaQinDeMiaoHaoKeAi._pkg_lock_content_ = value;
                        break;
                    case "弹窗包名锁定链接":
                        FaQinDeMiaoHaoKeAi._pkg_lock_link_ = value;
                        break;
                    case "用户自定义配置URL列表":
                        FaQinDeMiaoHaoKeAi._user_custom_urls_ = value;
                        break;
                    case "配置加载超时时间":
                        try {
                            FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ = Long.parseLong(value);
                        } catch (NumberFormatException e) {
                            // 解析失败则使用默认超时时间
                            FaQinDeMiaoHaoKeAi._config_load_timeout_ms_ = FaQinDeMiaoHaoKeAi._DEF_CONFIG_LOAD_TIMEOUT_;
                        }
                        break;
                    case "中立按钮内容":
                        FaQinDeMiaoHaoKeAi._neutral_btn_content_ = value;
                        break;
                    // 可以根据需要添加更多 case 来处理其他文本配置
                }
            }
        }
    }
}
