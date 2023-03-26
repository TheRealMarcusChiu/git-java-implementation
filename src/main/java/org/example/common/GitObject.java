package org.example.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.util.Comparator;

@Data
@NoArgsConstructor
public class GitObject {

    public static final Comparator<GitObject> BY_WORKING_RELATIVE_PATH = Comparator.comparing(
            GitObject::getWorkingRelativePath,
            String::compareTo
    );

    private File file;
    private String sha1;

    private String workingRelativePath;

    @SneakyThrows
    public static GitObject fromWorking(final File workingFile,
                                        final String rootProjectPath) {
        GitObject gitObject = new GitObject();

        String relativePath = workingFile.getAbsolutePath().replace(rootProjectPath, "");
        byte[] byteArray = Files.readAllBytes(workingFile.toPath());
        String sha1 = GitSha1.toSHA1(byteArray);

        gitObject.setFile(workingFile);
        gitObject.setWorkingRelativePath(relativePath);
        gitObject.setSha1(sha1);

        return gitObject;
    }


    public static GitObject fromObjects(final File file,
                                        final String sha1) {
        GitObject gitObject = new GitObject();

        gitObject.setFile(file);
        gitObject.setSha1(sha1);

        return gitObject;
    }
}