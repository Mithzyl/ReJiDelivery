package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> addToCart(@RequestBody ShoppingCart shoppingCart){

        // 获得当前操作的用户Id 从线程中获取
        Long currentUserId = BaseContext.getCurrentId();

        // 查询当前菜品是否在购物车中
        // 获取当前用户的购物车

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();

        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentUserId);

        // 查看当前加入购物车的商品是套餐还是单品
        // 将对应的id加入查询条件
        if(shoppingCart.getDishId() != null){
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else if (shoppingCart.getSetmealId() != null) {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // 查询当前购物车是否有该商品
        // 有则将该品的数量+1
        // 没有这添加到购物车中

        ShoppingCart cart = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if(!ObjectUtils.isEmpty(cart)){
            cart.setNumber(cart.getNumber() + 1);
        }else{
            shoppingCart.setUserId(currentUserId);
            shoppingCartService.save(shoppingCart);
        }


        return R.success(shoppingCart);
    }


    // 获取购物车商品集合
    
}
