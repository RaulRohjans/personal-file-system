package com.rohjans.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * This class is a static helper class design to hold Password related methods.
 *
 * @author Raul Rohjans 202100518
 */
public class PasswordManager {
    /**
     * This method creates a new password hash with a predefined hash length.
     *
     * @param password Password in plain text to be hashed.
     * @return HashedPassword.
     */
    public static String createPasswordHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * This method compares a plain text password against its hashed variant to see
     * if they match.
     *
     * @param candidate Plain text password to validate.
     * @param hashedPassword The hashed password to compare the candidate against.
     * @return true if the password matches or false if they are different.
     */
    public static boolean checkPasswordWithHash(String candidate, String hashedPassword) {
        if(candidate == null || candidate.isEmpty()) return false;

        return BCrypt.checkpw(candidate, hashedPassword);
    }
}
