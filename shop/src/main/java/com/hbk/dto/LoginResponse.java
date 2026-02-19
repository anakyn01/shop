package com.hbk.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String result;
    private String token;
    private String firstName;
    private String lastName;
}