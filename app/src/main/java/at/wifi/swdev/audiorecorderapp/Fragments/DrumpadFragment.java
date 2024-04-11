package at.wifi.swdev.audiorecorderapp.Fragments;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.viewmodel.CreationExtras;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import at.wifi.swdev.audiorecorderapp.dialogs.EditSoundDialog;
import at.wifi.swdev.audiorecorderapp.R;
import at.wifi.swdev.audiorecorderapp.dialogs.PresetsDialog;


public class DrumpadFragment extends Fragment implements View.OnClickListener, PresetsDialog.PresetSelectionListener {

        private SoundPool soundPool;
        private int[] soundIds = new int[12];
        private int longClickedButtonIndex = -1; // Initialize with an invalid index
        private ImageButton[] buttons = new ImageButton[12];
        private final boolean[] loopingStates = new boolean[12];
        private int currentButtonIndex = 0; // Default value
        private ToggleButton toggleButton;
        private Map<Integer, Float> playbackSpeedMap = new HashMap<>();

        private static final int[] presetSoundResources = {
                R.raw.preset1_sound1, R.raw.preset1_sound2, R.raw.preset1_sound3, R.raw.preset1_sound4,
                R.raw.preset1_sound5, R.raw.preset1_sound6, R.raw.preset1_sound7, R.raw.preset1_sound8,
                R.raw.preset1_sound9, R.raw.preset1_sound10, R.raw.preset1_sound11, R.raw.preset1_sound12,
                R.raw.preset2_sound1, R.raw.preset2_sound2, R.raw.preset2_sound3, R.raw.preset2_sound4,
                R.raw.preset2_sound5, R.raw.preset2_sound6, R.raw.preset2_sound7, R.raw.preset2_sound8,
                R.raw.preset2_sound9, R.raw.preset2_sound10, R.raw.preset2_sound11, R.raw.preset2_sound12,
                R.raw.preset3_sound1, R.raw.preset3_sound2, R.raw.preset3_sound3, R.raw.preset3_sound4,
                R.raw.preset3_sound5, R.raw.preset3_sound6, R.raw.preset3_sound7, R.raw.preset3_sound8,
                R.raw.preset3_sound9, R.raw.preset3_sound10, R.raw.preset3_sound11, R.raw.preset3_sound12,
        };

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drumpad, container, false);

            // Set up SoundPool
            setupSoundPool();

            // Set up click listeners for each button
            setupButtonListeners(view);

            // Set up button long click listeners
            setupButtonLongClickListeners(view);

            // Set up toggle button for long click listeners
            setupToggleButton(view);

            ImageButton btnPresets = view.findViewById(R.id.btnPresets);
            btnPresets.setOnClickListener(v -> showPresetDialog());


            return view;
        }


    private void showPresetDialog() {
        FragmentManager fragmentManager = getChildFragmentManager();
        PresetsDialog presetDialog = new PresetsDialog();
        presetDialog.setPresetSelectionListener(this); // Set the listener
        presetDialog.show(fragmentManager, "PresetDialog");
    }

    @Override
    public void onPresetSelected(int presetIndex) {
        loadPresetSounds(presetIndex);
    }
    private void loadPresetSounds(int presetIndex) {
        int startIndex = presetIndex * 12; // Each preset has 12 sounds
        for (int i = 0; i < 12; i++) {
            loadSoundResource(presetSoundResources[startIndex + i], i);
        }
    }

    private void loadSoundResource(int soundResource, int buttonIndex) {
        String resourcePath = "android.resource://" + requireContext().getPackageName() + "/" + soundResource;
        loadSound(resourcePath, buttonIndex);
    }

    public void loadSound(String filePath, int buttonIndex) {
        if (soundPool == null) {
            setupSoundPool();
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                soundIds[buttonIndex] = soundPool.load(file.getAbsolutePath(), 1);
            } else {
                Log.e("DrumpadFragment", "File does not exist: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("ObsoleteSdkInt")
        private void setupSoundPool(){
            AudioAttributes audioAttributes = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build();
            }

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(16)
                    .setAudioAttributes(audioAttributes)
                    .build();

            soundIds[0] = soundPool.load(requireContext(), R.raw.preset1_sound1, 1);
            soundIds[1] = soundPool.load(requireContext(), R.raw.preset1_sound2,  1);
            soundIds[2] = soundPool.load(requireContext(), R.raw.preset1_sound3,  1);
            soundIds[3] = soundPool.load(requireContext(), R.raw.preset1_sound4,  1);
            soundIds[4] = soundPool.load(requireContext(), R.raw.preset1_sound5,  1);
            soundIds[5] = soundPool.load(requireContext(), R.raw.preset1_sound6, 1);
            soundIds[6] = soundPool.load(requireContext(), R.raw.preset1_sound7,  1);
            soundIds[7] = soundPool.load(requireContext(), R.raw.preset1_sound8,  1);
            soundIds[8] = soundPool.load(requireContext(), R.raw.preset1_sound9,  1);
            soundIds[9] = soundPool.load(requireContext(), R.raw.preset1_sound10,  1);
            soundIds[10] = soundPool.load(requireContext(), R.raw.preset1_sound11,  1);
            soundIds[11] = soundPool.load(requireContext(), R.raw.preset1_sound12, 1);
        }

        private void setupButtonListeners(View view){
            // Find all buttons
            buttons[0] = view.findViewById(R.id.button1);
            buttons[1] = view.findViewById(R.id.button2);
            buttons[2] = view.findViewById(R.id.button3);
            buttons[3] = view.findViewById(R.id.button4);
            buttons[4] = view.findViewById(R.id.button5);
            buttons[5] = view.findViewById(R.id.button6);
            buttons[6] = view.findViewById(R.id.button7);
            buttons[7] = view.findViewById(R.id.button8);
            buttons[8] = view.findViewById(R.id.button9);
            buttons[9] = view.findViewById(R.id.button10);
            buttons[10] = view.findViewById(R.id.button11);
            buttons[11] = view.findViewById(R.id.button12);

            // Set click listeners for each button
            for (int i = 0; i < buttons.length; i++) {
                int soundIndex = i;
                buttons[i].setOnClickListener((View v) -> playSound(soundIndex));
            }
        }

        private void setupButtonLongClickListeners(View view){

            buttons[0] = view.findViewById(R.id.button1);
            buttons[1] = view.findViewById(R.id.button2);
            buttons[2] = view.findViewById(R.id.button3);
            buttons[3] = view.findViewById(R.id.button4);
            buttons[4] = view.findViewById(R.id.button5);
            buttons[5] = view.findViewById(R.id.button6);
            buttons[6] = view.findViewById(R.id.button7);
            buttons[7] = view.findViewById(R.id.button8);
            buttons[8] = view.findViewById(R.id.button9);
            buttons[9] = view.findViewById(R.id.button10);
            buttons[10] = view.findViewById(R.id.button11);
            buttons[11] = view.findViewById(R.id.button12);

            // Set long click listeners for each button
            for (int i = 0; i < buttons.length; i++) {
                int soundIndex = i;
                buttons[i].setOnLongClickListener(v -> {
                    if (toggleButton.isChecked()) {
                        // Store the index of the long-clicked button
                        currentButtonIndex = soundIndex; // Update the currentButtonIndex
                        showOptionsDialog(soundIndex); // Pass the index
                        return true; // Consume the long click event
                    }
                    return false;
                });
            }
        }

    private void setupToggleButton(View view) {
        toggleButton = view.findViewById(R.id.toggle_button);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Enable long click listeners
                setupButtonLongClickListeners(); // Call the method directly
            } else {
                // Disable long click listeners
                removeToggleLongClickListeners();
            }
        });
    }
    private void setupButtonLongClickListeners() {
        for (int i = 0; i < buttons.length; i++) {
            int buttonIndex = i;
            buttons[i].setOnLongClickListener(v -> {
                if (toggleButton.isChecked()) {
                    showOptionsDialog(buttonIndex);
                    return true; // Consume the long click event
                }
                return false;
            });
        }
    }
    private void removeToggleLongClickListeners() {
        for (ImageButton button : buttons) {
            button.setOnLongClickListener(null);
        }
    }

    private void playSound(int soundIndex) {
        if (soundIndex >= 0 && soundIndex < soundIds.length) {
            // Check if looping is enabled for the selected button
            boolean isLoopingEnabled = loopingStates[soundIndex];

            // Check if the button is currently being edited
            boolean isEditing = (longClickedButtonIndex == soundIndex);

            // Adjust playback rate based on button's playback speed
            Float playbackSpeed = playbackSpeedMap.get(soundIndex);
            float adjustedPlaybackRate = (playbackSpeed != null) ? playbackSpeed : 1.0f;

            // If looping is enabled and the button is not being edited
            // play the sound continuously as long as the button is pressed
            if (isLoopingEnabled && !isEditing) {
                // Start playing the sound with adjusted playback rate
                soundPool.play(soundIds[soundIndex], 1.0f, 1.0f, 0, -1, adjustedPlaybackRate);
            } else {
                // Otherwise, play the sound once with adjusted playback rate
                soundPool.play(soundIds[soundIndex], 1.0f, 1.0f, 0, 0, adjustedPlaybackRate);
            }
        }
    }
    private void showOptionsDialog(int buttonIndex) {
        if (buttonIndex >= 0 && buttonIndex < loopingStates.length) {
            // Update longClickedButtonIndex when showing the dialog
            longClickedButtonIndex = buttonIndex;
            EditSoundDialog dialog = new EditSoundDialog();
            dialog.setLongClickedButtonIndex(buttonIndex);
            dialog.setButtonIndex(buttonIndex); // Pass the index of the button
            dialog.show(getChildFragmentManager(), "EditSoundDialog");
        } else {
            Log.e("DrumpadFragment", "Invalid buttonIndex: " + buttonIndex);
        }
    }
    // Method to update looping state for a specific button
    public void updateLooping(int buttonIndex, boolean isLooping) {
        loopingStates[buttonIndex] = isLooping;
    }

    public void applyPlaybackSpeed() {
        Log.d("DrumpadFragment", "Applying playback speed changes.");
        for (Map.Entry<Integer, Float> entry : playbackSpeedMap.entrySet()) {
            int buttonIndex = entry.getKey();
            float playbackSpeed = entry.getValue();
            if (soundPool != null) {
                Log.d("DrumpadFragment", "Setting playback speed for sound at index " + buttonIndex + ": " + playbackSpeed);
                soundPool.setRate(soundIds[buttonIndex], playbackSpeed);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release SoundPool when the fragment's view is destroyed
        soundPool.release();
    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    // Add a method to update the image resource of the button
    public void updateButtonImage(int buttonIndex, int drawableResId) {
        if (buttonIndex >= 0 && buttonIndex < buttons.length) {
            buttons[buttonIndex].setImageResource(drawableResId);
        } else {
            Log.e("DrumpadFragment", "Invalid buttonIndex: " + buttonIndex);
        }
    }
}

