package at.wifi.swdev.audiorecorderapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import at.wifi.swdev.audiorecorderapp.Fragments.DrumpadFragment;

public class EditSoundDialog extends DialogFragment {

    private boolean isLooping = false;
    private boolean loopingState = false; //Default looping state

    private final String[] colorOptions = {
            "Blue", "Green", "Pink", "Orange", "Purple"
    };

    private final int[] colorResources = {
            R.drawable.blue_button,
            R.drawable.green_button,
            R.drawable.pink_button,
            R.drawable.orange_button,
            R.drawable.purple_button
    };

    public interface ColorSelectionListener{
        void onColorSelected(int colorResId);
    }
    // Declare listener variable
    private ColorSelectionListener colorSelectionListener;

    // Set listener method
    public void setColorSelectionListener(ColorSelectionListener listener) {
        this.colorSelectionListener = listener;
    }

    // When a color is selected, notify the listener
    private void notifyColorSelected(int colorResId) {
        if (colorSelectionListener != null) {
            colorSelectionListener.onColorSelected(colorResId);
        }
    }

    // This method will be called when a color is selected
    private void onColorItemSelected(int colorResId) {
        // Notify the listener with the selected color
        notifyColorSelected(colorResId);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_sound, null);

        // Find the toggle button for looping
        ImageButton loopImageButton = dialogView.findViewById(R.id.loop_image_button);
        updateLoopingButtonState(loopImageButton);
        loopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the state
                isLooping = !isLooping;
                updateLoopingButtonState(loopImageButton);
            }
        }); // Set initial state

        /*ImageButton colorButton = dialogView.findViewById(R.id.color_palette);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a new dialog for selecting colors
                showColorSelectionDialog();
            }
        });*/


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

    public void updateLoopingButtonState(ImageButton button){
        if (isLooping) {
            button.setImageResource(R.drawable.ic_loop_on); // Change to the looping icon
        }else{
            button.setImageResource(R.drawable.ic_loop_off);
        }
    }
}
