package at.wifi.swdev.audiorecorderapp;

import java.io.File;

public interface FileRenameListener {
    void onFileRenamed(File oldFile, File newFile);
}
