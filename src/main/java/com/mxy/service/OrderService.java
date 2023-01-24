package com.mxy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);

    Page getPage(int page, int pageSize);

    void again(Orders orders);

    Page page(int page, int pageSize, String number, String beginTime, String endTime);
}
