package com.heriec.blogmaster.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heriec.blogmaster.pojo.Tag;

import java.util.List;

public interface TagService extends IService<Tag> {
    /**
     * 查询所有标签
     *
     * @return
     */
    List<Tag> selectList();

    /**
     * 根据tag_id查询标签名字
     *
     * @param list
     * @return
     */
    List<String> selectTagById(List<Integer> list);

    /**
     * 根据标签名查询标签id
     *
     * @param list
     * @return
     */
    List<Integer> selectIdByName(List<String> list);

}
