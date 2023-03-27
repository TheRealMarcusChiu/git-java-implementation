package org.example.core;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class GitObjects {

    private static final String OBJECTS_DIRECTORY_PATH = "/objects";

    private final String objectsDirectoryPath;

    public GitObjects(final String gitDirectoryPath) {
        this.objectsDirectoryPath = gitDirectoryPath + OBJECTS_DIRECTORY_PATH;
    }

    @SneakyThrows
    public void save(final GitObjectI gitObjectI) {
        String sha1 = gitObjectI.getSha1();
        String content = gitObjectI.getContent();

        if (findBySha1(sha1).isPresent()) {
            return;
        }

        String firstPart = sha1.substring(0, 2);
        String secondPart = sha1.substring(2);

        String directoryPath = objectsDirectoryPath + "/" + firstPart;
        String filePath = directoryPath + "/" + secondPart;

        Files.createDirectories(Paths.get(directoryPath));
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
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
}
