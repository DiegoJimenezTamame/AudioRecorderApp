package at.wifi.swdev.audiorecorderapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import at.wifi.swdev.audiorecorderapp.Fragments.DrumpadFragment;
import at.wifi.swdev.audiorecorderapp.R;

public class EditSoundDialog extends DialogFragment {

    private boolean isLooping;
    private int longClickedButtonIndex = -1;
    private View dialogView;
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

    private int buttonIndex;

    private Map<Integer, Float> playbackSpeedMap = new HashMap<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_edit_sound, null);

        // Find the toggle button for looping
        ImageButton loopImageButton = dialogView.findViewById(R.id.loop_image_button);
        updateLoopingButtonState(loopImageButton);
        loopImageButton.setOnClickListener(v -> {
            // Toggle the state
            isLooping = !isLooping;
            updateLoopingButtonState(loopImageButton);
            setLooping(isLooping);
        }); // Set initial state

        ImageButton playbackSpeedButton = dialogView.findViewById(R.id.playback_speed_button);
        playbackSpeedButton.setOnClickListener(v -> showPlaybackSpeedDialog());

        ImageButton colorPalette = dialogView.findViewById(R.id.color_palette);
        colorPalette.setOnClickListener(v -> showColorSelectionDialog());

        ImageButton loadSoundButton = dialogView.findViewById(R.id.load_sound_button);
        loadSoundButton.setOnClickListener(v -> {
            // Load sounds from internal storage
            ArrayList<File> soundFiles = getSoundFilesFromInternalStorage();

            // Display a dialog to choose a sound file
            AlertDialog.Builder fileChooserBuilder = new AlertDialog.Builder(requireContext());
            fileChooserBuilder.setTitle("Choose a sound");
            String[] soundNames = new String[soundFiles.size()];
            for (int i = 0; i < soundFiles.size(); i++) {
                soundNames[i] = soundFiles.get(i).getName();
            }
            fileChooserBuilder.setItems(soundNames, (dialog, which) -> {
                // Pass the selected sound file's path back to DrumpadFragment
                File selectedFile = soundFiles.get(which);
                String filePath = selectedFile.getAbsolutePath();
                DrumpadFragment drumpadFragment = (DrumpadFragment) getParentFragment();
                if (drumpadFragment != null) {
                    drumpadFragment.loadSound(filePath, buttonIndex);
                }
            });
            fileChooserBuilder.show();
        });

        builder.setView(dialogView)
                .setPositiveButton("Apply", (dialog, id) -> applyChanges())
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        return builder.create();
    }

    public void setButtonIndex(int index) {
        buttonIndex = index;
    }
    public void showColorSelectionDialog() {
        ColorSelectionDialog dialog = new ColorSelectionDialog();
        dialog.setOnColorSelectedListener(color -> {
            int drawableResId = getColorDrawableResource(color);
            if (drawableResId != 0) {
                // Update the image resource in the Drumpad Fragment using the longClickedButtonIndex
                DrumpadFragment drumpadFragment = (DrumpadFragment) getParentFragment();
                if (drumpadFragment != null) {
                    drumpadFragment.updateButtonImage(longClickedButtonIndex, drawableResId);
                }
            }
        });
        dialog.show(getChildFragmentManager(), "ColorSelectionDialog");
    }
    private int getColorDrawableResource(String color){
        switch (color){
            case "Blue":
                return R.drawable.blue_button;
            case "Green":
                return R.drawable.green_button;
            case "Pink":
                return R.drawable.pink_button;
            case "Orange":
                return R.drawable.orange_button;
            case "Purple":
                return R.drawable.purple_button;
            default:
                return 0; // Return 0 or handle other cases as needed
        }
    }

    // Method to retrieve available sounds from internal storage
    private ArrayList<File> getSoundFilesFromInternalStorage() {
        ArrayList<File> soundFiles = new ArrayList<>();
        // Get the directory where sound files are stored
        File directory = new File(requireContext().getFilesDir(), "/AudioRecorderApp");
        // Check if the directory exists
        if (directory.exists() && directory.isDirectory()) {
            // List all files in the directory
            File[] files = directory.listFiles();
            // Check if any files were found
            if (files != null) {
                // Iterate through the files
                for (File file : files) {
                    // Check if the file is a sound file
                    if (file.getName().toLowerCase().endsWith(".amr")) {
                        soundFiles.add(file);
                    }
                }
            }
        }
        return soundFiles;
    }
    public void setLongClickedButtonIndex(int index) {
        longClickedButtonIndex = index;
    }
    private void showPlaybackSpeedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_playback_speed, null);
        SeekBar playbackSpeedSlider = dialogView.findViewById(R.id.playback_speed_slider);

        playbackSpeedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Adjust the playback speed
                float speed = (float) progress / 100;
                // Update the playback speed for the specific button
                playbackSpeedMap.put(buttonIndex, speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        builder.setView(dialogView)
                .setTitle("Adjust Playback Speed")
                .setPositiveButton("Apply", (dialog, which) -> {
                    // Apply the selected playback speed
                    DrumpadFragment drumpadFragment = (DrumpadFragment) getParentFragment();
                    if (drumpadFragment != null) {
                        drumpadFragment.applyPlaybackSpeed();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void applyChanges() {
        dismiss();
        Fragment parentFragment = getTargetFragment(); // Retrieve the target fragment (DrumpadFragment)
        if (parentFragment instanceof DrumpadFragment) {
            DrumpadFragment drumpadFragment = (DrumpadFragment) parentFragment;
            // Apply changes to the behavior of the long-clicked button
            if (longClickedButtonIndex != -1) {
                drumpadFragment.updateLooping(longClickedButtonIndex, isLooping);
                // Apply playback speed changes
                drumpadFragment.applyPlaybackSpeed();
            }
        }
    }
    public void setLooping(boolean looping){
        //Default looping state
        isLooping = false;
    }

    public void updateLoopingButtonState(ImageButton button){
        if(isLooping) {
            button.setImageResource(R.drawable.ic_loop_on); // Change to the looping icon
        }else{
            button.setImageResource(R.drawable.ic_loop_off);
        }
    }

}
