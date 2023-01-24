package com.mxy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.common.R;
import com.mxy.dto.SetmealDto;
import com.mxy.entity.Category;
import com.mxy.entity.Setmeal;
import com.mxy.service.CategoryService;
import com.mxy.service.SetmealDishService;
import com.mxy.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/*
* 套餐管理
*/
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    //套餐分页查询
    @GetMapping("/page")
    public R<Page>page(int page,int pageSize,String name){
        Page<Setmeal>pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto>setmealDtoPage=new Page<>();
        LambdaQueryWrapper<Setmeal>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal>records=pageInfo.getRecords();
        List<SetmealDto>list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId=item.getCategoryId();
            Category category=categoryService.getById(categoryId);
            if(category!=null){
                String categoryName=category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    //套餐回显
    @GetMapping("/{id}")
    public R<SetmealDto>get(@PathVariable Long id){
        SetmealDto setmealDto=setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    //编辑修改
    @PutMapping
    public R<String>update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    //修改状态
    @PostMapping("/status/{status}")
    public R<String>status(@PathVariable Integer status,@RequestParam List<Long>ids){
        for(Long id:ids){
            setmealService.updateStatusById(status,id);
        }
        return R.success("状态修改成功");
    }


    //删除套餐
    @DeleteMapping
    public R<String>delete(@RequestParam List<Long>ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    //跟据条件查询套餐
    @GetMapping("/list")
    public R<List<Setmeal>>list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal>list=setmealService.list(queryWrapper);
        return R.success(list);
    }
}
