package com.heriec.blogmaster.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  文章细节字段
 *  增加了所属分类和标签字段
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDto {
    Integer id;
    String title;
    String summary;
    String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
    int readNum;
    int likeNum;
    int top;
    int articleStatus;

    List<String> categories;
    List<String> tags;

}
