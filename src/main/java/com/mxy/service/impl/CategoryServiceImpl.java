package com.mxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.common.CustomException;
import com.mxy.entity.Category;
import com.mxy.entity.Dish;
import com.mxy.entity.Setmeal;
import com.mxy.mapper.CategoryMapper;
import com.mxy.service.CategoryService;
import com.mxy.service.DishService;
import com.mxy.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    //根据id删除分类，删除之前需要进行判断
    @Override
    public void remove(Long id) {
        //查询是否关联菜品
        LambdaQueryWrapper<Dish>dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count=dishService.count(dishLambdaQueryWrapper);
        if(count>0){
            throw new CustomException("当前分类项关联菜品，不能删除");
        }

        //查询是否关联套餐
        LambdaQueryWrapper<Setmeal>setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        count=setmealService.count(setmealLambdaQueryWrapper);
        if(count>0){
            throw new CustomException("当前分类项关联了套餐，不能删除");
        }

        super.removeById(id);
    }
}
