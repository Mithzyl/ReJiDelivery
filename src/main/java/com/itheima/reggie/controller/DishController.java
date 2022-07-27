package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dto){
        dishService.saveWithDishFlavor(dto);

        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        Page<DishDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, dishLambdaQueryWrapper);

        // 拷贝属性
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) ->{

            DishDto dishDto = new DishDto();
            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            String name1 = category.getName();

            dishDto.setName(name1);

            BeanUtils.copyProperties(item, dishDto);

            return dishDto;

        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    @GetMapping("/{id}")
    // 查询菜品详情
    public R<DishDto> getOne(@PathVariable("id") Long id){
        Dish dish = dishService.getById(id);

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        dishDto.setFlavors(list);

        return R.success(dishDto);
    }

    @PutMapping()
    public R<String> updateDish(@RequestBody DishDto dishDto){
        dishService.updateWithDishFlavor(dishDto);

        return R.success("修改成功");
    }

    // 查询同一品类下的菜品 eg. 川菜
    @GetMapping("/list")
    public R<List<DishDto>> getDishInCategory(@RequestParam("categoryId") Long id){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, id);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        List<DishDto> dishDtos = new ArrayList<>();

        List<Dish> dishList = dishService.list(queryWrapper);

        for(Dish dish : dishList){
            DishDto dto = new DishDto();

            BeanUtils.copyProperties(dish, dto);

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());

            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dto.setFlavors(dishFlavors);

            dishDtos.add(dto);
        }

        return R.success(dishDtos);
    }

}
