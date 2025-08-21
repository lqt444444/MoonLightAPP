package bbs.yuchen.icu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log; // Ensure Log is imported
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import bbs.yuchen.icu.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button debugPanelButton; // Declare the button

    // Backup for sharezhifu fields
    private Map<String, Object> sharezhifuBackup = new HashMap<>();
    private static final String ONCREATE_TAG = "MyActivityOnCreate"; // Consistent tag for onCreate logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(ONCREATE_TAG, "onCreate: Bắt đầu"); // Start
        super.onCreate(savedInstanceState);
        Log.d(ONCREATE_TAG, "onCreate: super.onCreate() đã hoàn tất");

        // Moonlight2.init(this, "https://gist.githubusercontent.com/Liqiuting-byte/939fa18978f54f16dcf257ef0e80f7f4/raw/gistfile1.txt", "https://gist.githubusercontent.com/Liqiuting-byte/939fa18978f54f16dcf257ef0e80f7f4/raw/gistfile1.txt");

        try {
            Log.d(ONCREATE_TAG, "onCreate: Chuẩn bị inflate binding");
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            Log.d(ONCREATE_TAG, "onCreate: Inflate binding đã hoàn tất");

            Log.d(ONCREATE_TAG, "onCreate: Chuẩn bị setContentView");
            setContentView(binding.getRoot());
            Log.d(ONCREATE_TAG, "onCreate: setContentView đã hoàn tất");
        } catch (Exception e) {
            Log.e(ONCREATE_TAG, "LỖI nghiêm trọng trong quá trình binding inflate hoặc setContentView", e);
            Toast.makeText(this, "Lỗi nghiêm trọng khi khởi tạo view!", Toast.LENGTH_LONG).show();
            // If setContentView fails, other view-related operations might also fail.
            // Consider finishing the activity or showing a critical error message.
            // It's possible the app won't even show this toast if the context is invalid.
            return; // Exit onCreate if UI setup fails critically
        }
        
        Log.d(ONCREATE_TAG, "onCreate: Chuẩn bị thiết lập Debug Panel Button");
        // --- Debug Panel Button ---
        try {
            debugPanelButton = findViewById(R.id.debug_panel_button);
            Log.d(ONCREATE_TAG, "onCreate: findViewById(R.id.debug_panel_button) đã hoàn tất. Button is: " + (debugPanelButton == null ? "null" : "not null"));

            if (debugPanelButton != null) {
                Log.d(ONCREATE_TAG, "onCreate: Nút debugPanelButton được tìm thấy bằng ID");
                debugPanelButton.setOnClickListener(v -> {
                    Toast.makeText(MainActivity.this, "Debug button clicked!", Toast.LENGTH_SHORT).show();
                    Log.d(ONCREATE_TAG, "Debug button onClick: Gọi showDebugOptionsDialog()");
                    showDebugOptionsDialog();
                });
                Toast.makeText(this, "调教喵喵", Toast.LENGTH_SHORT).show();
                Log.d(ONCREATE_TAG, "onCreate: Listener được đặt cho R.id.debug_panel_button");
            } else {
                Log.w(ONCREATE_TAG, "onCreate: Nút debugPanelButton không tìm thấy bằng ID, thử tìm bằng văn bản");
                ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
                debugPanelButton = findButtonByText(rootView, "调试面板"); // Ensure findButtonByText is robust
                if (debugPanelButton != null) {
                    Log.d(ONCREATE_TAG, "onCreate: Nút debugPanelButton được tìm thấy bằng văn bản '调试面板'");
                    debugPanelButton.setOnClickListener(v -> {
                        Toast.makeText(MainActivity.this, "Debug button (found by text) clicked!", Toast.LENGTH_SHORT).show();
                        Log.d(ONCREATE_TAG, "Debug button (text) onClick: Gọi showDebugOptionsDialog()");
                        showDebugOptionsDialog();
                    });
                    Toast.makeText(this, "Debug listener set for button with text '调试面板'!", Toast.LENGTH_SHORT).show();
                    Log.d(ONCREATE_TAG, "onCreate: Listener được đặt cho nút có văn bản '调试面板'");
                } else {
                    Log.e(ONCREATE_TAG, "onCreate: Nút debugPanelButton KHÔNG TÌM THẤY bằng ID hoặc văn bản");
                    Toast.makeText(this, "Debug panel button not found by ID or text.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.e(ONCREATE_TAG, "LỖI trong quá trình thiết lập Debug Panel Button", e);
        }
        // --- End Debug Panel Button ---
        Log.d(ONCREATE_TAG, "onCreate: Thiết lập Debug Panel Button đã hoàn tất (hoặc thất bại nếu có lỗi)");

        Log.d(ONCREATE_TAG, "onCreate: Chuẩn bị thiết lập AppBarConfiguration và NavController");
        try {
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            // Check if binding or binding.navView is null before using
            if (binding != null && binding.navView != null) {
                NavigationUI.setupWithNavController(binding.navView, navController);
            } else {
                Log.e(ONCREATE_TAG, "onCreate: binding or binding.navView is null before NavigationUI.setupWithNavController.");
                if (binding == null) Log.e(ONCREATE_TAG, "onCreate: binding is NULL");
                if (binding != null && binding.navView == null) Log.e(ONCREATE_TAG, "onCreate: binding.navView is NULL");
            }
            Log.d(ONCREATE_TAG, "onCreate: Thiết lập AppBarConfiguration và NavController đã hoàn tất");
        } catch (Exception e) {
            Log.e(ONCREATE_TAG, "LỖI trong quá trình thiết lập AppBarConfiguration hoặc NavController", e);
             if (binding == null) {
                Log.e(ONCREATE_TAG, "onCreate: 'binding' is null before NavigationUI.setupWithNavController. This is likely the cause of a NullPointerException if setContentView failed earlier.");
            }
        }
        
        Log.d(ONCREATE_TAG, "onCreate: Chuẩn bị gọi DialogLauncher.Thrym");
        try {
            DialogLauncher.Thrym(this); // Assuming DialogLauncher.Thrym exists and is correctly implemented
            Log.d(ONCREATE_TAG, "onCreate: Gọi DialogLauncher.Thrym đã hoàn tất");
        } catch (Exception e) {
            Log.e(ONCREATE_TAG, "LỖI trong quá trình gọi DialogLauncher.Thrym", e);
        }
        
        Log.d(ONCREATE_TAG, "onCreate: Đã hoàn tất (cuối phương thức)");
    }

    // Make sure this utility method is present and handles nulls gracefully
    private Button findButtonByText(ViewGroup parent, String text) {
        if (parent == null || text == null) return null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            android.view.View child = parent.getChildAt(i);
            if (child instanceof Button && text.equalsIgnoreCase(((Button) child).getText().toString())) {
                return (Button) child;
            } else if (child instanceof ViewGroup) {
                Button found = findButtonByText((ViewGroup) child, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private void backupShareZhiFuFields() {
        Log.d(ONCREATE_TAG, "backupShareZhiFuFields: Bắt đầu sao lưu");
        sharezhifuBackup.clear();
        sharezhifuBackup.put("_popup_title_", FaQinDeMiaoHaoKeAi._popup_title_);
        sharezhifuBackup.put("_dyn_content_", FaQinDeMiaoHaoKeAi._dyn_content_);
        sharezhifuBackup.put("_dialog_version_", FaQinDeMiaoHaoKeAi._dialog_version_);
        sharezhifuBackup.put("_show_close_btn_", FaQinDeMiaoHaoKeAi._show_close_btn_);
        sharezhifuBackup.put("_show_qq_btn_", FaQinDeMiaoHaoKeAi._show_qq_btn_);
        sharezhifuBackup.put("_qq_group_num_", FaQinDeMiaoHaoKeAi._qq_group_num_);
        sharezhifuBackup.put("_show_tg_btn_", FaQinDeMiaoHaoKeAi._show_tg_btn_);
        sharezhifuBackup.put("_tg_channel_link_", FaQinDeMiaoHaoKeAi._tg_channel_link_);
        sharezhifuBackup.put("_show_neutral_btn_", FaQinDeMiaoHaoKeAi._show_neutral_btn_);
        sharezhifuBackup.put("_neutral_btn_content_", FaQinDeMiaoHaoKeAi._neutral_btn_content_);

        sharezhifuBackup.put("_force_upd_content_", FaQinDeMiaoHaoKeAi._force_upd_content_);
        sharezhifuBackup.put("_force_upd_link_", FaQinDeMiaoHaoKeAi._force_upd_link_);
        sharezhifuBackup.put("_show_force_update_close_btn_", FaQinDeMiaoHaoKeAi._show_force_update_close_btn_);
        sharezhifuBackup.put("_force_upd_req_", FaQinDeMiaoHaoKeAi._force_upd_req_);

        sharezhifuBackup.put("_enable_sig_ver_", FaQinDeMiaoHaoKeAi._enable_sig_ver_);
        sharezhifuBackup.put("_pkg_lock_content_", FaQinDeMiaoHaoKeAi._pkg_lock_content_);
        sharezhifuBackup.put("_pkg_lock_link_", FaQinDeMiaoHaoKeAi._pkg_lock_link_);
        sharezhifuBackup.put("_show_package_lock_close_btn_", FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_);
        
        sharezhifuBackup.put("_enable_dev_id_check_", FaQinDeMiaoHaoKeAi._enable_dev_id_check_);
        sharezhifuBackup.put("_dev_id_card_link_", FaQinDeMiaoHaoKeAi._dev_id_card_link_);
        
        sharezhifuBackup.put("_enable_dev_card_key_check_", FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_);
        // sharezhifuBackup.put("_dev_card_key_prompt_msg_", sharezhifu._dev_card_key_prompt_msg_); // Assuming this field exists
        Log.d(ONCREATE_TAG, "backupShareZhiFuFields: Sao lưu hoàn tất");
    }

    private void restoreShareZhiFuFields() {
        Log.d(ONCREATE_TAG, "restoreShareZhiFuFields: Bắt đầu khôi phục");
        if (!sharezhifuBackup.isEmpty()) {
            Toast.makeText(this, "Restoring sharezhifu fields...", Toast.LENGTH_SHORT).show();
            FaQinDeMiaoHaoKeAi._popup_title_ = (String) sharezhifuBackup.get("_popup_title_");
            FaQinDeMiaoHaoKeAi._dyn_content_ = (String) sharezhifuBackup.get("_dyn_content_");
            FaQinDeMiaoHaoKeAi._dialog_version_ = (int) Objects.requireNonNullElse(sharezhifuBackup.get("_dialog_version_"), 1); // Default to 1 if null
            FaQinDeMiaoHaoKeAi._show_close_btn_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_show_close_btn_"), false);
            FaQinDeMiaoHaoKeAi._show_qq_btn_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_show_qq_btn_"), false);
            FaQinDeMiaoHaoKeAi._qq_group_num_ = (String) sharezhifuBackup.get("_qq_group_num_");
            FaQinDeMiaoHaoKeAi._show_tg_btn_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_show_tg_btn_"), false);
            FaQinDeMiaoHaoKeAi._tg_channel_link_ = (String) sharezhifuBackup.get("_tg_channel_link_");
            FaQinDeMiaoHaoKeAi._show_neutral_btn_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_show_neutral_btn_"), false);
            FaQinDeMiaoHaoKeAi._neutral_btn_content_ = (String) sharezhifuBackup.get("_neutral_btn_content_");

            FaQinDeMiaoHaoKeAi._force_upd_content_ = (String) sharezhifuBackup.get("_force_upd_content_");
            FaQinDeMiaoHaoKeAi._force_upd_link_ = (String) sharezhifuBackup.get("_force_upd_link_");
            FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_show_force_update_close_btn_"), false);
            FaQinDeMiaoHaoKeAi._force_upd_req_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_force_upd_req_"), false);

            FaQinDeMiaoHaoKeAi._enable_sig_ver_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_enable_sig_ver_"), false);
            FaQinDeMiaoHaoKeAi._pkg_lock_content_ = (String) sharezhifuBackup.get("_pkg_lock_content_");
            FaQinDeMiaoHaoKeAi._pkg_lock_link_ = (String) sharezhifuBackup.get("_pkg_lock_link_");
            FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_show_package_lock_close_btn_"), false);
            
            FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_enable_dev_id_check_"), false);
            FaQinDeMiaoHaoKeAi._dev_id_card_link_ = (String) sharezhifuBackup.get("_dev_id_card_link_");
            
            FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = (boolean) Objects.requireNonNullElse(sharezhifuBackup.get("_enable_dev_card_key_check_"), false);
            // sharezhifu._dev_card_key_prompt_msg_ = (String) sharezhifuBackup.get("_dev_card_key_prompt_msg_");
            Log.d(ONCREATE_TAG, "restoreShareZhiFuFields: Khôi phục từ backup map hoàn tất");
        } else {
            Log.d(ONCREATE_TAG, "restoreShareZhiFuFields: Backup map trống, không có gì để khôi phục");
        }
        sharezhifuBackup.clear(); // Clear backup after restoring
    }

    private void showDebugOptionsDialog() {
        Log.d(ONCREATE_TAG, "showDebugOptionsDialog: Bắt đầu");
        Toast.makeText(this, "showDebugOptionsDialog called!", Toast.LENGTH_SHORT).show();
        final CharSequence[] items = {"调教", "郊狼", "喷水", "震动", "炮机", "春药", "舔舐乳头"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Debug Panel: Select Dialog Type");
        builder.setItems(items, (dialog, item) -> {
            Log.d(ONCREATE_TAG, "showDebugOptionsDialog: Item selected - " + items[item]);
            // Backup fields BEFORE showing a specific test dialog that might modify them.
            backupShareZhiFuFields(); 

            if (items[item].equals("调教")) {
                showNormalPopupDebugDialog(dialog); // Pass dialog to dismiss later
            } else if (items[item].equals("郊狼")) {
                showForceUpdateDebugDialog(dialog); // Pass dialog to dismiss later
            } else if (items[item].equals("喷水")) {
                 FaQinDeMiaoHaoKeAi._enable_sig_ver_ = true; // Temporarily set for test
                 DialogLiQiuting.showSignatureFailureDialog(MainActivity.this); // Assuming this shows and dismisses itself
                 // No direct restore here, rely on the main dialog's cancel or a more specific mechanism if needed.
                 // Consider if showSignatureFailureDialog needs an onDismiss to restore _enable_sig_ver_
                 dialog.dismiss(); // Dismiss the options dialog
            } else if (items[item].equals("震动")) {
                showPackageLockDebugDialog(dialog);
            } else if (items[item].equals("炮机")) {
                showDeviceIdRestrictedDebugDialog(dialog);
            } else if (items[item].equals("春药")) {
                showCardKeyInputDebugDialog(dialog);
            } else if (items[item].equals("舔舐奶")) {
                 QQstart.showInitialOfflineDialog(MainActivity.this); // Assuming self-managing
                 dialog.dismiss(); // Dismiss the options dialog
            }
        });
        
        builder.setNegativeButton("Cancel & Restore All", (dialog, which) -> {
            Log.d(ONCREATE_TAG, "showDebugOptionsDialog: Cancel button clicked");
            restoreShareZhiFuFields(); 
            Toast.makeText(MainActivity.this, "Settings restored. Panel cancelled.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        
        Log.d(ONCREATE_TAG, "showDebugOptionsDialog: Chuẩn bị hiển thị dialog");
        Toast.makeText(this, "Attempting to show debug options dialog...", Toast.LENGTH_SHORT).show();
        builder.show();
        Log.d(ONCREATE_TAG, "showDebugOptionsDialog: Dialog đã được yêu cầu hiển thị");
    }

    // Helper to add EditText to a layout, used by specific dialogs
    private EditText addEditText(LinearLayout layout, String hint, String initialValue) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setText(initialValue != null ? initialValue : ""); // Handle null initialValue
        layout.addView(editText);
        return editText;
    }
    
    private EditText addNumericEditText(LinearLayout layout, String hint, int initialValue) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setText(String.valueOf(initialValue));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(editText);
        return editText;
    }

    private CheckBox addCheckBox(LinearLayout layout, String text, boolean initialValue) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(text);
        checkBox.setChecked(initialValue);
        layout.addView(checkBox);
        return checkBox;
    }

    // --- Implementations for specific debug dialogs ---
    // (Ensure these methods call restoreShareZhiFuFields() in their onDismiss listeners)

    private void showNormalPopupDebugDialog(DialogInterface optionsDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Debug: Normal Popup");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Add EditTexts for sharezhifu._popup_title_, _dyn_content_, etc.
        // Example:
        final EditText titleEt = addEditText(layout, "Popup Title", FaQinDeMiaoHaoKeAi._popup_title_);
        final EditText contentEt = addEditText(layout, "Dynamic Content", FaQinDeMiaoHaoKeAi._dyn_content_);
        // ... add more EditTexts and CheckBoxes for other relevant fields ...

        builder.setView(layout);
        builder.setPositiveButton("Show Popup", (d, w) -> {
            // Apply new values from EditTexts to sharezhifu fields
            FaQinDeMiaoHaoKeAi._popup_title_ = titleEt.getText().toString();
            FaQinDeMiaoHaoKeAi._dyn_content_ = contentEt.getText().toString();
            // ... apply other fields ...
            
            // Moonlight.originalPopupLogic(this); // Assuming this is the method to test
            DialogLauncher.Thrym(this); // Or whatever shows the normal popup
            optionsDialog.dismiss(); // Dismiss the main options dialog
        });
        builder.setNegativeButton("Cancel", (d, w) -> {
            // No need to apply, fields were already backed up. Restore them.
            d.dismiss();
        });
        builder.setOnDismissListener(d -> restoreShareZhiFuFields()); // Always restore on dismiss
        builder.show();
    }

    private void showForceUpdateDebugDialog(DialogInterface optionsDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Debug: Force Update");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText contentEt = addEditText(layout, "Force Update Content", FaQinDeMiaoHaoKeAi._force_upd_content_);
        final EditText linkEt = addEditText(layout, "Force Update Link", FaQinDeMiaoHaoKeAi._force_upd_link_);
        final CheckBox showCloseCb = addCheckBox(layout, "Show Close Button", FaQinDeMiaoHaoKeAi._show_force_update_close_btn_);
        final CheckBox forceReqCb = addCheckBox(layout, "Force Update Required", FaQinDeMiaoHaoKeAi._force_upd_req_);

        builder.setView(layout);
        builder.setPositiveButton("Show Dialog", (d, w) -> {
            FaQinDeMiaoHaoKeAi._force_upd_content_ = contentEt.getText().toString();
            FaQinDeMiaoHaoKeAi._force_upd_link_ = linkEt.getText().toString();
            FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ = showCloseCb.isChecked();
            FaQinDeMiaoHaoKeAi._force_upd_req_ = forceReqCb.isChecked();
            
            DialogLiQiuting.showForceUpdateDialog(MainActivity.this); // Assuming this shows the dialog
            optionsDialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());
        builder.setOnDismissListener(d -> restoreShareZhiFuFields());
        builder.show();
    }
    
    private void showPackageLockDebugDialog(DialogInterface optionsDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Debug: Package Locked");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText contentEt = addEditText(layout, "Package Lock Content", FaQinDeMiaoHaoKeAi._pkg_lock_content_);
        final EditText linkEt = addEditText(layout, "Package Lock Link", FaQinDeMiaoHaoKeAi._pkg_lock_link_);
        // Add EditText for _pkg_lock_map_ if you want to customize it (complex)
        // For simplicity, we'll use the existing map or a default test one.

        builder.setView(layout);
        builder.setPositiveButton("Show Dialog", (d, w) -> {
            FaQinDeMiaoHaoKeAi._pkg_lock_content_ = contentEt.getText().toString();
            FaQinDeMiaoHaoKeAi._pkg_lock_link_ = linkEt.getText().toString();
            // sharezhifu._pkg_lock_map_ can be set here if needed for test
            
            DialogLiQiuting.showPackageLockedDialog(MainActivity.this, FaQinDeMiaoHaoKeAi._pkg_lock_content_, FaQinDeMiaoHaoKeAi._pkg_lock_link_);
            optionsDialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());
        builder.setOnDismissListener(d -> restoreShareZhiFuFields());
        builder.show();
    }

    private void showDeviceIdRestrictedDebugDialog(DialogInterface optionsDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Debug: Device ID Restricted");
        // For this test, you might just enable it and provide a dummy link
        FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = true;
        FaQinDeMiaoHaoKeAi._dev_id_card_link_ = "https://example.com/device_id_info_debug"; // Dummy link for test

        // Show info
        TextView infoTv = new TextView(this);
        infoTv.setText("Device ID Check will be enabled. Link: " + FaQinDeMiaoHaoKeAi._dev_id_card_link_ + "\n(Actual device ID list not customized here)");
        infoTv.setPadding(20,20,20,20);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(infoTv);
        builder.setView(layout);

        builder.setPositiveButton("Show Dialog", (d, w) -> {
            DialogLiQiuting.showDeviceIdRestrictedDialog(MainActivity.this);
            optionsDialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());
        builder.setOnDismissListener(d -> {
            // Specific restoration for this test if _enable_dev_id_check_ was changed
            // The generic restoreShareZhiFuFields will handle it if it was backed up.
            restoreShareZhiFuFields(); 
        });
        builder.show();
    }

    private void showCardKeyInputDebugDialog(DialogInterface optionsDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Debug: Card Key Input");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Assuming _dev_card_key_prompt_msg_ exists in sharezhifu
        final EditText promptEt = addEditText(layout, "Card Key Prompt", FaQinDeMiaoHaoKeAi._dev_card_key_prompt_msg_);
        
        builder.setView(layout);
        builder.setPositiveButton("Show Dialog", (d, w) -> {
            if (promptEt != null) { // Check if promptEt was successfully added/exists
                 FaQinDeMiaoHaoKeAi._dev_card_key_prompt_msg_ = promptEt.getText().toString();
            }
            FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = true; // Enable for test
            
            DialogLiQiuting.showCardKeyInputDialog(MainActivity.this);
            optionsDialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());
        builder.setOnDismissListener(d -> restoreShareZhiFuFields());
        builder.show();
    }
}
