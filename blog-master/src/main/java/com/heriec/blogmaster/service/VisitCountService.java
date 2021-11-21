package com.heriec.blogmaster.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heriec.blogmaster.pojo.VisitCount;

import javax.servlet.http.HttpServletRequest;

public interface VisitCountService extends IService<VisitCount>  {

//    VisitCount getPVUV();

    /**
     * PV是Page View，访问量, 即页面浏览量或点击量，
     * UV（Unique Visitor）独立访客，统计1天内访问某站点的用户数
     * @param route
     * @param request
     * @return
     */
    VisitCount staticUP(String route, HttpServletRequest request);
}
