package org.orderpulse.orderpulsebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user in the system.
 * This class maps to the 'users' table in the database and contains user authentication
 * and identification information.
 *
 * @see Role for possible user roles
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    /**
     * Unique identifier for the user.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for authentication.
     * Cannot be null and must be unique across all users.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Encrypted password for authentication.
     * Cannot be null and should be stored in hashed form.
     */
    @Column(nullable = false)
    private String password;

    /**
     * User's email address.
     * Cannot be null and must be unique across all users.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * User's role in the system.
     * Stored as a string representation of the Role enum.
     */
    @Enumerated(EnumType.STRING)
    private Role role;
}