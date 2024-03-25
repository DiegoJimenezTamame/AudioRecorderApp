package at.wifi.swdev.audiorecorderapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import at.wifi.swdev.audiorecorderapp.OnSelectListener;
import at.wifi.swdev.audiorecorderapp.R;
import at.wifi.swdev.audiorecorderapp.RecViewHolder;

public class RecAdapter extends RecyclerView.Adapter<RecViewHolder> {

    private Context context;
    private List<File> fileList;
    private OnSelectListener listener;

    public RecAdapter(Context context, List<File> fileList, OnSelectListener listener) {
        this.context = context;
        this.fileList = fileList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecViewHolder holder, @SuppressLint("RecyclerView") int  position) {

        holder.tvName.setText(fileList.get(position).getName());
        holder.tvName.setSelected(true);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnSelected(fileList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void removeItem(int position){
        fileList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, fileList.size());
    }

}
