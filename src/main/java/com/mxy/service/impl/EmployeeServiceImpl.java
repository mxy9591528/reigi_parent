package com.mxy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.entity.Employee;
import com.mxy.mapper.EmployeeMapper;
import com.mxy.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
