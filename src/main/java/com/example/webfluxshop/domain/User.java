package com.example.webfluxshop.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",schema = "public")
@ToString
public class User {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("roles")
    private List<String> roles;

    @Column("enabled")
    private Boolean enabled;

    @Column("created_at")
    private ZonedDateTime createdAt;
}
