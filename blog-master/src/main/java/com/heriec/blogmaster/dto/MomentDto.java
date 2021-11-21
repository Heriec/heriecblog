package com.heriec.blogmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomentDto {

    private Integer id;
    private String content;
    private Integer status;
}
