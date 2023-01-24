package com.mxy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.common.R;
import com.mxy.dto.DishDto;
import com.mxy.entity.Category;
import com.mxy.entity.Dish;
import com.mxy.entity.DishFlavor;
import com.mxy.service.CategoryService;
import com.mxy.service.DishFlavorService;
import com.mxy.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
* 菜品管理
*/
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;
    //新增菜品
    @PostMapping
    public R<String>save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page>page(int page,int pageSize,String name){
        Page<Dish>pageInfo=new Page<>(page,pageSize);
        Page<DishDto>dishDtoPage=new Page<>();
        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish>records=pageInfo.getRecords();
        List<DishDto>list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId=item.getCategoryId();
            Category category=categoryService.getById(categoryId);
            if(category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    //编辑回显
    @GetMapping("/{id}")
    public R<DishDto>get(@PathVariable Long id){
        DishDto dishDto=dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    //编辑修改
    @PutMapping
    public R<String>update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        //清理全部
//        Set keys=redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        //清理某一分类
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    //修改状态
    @PostMapping("/status/{status}")
    public R<String > status(@PathVariable Integer status,@RequestParam List<Long>ids){
        for(Long id:ids){
            dishService.updateStatusById(status,id);
        }
        return R.success("状态修改成功");
    }

    //删除菜品
    @DeleteMapping
    public R<String>delete(@RequestParam List<Long>ids){
        for(Long id:ids){
            dishService.removeById(id);
        }
        return R.success("删除成功");
    }

    //跟据条件查询对应菜品数据
    @GetMapping("/list")
    public R<List<DishDto>>list(Dish dish){
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        List<DishDto>dishDtoList= (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }
        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish>list=dishService.list(queryWrapper);
        dishDtoList=list.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId=item.getCategoryId();
            Category category=categoryService.getById(categoryId);
            if(category!=null){
                String categoryName=category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId=item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor>dishFlavorList=dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }
}
