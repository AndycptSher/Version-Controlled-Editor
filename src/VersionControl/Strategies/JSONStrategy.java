package VersionControl.Strategies;

import java.io.IOException;

import VersionControl.JSONVersion;
import VersionControl.VersionControl;
import VersionControl.VersioningStrategy;


public class JSONStrategy implements VersioningStrategy<JSONVersion> {

    @Override
    public JSONVersion createVersion(VersionControl<JSONVersion> versionControl)
            throws IOException, Exception {
        JSONVersion newVersion = new JSONVersion(versionControl);
        return newVersion;
    }
}
