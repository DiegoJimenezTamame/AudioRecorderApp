package at.wifi.swdev.audiorecorderapp.Fragments;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import at.wifi.swdev.audiorecorderapp.R;

public class DrumpadFragment extends Fragment {

    View view;
    private SoundPool soundPool;
    AudioAttributes attributes;
    AudioAttributes.Builder audioAttributesBuilder;
    private int sound1;
    private int sound2;
    private int sound3;
    private int sound4;
    private int sound5;
    private int sound6;
    private int sound7;
    private int sound8;
    private int sound9;
    private int sound00;

    public DrumpadFragment(SoundPool soundPool, SoundPool.Builder soundPoolBuilder,
                           int sound1,
                           int sound2,
                           int sound3,
                           int sound4,
                           int sound5,
                           int sound6,
                           int sound7,
                           int sound8,
                           int sound9,
                           int sound00) {
        this.soundPool = soundPool;
        this.sound1 = sound1;
        this.sound2 = sound2;
        this.sound3 = sound3;
        this.sound4 = sound4;
        this.sound5 = sound5;
        this.sound6 = sound6;
        this.sound7 = sound7;
        this.sound8 = sound8;
        this.sound9 = sound9;
        this.sound00 = sound00;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_drumpad, container, false);

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

        sound1 = soundPool.load(getContext(), R.raw.sound1, 1);
        sound2 = soundPool.load(getContext(), R.raw.sound2, 1);
        sound3 = soundPool.load(getContext(), R.raw.sound3, 1);
        sound4 = soundPool.load(getContext(), R.raw.sound4, 1);
        sound5 = soundPool.load(getContext(), R.raw.sound5, 1);
        sound6 = soundPool.load(getContext(), R.raw.sound6, 1);
        sound7 = soundPool.load(getContext(), R.raw.sound7, 1);
        sound8 = soundPool.load(getContext(), R.raw.sound8, 1);
        sound9 = soundPool.load(getContext(), R.raw.sound9, 1);
        sound00 = soundPool.load(getContext(), R.raw.sound00, 1);




        return view;
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
    public void playSound00 (View v){
        soundPool.play(sound00,1.0f,1.0f,0,0,1f);
    }

    @Override
    public void onPause(){
        super.onPause();
        soundPool.release();
    }



}
