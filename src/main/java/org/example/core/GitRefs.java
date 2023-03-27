package org.example.core;

import lombok.Data;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Optional;

public class GitRefs {

    private static final String HEAD_FILE_PATH = "/HEAD";
    private final File headFile;
    private final String gitDirectoryPath;

    public GitRefs(final String gitDirectoryPath) {
        this.gitDirectoryPath = gitDirectoryPath;
        this.headFile = new File(gitDirectoryPath + HEAD_FILE_PATH);
    }

    @SneakyThrows
    public void updateHead(final String sha1) {
        try (BufferedReader reader = new BufferedReader(new FileReader(headFile))) {
            String firstLine = reader.readLine();
            Head head = new Head(firstLine);
            String branchPath = head.getCurrentBranchPath();
            File currentBranchFile = new File(gitDirectoryPath + "/" + branchPath);

            try (FileWriter writer = new FileWriter(currentBranchFile)) {
                writer.write(sha1);
            }
        }
    }

    @SneakyThrows
    public String getCurrentSha1() {
        String firstLine = getFirstLine(headFile);
        Head head = new Head(firstLine);
        String branchPath = head.getCurrentBranchPath();
        File currentBranchFile = new File(gitDirectoryPath + "/" + branchPath);

        if (currentBranchFile.exists()) {
            return getFirstLine(currentBranchFile);
        } else {
            return null;
        }
    }

    @SneakyThrows
    private String getFirstLine(final File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine();
        }
    }

    @Data
    public static class Head {
        String currentBranchPath;

        public Head(final String content) {
            String[] split = content.split(": ");
            currentBranchPath = split[1];
        }

        public String toString() {
            return "ref: " + currentBranchPath;
        }
    }
}
