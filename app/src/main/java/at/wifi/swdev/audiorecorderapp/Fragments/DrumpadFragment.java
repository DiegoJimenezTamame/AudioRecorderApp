package at.wifi.swdev.audiorecorderapp.Fragments;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import at.wifi.swdev.audiorecorderapp.EditSoundDialog;
import at.wifi.swdev.audiorecorderapp.R;


public class DrumpadFragment extends Fragment implements View.OnClickListener{

        private SoundPool soundPool;
        private int[] soundIds = new int[12];
        private int longClickedButtonIndex = -1; // Initialize with an invalid index
        private SoundPool.Builder soundPoolBuilder;
        private boolean defaultSoundsLoaded = false;
        private ImageButton[] buttons = new ImageButton[12];
        private boolean[] loopingStates = new boolean[12];
        private int buttonIndex = 0; // Default value
        private int currentButtonIndex = 0; // Default value
        private ToggleButton toggleButton;
        AudioAttributes attributes;
        AudioAttributes.Builder audioAttributesBuilder;


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


            return view;
        }

        public void loadSound(String filePath) {
        // Check if SoundPool is initialized
        if (soundPool == null) {
            setupSoundPool();
        }
        // Load sound into SoundPool
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    soundIds[0] = soundPool.load(file.getAbsolutePath(), 1);
                } else {
                    Log.e("DrumpadFragment", "File does not exist: " + filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

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

            soundIds[0] = soundPool.load(requireContext(), R.raw.sound1, 1);
            soundIds[1] = soundPool.load(requireContext(), R.raw.sound2, 1);
            soundIds[2] = soundPool.load(requireContext(), R.raw.sound3, 1);
            soundIds[3] = soundPool.load(requireContext(), R.raw.sound4, 1);
            soundIds[4] = soundPool.load(requireContext(), R.raw.sound5, 1);
            soundIds[5] = soundPool.load(requireContext(), R.raw.sound6, 1);
            soundIds[6] = soundPool.load(requireContext(), R.raw.sound7, 1);
            soundIds[7] = soundPool.load(requireContext(), R.raw.sound8, 1);
            soundIds[8] = soundPool.load(requireContext(), R.raw.sound9, 1);
            soundIds[9] = soundPool.load(requireContext(), R.raw.sound10, 1);
            soundIds[10] = soundPool.load(requireContext(), R.raw.sound11, 1);
            soundIds[11] = soundPool.load(requireContext(), R.raw.sound12, 1);
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
                final int soundIndex = i;
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
                final int soundIndex = i;
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
    private void setupButtonLongClickListeners() {
        for (ImageButton button : buttons) {
            button.setOnLongClickListener(v -> {
                if (toggleButton.isChecked()) {
                    showOptionsDialog(buttonIndex);
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
                removeButtonLongClickListeners();
            }
        });
    }

    private void removeButtonLongClickListeners() {
        for (ImageButton button : buttons) {
            button.setOnLongClickListener(null);
        }
    }

    private void playSound(int soundIndex) {
        if (soundIndex >= 0 && soundIndex < soundIds.length) {
            // Get the looping state for the selected button
            boolean isLooping = (buttonIndex == soundIndex) && loopingStates[soundIndex];
            // Use the looping state for the corresponding button
            int isooping = loopingStates[soundIndex] ? -1 : 0; // -1 for loop, 0 for no loop

            // Play sound continuously if looping is enabled and button is pressed
            if (isLooping) {
                // Start playing the sound
                soundPool.play(soundIds[soundIndex], 1.0f, 1.0f, 0, -1, 1f);
            } else {
                // Play the sound once
                soundPool.play(soundIds[soundIndex], 1.0f, 1.0f, 0, 0, 1f);
            }
        }
    }

    private void showOptionsDialog(int buttonIndex) {
        if (buttonIndex >= 0 && buttonIndex < loopingStates.length) {
            // Update currentButtonIndex when showing the dialog
            EditSoundDialog dialog = new EditSoundDialog();
            dialog.setLongClickedButtonIndex(buttonIndex);
            dialog.show(getChildFragmentManager(), "OptionsDialogFragment");
            dialog.setLooping(loopingStates[currentButtonIndex]);
        } else {
            Log.e("DrumpadFragment", "Invalid buttonIndex: " + buttonIndex);
        }
    }
    // Method to update looping state for a specific button
    public void updateLooping(int buttonIndex, boolean isLooping) {
        loopingStates[buttonIndex] = isLooping;
    }
    public int getCurrentButtonIndex(){
            return currentButtonIndex;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release SoundPool when the fragment is destroyed
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

         /*@Override
       public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_drumpad);

            AudioAttributes audioAttributes = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build();
            }

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(16)
                    .setAudioAttributes(audioAttributes)
                    .build();

            sound1 = soundPool.load(getApplicationContext(), R.raw.sound1, 1);
            sound2 = soundPool.load(getApplicationContext(), R.raw.sound2, 1);
            sound3 = soundPool.load(getApplicationContext(), R.raw.sound3, 1);
            sound4 = soundPool.load(getApplicationContext(), R.raw.sound4, 1);
            sound5 = soundPool.load(getApplicationContext(), R.raw.sound5, 1);
            sound6 = soundPool.load(getApplicationContext(), R.raw.sound6, 1);
            sound7 = soundPool.load(getApplicationContext(), R.raw.sound7, 1);
            sound8 = soundPool.load(getApplicationContext(), R.raw.sound8, 1);
            sound9 = soundPool.load(getApplicationContext(), R.raw.sound9, 1);
            sound10 = soundPool.load(getApplicationContext(), R.raw.sound10, 1);
            sound11 = soundPool.load(getApplicationContext(), R.raw.sound11, 1);
            sound12 = soundPool.load(getApplicationContext(), R.raw.sound12, 1);

        }


        public void playSound1 (View v){
            soundPool.play(sound1,1.0f,1.0f,0,0,1f);
        }
        public void playSound2 (View v){
            soundPool.play(sound2,1.0f,1.0f,0,0,1f);
        }
        public void playSound3 (View v){
            soundPool.play(sound3,1.0f,1.0f,0,0,1f);
        }
        public void playSound4 (View v){
            soundPool.play(sound4,1.0f,1.0f,0,0,1f);
        }
        public void playSound5 (View v){
            soundPool.play(sound5,1.0f,1.0f,0,0,1f);
        }
        public void playSound6 (View v){
            soundPool.play(sound6,1.0f,1.0f,0,0,1f);
        }
        public void playSound7 (View v){
            soundPool.play(sound7,1.0f,1.0f,0,0,1f);
        }
        public void playSound8 (View v){
            soundPool.play(sound8,1.0f,1.0f,0,0,1f);
        }
        public void playSound9 (View v){
            soundPool.play(sound9,1.0f,1.0f,0,0,1f);
        }
        public void playSound10 (View v){
            soundPool.play(sound10,1.0f,1.0f,0,0,1f);
        }
        public void playSound11 (View v){
            soundPool.play(sound11,1.0f,1.0f,0,0,1f);
        }
        public void playSound12 (View v){
            soundPool.play(sound10,1.0f,1.0f,0,0,1f);
        }

        @Override
        public void onClick(View v) {

        }
        @Override
        public void onPointerCaptureChanged(boolean hasCapture) {
            super.onPointerCaptureChanged(hasCapture);
        }

        @Override
        public void onPause(){
            super.onPause();
            soundPool.release();
        }
    }*/
}

