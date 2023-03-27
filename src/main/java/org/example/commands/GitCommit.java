package org.example.commands;

import lombok.SneakyThrows;
import org.example.core.GitCommitObject;
import org.example.core.GitIndex;
import org.example.core.GitObject;
import org.example.core.GitObjects;
import org.example.core.GitRefs;
import org.example.core.GitTreeNode;

import java.time.LocalDateTime;
import java.util.Optional;

public class GitCommit {

    public void process(final String gitDirectoryPath) {
        GitIndex index = new GitIndex(gitDirectoryPath);
        GitTreeNode treeRoot = index.getTreeRoot();
        GitObjects gitObjects = new GitObjects(gitDirectoryPath);
        GitRefs gitRefs = new GitRefs(gitDirectoryPath);

        String currentCommitSha1 = gitRefs.getCurrentCommitSha1();
        String currentTreeRootSha1 = null;
        Optional<GitCommitObject> commitObject = gitObjects.findGitCommitObject(currentCommitSha1);
        if (commitObject.isPresent()) {
            currentTreeRootSha1 = commitObject.get().getTreeRootSha1();
        }

        if (treeRoot.getEntrySha1().equals(currentTreeRootSha1)) {
            System.out.println("Everything up to date!");
        } else {
            saveTreeNodes(treeRoot, gitObjects);

            String treeRootSha1 = treeRoot.getEntrySha1();
            String previousCommit = gitRefs.getCurrentCommitSha1();
            GitCommitObject gitCommitObject = GitCommitObject.builder()
                    .treeRootSha1(treeRootSha1)
                    .previousCommit(previousCommit)
                    .author("Marcus, Chiu")
                    .localDateTime(LocalDateTime.now())
                    .build();
            gitObjects.save(gitCommitObject);

            gitRefs.updateHead(gitCommitObject.getSha1());
        }
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
