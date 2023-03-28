package org.example.commands;

import lombok.SneakyThrows;
import org.example.core.GitCommitObject;
import org.example.core.GitIndex;
import org.example.core.GitObjects;
import org.example.core.GitRefs;
import org.example.core.GitTreeNode;

import java.util.Optional;

public class GitCheckout {

    public void process(final String gitDirectoryPath,
                        final String branchName) {
        GitRefs gitRefs = new GitRefs(gitDirectoryPath);
        boolean response = gitRefs.switchBranch(branchName);
        if (response) {
            GitTreeNode workingCopyTreeNode = getWorkingCopyTreeNode(gitDirectoryPath);
            GitTreeNode branchCopyTreeNode = getBranchCopyTreeNode(gitDirectoryPath, gitRefs);
            // Compare working-copy's tree-node to current-commit's tree-node and CRUD accordingly
            System.out.println("Switched to branch '" + branchName + "'");
        } else {
            System.out.println("error: pathspec '" + branchName + "' did not match any file(s) known to git");
        }
    }

    private GitTreeNode getWorkingCopyTreeNode(final String gitDirectoryPath) {
        GitIndex index = new GitIndex(gitDirectoryPath);
        return index.getTreeRoot();
    }

    @SneakyThrows
    private GitTreeNode getBranchCopyTreeNode(final String gitDirectoryPath,
                                              final GitRefs gitRefs) {
        GitObjects gitObjects = new GitObjects(gitDirectoryPath);
        String currentCommitSha1 = gitRefs.getCurrentCommitSha1();
        Optional<GitCommitObject> gitCommitObject = gitObjects.findGitCommitObject(currentCommitSha1);

        if (gitCommitObject.isPresent()) {
            GitCommitObject gitCommitObject1 = gitCommitObject.get();
            String treeRootSha1 = gitCommitObject1.getTreeRootSha1();
            return gitObjects.findGitTreeNodeRoot(treeRootSha1);
        } else {
            throw new Exception("CRITICAL ERROR - missing commit object with SHA1=" + currentCommitSha1);
        }
    }
}
