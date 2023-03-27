package org.example.commands;

import org.example.core.GitRefs;

public class GitCheckout {

    public void process(final String gitDirectoryPath,
                        final String branchName) {
        GitRefs gitRefs = new GitRefs(gitDirectoryPath);
        boolean response = gitRefs.switchBranch(branchName);
        if (response) {
            // TODO update working-copy from tree node
            // Turn working-copy into tree node
            // Compare working-copy's tree-node to current-commit's tree-node and CRUD accordingly
            System.out.println("Switched to branch '" + branchName + "'");
        } else {
            System.out.println("error: pathspec '" + branchName + "' did not match any file(s) known to git");
        }
    }
}
