package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;

    // 新增菜品 保存对应口味数据
    @Override
    @Transactional()
    public void saveWithDishFlavor(DishDto dto) {
        // 保存菜品基本信息到菜品表
        // 继承自Dish 可直接保存
        this.save(dto);


        // 保存菜品口味数据到DishFlavor
        Long id = dto.getId();  // 菜品Id
        List<DishFlavor> flavors = dto.getFlavors();
        for(DishFlavor flavor : flavors){
            flavor.setDishId(id);
        }

        // stream流方法
//        flavors.stream().map((item) ->{
//            item.setDishId(id);
//            return item;
//        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional()
    public void updateWithDishFlavor(DishDto dto) {

        this.updateById(dto);

        // 保存菜品口味到DishFlavor

        // 菜品id
        Long dishId = dto.getId();

        // 清理口味数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        // 添加口味数据
        List<DishFlavor> lists = dto.getFlavors();
        for(DishFlavor flavor : lists){
            flavor.setDishId(dishId);
        }

        dishFlavorService.saveBatch(lists);

    }
}
