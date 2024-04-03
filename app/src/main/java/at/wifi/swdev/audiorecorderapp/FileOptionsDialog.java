package at.wifi.swdev.audiorecorderapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.chibde.visualizer.LineBarVisualizer;

import java.io.File;
import java.io.IOException;

public class FileOptionsDialog extends Dialog {

    private File selectedFile;
    private MediaPlayer mediaPlayer;
    private LineBarVisualizer lineBarVisualizer;
    private boolean isPlaying = false;


    public FileOptionsDialog(@NonNull Context context, File file) {
        super(context);
        selectedFile = file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_file_options);

        // Initialize and set up MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(selectedFile.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lineBarVisualizer= findViewById(R.id.visualizer_view_dialog);
        lineBarVisualizer.setPlayer(mediaPlayer);

        ImageButton playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    playButton.setImageResource(R.drawable.play_button_icon);
                    lineBarVisualizer.setVisibility(View.GONE);
                } else {
                    mediaPlayer.start();
                    playButton.setImageResource(R.drawable.pause_button_icon);
                    lineBarVisualizer.setVisibility(View.VISIBLE);
                    lineBarVisualizer.setColor(ContextCompat.getColor(getContext(), R.color.purple_700)); // Set color as per your requirement
                    lineBarVisualizer.setDensity(1000); // Set density as per your requirement
                }
                isPlaying = !isPlaying;
            }
        });

        // Set completion listener for MediaPlayer
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Reset icon to play button and hide visualizer
                playButton.setImageResource(R.drawable.play_button_icon);
                lineBarVisualizer.setVisibility(View.GONE);
                isPlaying = false; // Reset play state
            }
        });

        ImageButton deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog to confirm file deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialogStyle);
                builder.setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteFile(selectedFile);
                                dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and show it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        ImageButton renameButton = findViewById(R.id.rename_button);
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement file renaming functionality here
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialogStyle);
                builder.setTitle("Rename File");
                // Set Up the Input
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set Up the Buttons
                builder.setPositiveButton("Ok", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFileName = input.getText().toString();
                        //Rename the file
                        renameFile(selectedFile,newFileName);
                        //Dismiss the dialog
                        dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });


        ImageButton trimButton = findViewById(R.id.trim_button);
        trimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement file trimming functionality here
                // TODO: IMPLEMENT TRIM LOGIC HERE
                dismiss();
            }
        });

        ImageButton shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFile(selectedFile);
                // TODO: CUSTOMIZE SHARE MENU
            }
        });
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    private void renameFile(File file, String newFileName){
        if (file.exists()){
            File newFile = new File(file.getParent(), newFileName + ".amr");
            boolean renamed = file.renameTo(newFile);
            if (renamed){
                // File Renamed successfully
                selectedFile = newFile; // Update selectedFile reference if necessary
                // Update any UI elements or references
            } else {
                // Failed to rename file
                // Handle error or show a message to the user
            }
        }
    }

    private void shareFile(File file) {
        Uri contentUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        getContext().startActivity(Intent.createChooser(intent, "Share File"));
    }
    @Override
    public void dismiss() {
        super.dismiss();
        mediaPlayer.release();
    }

}
