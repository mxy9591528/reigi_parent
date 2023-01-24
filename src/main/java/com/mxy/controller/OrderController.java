package com.mxy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.common.R;
import com.mxy.entity.Orders;
import com.mxy.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
* 订单
*/
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    //用户下单
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    //订单管理
    @GetMapping("/userPage")
    public R<Page>userPage(int page,int pageSize){
        return R.success(orderService.getPage(page,pageSize));
    }

    //再来一单
    @PostMapping("/again")
    public R<String>again(@RequestBody Orders orders){
        orderService.again(orders);
        return R.success("再来一单");
    }

    //管理端订单明细
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){
        return R.success(orderService.page(page,pageSize,number,beginTime,endTime));
    }

    //外卖订单派送
    @PutMapping
    public R<String>send(@RequestBody Orders orders){
        Long id=orders.getId();
        Integer status=orders.getStatus();
        LambdaQueryWrapper<Orders>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,id);
        Orders orders1=orderService.getOne(queryWrapper);
        orders1.setStatus(status);
        orderService.updateById(orders1);
        return R.success("派送成功");
    }
}
