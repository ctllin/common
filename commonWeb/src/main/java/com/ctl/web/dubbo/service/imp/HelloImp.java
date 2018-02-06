package com.ctl.web.dubbo.service.imp;

import com.ctl.web.common.mapper.CommonMapper;
import com.ctl.web.dubbo.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="helloService")
public class HelloImp implements HelloService{
    Logger logger= LoggerFactory.getLogger(HelloService.class);

    @Autowired(required = true)
    private CommonMapper commonMapper;
    @Override
    public String say(String name) {
        if(commonMapper!=null){
           logger.info(""+ commonMapper.selectBySql("select NOW()"));
        }
        return "HelloImp say:"+name;
    }
}
