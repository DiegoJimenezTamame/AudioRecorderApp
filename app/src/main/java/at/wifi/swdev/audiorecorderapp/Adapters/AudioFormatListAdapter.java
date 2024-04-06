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

import at.wifi.swdev.audiorecorderapp.R;

public class AudioFormatListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] audioFormats;
    private final int[] icons;

    public AudioFormatListAdapter(Context context, String[] audioFormats, int[] icons) {
        super(context, R.layout.audio_format_list_view, audioFormats);
        this.context = context;
        this.audioFormats = audioFormats;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listItemView = inflater.inflate(R.layout.audio_format_list_view, parent, false);

        TextView formatTextView = listItemView.findViewById(R.id.formatTextView);
        formatTextView.setText(audioFormats[position]);

        ImageView iconImageView = listItemView.findViewById(R.id.iconImageView);
        iconImageView.setImageResource(icons[position]);

        return listItemView;
    }
}
