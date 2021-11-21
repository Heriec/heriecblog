package com.heriec.blogmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSelfModify {
    Integer id;
    String username;
    String oldPassword;
    String newPassword;
}
