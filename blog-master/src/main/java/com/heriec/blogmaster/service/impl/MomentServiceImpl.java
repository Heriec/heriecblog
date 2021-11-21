package com.heriec.blogmaster.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.heriec.blogmaster.mapper.MomentMapper;
import com.heriec.blogmaster.pojo.Moment;
import com.heriec.blogmaster.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

}
