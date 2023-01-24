package com.mxy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mxy.common.BaseContext;
import com.mxy.common.R;
import com.mxy.entity.ShoppingCart;
import com.mxy.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


/*
* 购物车
*/
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    //添加购物车
    @PostMapping("/add")
    public R<ShoppingCart>add(@RequestBody ShoppingCart shoppingCart){
        Long currentId= BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        Long dishId=shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1= shoppingCartService.getOne(queryWrapper);
        if(shoppingCart1!=null){
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartService.updateById(shoppingCart1);
        }else{
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCart1=shoppingCart;
        }
        return R.success(shoppingCart1);
    }

    //减少购物车
    @PostMapping("/sub")
    public R<ShoppingCart>sub(@RequestBody ShoppingCart shoppingCart){
        Long setmealId=shoppingCart.getSetmealId();
        Long dishId=shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if(setmealId!=null){
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }else{
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        ShoppingCart shoppingCart1=shoppingCartService.getOne(queryWrapper);
        Integer number= shoppingCart1.getNumber();
        if(number==1){
            shoppingCartService.remove(queryWrapper);
        }else{
            shoppingCart1.setNumber(number-1);
            shoppingCartService.updateById(shoppingCart1);
        }
        return R.success(shoppingCart1);
    }

    //查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>>list(){
        LambdaQueryWrapper<ShoppingCart>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart>list=shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String>clean(){
        LambdaQueryWrapper<ShoppingCart>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
}
