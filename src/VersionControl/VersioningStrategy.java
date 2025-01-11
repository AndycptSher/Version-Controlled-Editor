package VersionControl;

import java.io.IOException;

/**
 * Versioning algorithm stratagy interface
 */
public interface VersioningStrategy <T extends Version<T>> {
    /**
     * creates new version object from the current state of the targte file
     * @param   versionControlPath
     *          path of the versioning data file
     * @return the version object 
     * @throws  IOException
     *          thrown when error occurs during writing of file
     * @throws  Exception
     *          thrown for other exceptions
     */
    public T createVersion(VersionControl<T> versionControl) throws IOException, Exception;
}
