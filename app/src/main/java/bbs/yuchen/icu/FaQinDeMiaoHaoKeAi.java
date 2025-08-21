package bbs.yuchen.icu;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaQinDeMiaoHaoKeAi {


    // Enum to represent the type of dialog currently being shown
    public enum DialogType {
        NONE,
        FORCE_UPDATE,
        SIGNATURE_FAILURE,
        PACKAGE_LOCKED,
        DEVICE_ID_RESTRICTED,
        CARD_KEY_INPUT,
        NORMAL_POPUP,
        CLOUD_NOTICE, // For Cloud Injection Notice
        CLOUD_REGISTER // For Cloud Injection Registration/Verification
    }
    // SharedPreferences keys for caching settings
    static final String _PREF_A_ = "Key1"; // 缓存配置内容
    static final String _PREF_B_ = "Key2"; // 配置版本
    static final String _PREF_E_ = "Key5"; // 强制更新链接
    static final String _PREF_F_ = "Key6"; // 强制更新是否需要
    static final String _PREF_G_ = "Key7"; // 签名SHA1列表
    static final String _PREF_H_ = "Key8"; // 启用签名验证
    static final String _PREF_I_ = "Key9"; // 启用夜间模式
    static final String _PREF_J_ = "Key10"; // 启用Monet动态取色
    static final String _PREF_K_ = "Key11"; // 启用内置Monet
    static final String _PREF_L_ = "Key12"; // Monet主题色
    static final String _PREF_M_ = "Key13"; // 是否大屏幕
    static final String _PREF_N_ = "Key14"; // DPI适配是否已检查
    static final String _PREF_O_ = "Key15"; // 启用DPI适配
    static final String _PREF_P_ = "Key16"; // 弹窗显示控制 (Package-specific main dialog visibility) - NEW
    static final String _PREF_Q_ = "Key17"; // 包名版本配置
    static final String _PREF_R_ = "Key18"; // 启用远程通知
    static final String _PREF_S_ = "Key19"; // 通知内容
    static final String _PREF_T_ = "Key20"; // 通知渠道ID
    static final String _PREF_U_ = "Key21"; // 通知渠道名称
    static final String _PREF_V_ = "Key22"; // 包名内容映射
    static final String _PREF_W_ = "Key23"; // 设备卡密映射
    static final String _PREF_X_ = "Key24_"; // 设备卡密绑定验证状态
    static final String _PREF_Y_ = "Key25"; // 启用设备ID检查
    static final String _PREF_Z_ = "Key26"; // 允许的设备ID列表
    static final String _PREF_AA_ = "Key27"; // 设备ID卡密链接
    // Keys for switch type config items
    static final String _PREF_BB_ = "Key28"; // 开关配置字符串
    static final String _PREF_CC_ = "Key29"; // 文本配置字符串
    // New SharedPreferences keys for additional configurations
    static final String _PREF_DD_ = "Key30"; // 显示Moonlight图标
    static final String _PREF_EE_ = "Key31"; // 启用设备卡密检查
    static final String _PREF_FF_ = "Key32"; // 启用不再提示倒计时
    static final String _PREF_GG_ = "Key33"; // 包名锁定映射
    static final String _PREF_HH_ = "Key34"; // 包名锁定内容
    static final String _PREF_II_ = "Key35"; // 包名锁定链接
    static final String _PREF_JJ_ = "Key36"; // 强制更新弹窗显示关闭按钮
    static final String _PREF_KK_ = "Key37"; // 包名锁定弹窗显示关闭按钮
    static final String _PREF_LL_ = "Key38"; // 强制使用国内URL
    static final String _PREF_MM_ = "Key39"; // 强制使用国际URL
    static final String _PREF_NN_ = "Key40"; // 启用用户自定义URL
    static final String _PREF_OO_ = "Key41"; // 用户自定义URL列表
    static final String _PREF_PP_ = "Key42"; // 弹窗版本
    static final String _PREF_QQ_ = "Key43"; // 配置加载超时时间
    static final String _PREF_RR_ = "Key44"; // 启用配置文件语法检查
    static final String _PREF_SS_ = "Key45"; // 启用更丰富的设备信息收集
    static final String _PREF_NEW_DYNAMIC_TEXT_COLORS_ = "Key46"; // 启用弹窗背景改变文字颜色按钮颜色
    static final String _PREF_NEUTRAL_BTN_SWITCH_ = "Key47"; // 中立按钮是否显示 - NEW (used by original dialogs)
    static final String _PREF_NEUTRAL_BTN_CONTENT_ = "Key48"; // 中立按钮内容/动作 - NEW (used by original dialogs)
    static final String _PREF_NEW_NORMAL_POPUP_CONTROL_ = "Key49"; // 普通弹窗显示控制

    // --- Cloud Inject SharedPreferences Keys (NEW) ---
    public static final String _PREF_CLOUD_INJECT_CONFIG_CACHE_ = "Key50_CloudConfigCache"; // 云注入网络配置缓存
    public static final String _PREF_CLOUD_INJECT_SECRET_ = "Key51_CloudSecret"; // 云注入卡密缓存
    public static final String _PREF_CLOUD_INJECT_LAST_NOTICE_HASH_ = "Key52_CloudNoticeHash"; // 云注入公告哈希缓存
    public static final String _PREF_SECURITY_SETTINGS_ = "Key53_SecuritySettings"; // 安全设置缓存

    static final int _DONT_SHOW_AGAIN_CD_ = 7000; // 不再提示倒计时时间 (7秒)
    static final int _CARD_KEY_CD_ = 15000; // 卡密倒计时时间 (15秒)
    static final int _DEV_ID_CRASH_CD_ = 15; // 设备ID限制对话框崩溃倒计时 (15秒)
    static final int _DEF_CONFIG_LOAD_TIMEOUT_ = 8000; // 默认配置加载超时时间 (8秒)
    // Default Day Colors
    public static final int _DIALOG_BG_DAY_ = 0xFFFFFFFF; // 对话框背景日间模式
    public static String _dev_card_key_prompt_msg_;
    // Current resolved colors
    public static int _current_dialog_bg_ = _DIALOG_BG_DAY_; // 当前对话框背景色
    static final int _BUTTON_BG_DAY_ = 0xFFE0E0E0; // 按钮背景日间模式
    static int _current_button_bg_ = _BUTTON_BG_DAY_; // 当前按钮背景色
    public static final int _TEXT_COLOR_DAY_ = 0xFF000000; // 文本颜色日间模式
    public static int _current_text_color_ = _TEXT_COLOR_DAY_; // 当前文本颜色
    static final int _BUTTON_TEXT_COLOR_DAY_ = 0xFF000000; // 按钮文本颜色日间模式
    static int _current_button_text_ = _BUTTON_TEXT_COLOR_DAY_; // 当前按钮文本颜色
    private static final int _EDITTEXT_TEXT_COLOR_DAY_ = 0xFF000000; // EditText文本颜色日间模式
    static int _current_edittext_text_ = _EDITTEXT_TEXT_COLOR_DAY_; // 当前EditText文本颜色
    private static final int _EDITTEXT_HINT_COLOR_DAY_ = 0x80000000; // EditText提示颜色日间模式
    static int _current_edittext_hint_ = _EDITTEXT_HINT_COLOR_DAY_; // 当前EditText提示颜色
    // Default Night Colors
    static final int _DIALOG_BG_NIGHT_ = 0xFF121212; // 对话框背景夜间模式
    static final int _BUTTON_BG_NIGHT_ = 0xFF363636; // 按钮背景夜间模式
    static final int _TEXT_COLOR_NIGHT_ = 0xFFFFFFFF; // 文本颜色夜间模式
    static final int _BUTTON_TEXT_COLOR_NIGHT_ = 0xFFFFFFFF; // 按钮文本颜色夜间模式
    private static final int _EDITTEXT_TEXT_COLOR_NIGHT_ = 0xFFFFFFFF; // EditText文本颜色夜间模式
    private static final int _EDITTEXT_HINT_COLOR_NIGHT_ = 0x80FFFFFF; // EditText提示颜色夜间模式
    static long _config_load_timeout_ms_ = _DEF_CONFIG_LOAD_TIMEOUT_; // 配置加载超时时间
    private static final float _MAX_LANDSCAPE_DIALOG_HEIGHT_PERCENT_ = 0.8f; // 横屏模式下对话框的最大高度百分比
    static final List<String> _DEF_SIG_LIST_ = Arrays.asList( // 默认签名SHA1列表
            "12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78"
    );
    static List<String> _sig_sha1_list_ = new ArrayList<>(_DEF_SIG_LIST_); // 签名SHA1列表
    // Configurable Fields
    public static int _dialog_version_ = 1; // 弹窗版本, 1 = Simple Style, 2 = Themed Style
    public static String _dyn_content_ = "Content1"; // 动态内容
    static String _eng_content_ = "Content2"; // 英文内容
    public static String _force_upd_link_ = ""; // 强制更新链接
    public static String _tg_channel_link_ = "https://t.me/channel"; // Telegram频道链接
    public static String _qq_group_num_ = ""; // QQ群号
    public static String _popup_title_ = "Title1"; // 弹窗标题
    static String _popup_title_eng_ = "Title2"; // 弹窗英文标题
    public static String _force_upd_content_ = "UpdateMsg1"; // 强制更新内容
    static String _force_upd_content_eng_ = "UpdateMsg2"; // 强制更新英文内容
    static String _upd_log_ = "Log1"; // 更新日志
    static String _upd_log_eng_ = "Log2"; // 更新英文日志
    static String _cfg_version_ = "1.0"; // 配置版本
    static Map<String, Integer> _pkg_ver_cfg_ = new HashMap<>(); // 包名版本配置
    static Map<String, String> _pkg_content_map_ = new HashMap<>(); // 包名内容映射
    public static boolean _force_upd_req_ = false; // 是否需要强制更新
    // Switch Fields
    static boolean _net_cfg_enabled_ = true; // 网络配置总开关 (original, not cloud inject specific)
    static boolean _show_dyn_content_ = true; // 显示动态内容
    static boolean _show_upd_log_ = true; // 显示更新日志
    static boolean _reset_dont_show_ = false; // 重置不再提示
    static boolean _enable_img_rot_ = true; // 启用Moonlight图旋转
    static boolean _enable_bg_blur_ = true; // 启用背景模糊
    static boolean _enable_norm_popup_ = true; // 启用普通弹窗
    public static boolean _show_close_btn_ = true; // 显示关闭弹窗按钮
    static boolean _show_dont_show_btn_ = true; // 显示不再提示按钮
    public static boolean _show_qq_btn_ = true; // 显示QQ群按钮
    public static boolean _show_tg_btn_ = true; // 显示TG频道按钮
    static boolean _enable_out_click_ = true; // 启用外部点击消失
    public static boolean _enable_sig_ver_ = false; // 启用签名验证
    static boolean _enable_dpi_auto_ = true; // 启用DPI适配
    static Map<String, String> _dev_card_keys_ = new HashMap<>(); // 设备卡密映射
    public static boolean _enable_dev_id_check_ = false; // 启用设备ID检查
    static List<String> _allowed_dev_ids_ = new ArrayList<>(); // 允许的设备ID列表
    public static String _dev_id_card_link_ = ""; // 设备ID卡密链接
    // Remote notification switches
    static boolean _enable_remote_notif_ = false; // 启用远程通知
    static String _remote_notif_content_ = ""; // 远程通知内容
    // New configurable fields
    static boolean _show_moonlight_icon_ = true; // 显示Moonlight图标 (Default: true)
    public static boolean _enable_dev_card_key_check_ = false; // 启用设备卡密检查 (Default: false)
    static boolean _enable_countdown_dont_show_ = true; // 开启不再提示确认倒计时 (Default: true)
    static Map<String, Boolean> _pkg_lock_map_ = new HashMap<>(); // 包名锁定映射 (key: package name, value: true if locked)
    public static String _pkg_lock_content_ = ""; // 包名锁定内容
    public static String _pkg_lock_link_ = ""; // 包名锁定链接
    public static boolean _show_force_update_close_btn_ = false; // 强制更新弹窗显示关闭按钮 (Default: false)
    public static boolean _show_package_lock_close_btn_ = false; // 包名锁定弹窗显示关闭按钮 (Default: false)
    static boolean _force_use_domestic_url_ = false; // 强制使用国内URL (original, not cloud inject specific)
    static boolean _force_use_international_url_ = false; // 强制使用国际URL (original, not cloud inject specific)
    static boolean _enable_user_custom_url_ = false; // 启用用户自定义URL
    static String _user_custom_urls_ = ""; // 用户自定义URL列表 (逗号分隔)
    static boolean _enable_config_syntax_check_ = false; // 启用配置文件语法检查
    static boolean _enable_rich_device_info_collection_ = false; // 启用更丰富的设备信息收集
    // NEW: Enable dynamic text/button color based on background luminosity
    static boolean _enable_dynamic_text_colors_ = true; // Default: true (enabled)
    
    // Original Neutral Button fields (used by non-cloud dialogs)
    public static boolean _show_neutral_btn_ = false; // 是否显示中立按钮
    public static String _neutral_btn_content_ = ""; // 中立按钮内容 (e.g., "网站=https://example.com" or "QQ=12345")
    
    // NEW: Package-specific popup controls
    static Map<String, Boolean> _pkg_popup_control_ = new HashMap<>(); // 包名主弹窗显示控制
    static Map<String, Boolean> _normal_popup_control_ = new HashMap<>(); // 包名普通弹窗显示控制
    // Color Switch Fields
    static boolean _enable_night_mode_ = false; // 启用夜间模式
    static boolean _enable_monet_dyn_ = true; // 启用Monet动态取色
    static boolean _enable_builtin_monet_ = true; // 启用内置Monet
    static String _monet_theme_color_ = "Color_Val"; // Monet主题色
    static int _current_edittext_stroke_ = 0xFFCCCCCC; // 当前EditText描边颜色
    // Internal State
    static WeakReference<Activity> _s_activity_ref_ = null; // 正在显示的Activity弱引用
    static AlertDialog _s_main_dialog_ = null; // 主对话框实例
    static DialogType _current_dialog_type_ = DialogType.NONE; // 当前显示的对话框类型
    static boolean _is_blur_applied_ = false; // 是否已应用背景模糊
    static String _cfg_dom_url_ = ""; // 国内配置URL (Used by original config loading)
    static String _cfg_intl_url_ = ""; // 国际配置URL (Used by original config loading)
    static boolean _is_large_screen_ = false; // 是否为大屏幕设备
    static CountDownTimer _card_key_timer_ = null; // 卡密倒计时器
    static CountDownTimer _dev_id_timer_ = null; // 设备ID倒计时器
    static TextView _dev_id_timer_text_ = null; // 设备ID倒计时文本视图
    static String _full_config_string_ = ""; // 完整的配置字符串，用于自定义解析 (original config)

    // --- Cloud Injection Module Fields (NEW) ---
    public static boolean _cloud_inject_module_enabled_ = false; // 云注入模块总开关 (parsed from <云注入模块>)
    public static String _cloud_inject_global_appid_ = ""; // 云注入全局APPID (parsed from <云注入模块>)
    public static boolean _cloud_inject_pkg_management_enabled_ = false; // 云注入包管理开关 (parsed from <云注入管理>)
    public static Map<String, String> _cloud_inject_pkg_appid_map_ = new HashMap<>(); // 云注入包名与APPID映射 (parsed from <云注入管理>)
    public static boolean _cloud_inject_notice_enabled_ = true; // 云注入公告显示开关 (parsed from <云注入公告显示> or default true)

    // Cloud notice dialog content (parsed from specific tags if _cloud_inject_notice_enabled_ is true)
    public static String _cloud_inject_notice_title_ = "提示";
    public static String _cloud_inject_notice_message_ = "";
    public static String _cloud_inject_notice_positive_text_ = "确定";
    public static int _cloud_inject_notice_positive_action_type_ = 0; // 0:dismiss, 1:QQ, 2:URL, 3:Exit, 4:Share
    public static String _cloud_inject_notice_positive_action_ext_ = "";
    public static String _cloud_inject_notice_negative_text_ = "";
    public static int _cloud_inject_notice_negative_action_type_ = 0;
    public static String _cloud_inject_notice_negative_action_ext_ = "";
    public static String _cloud_inject_notice_neutral_text_ = ""; // For cloud notice neutral button
    public static int _cloud_inject_notice_neutral_action_type_ = 0;
    public static String _cloud_inject_notice_neutral_action_ext_ = "";
    public static boolean _cloud_inject_notice_show_neutral_btn_ = false; // Parsed from <中立按钮> under cloud notice context
    public static int _cloud_inject_notice_force_type_ = 0; // 0: not forced, 1: forced
    public static String _cloud_inject_notice_hash_ = ""; // Parsed from <公告哈希>

    // Cloud register dialog content (usually from server JSON, but might have defaults)
    public static String _cloud_inject_register_cancel_text_ = "";
    public static int _cloud_inject_register_cancel_action_type_ = 0;
    public static String _cloud_inject_register_cancel_action_ext_ = "";
    public static String _cloud_inject_register_neutral_text_ = "";
    public static int _cloud_inject_register_neutral_action_type_ = 0;
    public static String _cloud_inject_register_neutral_action_ext_ = "";
    public static String _cloud_inject_register_use_text_ = "确定";


    // Security Settings (NEW) - Parsed from <安全>
    public static Map<String, String> _security_settings_map_ = new HashMap<>();

    // Cloud Inject Internal State (NEW)
    public static String _cloud_inject_cached_secret_ = null; // Cached secret from SharedPreferences
    public static String _cloud_inject_current_config_json_ = null; // Raw JSON from cloud
    public static String _cloud_inject_domestic_url_ = ""; // Passed from DialogLauncher
    public static String _cloud_inject_international_url_ = ""; // Passed from DialogLauncher
}
