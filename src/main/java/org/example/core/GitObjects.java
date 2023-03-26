package org.example.core;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class GitObjects {

    private static final String OBJECTS_DIRECTORY_PATH = "/objects";

    private final String objectsDirectoryPath;

    public GitObjects(final String gitDirectoryPath) {
        this.objectsDirectoryPath = gitDirectoryPath + OBJECTS_DIRECTORY_PATH;
    }

    public Optional<GitObject> findBySha1(final String sha1) {
        GitObject gitObject = null;

        String firstPart = sha1.substring(0, 2);
        String secondPart = sha1.substring(2);

        String directoryPath = objectsDirectoryPath + "/" + firstPart;
        String filePath = directoryPath + "/" + secondPart;

        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                gitObject = GitObject.fromObjects(file, sha1);
            }
        }

        return Optional.ofNullable(gitObject);
    }

    @SneakyThrows
    public void save(final GitObject gitObject) {
        if (findBySha1(gitObject.getSha1()).isPresent()) {
            return;
        }

        String sha1 = gitObject.getSha1();

        String firstPart = sha1.substring(0, 2);
        String secondPart = sha1.substring(2);

        String directoryPath = objectsDirectoryPath + "/" + firstPart;
        String filePath = directoryPath + "/" + secondPart;

        Files.createDirectories(Paths.get(directoryPath));
        Files.copy(gitObject.getFile().toPath(), Paths.get(filePath));
    }
}
