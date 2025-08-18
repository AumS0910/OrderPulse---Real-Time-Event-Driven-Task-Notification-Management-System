package org.orderpulse.orderpulsebackend.entity;

/**
 * Enumeration of possible user roles in the system.
 * Used to define access levels and permissions for different types of users.
 */
public enum Role {
    /**
     * Regular user role with basic permissions
     */
    USER,
    
    /**
     * Administrator role with elevated permissions
     */
    ADMIN
}