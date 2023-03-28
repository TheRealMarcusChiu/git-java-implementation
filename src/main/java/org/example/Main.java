package org.example;

import org.example.commands.GitAdd;
import org.example.commands.GitBranch;
import org.example.commands.GitBranchCreate;
import org.example.commands.GitCheckout;
import org.example.commands.GitCommit;
import org.example.commands.GitInit;

import java.io.IOException;

public class Main {

    public static final String GIT_REPO_NAME = ".git-impl";
    public static final String USER_DIRECTORY_PATH = System.getProperty("user.dir");
    public static final String GIT_DIRECTORY_PATH = System.getProperty("user.dir") + "/" + GIT_REPO_NAME;

    public static void main(String[] args) throws IOException {
        String arg1 = args[0];
//        String arg1 = "checkout";

        if ("init".equals(arg1)) {
            new GitInit().init(GIT_DIRECTORY_PATH);
        } else if ("add".equals(arg1)) {
            new GitAdd().process(GIT_DIRECTORY_PATH, USER_DIRECTORY_PATH);
        } else if ("commit".equals(arg1)) {
            new GitCommit().process(GIT_DIRECTORY_PATH);
        } else if ("branch".equals(arg1)) {
            if (args.length == 1) {
                new GitBranch().process(GIT_DIRECTORY_PATH);
            } else if (args.length == 2) {
                String arg2 = args[1];
                new GitBranchCreate().process(GIT_DIRECTORY_PATH, arg2);
            }
        } else if ("checkout".equals(arg1)) {
            String arg2 = args[1];
//            String arg2 = "second";
            new GitCheckout().process(GIT_DIRECTORY_PATH, arg2);
        }
    }
}