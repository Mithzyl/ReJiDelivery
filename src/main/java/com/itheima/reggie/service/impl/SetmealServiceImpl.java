package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {

    @Resource
    private SetMealDishService setMealDishService;

    @Override
    @Transactional()
    public void saveWithDishes(SetmealDto dto) {
        // DTO继承自SetMeal 首先保存SetMeal相关信息
        // 再得到新增的id 保存SetMealDish
        this.save(dto);

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getName, dto.getName());
        Setmeal setmeal = this.getOne(setmealLambdaQueryWrapper);

        // 优化: 判断， 查询不到该套餐则返回失败
        if(!ObjectUtils.isEmpty(setmeal)){
            Long setmealId = setmeal.getId();

            List<SetmealDish> setmealDishes = dto.getSetmealDishes();

            for(SetmealDish dish : setmealDishes){
                dish.setSetmealId(setmealId);
            }

            setMealDishService.saveBatch(setmealDishes);

        }

    }

    @Override
    @Transactional()
    public void updateWithDishes(SetmealDto dto) {
        // 修改套餐信息
        this.saveOrUpdate(dto);

        Long setMealId = dto.getId();

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setMealId);

        setMealDishService.remove(setmealDishLambdaQueryWrapper);

        List<SetmealDish> lists = dto.getSetmealDishes();

        for(SetmealDish dish : lists){
            dish.setSetmealId(setMealId);
        }

        // 保存菜品列表
        setMealDishService.saveBatch(lists);
    }

    @Override
    public void removeWithDishes(List<Long> ids) {
        for(Long mealId : ids){

            // 删除套餐信息
            // 启售时不能删除
            LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealLambdaQueryWrapper.eq(Setmeal::getId, mealId);

            Setmeal setmeal = this.getOne(setmealLambdaQueryWrapper);
            Integer setmealStatus = setmeal.getStatus();
            if(setmealStatus == 1){
                throw new CustomException("套餐启售时无法删除");
            }

            this.removeById(mealId);

            // 删除套餐对应菜单信息
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();

            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, mealId);

            setMealDishService.remove(setmealDishLambdaQueryWrapper);
        }
    }
}
