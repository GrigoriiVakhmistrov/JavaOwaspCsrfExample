package ru.netology.springsecurity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private long id;

    private String name;

    private String token;

    private String reskey;

    private String passwordHash;
}

