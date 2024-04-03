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
import android.view.animation.AccelerateInterpolator;
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
import android.media.audiofx.Visualizer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//importing required classes
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import com.chibde.visualizer.BarVisualizer;
import com.chibde.visualizer.CircleBarVisualizer;
import com.chibde.visualizer.CircleVisualizer;
import com.chibde.visualizer.LineBarVisualizer;
import com.chibde.visualizer.LineVisualizer;
import com.chibde.visualizer.SquareBarVisualizer;


public class RecorderFragment extends Fragment {

    View view;
    ImageButton btnRec;
    ImageButton btnPlay;
    TextView txtRecStatus;
    Chronometer timeRec;
    private LineBarVisualizer lineBarVisualizer;
    private File path;
    public  MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    boolean isRecording = false;
    private static String fileName;
    private Context context;

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
        btnPlay = view.findViewById(R.id.play_button);
        txtRecStatus = view.findViewById(R.id.textRecStatus);
        timeRec = view.findViewById(R.id.timeRec);
        lineBarVisualizer = view.findViewById(R.id.visualizerLineBar);

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
        if (!path.exists()) {
            path.mkdirs();
        }

        btnRec.setOnClickListener(view -> {
            if (!isRecording) {
                try {
                    startRecording();
                    timeRec.setBase(SystemClock.elapsedRealtime());
                    timeRec.start();
                    txtRecStatus.setText(R.string.recordinglive);
                    btnRec.setImageResource(R.drawable.ic_stop);
                    isRecording = true;

                    // Start visualization
                    lineBarVisualizer.setVisibility(View.VISIBLE);
                    lineBarVisualizer.setColor(ContextCompat.getColor(getContext(), R.color.purple_200)); // Set color as per your requirement
                    lineBarVisualizer.setDensity(10000); // Set density as per your requirement
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Recording failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                stopRecording();
                timeRec.setBase(SystemClock.elapsedRealtime());
                timeRec.stop();
                txtRecStatus.setText("");
                btnRec.setImageResource(R.drawable.ic_microphone);
                isRecording = false;

                // Stop visualization
                lineBarVisualizer.setVisibility(View.GONE);
            }
        });

        btnPlay.setOnClickListener(view -> {
            if (mediaPlayer == null) {
                // Create and start MediaPlayer
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(fileName);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getContext(), "Playing recorded audio", Toast.LENGTH_SHORT).show();
                    // Change icon to Pause
                    btnPlay.setImageResource(R.drawable.pause_button_icon);

                    // Start visualization
                    lineBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
                    lineBarVisualizer.setVisibility(View.VISIBLE);
                    lineBarVisualizer.setColor(ContextCompat.getColor(getContext(), R.color.purple_700)); // Set color as per your requirement
                    lineBarVisualizer.setDensity(1000); // Set density as per your requirement

                    // Start chronometer
                    timeRec.setBase(SystemClock.elapsedRealtime());
                    timeRec.start();

                    // Set completion listener to handle playback completion
                    mediaPlayer.setOnCompletionListener(mp -> {
                        // Change icon back to play icon
                        btnPlay.setImageResource(R.drawable.play_button_icon);

                        // Stop visualization
                        lineBarVisualizer.setVisibility(View.GONE);

                        // Stop chronometer
                        timeRec.stop();

                        // Release MediaPlayer resources
                        mediaPlayer.release();
                        mediaPlayer = null;
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to play recorded audio", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mediaPlayer.isPlaying()) {
                    // Pause MediaPlayer
                    mediaPlayer.pause();
                    // Change icon to Play
                    btnPlay.setImageResource(R.drawable.play_button_icon);
                    Toast.makeText(getContext(), "Playback paused", Toast.LENGTH_SHORT).show();

                    // Stop visualization
                    lineBarVisualizer.setVisibility(View.GONE);

                    // Stop chronometer
                    timeRec.stop();
                } else {
                    // Resume MediaPlayer
                    mediaPlayer.start();
                    // Change icon to Pause
                    btnPlay.setImageResource(R.drawable.pause_button_icon);
                    Toast.makeText(getContext(), "Playback resumed", Toast.LENGTH_SHORT).show();

                    // Start visualization
                    lineBarVisualizer.setVisibility(View.VISIBLE);

                    // Resume chronometer
                    timeRec.start();
                }
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

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        btnPlay.setVisibility(View.GONE);

        try {
            recorder.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recorder.start();
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        btnPlay.setVisibility(View.VISIBLE);
    }

    // Define a method to update the visualizer view
    private void updateVisualizer(byte[] waveform) {
        // Process the waveform data and update the visualizer view
    }

    @Override
    public void onPause() {
        super.onPause();
        btnPlay.setVisibility(View.GONE);
        timeRec.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
