package com.example.webfluxshop.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table(name = "tokens",schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RefreshToken {

    @Id
    @Column("id")
    private Long id;
    @Column("username")
    private String username;
    @Column("token")
    private String token;
    @Column("duration")
    private Date duration;
}
