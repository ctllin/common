package com.ctl.web.dubbo.controller;

import com.ctl.web.dubbo.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value="/customer")
public class CustomerController {
    @Resource(name="helloService")
    private HelloService helloService;
    @Resource(name="helloServiceConsumer")
    private HelloService helloServiceConsumer;
    Logger logger= LoggerFactory.getLogger(CustomerController.class);
    @RequestMapping(value="/say")
    public ModelAndView say(HttpServletRequest request, HttpServletResponse response){
        logger.info("--------------------------------say--------------------------------");
        String result = helloService.say("world");
        logger.info("--------------------------------"+result+"--------------------------------");
        return null;
    }

}
