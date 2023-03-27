package org.example.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitCommitObject implements GitObjectI {
    private String treeRootSha1;
    private String previousCommit;
    private String author;
    private LocalDateTime localDateTime;

    public GitCommitObject(final List<String> lines) {
        this.treeRootSha1 = secondPart(lines.get(0));
        this.previousCommit = secondPart(lines.get(1));
        this.author = secondPart(lines.get(2));
        this.localDateTime = LocalDateTime.parse(secondPart(lines.get(3)));
    }

    private String secondPart(final String str) {
        String[] parts = str.split("\\s+", 2);
        return parts[1];
    }

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
