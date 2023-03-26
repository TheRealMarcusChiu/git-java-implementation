package org.example.core;

import lombok.SneakyThrows;

import java.security.MessageDigest;

public class GitSha1 {

    @SneakyThrows
    public static String toSHA1(final byte[] byteData) {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(byteData);

        // Convert the hash to a hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
