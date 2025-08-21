package bbs.yuchen.icu.ui.dashboard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import bbs.yuchen.icu.DialogLiQiuting;
import bbs.yuchen.icu.QQstart;
// It seems your sharezhifu class is in the default package or bbs.yuchen.icu
// Ensure this import is correct based on your project structure.
import bbs.yuchen.icu.FaQinDeMiaoHaoKeAi;
import bbs.yuchen.icu.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DevPanelDashboard";
    private FragmentDashboardBinding binding;
    private final Map<String, Object> originalShareZhiFuValues = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewBinding guarantees developerOptionsButton is not null if ID exists in XML
        // and has been correctly processed by the build.
        if (binding.developerOptionsButton == null) {
            Log.e(TAG, "Developer Options button not found in binding. Check fragment_dashboard.xml and binding class generation.");
            Toast.makeText(getContext(), "Error: Dev button not found in layout.", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Developer Options button found in binding.");
        binding.developerOptionsButton.setOnClickListener(v -> {
            Log.d(TAG, "Developer Options button clicked.");
            showDeveloperDialogOptions();
        });
        Toast.makeText(getContext(), "主人...喵喵的屁股已经准备好了呢", Toast.LENGTH_SHORT).show();
    }

    private void showDeveloperDialogOptions() {
        if (getContext() == null) {
            Log.e(TAG, "Context is null, cannot show developer dialog options.");
            return;
        }
        Log.d(TAG, "Showing developer dialog options...");

        final CharSequence[] items = {
                "Test Normal Popup (Moonlight.originalPopupLogic)",
                "Test Force Update Dialog (changku.showForceUpdateDialog)",
                "Test Signature Failure Dialog (changku.showSignatureFailureDialog)",
                "Test Package Locked Dialog (QQstart.showPackageLockedDialog)",
                "Test Device ID Restricted Dialog (changku.showDeviceIdRestrictedDialog)",
                "Test Card Key Input Dialog (changku.showCardKeyInputDialog)",
                "Test Initial Offline Dialog (QQstart.showInitialOfflineDialog)",
                "Restore All Original Configs",
                "Cancel"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Dialog to Test");
        builder.setItems(items, (dialog, item) -> {
            backupShareZhiFuFields(); // Backup before any test
            switch (item) {
                case 0:
                    showNormalPopupDebugDialog();
                    break;
                case 1:
                    showForceUpdateDebugDialog();
                    break;
                case 2:
                    testSignatureFailureDialog();
                    break;
                case 3:
                    showPackageLockDebugDialog();
                    break;
                case 4:
                    testDeviceIdRestrictedDialog();
                    break;
                case 5:
                    showCardKeyInputDebugDialog();
                    break;
                case 6:
                    testInitialOfflineDialog();
                    break;
                case 7: // Restore All
                    Log.d(TAG, "User selected Restore All Original Configs.");
                    restoreShareZhiFuFields();
                    Toast.makeText(getContext(), "Original configs restored.", Toast.LENGTH_SHORT).show();
                    break;
                case 8: // Cancel
                    Log.d(TAG, "Developer options dialog cancelled.");
                    restoreShareZhiFuFields(); // Also restore on cancel
                    dialog.dismiss();
                    break;
            }
        });
        // Safety net: restore if the main options dialog is dismissed for any other reason
        builder.setOnDismissListener(dialogInterface -> {
            Log.d(TAG, "Main developer options dialog dismissed. Ensuring config restoration.");
            // No need to call restore here if each path handles its own or if only "Restore All" and "Cancel" should restore.
            // For now, let individual test paths manage their state or have the explicit restore option.
        });
        builder.show();
    }

    private EditText addEditText(LinearLayout layout, String hint, String initialText) {
        EditText editText = new EditText(getContext());
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setHint(hint);
        if (initialText != null) {
            editText.setText(initialText);
        }
        layout.addView(editText);
        return editText;
    }

    // --- Backup and Restore ---
    private void backupShareZhiFuFields() {
        Log.d(TAG, "Backing up sharezhifu fields...");
        originalShareZhiFuValues.clear();
        try {
            // Fields from your sharezhifu.java
            originalShareZhiFuValues.put("_dev_card_key_prompt_msg_", FaQinDeMiaoHaoKeAi._dev_card_key_prompt_msg_);
            originalShareZhiFuValues.put("_current_dialog_bg_", FaQinDeMiaoHaoKeAi._current_dialog_bg_);
            originalShareZhiFuValues.put("_current_text_color_", FaQinDeMiaoHaoKeAi._current_text_color_);
            originalShareZhiFuValues.put("_dialog_version_", FaQinDeMiaoHaoKeAi._dialog_version_);
            originalShareZhiFuValues.put("_dyn_content_", FaQinDeMiaoHaoKeAi._dyn_content_);
            originalShareZhiFuValues.put("_force_upd_link_", FaQinDeMiaoHaoKeAi._force_upd_link_);
            originalShareZhiFuValues.put("_tg_channel_link_", FaQinDeMiaoHaoKeAi._tg_channel_link_);
            originalShareZhiFuValues.put("_qq_group_num_", FaQinDeMiaoHaoKeAi._qq_group_num_);
            originalShareZhiFuValues.put("_popup_title_", FaQinDeMiaoHaoKeAi._popup_title_);
            originalShareZhiFuValues.put("_force_upd_content_", FaQinDeMiaoHaoKeAi._force_upd_content_);
            originalShareZhiFuValues.put("_force_upd_req_", FaQinDeMiaoHaoKeAi._force_upd_req_);
            originalShareZhiFuValues.put("_show_close_btn_", FaQinDeMiaoHaoKeAi._show_close_btn_);
            originalShareZhiFuValues.put("_show_qq_btn_", FaQinDeMiaoHaoKeAi._show_qq_btn_);
            originalShareZhiFuValues.put("_show_tg_btn_", FaQinDeMiaoHaoKeAi._show_tg_btn_);
            originalShareZhiFuValues.put("_enable_sig_ver_", FaQinDeMiaoHaoKeAi._enable_sig_ver_);
            originalShareZhiFuValues.put("_enable_dev_id_check_", FaQinDeMiaoHaoKeAi._enable_dev_id_check_);
            originalShareZhiFuValues.put("_enable_dev_card_key_check_", FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_);
            originalShareZhiFuValues.put("_pkg_lock_content_", FaQinDeMiaoHaoKeAi._pkg_lock_content_);
            originalShareZhiFuValues.put("_pkg_lock_link_", FaQinDeMiaoHaoKeAi._pkg_lock_link_);
            originalShareZhiFuValues.put("_show_force_update_close_btn_", FaQinDeMiaoHaoKeAi._show_force_update_close_btn_);
            originalShareZhiFuValues.put("_show_package_lock_close_btn_", FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_);
            originalShareZhiFuValues.put("_show_neutral_btn_", FaQinDeMiaoHaoKeAi._show_neutral_btn_);
            originalShareZhiFuValues.put("_neutral_btn_content_", FaQinDeMiaoHaoKeAi._neutral_btn_content_);

            // Note: Map and List fields require deep copies if their contents might be modified
            // For now, assuming direct modification and restoration. If they are modified in place,
            // this backup won't be effective for their internal state.
            // Example for a Map (if needed for other fields not shown as public static in your snippet):
            // if (sharezhifu._pkg_lock_map_ != null) {
            //      originalShareZhiFuValues.put("_pkg_lock_map_", new HashMap<>(sharezhifu._pkg_lock_map_));
            // }


            Log.d(TAG, "sharezhifu fields backup complete. Count: " + originalShareZhiFuValues.size());
        } catch (Exception e) {
            Log.e(TAG, "Error during backupShareZhiFuFields", e);
            if (getContext() != null) Toast.makeText(getContext(), "Error backing up configs", Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreShareZhiFuFields() {
        Log.d(TAG, "Restoring sharezhifu fields...");
        if (originalShareZhiFuValues.isEmpty()) {
            Log.w(TAG, "No values in backup to restore.");
            return;
        }
        try {
            // Restore fields that were backed up
            FaQinDeMiaoHaoKeAi._dev_card_key_prompt_msg_ = (String) originalShareZhiFuValues.get("_dev_card_key_prompt_msg_");
            FaQinDeMiaoHaoKeAi._current_dialog_bg_ = (int) Objects.requireNonNullElse(originalShareZhiFuValues.get("_current_dialog_bg_"), FaQinDeMiaoHaoKeAi._DIALOG_BG_DAY_); // Assuming _DIALOG_BG_DAY_ is a valid default
            FaQinDeMiaoHaoKeAi._current_text_color_ = (int) Objects.requireNonNullElse(originalShareZhiFuValues.get("_current_text_color_"), FaQinDeMiaoHaoKeAi._TEXT_COLOR_DAY_); // Assuming _TEXT_COLOR_DAY_ is a valid default
            FaQinDeMiaoHaoKeAi._dialog_version_ = (int) Objects.requireNonNullElse(originalShareZhiFuValues.get("_dialog_version_"), 1);
            FaQinDeMiaoHaoKeAi._dyn_content_ = (String) originalShareZhiFuValues.get("_dyn_content_");
            FaQinDeMiaoHaoKeAi._force_upd_link_ = (String) originalShareZhiFuValues.get("_force_upd_link_");
            FaQinDeMiaoHaoKeAi._tg_channel_link_ = (String) originalShareZhiFuValues.get("_tg_channel_link_");
            FaQinDeMiaoHaoKeAi._qq_group_num_ = (String) originalShareZhiFuValues.get("_qq_group_num_");
            FaQinDeMiaoHaoKeAi._popup_title_ = (String) originalShareZhiFuValues.get("_popup_title_");
            FaQinDeMiaoHaoKeAi._force_upd_content_ = (String) originalShareZhiFuValues.get("_force_upd_content_");
            FaQinDeMiaoHaoKeAi._force_upd_req_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_force_upd_req_"), false);
            FaQinDeMiaoHaoKeAi._show_close_btn_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_show_close_btn_"), false);
            FaQinDeMiaoHaoKeAi._show_qq_btn_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_show_qq_btn_"), false);
            FaQinDeMiaoHaoKeAi._show_tg_btn_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_show_tg_btn_"), false);
            FaQinDeMiaoHaoKeAi._enable_sig_ver_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_enable_sig_ver_"), false);
            FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_enable_dev_id_check_"), false);
            FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_enable_dev_card_key_check_"), false);
            FaQinDeMiaoHaoKeAi._pkg_lock_content_ = (String) originalShareZhiFuValues.get("_pkg_lock_content_");
            FaQinDeMiaoHaoKeAi._pkg_lock_link_ = (String) originalShareZhiFuValues.get("_pkg_lock_link_");
            FaQinDeMiaoHaoKeAi._show_force_update_close_btn_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_show_force_update_close_btn_"), false);
            FaQinDeMiaoHaoKeAi._show_package_lock_close_btn_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_show_package_lock_close_btn_"), false);
            FaQinDeMiaoHaoKeAi._show_neutral_btn_ = (boolean) Objects.requireNonNullElse(originalShareZhiFuValues.get("_show_neutral_btn_"), false);
            FaQinDeMiaoHaoKeAi._neutral_btn_content_ = (String) originalShareZhiFuValues.get("_neutral_btn_content_");

            // Example for restoring a Map (if it was deep copied)
            // if (originalShareZhiFuValues.containsKey("_pkg_lock_map_")) {
            //    sharezhifu._pkg_lock_map_ = new HashMap<>((Map<String, Boolean>) originalShareZhiFuValues.get("_pkg_lock_map_"));
            // }

            Log.d(TAG, "sharezhifu fields restored.");
            if (getContext() != null) Toast.makeText(getContext(), "Configs Restored", Toast.LENGTH_SHORT).show();
            originalShareZhiFuValues.clear(); // Clear backup after restoring
        } catch (Exception e) {
            Log.e(TAG, "Error during restoreShareZhiFuFields", e);
            if (getContext() != null) Toast.makeText(getContext(), "Error restoring configs", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Dialog Test Methods ---

    private void showNormalPopupDebugDialog() {
        if (getContext() == null || getActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Test Normal Popup");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final EditText titleEt = addEditText(layout, "Popup Title", FaQinDeMiaoHaoKeAi._popup_title_);
        final EditText contentEt = addEditText(layout, "Dynamic Content", FaQinDeMiaoHaoKeAi._dyn_content_);

        builder.setView(layout);
        builder.setPositiveButton("Show Dialog", (dialog, which) -> {
            FaQinDeMiaoHaoKeAi._popup_title_ = titleEt.getText().toString();
            FaQinDeMiaoHaoKeAi._dyn_content_ = contentEt.getText().toString();
            DialogLiQiuting.originalPopupLogic(getActivity());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setOnDismissListener(d -> restoreShareZhiFuFields());
        builder.show();
    }

    private void showForceUpdateDebugDialog() {
        if (getContext() == null || getActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Test Force Update Dialog");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final EditText contentEt = addEditText(layout, "Force Update Content", FaQinDeMiaoHaoKeAi._force_upd_content_);
        final EditText linkEt = addEditText(layout, "Force Update Link", FaQinDeMiaoHaoKeAi._force_upd_link_);
        // You could add a switch for sharezhifu._force_upd_req_ here if needed

        builder.setView(layout);
        builder.setPositiveButton("Show Dialog", (dialog, which) -> {
            FaQinDeMiaoHaoKeAi._force_upd_content_ = contentEt.getText().toString();
            FaQinDeMiaoHaoKeAi._force_upd_link_ = linkEt.getText().toString();
            FaQinDeMiaoHaoKeAi._force_upd_req_ = true; // Ensure it's treated as required for test
            DialogLiQiuting.showForceUpdateDialog(getActivity());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setOnDismissListener(d -> restoreShareZhiFuFields());
        builder.show();
    }

    private void testSignatureFailureDialog() {
        if (getActivity() == null) return;
        Log.d(TAG, "Testing Signature Failure Dialog. Original _enable_sig_ver_: " + FaQinDeMiaoHaoKeAi._enable_sig_ver_);
        FaQinDeMiaoHaoKeAi._enable_sig_ver_ = true; // Temporarily enable for test
        DialogLiQiuting.showSignatureFailureDialog(getActivity());
        // No separate parameter dialog, so restore happens when main options dialog is dismissed or "Restore All" is clicked.
        // Or, more robustly, schedule a delayed restore if this dialog is non-blocking or if a global restore is preferred.
        // For now, relying on the main options dialog's dismiss or explicit restore.
        // If this dialog is modal and blocks, the restore will happen when the user navigates away or explicitly restores.
        // To be safer, we can restore immediately after the call if it's a one-shot display test.
        // However, if the dialog itself relies on the flag remaining true during its lifecycle,
        // immediate restore is bad. This depends on changku.showSignatureFailureDialog's behavior.
        // A common pattern is to show a parameter dialog which then restores on its own dismiss.
        // Since this one has no params, we restore here for simplicity IF it's a quick test.
        // Given it's a test, making it explicit:
        // restoreShareZhiFuFields(); // This would revert _enable_sig_ver_ immediately.
        // For now, let's assume the user will see it, then click "Restore All" or navigate away.
        // The backupShareZhiFuFields was called before this method.
        Toast.makeText(getContext(), "Signature Failure Dialog test initiated. Restore configs manually or on exit.", Toast.LENGTH_LONG).show();
    }


    private void showPackageLockDebugDialog() {
        if (getContext() == null || getActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Test Package Locked Dialog");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final EditText contentEt = addEditText(layout, "Package Lock Content", FaQinDeMiaoHaoKeAi._pkg_lock_content_);
        final EditText linkEt = addEditText(layout, "Package Lock Link", FaQinDeMiaoHaoKeAi._pkg_lock_link_);

        builder.setView(layout);
        builder.setPositiveButton("Show Dialog", (dialog, which) -> {
            // QQstart.showPackageLockedDialog takes content and link directly
            DialogLiQiuting.showPackageLockedDialog(getActivity(), contentEt.getText().toString(), linkEt.getText().toString());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setOnDismissListener(d -> restoreShareZhiFuFields()); // Restore any other sharezhifu fields that might have been changed
        builder.show();
    }


    private void testDeviceIdRestrictedDialog() {
        if (getActivity() == null) return;
        Log.d(TAG, "Testing Device ID Restricted Dialog. Original _enable_dev_id_check_: " + FaQinDeMiaoHaoKeAi._enable_dev_id_check_);
        FaQinDeMiaoHaoKeAi._enable_dev_id_check_ = true; // Temporarily enable for test
        DialogLiQiuting.showDeviceIdRestrictedDialog(getActivity());
        Toast.makeText(getContext(), "Device ID Restricted Dialog test initiated. Restore configs manually or on exit.", Toast.LENGTH_LONG).show();
    }

    private void showCardKeyInputDebugDialog() {
         if (getContext() == null || getActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Test Card Key Input Dialog");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final EditText promptEt = addEditText(layout, "Card Key Prompt Message", FaQinDeMiaoHaoKeAi._dev_card_key_prompt_msg_);

        builder.setView(layout);
        builder.setPositiveButton("Show Dialog", (dialog, which) -> {
            FaQinDeMiaoHaoKeAi._dev_card_key_prompt_msg_ = promptEt.getText().toString();
            FaQinDeMiaoHaoKeAi._enable_dev_card_key_check_ = true; // Ensure it's enabled for test
            DialogLiQiuting.showCardKeyInputDialog(getActivity());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setOnDismissListener(d -> restoreShareZhiFuFields());
        builder.show();
    }

    private void testInitialOfflineDialog() {
        if (getActivity() == null) return;
        // Assuming _enable_initial_offline_dialog_ is a field in sharezhifu, though not in your provided snippet.
        // If it's always shown or controlled by other logic, this direct call is fine.
        // If there's a flag like:
        // sharezhifu._enable_initial_offline_dialog_ = true; // Temporarily enable for test
        QQstart.showInitialOfflineDialog(getActivity());
        Toast.makeText(getContext(), "Initial Offline Dialog test initiated. Restore configs manually or on exit.", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called. Ensuring config restoration.");
        restoreShareZhiFuFields(); // Ensure configs are restored when view is destroyed
        binding = null;
    }
}
