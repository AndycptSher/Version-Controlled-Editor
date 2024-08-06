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

    public String getCurrentVersion() throws IOException {
        return Files.readString(versionControlPath.resolve("current_version"));
    }

    public void save() throws IOException, NoSuchAlgorithmException {
        /*
         * Saves compressed file into versions folder
         */
        byte[] compressedData;

        // Compressing file
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
             FileInputStream fileInputStream = new FileInputStream(filePath.toFile())) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                deflaterOutputStream.write(buffer, 0, bytesRead);
            }
            deflaterOutputStream.finish();
            compressedData = byteArrayOutputStream.toByteArray();
        }

        // Use base64 to prevent compressed data from being missinterpreted
        String encodedData = Base64.getEncoder().encodeToString(compressedData);

        // Create JSON string
        String jsonString = String.format(String.join("\n",
            "{",
            "\"previous\": [\"%s\"],",
            "\"next\": [],",
            "\"data\": \"%s\"",
            "}"),
            getCurrentVersion(), encodedData
         );

        // Getting hash of current versions contents
        String fileHash = getFileHash();

        // TODO: Read previous version (or in this case still the getCurrentVersion()) and change it's next value

        // Saving to the file
        Files.createDirectories(versionControlPath.resolve("versions"));
        Path jsonFilePath = versionControlPath.resolve("versions/" + fileHash);
        try (FileOutputStream fileOutputStream = new FileOutputStream(jsonFilePath.toFile())) {
            fileOutputStream.write(jsonString.getBytes());
        }

        // Saving version as the current_version
        Files.writeString(versionControlPath.resolve("current_version"), fileHash);
    }

    public void load(String version) {
        // TODO: Finish
    }

    private String getFileHash() throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        // Hashing file
        try (FileInputStream fileInputStream = new FileInputStream(filePath.toString())) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();

        // Convert hash bytes to string
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
                Files.createFile(versionControlPath.resolve("current_version"));
            } catch (IOException _error) {
                System.err.println("Couldn't create version control folder");
            }
        }
    }
}

