package at.wifi.swdev.audiorecorderapp.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;

import at.wifi.swdev.audiorecorderapp.R;

public class FileDetailsDialogFragment extends DialogFragment {

    private TextView fileNameTextView;

    public static FileDetailsDialogFragment newInstance(File file) {
        FileDetailsDialogFragment fragment = new FileDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString("file_name", file.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_file_details, container, false);
        fileNameTextView = view.findViewById(R.id.fileNameTextView);
        if (getArguments() != null) {
            String fileName = getArguments().getString("file_name");
            fileNameTextView.setText(fileName);
        }
        return view;
    }
}
