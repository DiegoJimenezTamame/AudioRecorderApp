package at.wifi.swdev.audiorecorderapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import at.wifi.swdev.audiorecorderapp.R;

public class PresetsDialog extends DialogFragment {

    public interface PresetSelectionListener {
        void onPresetSelected(int presetIndex);
    }

    private PresetSelectionListener listener;

    public void setPresetSelectionListener(PresetSelectionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_presets, null);
        builder.setView(view);

        ListView presetListView = view.findViewById(R.id.presetListView);

        // List of presets
        String[] presets = {
                "Preset 1",
                "Preset 2",
                "Preset 3"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, presets);
        presetListView.setAdapter(adapter);

        presetListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (listener != null) {
                listener.onPresetSelected(position);
            }
            dismiss();
        });

        return builder.create();
    }
}
