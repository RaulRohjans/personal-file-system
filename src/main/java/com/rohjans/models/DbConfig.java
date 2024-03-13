package com.rohjans.models;

/**
 * This is a "class" (it's defined as a record) that represents the database connection configuration object.
 *
 * @author Raul Rohjans 202100518
 */
public record DbConfig(String hostname, String port, String username, String password) {}
