package com.heriec.blogmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    Integer id;
    String username;
    String newPassword;
    String newRole;

    public UserDto(Integer id, String newPassword) {
        this.id = id;
        this.newPassword = newPassword;
    }
}
