package org.example.core;

import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Data
@Builder
public class GitCommitObject implements GitObjectI {
    private String treeRootSha1;
    private String previousCommit;
    private String author;
    private final LocalDateTime localDateTime = LocalDateTime.now();

    public String toString() {
        return "TREE " + treeRootSha1 + "\n"
                + "previousCommit " + previousCommit + "\n"
                + "author " + author + "\n"
                + "localDateTime " + localDateTime;
    }

    @Override
    public String getContent() {
        return toString();
    }

    @Override
    public String getSha1() {
        String concat = treeRootSha1 + author + localDateTime;
        return GitSha1.toSHA1(concat.getBytes(StandardCharsets.UTF_8));
    }
}
