package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;

public interface DishService extends IService<Dish> {

    // 新增菜品 同时插入菜品的对应口味数据 操作dish dish_flavor表
    public void saveWithDishFlavor(DishDto dto);

    // 修改菜品
    public void updateWithDishFlavor(DishDto dto);
}
