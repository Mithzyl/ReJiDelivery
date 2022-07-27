package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetController {

    @Resource
    private SetmealService setmealService;

    @Resource
    private SetMealDishService setMealDishService;

    // 分页查看套餐列表
    @GetMapping("/page")
    public R<Page<Setmeal>> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(name != null, Setmeal::getName, name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo);

        return R.success(pageInfo);
    }

    // 新增套餐
    @PostMapping()
    public R<String> save(@RequestBody SetmealDto dto){

        setmealService.saveWithDishes(dto);

        return R.success("添加成功");

    }

    // 查询套餐详情
    @GetMapping("/{id}")
    public R<SetmealDto> getMealDetail(@PathVariable("id") Long id){

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //setmealLambdaQueryWrapper.eq(Setmeal::getId, id);

        //System.out.println("当前查询的套餐ID: " + id);
        Setmeal setmeal = setmealService.getById(id);

        //log.info(setmeal.getName());

        SetmealDto mealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, mealDto);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setMealDishService.list(setmealDishLambdaQueryWrapper);

        mealDto.setSetmealDishes(setmealDishes);

        return R.success(mealDto);

    }

    // 修改套餐
    @PutMapping()
    public R<String> modifyMeal(@RequestBody SetmealDto dto){
        setmealService.updateWithDishes(dto);

        return R.success("修改成功");
    }

    // 删除套餐
    @DeleteMapping()
    public R<String> delete(@RequestParam("ids") List<Long>ids){
        setmealService.removeWithDishes(ids);

        return R.success("删除成功");
    }

    // 修改状态
    @PostMapping("/status/{status}")
    @Transactional()
    public R<String> modifyStatus(@PathVariable("status") Integer status, @RequestParam("ids") List<Long> ids){
        // 无法批量删除
        for(Long id : ids){
            LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setMealLambdaQueryWrapper.eq(Setmeal::getId, id);

            Setmeal setmeal = setmealService.getOne(setMealLambdaQueryWrapper);


            if(status == 0){
                setmeal.setStatus(0);
            }else{
                setmeal.setStatus(1);
            }

            setmealService.saveOrUpdate(setmeal);

            return R.success("修改状态成功");
        }

        return R.success("修改成功");
    }

    // 获取套餐菜单
    @GetMapping("list")
    public R<List<Setmeal>> list(@RequestParam("categoryId") Long id) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);

        List<Setmeal> lists = this.setmealService.list(setmealLambdaQueryWrapper);

//        if(!ObjectUtils.isEmpty(lists)){
//            return R.success(lists);
//        }
//        else {
//            return R.error("无套餐");
//        }
//    }

        return R.success(lists);
    }

}
