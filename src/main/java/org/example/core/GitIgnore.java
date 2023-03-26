package org.example.core;

import java.util.List;

import static org.example.Main.GIT_REPO_NAME;

public class GitIgnore {

    private static final List<String> FILE_NAMES_TO_IGNORE = List.of(
            ".git",
            GIT_REPO_NAME,
            "target",
            ".idea"
    );

    public static List<String> get() {
        return FILE_NAMES_TO_IGNORE;
    }
}
