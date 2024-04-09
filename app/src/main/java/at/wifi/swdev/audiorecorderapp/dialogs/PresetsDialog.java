package at.wifi.swdev.audiorecorderapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import at.wifi.swdev.audiorecorderapp.R;

public class PresetsDialog extends DialogFragment {

    public interface OnPresetSelectedListener {
        void onPresetSelected(int presetIndex);
    }

    private OnPresetSelectedListener mListener;
    private SoundPool mSoundPool;

    // Constructor to pass SoundPool instance
    public PresetsDialog(SoundPool soundPool) {
        this.mSoundPool = soundPool;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Preset")
                .setItems(R.array.presets_array, (dialog, which) -> {
                    if (mListener != null) {
                        mListener.onPresetSelected(which + 1); // Adjust index to start from 1
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnPresetSelectedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnPresetSelectedListener");
        }
    }
}
