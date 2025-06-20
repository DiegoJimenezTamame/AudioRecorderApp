package at.wifi.swdev.audiorecorderapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.List;

import at.wifi.swdev.audiorecorderapp.Adapters.CustomColorListAdapter;
import at.wifi.swdev.audiorecorderapp.R;

public class ColorSelectionDialog extends DialogFragment {

    private OnColorSelectedListener mListener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_color_selection, null);

        ListView colorListView = dialogView.findViewById(R.id.colorListView);

        List<Integer> circleIcons = Arrays.asList(
                R.drawable.blue_button,
                R.drawable.green_button,
                R.drawable.pink_button,
                R.drawable.orange_button,
                R.drawable.purple_button
        );

        // Create custom adapter with circle icons
        CustomColorListAdapter adapter = new CustomColorListAdapter(requireContext(), R.layout.color_list_view,
                Arrays.asList(getResources().getStringArray(R.array.color_options)), circleIcons);

        colorListView.setAdapter(adapter); // Set adapter to ListView

        colorListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedColor = (String) parent.getItemAtPosition(position);
            if (mListener != null) {
                mListener.onColorSelected(selectedColor);
            }
            dismiss();
        });

        builder.setView(dialogView);
        return builder.create();
    }
    public void setOnColorSelectedListener(OnColorSelectedListener listener){
        mListener = listener;
    }

    public interface OnColorSelectedListener{
        void onColorSelected(String color);
    }

}
