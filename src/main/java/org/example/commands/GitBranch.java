package org.example.commands;

import org.example.core.GitRefs;

public class GitBranch {

    public void process(final String gitDirectoryPath,
                        final String branchName) {
        GitRefs gitRefs = new GitRefs(gitDirectoryPath);
        gitRefs.createNewBranch(branchName);
    }
}
