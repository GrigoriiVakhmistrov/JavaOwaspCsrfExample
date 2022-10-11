package ru.netology.springsecurity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthenticatedUser {
    private String name;

    private long isAuthenticated;
}
