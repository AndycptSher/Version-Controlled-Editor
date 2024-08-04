package VersionControl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VersionControl {
    private Path filePath;
    private Path versionControlPath;

    public VersionControl(String filePath) {
        this.filePath = Paths.get(filePath);
        this.versionControlPath = this.filePath.getParent().resolve("." + this.filePath.getFileName() + "_version_control");
        createVersionControlFolder();
    }

    public String getFilePath() {
        return filePath.toString();
    }

    public String getVersionControlPath() {
        return versionControlPath.toString();
    }

    public void save() {

    }

    private String getFileHash() throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException _error) {
            System.err.println("SHA-1 hashing algorithm couldn't be found");
            System.exit(1);
        }
        /*
        FileInputStream fileInputSteam = new FileInputStream(filePath);

        byte[] buffer = new byte[1024];

        int bytesRead = 0;
        */

        return "";
    }

    private void createVersionControlFolder() {
        if (Files.notExists(versionControlPath)) {
            try {
                Files.createDirectories(versionControlPath);
                Files.createDirectories(versionControlPath.resolve("versions"));
                Files.createFile(versionControlPath.resolve("branches"));
            } catch (IOException _error) {
                System.err.println("Couldn't create version control folder");
            }
        }
    }
}

