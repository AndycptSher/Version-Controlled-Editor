package VersionControl;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.DeflaterOutputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;

public final class JSONVersion extends Version<JSONVersion> {

    private static final int BUFFER_SIZE = 1024;

    private String versionName;
    private boolean savedVersion; // indicate if object contents has been altered

    /**
     * constructor for new version file objects
     * @param controlFolderPath path to the folder containing versioning data
     * @throws IOException thrown when error raised when writing to versioning folder
     */
    public JSONVersion(VersionControl<JSONVersion> versionControl) throws IOException {
        super(versionControl);
        // timestamp prevents verisoning files overwriting each other
        this.versionName = System.currentTimeMillis() / 1000L + getCurrentFileHash();
        this.previousVersions = versionControl.getCurrentVersion().map(path -> new Path[]{path}).orElse(new Path[]{});
        this.savedVersion = false;
    }

    /**
     * constructor for existing version file objects
     * @param controlFolderPath path to the folder containing versioning data
     * @param verisonFile the version file object
     */
    private JSONVersion(VersionControl<JSONVersion> versionControl, Path versionFile) {
        super(versionControl);
        this.versionName = versionFile.getFileName().toString();
        // TODO: extract info from version file
        this.savedVersion = true;
    }

    @Override
    protected Path getPath() {
        return this.versionControlFilePath.resolve(this.versionName);
    }

    /**
     * commpresses current file contents into a compressed string
     * @throws  IOException
     *          thrown when unable to write to versioning folder
     */
    private String getCurrentCompressedFile() throws IOException {
        byte[] compressedData;
    
        // Compressing file
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
             FileInputStream fileInputStream = new FileInputStream(this.filePath.toFile())) {
    
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

    /**
     * Calculates the SHA-1 hash of the target file
     * Used to generate unique identifier for the versioning file
     * @return string hash of the file
     * @throws IOException thrown if IO error occurs
     */
    private String getCurrentFileHash() throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException notFound) {
            assert false : "Name of algorithm should be correct";
            digest = null;
        }

        // Hashing file
        try (FileInputStream fileInputStream = new FileInputStream(this.filePath.toString())) {
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
    public boolean setPreviousVersions(Version<JSONVersion> version) {
        this.savedVersion = false;
        ArrayList<Path> previousVersions = new ArrayList<>(Arrays.asList(this.previousVersions));
        previousVersions.add(version.getPath());
        this.previousVersions = previousVersions.toArray(Path[]::new);
        return true;
    }

    @Override
    public boolean setNextVersions(Version<JSONVersion> version) {
        this.savedVersion = false;
        ArrayList<Path> nextVersions = new ArrayList<>(Arrays.asList(this.nextVersions));
        nextVersions.add(version.getPath());
        this.nextVersions = nextVersions.toArray(Path[]::new);
        return true;
    }

    /**
     * Creates/overrites the persistent versioning object in versioning folder 
     */
    @Override
    public void close() throws Exception {
        // TODO: add check if file changed since last version
        if (savedVersion) {
            return;
        }
        String encodedData = getCurrentCompressedFile();
        
        // Create JSON string
        String jsonString = String.format(String.join("\n",
                "{",
                "\"previous\": [%s],",
                "\"next\": [%s],",
                "\"data\": \"%s\"",
                "}"
            ),
            String.join(",", Arrays.stream(this.previousVersions).map(path -> "\""+path.getFileName().toString()+"\"").toList()), 
            String.join(",", Arrays.stream(this.nextVersions).map(path -> "\""+path.getFileName().toString()+"\"").toList()),
            encodedData
        );

        // TODO: Read previous version (or in this case still the getCurrentVersion()) and change it's next value
        for (Path prevPath: this.previousVersions) {
            try (JSONVersion prevVer = new JSONVersion(this.controller, prevPath)) {
                prevVer.setNextVersions(this);
            }
        }

        // Saving to the file
        Path versionsFilePath = versionControlFilePath.resolve("versions");
        versionsFilePath = Files.createDirectories(versionsFilePath);
        Path jsonFilePath = versionsFilePath.resolve(this.versionName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(jsonFilePath.toFile())) {
            fileOutputStream.write(jsonString.getBytes());
        }

        // Saving version as the current_version
        Files.writeString(currentVersion, versionName);
    }
}
