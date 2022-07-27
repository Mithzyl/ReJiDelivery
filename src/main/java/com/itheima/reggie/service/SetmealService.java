package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDishes(SetmealDto dto);

    void updateWithDishes(SetmealDto dto);

    void removeWithDishes(List<Long> ids);
}
