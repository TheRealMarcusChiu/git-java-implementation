package org.example.commands;

import lombok.SneakyThrows;
import org.example.core.GitCommitObject;
import org.example.core.GitIndex;
import org.example.core.GitObject;
import org.example.core.GitObjects;
import org.example.core.GitRefs;
import org.example.core.GitTreeNode;

import java.util.Optional;

public class GitCommit {

    public void process(final String gitDirectoryPath) {
        GitIndex index = new GitIndex(gitDirectoryPath);
        GitTreeNode treeRoot = index.getTreeRoot();
        GitObjects gitObjects = new GitObjects(gitDirectoryPath);
        GitRefs gitRefs = new GitRefs(gitDirectoryPath);

        saveTreeNodes(treeRoot, gitObjects);

        String treeRootSha1 = treeRoot.getEntrySha1();
        String previousCommit = gitRefs.getCurrentSha1();
        GitCommitObject gitCommitObject = GitCommitObject.builder()
                .treeRootSha1(treeRootSha1)
                .previousCommit(previousCommit)
                .author("Marcus, Chiu")
                .build();
        gitObjects.save(gitCommitObject);

        gitRefs.updateHead(gitCommitObject.getSha1());
    }

    @SneakyThrows
    private void saveTreeNodes(final GitTreeNode gitTreeNode,
                               final GitObjects gitObjects) {
        String sha1 = gitTreeNode.getEntrySha1();
        Optional<GitObject> gitObjectOptional = gitObjects.findBySha1(sha1);
        if (gitObjectOptional.isEmpty()) {
            gitObjects.save(gitTreeNode);
            gitTreeNode.getTreeNodes().forEach(treeNode -> saveTreeNodes(treeNode, gitObjects));
        }
    }
}
