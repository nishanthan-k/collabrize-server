package com.collabrize.features.user.domain;

import java.time.LocalDateTime;
import com.collabrize.features.oauth.enums.OAuthProvider;
import com.collabrize.features.user.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "oauth_id") // Updated column name for clarity
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false) // Ensuring non-null names
    private String name;

    @Column(name = "avatar_url") // Explicit column naming
    private String avatarUrl;

    private String password; // Only used if password-based login is enabled

    @Column(name = "oauth_id", unique = true)
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
