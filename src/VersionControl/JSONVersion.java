package VersionControl;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.DeflaterOutputStream;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.security.NoSuchAlgorithmException;

public final class JSONVersion extends Version<JSONVersion> {

    private static final int BUFFER_SIZE = 1024;

    private String versionName;
    private boolean savedVersion; // indicate if object contents has been altered

    private final String fileContents;

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
        this.fileContents = getCurrentCompressedFile();
    }

    /**
     * constructor for existing version file objects
     * @param controlFolderPath path to the folder containing versioning data
     * @param verisonFile the version file object
     */
    private JSONVersion(VersionControl<JSONVersion> versionControl, Path versionFile) throws IOException{
        super(versionControl);
        this.versionName = versionFile.getFileName().toString();
        String fileContents = ""; // ensure final var fileContents has a value, empty default

        // TODO: consider refactor to a more lazy retrieval
        for (String line: Files.readAllLines(versionFile)) {
            if (!line.contains(":")) continue;
            String[] pair = line.strip().split(":", 2);
            
            switch (pair[0]) {
                case "\"previous\"" -> {
                    this.previousVersions = Optional.of(pair[1].substring(pair[1].indexOf("[")+1, pair[1].lastIndexOf("]")))
                    .filter(Predicate.not(String::isEmpty))
                    .map(entries -> entries.split(","))
                    .map(entries -> Arrays.stream(entries)
                        .map(file -> file.substring(1, file.length()-1))
                        .map(Path::of)
                        .toArray(Path[]::new)
                    )
                    .orElse(new Path[]{});
                }
                case "\"next\"" -> {
                    this.nextVersions = Optional.of(pair[1].substring(pair[1].indexOf("[")+1, pair[1].lastIndexOf("]")))
                    .filter(Predicate.not(String::isEmpty))
                    .map(entries -> entries.split(","))
                    .map(entries -> Arrays.stream(entries)
                        .map(file -> file.substring(1, file.length()-1))
                        .map(Path::of)
                        .toArray(Path[]::new)
                    )
                    .orElse(new Path[]{});
                }
                case "\"data\"" -> {
                    fileContents = pair[1].substring(1, pair[1].length()-1);
                }
                default -> {

                }
            }
        }

        this.fileContents = fileContents;
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
    public void setPreviousVersions(Version<JSONVersion> version) {
        Set<Path> previousVersions = new HashSet<>(Arrays.asList(this.previousVersions));
        int originalLength = previousVersions.size();
        previousVersions.add(version.getPath().getFileName()); // getFileName to ensure consistency/remove duplicates
        this.savedVersion = previousVersions.size() == originalLength;
        if (!this.savedVersion) { // reduce unneccesary operation?
            this.previousVersions = previousVersions.toArray(Path[]::new);
        }
    }

    @Override
    public void setNextVersions(Version<JSONVersion> version) {
        Set<Path> nextVersions = new HashSet<>(Arrays.asList(this.nextVersions));
        int originalLength = nextVersions.size();
        nextVersions.add(version.getPath().getFileName()); // getFileName to ensure consistency/remove duplicates
        this.savedVersion = nextVersions.size() == originalLength;
        if (!this.savedVersion) { // reduce unneccesary operation?
            this.nextVersions = nextVersions.toArray(Path[]::new);
        }
    }

    /**
     * Creates/overrites the persistent versioning object in versioning folder 
     */
    @Override
    public void close() throws Exception {
        if (savedVersion) {
            return;
        }
        
        // if there is no change since last version, do not write a new file
        if (this.previousVersions.length == 1) {
            try (JSONVersion prevVersion = new JSONVersion(this.controller, this.previousVersions[0])) {
                if (this.fileContents == prevVersion.fileContents) return;
            }
        } 
        
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
            this.fileContents
        );

        Path versionsFilePath = versionControlFilePath.resolve("versions");
        for (Path prevPath: this.previousVersions) {
            try (JSONVersion prevVer = new JSONVersion(this.controller, versionsFilePath.resolve(prevPath))) {
                prevVer.setNextVersions(this);
            }
        }

        // Saving to the file
        versionsFilePath = Files.createDirectories(versionsFilePath);
        Path jsonFilePath = versionsFilePath.resolve(this.versionName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(jsonFilePath.toFile())) {
            fileOutputStream.write(jsonString.getBytes());
        }

        // Saving version as the current_version
        Files.writeString(currentVersion, versionName);
    }
}
