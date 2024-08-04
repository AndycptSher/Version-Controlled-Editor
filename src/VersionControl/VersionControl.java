package VersionControl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.zip.DeflaterOutputStream;

// Exception imports
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class VersionControl {
    private static final int BUFFER_SIZE = 1024;

    // Path and name of the file being version controlled
    private Path filePath;
    // Path and name for the version control folder
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

    public void save() throws IOException, NoSuchAlgorithmException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath.toString());
            FileOutputStream fileOutputStream = new FileOutputStream(versionControlPath.resolve("versions/" + getFileHash()).toString());
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fileOutputStream)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                deflaterOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private String getFileHash() throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        try (FileInputStream fileInputStream = new FileInputStream(filePath.toString())) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();

        StringBuilder stringBuilder = new StringBuilder();
        for (byte hashByte : hashBytes) {
            stringBuilder.append(String.format("%02x", hashByte));
        }

        return stringBuilder.toString();
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

