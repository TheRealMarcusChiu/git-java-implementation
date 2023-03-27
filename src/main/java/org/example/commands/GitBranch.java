package org.example.commands;

import org.example.core.GitRefs;

public class GitBranch {

    public void process(final String gitDirectoryPath) {
        GitRefs gitRefs = new GitRefs(gitDirectoryPath);
        String currentBranchName = gitRefs.getCurrentBranchName();
        System.out.println("You are currently on branch '" + currentBranchName + "'");
    }
}
