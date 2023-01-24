package com.mxy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.entity.Dish;
import com.mxy.mapper.DishMapper;
import com.mxy.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
