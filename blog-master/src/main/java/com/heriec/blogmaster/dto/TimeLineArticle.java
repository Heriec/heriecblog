package com.heriec.blogmaster.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 归档时间线的文章信息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeLineArticle {

    private Integer id;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date publishTime;

}
