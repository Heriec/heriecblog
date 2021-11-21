package com.heriec.blogmaster.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heriec.blogmaster.mapper.VisitCountMapper;
import com.heriec.blogmaster.pojo.VisitCount;
import com.heriec.blogmaster.service.VisitCountService;
import com.heriec.blogmaster.utils.IPUtils;
import com.heriec.blogmaster.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class VisitCountServiceImpl extends ServiceImpl<VisitCountMapper, VisitCount> implements VisitCountService {

    @Autowired
    VisitCountMapper visitCountMapper;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    IPUtils ipUtils;

    @Override
    public VisitCount staticUP(String route, HttpServletRequest request) {
        String realIp = ipUtils.getRealIp(request);

        // 先统计访客量
        String key="blog:UV";
        String key2="blog:PV";
        boolean b = redisUtils.hasKey(key);
        boolean b2=redisUtils.hasKey(key2);
        // 没访问过
        if(b==false||b2==false){
            VisitCount visitCount = visitCountMapper.selectById(1);
            // redis中添加值
            redisUtils.set("blog:UV",String.valueOf(visitCount.getUv()));
            redisUtils.set("blog:PV",String.valueOf(visitCount.getPv()));


        }

        redisUtils.sSet("blog:ip",realIp);
        redisUtils.sSet("blog:ip:route",realIp+":"+route);

        String s = (String) redisUtils.get("blog:UV");
        int uv = Integer.parseInt(s)+(int)redisUtils.sGetSetSize("blog:ip");
        s = (String) redisUtils.get("blog:PV");
        int pv = Integer.parseInt(s)+(int)redisUtils.sGetSetSize("blog:ip:route");
        return new VisitCount(1,uv,pv);


    }
}
