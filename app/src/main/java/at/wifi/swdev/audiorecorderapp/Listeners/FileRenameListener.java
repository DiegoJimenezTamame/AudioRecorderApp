package at.wifi.swdev.audiorecorderapp.Listeners;

import java.io.File;

public interface FileRenameListener {
    void onFileRenamed(File oldFile, File newFile);
}
