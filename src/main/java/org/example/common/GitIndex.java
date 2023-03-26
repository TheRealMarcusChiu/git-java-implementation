package org.example.common;

import lombok.Data;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class GitIndex {

    private static final String INDEX_FILE_PATH = "/index";

    private final String indexFilePath;

    @SneakyThrows
    public GitIndex(final String gitDirectoryPath) {
        this.indexFilePath = gitDirectoryPath + INDEX_FILE_PATH;
    }

    @SneakyThrows
    public void update(final GitObject gitObject) {
        String workingRelativePath = gitObject.getWorkingRelativePath();
        String sha1 = gitObject.getSha1();

        Index index = getIndex();
        index.getKeyValuePairs().put(workingRelativePath, sha1);

        saveIndex(index);
    }

    @SneakyThrows
    private Index getIndex() {
        Index index = new Index();

        File indexFile = new File(indexFilePath);
        indexFile.createNewFile();

        try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                int lastDelimiterIndex = line.lastIndexOf("/");
                String key = line.substring(0, lastDelimiterIndex);
                String value = line.substring(lastDelimiterIndex + 1);

                index.getKeyValuePairs().put(key, value);
            }
        }

        return index;
    }

    @SneakyThrows
    private void saveIndex(final Index index) {
        File indexFile = new File(indexFilePath);
        indexFile.createNewFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile))) {
            Map<String, String> keyValuePairs = index.getKeyValuePairs();

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
        private Map<String, String> keyValuePairs = new HashMap<>();
    }
}
