package org.example.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.example.Main.GIT_REPO_NAME;

public class GitAddImpl {

    public void process(final String gitDirectoryPath,
                        final String userDirectoryPath) {

    }

    private void process(String directoryPath) {
        File[] fileAndDirectories = new File(directoryPath).listFiles();

        List<File> files = new ArrayList<>();
        List<File> directories = new ArrayList<>();

        for (File fileAndDirectory : fileAndDirectories) {
            if (fileAndDirectory.getName().equals(GIT_REPO_NAME)) {
                break;
            } else if (fileAndDirectory.isFile()) {
                files.add(fileAndDirectory);
            } else if (fileAndDirectory.isDirectory()) {
                directories.add(fileAndDirectory);
            }
        }
    }
}
