package org.example.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class GitTreeNode implements GitObjectI {

    private static final String ENTRY_DELIMITER = " ";
    private String entryPermissions;
    private EntryType entryType;
    private String entrySha1;
    private String entryName;

    private final Map<String, GitTreeNode> entries = new TreeMap<>();

    public List<GitTreeNode> getTreeNodes() {
        return this.entries.values().stream()
                .filter(entry -> entry.getEntryType().equals(EntryType.TREE))
                .collect(Collectors.toList());
    }

    public void computeSha1() {
        if (entryType.equals(EntryType.TREE)) {
            entries.values().stream()
                    .filter(node -> node.entryType.equals(EntryType.TREE))
                    .forEach(GitTreeNode::computeSha1);
            List<String> sha1s = entries.values().stream().map(GitTreeNode::getEntrySha1)
                    .collect(Collectors.toList());
            StringBuilder result = new StringBuilder();
            for (String s : sha1s) {
                result.append(s);
            }
            entrySha1 = GitSha1.toSHA1(result.toString().getBytes());
        }
    }

    public GitTreeNode(final String line) {
        String[] strings = line.split(ENTRY_DELIMITER);
        this.setEntryType(EntryType.valueOf(strings[0]));
        this.setEntrySha1(strings[1]);
        this.setEntryName(strings[2]);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (GitTreeNode entry : entries.values()) {
            str.append(entry.getEntryType())
                    .append(ENTRY_DELIMITER)
                    .append(entry.getEntrySha1())
                    .append(ENTRY_DELIMITER)
                    .append(entry.getEntryName())
                    .append("\n");
        }
        return str.toString();
    }

    public void add(final List<String> path,
                    final String sha1) {
        if (path.size() == 1) {
            String fileName = path.get(0);
            GitTreeNode nodeBlob = GitTreeNode.builder()
                    .entryName(fileName)
                    .entrySha1(sha1)
                    .entryType(EntryType.BLOB)
                    .entryPermissions(null)
                    .build();
            entries.put(fileName, nodeBlob);
        } else {
            String directoryName = path.get(0);
            GitTreeNode nodeTree = entries.get(directoryName);
            if (nodeTree == null) {
                nodeTree = GitTreeNode.builder()
                        .entryPermissions(null)
                        .entryType(EntryType.TREE)
                        .entryName(directoryName)
                        .entrySha1(null)
                        .build();
                entries.put(directoryName, nodeTree);
            }
            path.remove(0);
            nodeTree.add(path, sha1);
        }
    }

    @Override
    public String getContent() {
        return toString();
    }

    @Override
    public String getSha1() {
        return entrySha1;
    }

    public enum EntryType {
        BLOB,
        TREE
    }
}
