package at.wifi.swdev.audiorecorderapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;

import at.wifi.swdev.audiorecorderapp.Fragments.DrumpadFragment;

public class EditSoundDialog extends DialogFragment {

    private boolean isLooping;
    private int longClickedButtonIndex = -1;

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


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_sound, null);

        // Find the toggle button for looping
        ImageButton loopImageButton = dialogView.findViewById(R.id.loop_image_button);
        updateLoopingButtonState(loopImageButton);
        loopImageButton.setOnClickListener(v -> {
            // Toggle the state
            isLooping = !isLooping;
            updateLoopingButtonState(loopImageButton);
        }); // Set initial state

        ImageButton colorPalette = dialogView.findViewById(R.id.color_palette);
        colorPalette.setOnClickListener(v -> showColorSelectionDialog());
        ImageButton updateColorButton; // Declare updateColorButton variable

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
                    drumpadFragment.loadSound(filePath);
                }
            });
            fileChooserBuilder.show();
        });

        builder.setView(dialogView)
                .setPositiveButton("Apply", (dialog, id) -> applyChanges())
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        return builder.create();
    }

    private void showColorSelectionDialog() {
        ColorSelectionDialog dialog = new ColorSelectionDialog();
        dialog.setOnColorSelectedListener(color -> {
            ImageButton updateColor = getView().findViewById(R.id.color_list_view);
            int drawableResId = getColorDrawableResource(color);
            if (drawableResId != 0) {
                updateColor.setImageResource(drawableResId);
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

    private void showLoadSoundDialog() {
        // Retrieve available sounds from internal storage
        ArrayList<File> soundFiles = getSoundFilesFromInternalStorage();

        // Create and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Sound");
        String[] soundNames = new String[soundFiles.size()];
        for (int i = 0; i < soundFiles.size(); i++) {
            soundNames[i] = soundFiles.get(i).getName();
        }
        builder.setItems(soundNames, (dialog, which) -> {
            // Load selected sound
            // Implement your logic here to load the selected sound into SoundPool
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
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
                    // Check if the file is a sound file (you can use any criteria to identify sound files)
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
    private void applyChanges() {
        dismiss();
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DrumpadFragment) {
            DrumpadFragment drumpadFragment = (DrumpadFragment) parentFragment;
            // Apply changes to the behavior of the long-clicked button
            if (longClickedButtonIndex != -1) {
                drumpadFragment.updateLooping(longClickedButtonIndex, isLooping);
            }
        }
    }
    public void setLooping(boolean looping){
        //Default looping state
        isLooping = false;
    }

    public void updateLoopingButtonState(ImageButton button){
        if (isLooping) {
            button.setImageResource(R.drawable.ic_loop_on); // Change to the looping icon
        }else{
            button.setImageResource(R.drawable.ic_loop_off);
        }
    }

}
