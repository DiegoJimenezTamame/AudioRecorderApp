package at.wifi.swdev.audiorecorderapp.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import androidx.core.content.ContextCompat;

//importing required classes
import com.chibde.visualizer.LineBarVisualizer;


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
                    startRecording("AMR_NB");
                    timeRec.setBase(SystemClock.elapsedRealtime());
                    timeRec.start();
                    txtRecStatus.setText(R.string.recordinglive);
                    btnRec.setImageResource(R.drawable.ic_stop);
                    isRecording = true;

                    // Start visualization
                    lineBarVisualizer.setVisibility(View.VISIBLE);
                    lineBarVisualizer.setColor(ContextCompat.getColor(getContext(), R.color.teal_200)); // Set color as per your requirement
                    lineBarVisualizer.setDensity(70); // Set density as per your requirement
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
                    lineBarVisualizer.setColor(ContextCompat.getColor(getContext(), R.color.teal_200)); // Set color as per your requirement
                    lineBarVisualizer.setDensity(70); // Set density as per your requirement

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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recorder_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_audio_format) {
            // Show audio format dialog
            showAudioFormatsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAudioFormatsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme);
        builder.setTitle("Choose Audio Format")
                .setItems(R.array.audio_formats, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        String selectedFormat = getResources().getStringArray(R.array.audio_formats)[which];
                        updateFileName(selectedFormat); // Call updateFileName to update the file name
                        startRecording(selectedFormat);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void updateFileName(String selectedFormat) {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        String date = format.format(new Date());

        switch (selectedFormat) {
            case "MPEG4":
                fileName = path + "/recording_" + date + ".mp4";
                break;
            case "3GP":
                fileName = path + "/recording_" + date + ".3gp";
                break;
            case "OGG":
                fileName = path + "/recording_" + date + ".ogg";
                break;
            case "AMR (Default)":
                fileName = path + "/recording_" + date + ".amr";
            default: // Default case (AMR_NB)
                fileName = path + "/recording_" + date + ".amr";
                break;
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                Toast.makeText(getContext(), "Some permissions were denied. DW Sampler needs granted permission to operate properly.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording (String selectedFormat) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        // Set output format based on the selected format
        switch (selectedFormat) {
            case "MPEG4":
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                break;
            case "3GP":
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                break;
            case "OGG":
                recorder.setOutputFormat(MediaRecorder.OutputFormat.OGG);
                break;
            case "AMR (Default)":
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            // Add cases for additional formats if needed
            default: // Default case (AMR_NB)
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                break;
        }
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(fileName);
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
