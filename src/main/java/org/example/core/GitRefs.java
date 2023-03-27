package org.example.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;

public class GitRefs {

    private static final String HEAD_FILE_PATH = "/HEAD";
    private static final String REFS_HEADS_DIRECTORY_PATH = "refs/heads";
    private final File headFile;
    private final String gitDirectoryPath;

    public GitRefs(final String gitDirectoryPath) {
        this.gitDirectoryPath = gitDirectoryPath;
        this.headFile = new File(gitDirectoryPath + HEAD_FILE_PATH);
    }

    @SneakyThrows
    public void updateHead(final String sha1) {
        File currentBranchFile = getCurrentBranchFile();
        try (FileWriter writer = new FileWriter(currentBranchFile)) {
            writer.write(sha1);
        }
    }

    @SneakyThrows
    private File getCurrentBranchFile() {
        Head head = new Head(headFile);
        String branchPath = head.getCurrentBranchPath();
        return new File(gitDirectoryPath + "/" + branchPath);
    }

    @SneakyThrows
    public boolean switchBranch(final String branchName) {
        File branch = new File(gitDirectoryPath + "/" + REFS_HEADS_DIRECTORY_PATH + "/" + branchName);
        if (!branch.exists()) {
            return false;
        } else {
            Head head = Head.builder()
                    .currentBranchPath(REFS_HEADS_DIRECTORY_PATH + "/" + branchName)
                    .build();
            Files.write(headFile.toPath(), head.toString().getBytes());
            return true;
        }
    }

    public String getCurrentBranchName() {
        return new Head(headFile).getCurrentBranchName();
    }

    @SneakyThrows
    public void createNewBranch(final String branchName) {
        File currentBranchFile = getCurrentBranchFile();
        File newBranchFile = new File(gitDirectoryPath + "/" + REFS_HEADS_DIRECTORY_PATH + "/" + branchName);
        Files.copy(currentBranchFile.toPath(), newBranchFile.toPath());
    }

    @SneakyThrows
    public String getCurrentCommitSha1() {
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
    private static String getFirstLine(final File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Head {
        private String currentBranchPath;
        private String currentBranchName;
        private String temp;

        public Head(final File headFile) {
            String firstLine = getFirstLine(headFile);
            process(firstLine);
        }

        public Head(final String content) {
            process(content);
        }

        private void process(final String content) {
            String[] split = content.split(": ");
            currentBranchPath = split[1];
            int lastCommaIndex = currentBranchPath.lastIndexOf("/");
            currentBranchName = currentBranchPath.substring(lastCommaIndex + 1);
        }

        public String toString() {
            return "ref: " + currentBranchPath;
        }
    }
}
