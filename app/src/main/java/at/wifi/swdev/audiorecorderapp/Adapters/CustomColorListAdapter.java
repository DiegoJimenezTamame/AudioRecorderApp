package at.wifi.swdev.audiorecorderapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import at.wifi.swdev.audiorecorderapp.R;

public class CustomColorListAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final List<String> mItems;
    private final List<Integer> mCircleIcons; // List of drawable resource IDs for circle icons

    public CustomColorListAdapter(@NonNull Context context, int resource, @NonNull List<String> items, List<Integer> circleIcons) {
        super(context, resource, items);
        this.mContext = context;
        this.mItems = items;
        this.mCircleIcons = circleIcons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.color_list_view, null);
        }

        // Get the views
        TextView textView = view.findViewById(R.id.textView);
        ImageView circleImageView = view.findViewById(R.id.circleImageView);

        // Set the item text
        textView.setText(mItems.get(position));

        // Set the circle icon based on the corresponding drawable resource ID
        circleImageView.setImageResource(mCircleIcons.get(position));
        circleImageView.setVisibility(View.VISIBLE); // Make the circle visible

        return view;
    }
}
