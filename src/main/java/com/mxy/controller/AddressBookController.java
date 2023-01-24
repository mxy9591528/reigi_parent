package com.mxy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mxy.common.BaseContext;
import com.mxy.common.R;
import com.mxy.entity.AddressBook;
import com.mxy.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* 地址簿管理
*/
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    //新增
    @PostMapping
    public R<AddressBook>save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook：{}",addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    //设置默认地址
    @PutMapping("default")
    public R<AddressBook>setDefault(@RequestBody AddressBook addressBook){
        log.info("addressBook：{}",addressBook);
        LambdaUpdateWrapper<AddressBook> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    //跟据id查询地址
    @GetMapping("/{id}")
    public R get(@PathVariable Long id){
        AddressBook addressBook=addressBookService.getById(id);
        if(addressBook!=null){
            return R.success(addressBook);
        }
        return R.error("未找到对象");
    }

    //查询默认地址
    @GetMapping("default")
    public R<AddressBook>getDefault(){
        LambdaQueryWrapper<AddressBook>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook=addressBookService.getOne(queryWrapper);
        if(addressBook==null){
            return R.error("未找到对象");
        }
        return R.success(addressBook);
    }

    //查询指定用户的全部地址
    @GetMapping("/list")
    public R<List<AddressBook>>list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook：{}",addressBook);
        LambdaQueryWrapper<AddressBook>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        return R.success(addressBookService.list(queryWrapper));
    }
}
