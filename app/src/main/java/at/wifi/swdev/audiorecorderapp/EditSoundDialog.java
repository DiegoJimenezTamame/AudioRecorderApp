package at.wifi.swdev.audiorecorderapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import at.wifi.swdev.audiorecorderapp.Fragments.DrumpadFragment;

public class EditSoundDialog extends DialogFragment {

    private boolean isLooping = false;
    private boolean loopingState = false; //Default looping state

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_sound, null);

        // Find the toggle button for looping
        ToggleButton loopToggleButton = dialogView.findViewById(R.id.loop_toggle_button);
        loopToggleButton.setChecked(isLooping); // Set initial state

        loopToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isLooping = isChecked; // Update looping state
        });

        builder.setView(dialogView)
                .setPositiveButton("Apply", (dialog, id) -> applyChanges())
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        return builder.create();
    }

    private void applyChanges() {
        // Apply changes
        dismiss(); // Dismiss the dialog
        // Pass the looping state back to the fragment if parent fragment is not null
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DrumpadFragment) {
            ((DrumpadFragment) parentFragment).updateLooping(isLooping);
        }
            // Log a warning or handle the case where the parent fragment is not DrumpadFragment

    }

    public void setLooping(boolean looping){
        loopingState = looping;
    }
}
