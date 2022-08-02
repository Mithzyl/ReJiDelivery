package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.service.OrdersService;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrdersService ordersService;

    @Resource
    private OrderService orderService;

    @Resource
    private ShoppingCartService shoppingCartService;

    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, Integer name){
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();

        ordersLambdaQueryWrapper.like(name != null, Orders::getId, name);

        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(pageInfo, ordersLambdaQueryWrapper);

        return R.success(pageInfo);
    }

    // Submit order function
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders orders ){

        orderService.submit(orders);
        return R.success("提交成功");
    }

}
