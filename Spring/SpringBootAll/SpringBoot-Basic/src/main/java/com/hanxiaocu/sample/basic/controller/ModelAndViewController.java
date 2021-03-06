package com.hanxiaocu.sample.basic.controller;

import com.hanxiaocu.sample.basic.domain.PO.User;
import com.hanxiaocu.sample.basic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description:
 * User: hanchenghai
 * Date: 2018/10/23 4:12 PM
 */
@Controller
@RequestMapping("/model")
public class ModelAndViewController {

    @Autowired
    UserService userService;

    @GetMapping(path = "/view")
    public String addModelAttribute(Model model) {
        model.addAttribute("nihao");// 键名就是对象类型String  首字母小写 string=nihao
        User userInfo = userService.getUserById(1L);
        //model.addAttribute(userInfo);
        model.addAttribute("user", userInfo);
        System.out.println(model);
        return "/user/userInfo";
    }

    public static void main(String[] args) {
        Model model = new ConcurrentModel();
        model.addAttribute("nihao");
        model.addAttribute("hanxiaocu");
        model.addAttribute("enna");
        System.out.println(model);
    }
}
