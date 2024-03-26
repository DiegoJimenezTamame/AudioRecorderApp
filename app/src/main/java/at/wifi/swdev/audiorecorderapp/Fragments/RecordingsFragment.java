package at.wifi.swdev.audiorecorderapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.wifi.swdev.audiorecorderapp.Adapters.RecAdapter;
import at.wifi.swdev.audiorecorderapp.FileOptionsDialog;
import at.wifi.swdev.audiorecorderapp.Listeners.OnSelectListener;
import at.wifi.swdev.audiorecorderapp.R;

public class RecordingsFragment extends Fragment implements OnSelectListener{

    private RecyclerView recyclerView;
    private List<File> fileList;
    private RecAdapter recAdapter;

    private Context context;
    private File path; // Moved declaration here

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recordings, container, false);

        // Initialize context
        context = getContext();
        // Initialize path
        if (context != null) {
            path = new File(context.getFilesDir(), "/AudioRecorderApp");
        }

        displayFiles();
        return  view;
    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_recordings);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        fileList = new ArrayList<>();
        fileList.addAll(findFile(path));
        recAdapter = new RecAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(recAdapter);
    }

    public ArrayList<File> findFile(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files){
                if (singleFile.getName().toLowerCase().endsWith(".amr")){
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    /*@Override
    public void OnSelected(File file) {

        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/x-wav");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);

        /*FileDetailsDialogFragment dialogFragment = FileDetailsDialogFragment.newInstance(file);
        dialogFragment.show(getChildFragmentManager(), "file_details_dialog");
    } */

    @Override
    public void OnSelected(File file) {
        FileOptionsDialog dialog = new FileOptionsDialog(context, file);
        dialog.show();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            displayFiles();
        }
    }
}
