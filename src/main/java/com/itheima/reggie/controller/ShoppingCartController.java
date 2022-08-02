package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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
            shoppingCartService.updateById(cart);
        }else{
            shoppingCart.setUserId(currentUserId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }


        return R.success(shoppingCart);
    }


    // 获取购物车商品集合
    @GetMapping("/list")
    public R<List<ShoppingCart>> getCartList(){
        // Get current user id
        Long currentId = BaseContext.getCurrentId();

        // create a new query wrapper to get the cart of the current user
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        return R.success(list);
    }

    // 清空购物车
    @Transactional
    @DeleteMapping("clean")
    public R<String> cleanShoppingCart(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);

        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        if(ObjectUtils.isEmpty(list)){
            return R.error("购物车不存在");
        }else{
            shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        }

        return R.success("购物车已清空");
    }
    
}

