package org.example.commands;

import org.example.core.GitIndex;
import org.example.core.GitObject;
import org.example.core.GitObjects;
import org.example.core.GitWorkingCopy;

public class GitAdd {

    public void process(final String gitDirectoryPath,
                        final String rootProjectPath) {
        GitIndex gitIndex = new GitIndex(gitDirectoryPath);
        GitObjects gitObjects = new GitObjects(gitDirectoryPath);
        GitWorkingCopy gitWorkingCopy = new GitWorkingCopy();

        gitWorkingCopy.getAllWorkingFiles(rootProjectPath).stream()
                .map(file -> GitObject.fromWorking(file, rootProjectPath))
                .forEach(gitObject -> {
                    gitIndex.update(gitObject);
                    gitObjects.save(gitObject);
                });

        gitIndex.saveIndex();
    }
}
