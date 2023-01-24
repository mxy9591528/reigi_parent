package com.mxy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.entity.ShoppingCart;
import com.mxy.mapper.ShoppingCartMapper;
import com.mxy.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
