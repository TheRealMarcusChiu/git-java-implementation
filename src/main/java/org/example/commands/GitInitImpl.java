package org.example.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GitInitImpl {

    public void init(final String gitDirectoryPath) throws IOException {
        File file = new File(gitDirectoryPath);

        if (file.exists() && file.isDirectory()) {
            System.out.println("git repository already initialized");
        } else {
            Files.createDirectories(Paths.get(gitDirectoryPath));
            Files.createDirectories(Paths.get(gitDirectoryPath + "/objects"));
            Files.createDirectories(Paths.get(gitDirectoryPath + "/refs/heads"));

            Path head = Paths.get(gitDirectoryPath + "/HEAD");
            Files.write(head, List.of("ref: refs/heads/master"), StandardCharsets.UTF_8);

            System.out.println("Initialized empty Git repository in " + gitDirectoryPath);
        }
    }
}
