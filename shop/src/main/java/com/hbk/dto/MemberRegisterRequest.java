package com.hbk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String repeatPassword;
    private String gender;
    private String companyName;
    private String position;
    private String tel;
    private String address;
    private String detailAddress;
}
