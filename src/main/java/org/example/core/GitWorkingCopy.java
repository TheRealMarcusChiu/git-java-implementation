package org.example.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GitWorkingCopy {

    public List<File> getAllWorkingFiles(final String directoryPath) {
        File[] fileAndDirectories = new File(directoryPath).listFiles();

        List<File> files = new ArrayList<>();
        List<File> directories = new ArrayList<>();

        for (File fileAndDirectory : fileAndDirectories) {
            if (!GitIgnore.get().contains(fileAndDirectory.getName())) {
                if (fileAndDirectory.isFile()) {
                    files.add(fileAndDirectory);
                } else if (fileAndDirectory.isDirectory()) {
                    directories.add(fileAndDirectory);
                }
            }
        }

        directories.forEach(directory -> {
            List<File> subFiles = getAllWorkingFiles(directory.getAbsolutePath());
            files.addAll(subFiles);
        });

        return files;
    }
}
