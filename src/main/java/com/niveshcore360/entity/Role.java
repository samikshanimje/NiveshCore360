package com.niveshcore360.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a system security Role.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String name; // e.g., "ROLE_USER", "ROLE_ADMIN", "ROLE_ADVISOR"
}
