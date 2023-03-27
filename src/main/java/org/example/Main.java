package org.example;

import org.example.commands.GitAdd;
import org.example.commands.GitCommit;
import org.example.commands.GitInit;

import java.io.IOException;

public class Main {

    public static final String GIT_REPO_NAME = ".git-impl";
    public static final String USER_DIRECTORY_PATH = System.getProperty("user.dir");
    public static final String GIT_DIRECTORY_PATH = System.getProperty("user.dir") + "/" + GIT_REPO_NAME;

    public static void main(String[] args) throws IOException {
        String arg = args[0];
//        String arg = "commit";

        if ("init".equals(arg)) {
            new GitInit().init(GIT_DIRECTORY_PATH);
        } else if ("add".equals(arg)) {
            new GitAdd().process(GIT_DIRECTORY_PATH, USER_DIRECTORY_PATH);
        } else if ("commit".equals(arg)) {
            new GitCommit().process(GIT_DIRECTORY_PATH);
        }
    }
}