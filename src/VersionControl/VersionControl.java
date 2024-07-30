package VersionControl;

public class VersionControl{
    private String file_path;

    public VersionControl(String file_path) {
        this.file_path = file_path;
    }

    public String get_file_path() {
        return file_path;
    }
}
