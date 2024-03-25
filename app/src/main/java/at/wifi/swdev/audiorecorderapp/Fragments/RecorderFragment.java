package at.wifi.swdev.audiorecorderapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import at.wifi.swdev.audiorecorderapp.R;
import at.wifi.swdev.audiorecorderapp.WaveformVisualizerView;


public class RecorderFragment extends Fragment {

    View view;
    ImageButton btnRec;
    TextView txtRecStatus;
    Chronometer timeRec;

    private static String fileName;
    private MediaRecorder recorder;
    //private Visualizer visualizer;
    private WaveformVisualizerView visualizerView;
    boolean isRecording = false;

    private Context context;
    private File path; // Moved declaration here

    private static final int REQUEST_PERMISSION_CODE = 1001;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_MEDIA_LOCATION
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recorder, container, false);

        btnRec = view.findViewById(R.id.btnRec);
        txtRecStatus = view.findViewById(R.id.textRecStatus);
        timeRec = view.findViewById(R.id.timeRec);
        visualizerView = view.findViewById(R.id.visualizer_view_recorder);

        isRecording = false;

        // Initialize context
        context = getContext();

        // Request permissions
        askRuntimePermissions();

        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        String date = format.format(new Date());

        // Initialize path
        path = new File(context.getFilesDir(), "/AudioRecorderApp");

        fileName = path + "/recording_" + date + ".amr";
        if(!path.exists()){
            path.mkdirs();
        }

        btnRec.setOnClickListener(view -> {
            if (!isRecording){
                try {
                    startRecording();
                    timeRec.setBase(SystemClock.elapsedRealtime());
                    timeRec.start();
                    txtRecStatus.setText("Recording...");
                    btnRec.setImageResource(R.drawable.ic_stop);
                    isRecording = true;
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Recording failed", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                stopRecording();
                timeRec.setBase(SystemClock.elapsedRealtime());
                timeRec.stop();
                txtRecStatus.setText("");
                btnRec.setImageResource(R.drawable.ic_microphone);
                isRecording = false;
            }
        });

        return view;
    }

    private void askRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean allPermissionsGranted = true;
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (!allPermissionsGranted) {
                requestPermissions(PERMISSIONS, REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                // Permissions granted
                Toast.makeText(getContext(), "Granted", Toast.LENGTH_SHORT).show();
            } else {
                // Handle denied permissions
                Toast.makeText(getContext(), "Some permissions were denied. Audio Recorder Pro needs granted permission to operate properly.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        visualizerView.setVisibility(View.VISIBLE);
        visualizerView.setMediaPlayer(new MediaPlayer());

        try {
            recorder.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recorder.start();
    }

    private void stopRecording(){
        recorder.stop();
        recorder.release();
        recorder = null;
        visualizerView.release();
        visualizerView.setVisibility(View.GONE);
    }


    /*private void startVisualizer() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioSessionId(Objects.requireNonNull(recorder.getActiveRecordingConfiguration()).getClientAudioSessionId());

        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setEnabled(true);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        // Draw your waveform here using the bytes array
                    }
                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
        visualizer.setEnabled(true);

        mediaPlayer.release();
    }
    private void stopVisualizer() {
        if (visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.release();
            visualizer = null;
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (visualizerView != null) {
            visualizerView.release();
            visualizerView = null;
        }
    }

}