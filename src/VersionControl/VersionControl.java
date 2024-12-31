package VersionControl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.zip.DeflaterOutputStream;
import java.util.Base64;

// Exception imports
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class VersionControl {
    
    // Path and name of the file being version controlled
    private Path filePath;
    // Path and name for the version control folder
    private Path versionControlPath;
    
    public VersionControl(String filePath) {
        this.filePath = Paths.get(filePath);
        this.versionControlPath = this.filePath.getParent().resolve("." + this.filePath.getFileName() + "_version_control");
        createVersionControlFolderifNotExists();
    }
    
    public String getFilePath() {
        return filePath.toString();
    }

    public String getVersionControlPath() {
        return versionControlPath.toString();
    }

    public void save() throws IOException, NoSuchAlgorithmException {
        /*
         * Saves file into versions folder
         */
        // TODO: finish
        // JSONVersion.appendNewVersionToCurrentBranch();
    }
    
    public void load(String version) {
        // TODO: Finish
    }
    
    private void createVersionControlFolderifNotExists() {
        if (Files.notExists(versionControlPath)) {
            try {
                Files.createDirectories(versionControlPath);
                Files.createDirectories(versionControlPath.resolve("versions"));
                Files.createFile(versionControlPath.resolve("branches"));
                Files.createFile(versionControlPath.resolve("current_version"));
            } catch (IOException _error) {
                System.err.println("Couldn't create version control folder");
            }
        }
    }
}


