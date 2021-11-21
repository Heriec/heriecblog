package com.heriec.blogmaster.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RegisterDto {
    String username;
    String password;
    String nick;
}
