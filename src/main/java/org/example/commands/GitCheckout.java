package org.example.commands;

import lombok.SneakyThrows;
import org.example.core.GitCommitObject;
import org.example.core.GitIndex;
import org.example.core.GitObject;
import org.example.core.GitObjects;
import org.example.core.GitRefs;
import org.example.core.GitTreeNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.example.core.GitTreeNode.EntryType.BLOB;
import static org.example.core.GitTreeNode.EntryType.TREE;

public class GitCheckout {

    public void process(final String gitDirectoryPath,
                        final String rootProjectPath,
                        final String branchName) {
        GitRefs gitRefs = new GitRefs(gitDirectoryPath);
        boolean response = gitRefs.switchBranch(branchName);
        if (response) {
            GitTreeNode workingCopyTreeNode = getWorkingCopyTreeNode(gitDirectoryPath);
            GitTreeNode branchCopyTreeNode = getBranchCopyTreeNode(gitDirectoryPath, gitRefs);

            GitObjects gitObjects = new GitObjects(gitDirectoryPath);
            update(workingCopyTreeNode, branchCopyTreeNode, "", gitObjects);

            new GitAdd().process(gitDirectoryPath, rootProjectPath);

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

    // workinCopy is assumed to have to same name as branchCopy
    @SneakyThrows
    private void update(final GitTreeNode workinCopy,
                        final GitTreeNode branchCopy,
                        final String currentPath,
                        final GitObjects gitObjects) {
        if (!workinCopy.getSha1().equals(branchCopy.getSha1())) {
            if (TREE.equals(workinCopy.getEntryType()) && TREE.equals(branchCopy.getEntryType())) {
                bothTrees(workinCopy, branchCopy, currentPath, gitObjects);
            } else if (BLOB.equals(workinCopy.getEntryType()) && BLOB.equals(branchCopy.getEntryType())) {
                File branchFile = gitObjects.findBySha1(branchCopy.getSha1()).get().getFile();
                File workinFile = new File(currentPath + workinCopy.getEntryName());
                // update workingCopy file contents as copy of branchCopy
                Files.copy(branchFile.toPath(), workinFile.toPath(), REPLACE_EXISTING);
            } else if (BLOB.equals(workinCopy.getEntryType()) && TREE.equals(branchCopy.getEntryType())) {
                File workinFile = new File(currentPath + workinCopy.getEntryName());
                // delete workingCopy's file
                Files.deleteIfExists(workinFile.toPath());
                // create branchCopy's directory empty
                // create all other files underneath it
                createDirectoryAndContentsRecursively(workinCopy.getEntryName(),
                        branchCopy,
                        currentPath,
                        gitObjects);
            } else if (TREE.equals(workinCopy.getEntryType()) && BLOB.equals(branchCopy.getEntryType())) {
                File branchFile = gitObjects.findBySha1(branchCopy.getSha1()).get().getFile();
                File workinDirectory = new File(currentPath + workinCopy.getEntryName());
                // delete workingCopy's directory
                deleteDirectory(workinDirectory);
                // create branchCopy's file in place
                File workinFile = new File(currentPath + workinCopy.getEntryName());
                Files.copy(branchFile.toPath(), workinFile.toPath());
            }
        } else {
            // DO NOTHING!!!!
        }
    }

    @SneakyThrows
    private void bothTrees(final GitTreeNode workinCopy,
                           final GitTreeNode branchCopy,
                           final String currentPath,
                           final GitObjects gitObjects) {
        Set<String> collect = Stream
                .concat(workinCopy.getEntries().keySet().stream(), branchCopy.getEntries().keySet().stream())
                .collect(Collectors.toSet());
        for (String entryName : collect) {
            GitTreeNode wEntry = workinCopy.getEntries().get(entryName);
            GitTreeNode bEntry = branchCopy.getEntries().get(entryName);

            if (wEntry != null && bEntry != null) {
                update(wEntry, bEntry, currentPath, gitObjects);
            } else if (wEntry == null && bEntry != null) {
                // create copy of bEntry onto working-copy
                if (TREE.equals(bEntry.getEntryType())) {
                    // create empty directory, and everything underneath it
                    createDirectoryAndContentsRecursively(entryName,
                            bEntry,
                            currentPath,
                            gitObjects);
                } else {
                    // create file
                    File branchFile = gitObjects.findBySha1(bEntry.getSha1()).get().getFile();
                    File workinFile = new File(currentPath + entryName);
                    // update workingCopy file contents as copy of branchCopy
                    Files.copy(branchFile.toPath(), workinFile.toPath());
                }
            } else if (wEntry != null && bEntry == null) {
                // delete wEntry
                if (wEntry.getEntryType().equals(BLOB)) {
                    File workinFile = new File(currentPath + entryName);
                    Files.deleteIfExists(workinFile.toPath());
                } else {
                    File workingDirectory = new File(currentPath + entryName);
                    deleteDirectory(workingDirectory);
                }
            }
        }
    }

    @SneakyThrows
    private void createDirectoryAndContentsRecursively(final String directoryName,
                                                       final GitTreeNode branchCopy,
                                                       final String currentPath,
                                                       final GitObjects gitObjects) {
        // Create empty directory
        Files.createDirectory(Paths.get(currentPath + directoryName));

        for (Map.Entry<String, GitTreeNode> entry : branchCopy.getEntries().entrySet()) {
            String entryName = entry.getKey();
            GitTreeNode value = entry.getValue();
            if (value.getEntryType().equals(TREE)) {
                createDirectoryAndContentsRecursively(entryName,
                        value,
                        currentPath + directoryName + "/",
                        gitObjects);
            } else {
                GitObject branchFileCopy = gitObjects.findBySha1(value.getSha1()).get();
                Path workinFileCopy = Paths.get(currentPath + directoryName + "/" + entryName);
                Files.copy(branchFileCopy.getFile().toPath(), workinFileCopy);
            }
        }
    }

    private void deleteDirectory(final File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
