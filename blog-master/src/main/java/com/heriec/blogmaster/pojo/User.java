package com.heriec.blogmaster.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class User implements Serializable {
    @TableId(type= IdType.AUTO)
    Integer id;
    String username;
    String password;
    String nick;//昵称
    String avatar;
    String introduction;
    String role;
}
