package VersionControl;

import java.nio.file.Path;

/**
 * Base class for all Versioning Stratagies
 * version file object 
 */
public abstract class Version implements AutoCloseable{
    /**
     * Thrown when a version file or object is not found
     */
    public class VersionNotFoundException extends Exception {}

    public final String title; // name of target file
    protected Path filePath; // path of version controlled file
    protected Path versionControlFilePath; // path of overarching version control folder
    // TODO: consider usefulness v
    protected Path currentVersion; // path of file specifying the current version
    protected Path[] previousVersions; // the immediate previous version
    protected Path[] nextVersions; // the immediate next version
    
    /**
     * Default constructor for Version 
     * @param   versionControlFilePath
     *          the path of the directory containing everything needed
     */
    public Version(VersionControl<? extends Version> versionControl){
        this.versionControlFilePath = versionControl.getVersionControlPath();
        
        String[] sections = this.versionControlFilePath.toString().split("\\.");

        this.title = sections[sections.length -2].split("_")[0];

        this.filePath = versionControl.getFilePath();

        this.currentVersion = this.versionControlFilePath.resolve("current_version");
    }

    /**
     * returns the path of the versioning object
     * @return path of the represented versioning object
     */
    protected abstract Path getPath();
    
    /**
     * retrieves the immediate previous versioning objects
     * @return Array of verison objects
     */
    protected abstract Version[] getPreviousVersions();
    
    /**
     * retrieves the immediate following versioning objects
     * @return Array of verison objects
     */
    protected abstract Version[] getNextVersions();
    
    /**
     * setter method for previous version
     * @param   version
     *          version to set as previous version
     * @return
     */
    protected abstract boolean setPreviousVersions(Version version);
    
    protected abstract boolean setNextVersions(Version version);
    
    public static void appendNewVersionToCurrentBranch(Path versionControlPath) throws Exception{
        throw new UnsupportedOperationException("Unimplemented method 'appendNewVersionToCurrentBranch'");
    };
}
