package com.hanxiaocu.sample.controller;

import com.hanxiaocu.sample.domain.PO.User;
import com.hanxiaocu.sample.domain.VO.OrderPostForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * User: hanchenghai
 * Date: 2018/10/23 4:44 PM
 */

@Controller
@RequestMapping("/order")
public class OrderController {
    @PostMapping(path = "/save.json")
    @NotNull
    public @ResponseBody
    String saveOrder(OrderPostForm form) {
        return "success";
    }

    // 请求参数时Json
    @PostMapping(path = "/saveJsonOrder.json")
    @ResponseBody
    public String saveOrderByJson(@RequestBody User user) {
        return user.getName();
    }

    @GetMapping(path = "/info")
    public String info(Model model) {
        return "/order/orderForm";
    }
}
