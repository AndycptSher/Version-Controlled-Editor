package VersionControl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.DeflaterOutputStream;
import java.util.Base64;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public final class JSONVersion extends Version implements AutoCloseable {

    private static final int BUFFER_SIZE = 1024;

    private String versionName;
    private boolean savedVersion;

    private JSONVersion(Path controlFolderPath) throws IOException, NoSuchAlgorithmException {
        /*
         * constructor for new version file objects
         */
        super(controlFolderPath);
        this.versionName = getCurrentFileHash();
        this.previousVersions = new String[]{getCurrentVersion()};
        this.savedVersion = false;
    }

    private JSONVersion(Path controlFolderPath, Path versionFile) {
        /*
         * constructor for existing version file objects
         */
        super(controlFolderPath);
        this.versionName = versionFile.getFileName().toString();
        this.savedVersion = true;
    }

    public String getCurrentVersion() throws IOException {
        /*
         * gets the current version in the current_version file
         */
        return Files.readString(versionControlFilePath.resolve("current_version"));
    }

    private String getCurrentCompressedFile() throws IOException {
        /*
         * commpresses current file contents into a compressed string
         */
        byte[] compressedData;
    
        // Compressing file
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
             FileInputStream fileInputStream = new FileInputStream(versionControlFilePath.toFile())) {
    
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                deflaterOutputStream.write(buffer, 0, bytesRead);
            }
            deflaterOutputStream.finish();
            compressedData = byteArrayOutputStream.toByteArray();
        }
    
        // Use base64 to prevent compressed data from being missinterpreted
        return Base64.getEncoder().encodeToString(compressedData);

    }

    private String getCurrentFileHash() throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        // Hashing file
        try (FileInputStream fileInputStream = new FileInputStream(versionControlFilePath.toString())) {
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

    @Override
    public JSONVersion[] getPreviousVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPreviousVersions'");
    }
    
    @Override
    public JSONVersion[] getNextVersions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNextVersions'");
    }
    
    @Override
    public boolean setPreviousVersions(Version version) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPreviousVersions'");
        // this.savedVersion = false;
    }

    @Override
    public boolean setNextVersions(Version version) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setNextVersions'");
    }

    @Override
    public void close() throws Exception {
        if (savedVersion) {
            return;
        }
        String encodedData = getCurrentCompressedFile();
        
        // Create JSON string
        String jsonString = String.format(String.join("\n",
                "{",
                "\"previous\": [\"%s\"],",
                "\"next\": [],",
                "\"data\": \"%s\"",
                "}"
            ),
            getCurrentVersion(), encodedData
         );

        // TODO: Read previous version (or in this case still the getCurrentVersion()) and change it's next value

        // Saving to the file
        Files.createDirectories(versionControlFilePath.resolve("versions"));
        Path jsonFilePath = versionControlFilePath.resolve("versions").resolve(versionName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(jsonFilePath.toFile())) {
            fileOutputStream.write(jsonString.getBytes());
        }

        // Saving version as the current_version
        Files.writeString(currentVersion, versionName);
    }

    public static void appendNewVersionToCurrentBranch(Path versionControlPath) throws Exception {
        JSONVersion newVersion = new JSONVersion(versionControlPath);
        newVersion.close();
        return;
    }
}

