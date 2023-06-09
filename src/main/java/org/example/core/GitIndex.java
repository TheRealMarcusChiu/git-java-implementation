package org.example.core;

import lombok.Data;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GitIndex {

    private static final String INDEX_FILE_PATH = "/index";

    private final String indexFilePath;
    private final Index index;

    public GitTreeNode getTreeRoot() {
        GitTreeNode rootTree = GitTreeNode.builder()
                .entryName("root")
                .entryType(GitTreeNode.EntryType.TREE)
                .entryPermissions(null)
                .entrySha1(null)
                .build();

        this.index.keyValuePairs.forEach((key, value) -> {
            List<String> files = new ArrayList<>(Arrays.asList(key.split("/")));
            rootTree.add(files, value);
        });

        rootTree.computeSha1();

        return rootTree;
    }

    @SneakyThrows
    public GitIndex(final String gitDirectoryPath) {
        this.indexFilePath = gitDirectoryPath + INDEX_FILE_PATH;
        this.index = getIndex();
    }

    public void clearIndex() {
        this.index.getKeyValuePairs().clear();
    }

    @SneakyThrows
    public void update(final GitObject gitObject) {
        String workingRelativePath = gitObject.getWorkingRelativePath();
        String sha1 = gitObject.getSha1();

        this.index.getKeyValuePairs().put(workingRelativePath, sha1);
    }

    @SneakyThrows
    private Index getIndex() {
        Index index = new Index();

        File indexFile = new File(indexFilePath);
        indexFile.createNewFile();

        try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                int lastDelimiterIndex = line.lastIndexOf(" ");
                String key = line.substring(0, lastDelimiterIndex);
                String value = line.substring(lastDelimiterIndex + 1);

                index.getKeyValuePairs().put(key, value);
            }
        }

        return index;
    }

    @SneakyThrows
    public void saveIndex() {
        File indexFile = new File(indexFilePath);
        indexFile.createNewFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile))) {
            Map<String, String> keyValuePairs = this.index.getKeyValuePairs();

            for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String line = key + " " + value;
                writer.write(line + System.getProperty("line.separator"));
            }
        }
    }

    @Data
    private static class Index {
        private Map<String, String> keyValuePairs = new TreeMap<>();
    }
}
