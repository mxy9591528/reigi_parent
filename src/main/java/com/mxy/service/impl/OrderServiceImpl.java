package com.mxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.common.BaseContext;
import com.mxy.common.CustomException;
import com.mxy.dto.OrdersDto;
import com.mxy.entity.*;
import com.mxy.mapper.OrderMapper;
import com.mxy.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderService orderService;
    @Override
    @Transactional
    public void submit(Orders orders) {
        Long userId= BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList=shoppingCartService.list(queryWrapper);
        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车为空");
        }
        User user=userService.getById(userId);
        AddressBook addressBook=addressBookService.getById(orders.getAddressBookId());
        if(addressBook==null){
            throw new CustomException("地址信息为空");
        }
        Long orderId= IdWorker.getId();
        AtomicInteger amount=new AtomicInteger(0);
        List<OrderDetail>orderDetails=shoppingCartList.stream().map((item)->{
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getProvinceName()==null?"":addressBook.getProvinceName()+
                (addressBook.getCityName()==null?"":addressBook.getCityName())+
                (addressBook.getDetail()==null?"":addressBook.getDetail()));
        this.save(orders);
        orderDetailService.saveBatch(orderDetails);
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    @Transactional
    public Page getPage(int page, int pageSize) {
        Page<Orders>pageInfo=new Page<>(page,pageSize);
        Page<OrdersDto>ordersDtoPage=new Page<>();
        LambdaQueryWrapper<Orders>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        List<Orders>records=pageInfo.getRecords();
        List<OrdersDto>list=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            Long Id=item.getId();
            Orders orders=orderService.getById(Id);
            String number=orders.getNumber();
            LambdaQueryWrapper<OrderDetail>lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId,number);
            List<OrderDetail>orderDetailList=orderDetailService.list(lambdaQueryWrapper);
            int num=0;
            for(OrderDetail l:orderDetailList){
                num+=l.getNumber().intValue();
            }
            ordersDto.setSumNum(num);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);
        return ordersDtoPage;
    }

    @Override
    @Transactional
    public void again(Orders orders) {
        Long id = orders.getId();
        Orders orders1 = orderService.getById(id);
        long orderId = IdWorker.getId();
        orders1.setId(orderId);
        String number = String.valueOf(IdWorker.getId());
        orders1.setNumber(number);
        orders1.setOrderTime(LocalDateTime.now());
        orders1.setCheckoutTime(LocalDateTime.now());
        orders1.setStatus(2);
        orderService.save(orders1);
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> list = orderDetailService.list(queryWrapper);
        list.stream().map((item)->{
            long detailId = IdWorker.getId();
            item.setOrderId(orderId);
            item.setId(detailId);
            return item;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(list);
    }

    @Override
    public Page page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage=new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        if(beginTime!=null&&endTime!=null){
            queryWrapper.ge(Orders::getOrderTime,beginTime);
            queryWrapper.le(Orders::getOrderTime,endTime);
        }
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        List<Orders> records=pageInfo.getRecords();
        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            String name="用户"+item.getUserId();
            ordersDto.setUserName(name);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);
        return ordersDtoPage;
    }
}
