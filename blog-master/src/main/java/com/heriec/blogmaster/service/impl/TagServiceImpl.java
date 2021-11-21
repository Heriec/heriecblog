package com.heriec.blogmaster.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heriec.blogmaster.mapper.TagMapper;
import com.heriec.blogmaster.pojo.Tag;
import com.heriec.blogmaster.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper,Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> selectList() {
        List<Tag> selectList = tagMapper.selectList(null);
        return selectList;
    }

    @Override
    public List<String> selectTagById(List<Integer> list) {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.select("name")
                .in("id",list);
        List<Tag> tags = tagMapper.selectList(wrapper);
        List<String> res = new ArrayList<>();
        for(Tag tag:tags){
            res.add(tag.getName());
        }
        return res;
    }

    @Override
    public List<Integer> selectIdByName(List<String> list) {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.select("id")
                .in("name",list);
        List<Tag> tags = tagMapper.selectList(wrapper);
        List<Integer> res = new ArrayList<>();
        for(Tag tag:tags){
            res.add(tag.getId());
        }
        return res;
    }

}
