package com.mxy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.entity.Setmeal;
import com.mxy.mapper.SetmealMapper;
import com.mxy.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
