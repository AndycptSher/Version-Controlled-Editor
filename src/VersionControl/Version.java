package VersionControl;

import java.nio.file.Path;

public abstract class Version {
    public class VersionNotFoundException extends Exception {
    /*
    * thrown when a version file or object is not found
    */
    }
    protected String title; // name of file
    protected Path filePath; // path of version controlled file
    protected Path versionControlFilePath; // path of overarching version control folder
    protected Path currentVersion; // path of file specifying the current version
    protected String[] nextVersions;
    protected String[] previousVersions;
    
    protected Version(Path versionControlFilePath){
        this.versionControlFilePath = versionControlFilePath;
        
        String[] sections = this.versionControlFilePath.toString().split(".");
        this.title = sections[sections.length -1].split("_")[0];

        this.filePath = this.versionControlFilePath.getParent().resolve(this.title);
        
        this.currentVersion = this.versionControlFilePath.resolve("current_version");
    }
    
    protected abstract Version[] getPreviousVersions();
    
    protected abstract Version[] getNextVersions();
    
    protected abstract boolean setPreviousVersions(Version version);
    
    protected abstract boolean setNextVersions(Version version);
    
    public static void appendNewVersionToCurrentBranch(Path versionControlPath) throws Exception{
        throw new UnsupportedOperationException("Unimplemented method 'appendNewVersionToCurrentBranch'");
    };
}
