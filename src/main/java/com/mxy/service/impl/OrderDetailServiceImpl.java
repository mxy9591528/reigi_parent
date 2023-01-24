package com.mxy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.entity.User;
import com.mxy.mapper.UserMapper;
import com.mxy.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
