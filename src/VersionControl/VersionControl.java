package VersionControl;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import VersionControl.Strategies.JSONStrategy;

// Exception imports
import java.io.IOException;


/**
 * Version Controller object to give unified API for file versioning
 */
public class VersionControl <T extends Version>{
    
    // Path and name of the file being version controlled
    private Path filePath;
    // Path and name for the version control folder
    private Path versionControlPath;
    // Stratagy used to store version history
    public final VersioningStrategy<T> stratagy;


    /**
     * Initalizes a Versioning file structure
     * <p> ...
     * <p> target file
     * <p> ... </p>
     * 
     * Into:
     * 
     * <p> ...
     * <p> target file
     * <p> .{@code target file}_version_control
     * <p> - {@code versions} versioning history
     * <p> - ...
     * <p> - {@code branches} file with all branches
     * <p> - {@code current_version} file indicating the current version being edited
     * <p> ...
     * 
     * 
     * @param filePath path string of the target file
     * @throws  InvalidPathException 
     *          if the {@code filepath} is invalid
     */
    public VersionControl(String filePath, VersioningStrategy<T> stratagyT) throws InvalidPathException {
        this.filePath = Paths.get(filePath);
        this.versionControlPath = this.filePath.getParent().resolve("." + this.filePath.getFileName() + "_version_control");
        // create dotdirectory in the same directory as the target file
        createVersionControlFolderifNotExists();
        stratagy = stratagyT;
    }
    
    /**
     * returns the target file's path
     * @return target file's path
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * returns the path of versioning control data
     * @return versioning control file's path
     */
    public Path getVersionControlPath() {
        return versionControlPath;
    }
    
    /**
     * gets the path of current version file in the current_version file
     * @return returns the version in the target file
     */
    public Path getCurrentVersion() throws IOException {
        return Path.of(Files.readString(versionControlPath.resolve("current_version")));
    }

    /**
     * Saves target file into versions folder by creating new version object/file
     * @throws  IOException
     *          thrown when unable to write to versioning file
     * @throws  Exception
     *          thrown for other miscellaneous errors
     */
    public void save() throws IOException, Exception {
        try (T _ = stratagy.createVersion(this)) {}
    }
    
    /**
     * loads a version into the target file
     * @param version
     */
    public void load(String version) {
        // TODO: Finish
    }
    
    private void createVersionControlFolderifNotExists() {
        if (Files.notExists(versionControlPath) ||
            Files.notExists(versionControlPath.resolve("branches")) ||
            Files.notExists(versionControlPath.resolve("current_version"))
            ) {
            try {
                Files.createDirectories(versionControlPath);

                Files.createDirectories(versionControlPath.resolve("versions"));

                try { Files.createFile(versionControlPath.resolve("branches"));
                } catch (FileAlreadyExistsException _) {}

                try { Files.createFile(versionControlPath.resolve("current_version"));
                } catch (FileAlreadyExistsException _) {}

            } catch (IOException _error) {
                System.err.println("Couldn't create version control folder");
            }
        }
    }

    public static void main(String[] args) throws Exception{
        System.out.println("Hi");
        VersionControl<JSONVersion> vc = new VersionControl<>("src/EditorUI/JavaEditor.java", new JSONStrategy());
        java.util.concurrent.TimeUnit.SECONDS.sleep(1);
        vc.save();
    }
}


