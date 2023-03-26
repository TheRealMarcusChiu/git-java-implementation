package org.example.commands;

import org.example.core.GitIgnore;
import org.example.core.GitIndex;
import org.example.core.GitObject;
import org.example.core.GitObjects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GitAdd {

    public void process(final String gitDirectoryPath,
                        final String rootProjectPath) {
        GitIndex gitIndex = new GitIndex(gitDirectoryPath);
        GitObjects gitObjects = new GitObjects(gitDirectoryPath);

        getAllWorkingFiles(rootProjectPath).stream()
                .map(file -> GitObject.fromWorking(file, rootProjectPath))
                .forEach(gitObject -> {
                    gitIndex.update(gitObject);
                    gitObjects.save(gitObject);
                });

        gitIndex.saveIndex();
    }

    private List<File> getAllWorkingFiles(final String directoryPath) {
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
