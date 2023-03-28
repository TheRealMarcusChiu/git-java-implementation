package org.example.core;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

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

    public GitTreeNode findGitTreeNodeRoot(final String sha1) {
        final GitTreeNode gitTreeNode = GitTreeNode.builder().build();

        String firstPart = sha1.substring(0, 2);
        String secondPart = sha1.substring(2);

        String directoryPath = objectsDirectoryPath + "/" + firstPart;
        String filePath = directoryPath + "/" + secondPart;

        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                List<String> lines = getLines(file);
                Map<String, GitTreeNode> entries = lines.stream()
                        .map(GitTreeNode::new)
                        .collect(toMap(GitTreeNode::getEntryName, Function.identity()));
                entries.values().forEach(entry -> {
                    if (entry.getEntryType().equals(GitTreeNode.EntryType.TREE)) {
                        GitTreeNode gtn = findGitTreeNodeRoot(entry.getSha1());
                        gtn.getEntries().forEach((name, e) -> entry.getEntries().put(name, e));
                    }
                });
                entries.forEach((key, value) -> gitTreeNode.getEntries().put(key, value));
            }
        }

        if (gitTreeNode.getEntryType() == null) { // Then this is the root
            gitTreeNode.setEntryType(GitTreeNode.EntryType.TREE);

            // Set SHA1
            List<String> sha1s = gitTreeNode.getEntries().values().stream().map(GitTreeNode::getEntrySha1)
                    .collect(Collectors.toList());
            StringBuilder result = new StringBuilder();
            for (String s : sha1s) {
                result.append(s);
            }
            gitTreeNode.setEntrySha1(GitSha1.toSHA1(result.toString().getBytes()));
        }
        return gitTreeNode;
    }

    public Optional<GitCommitObject> findGitCommitObject(final String sha1) {
        if (sha1 == null || "null".equals(sha1)) {
            return Optional.empty();
        }

        GitCommitObject gitCommitObject = null;

        String firstPart = sha1.substring(0, 2);
        String secondPart = sha1.substring(2);

        String directoryPath = objectsDirectoryPath + "/" + firstPart;
        String filePath = directoryPath + "/" + secondPart;

        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                List<String> lines = getLines(file);
                gitCommitObject = new GitCommitObject(lines);
            }
        }

        return Optional.ofNullable(gitCommitObject);
    }

    @SneakyThrows
    private List<String> getLines(final File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            return lines;
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
