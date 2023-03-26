package org.example.commands;

import org.example.common.GitIndex;
import org.example.common.GitObject;
import org.example.common.GitObjects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.example.Main.GIT_REPO_NAME;

public class GitAddImpl {

    private static final List<String> FILE_NAMES_TO_IGNORE = List.of(
            ".git",
            GIT_REPO_NAME,
            "target"
    );

    public void process(final String gitDirectoryPath,
                        final String rootProjectPath) {
        GitIndex gitIndex = new GitIndex(gitDirectoryPath);
        GitObjects gitObjects = new GitObjects(gitDirectoryPath);

        getAllWorkingFiles(rootProjectPath).stream()
                .map(file -> GitObject.fromWorking(file, rootProjectPath))
                .sorted(GitObject.BY_WORKING_RELATIVE_PATH)
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
            if (!FILE_NAMES_TO_IGNORE.contains(fileAndDirectory.getName())) {
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
