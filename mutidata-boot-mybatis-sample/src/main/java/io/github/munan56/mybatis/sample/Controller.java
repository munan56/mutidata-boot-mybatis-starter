package io.github.munan56.mybatis.sample;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.github.munan56.mybatis.sample.test2.mapper.AccountMapper;
import io.github.munan56.mybatis.sample.test1.mapper.UserMapper;
import io.github.munan56.mybatis.sample.mode.Account;
import io.github.munan56.mybatis.sample.mode.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: munan
 * @Date: 2020/7/11 4:37 下午
 */
@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private Service service;

    @GetMapping("/user")
    public Page<User> page(Integer start , Integer end){
        PageHelper.startPage(start,end);
        Page<User> users = userMapper.listUser();
        long total = users.getTotal();
        List<User> result = users.getResult();
        Page page = new Page();
        page.setTotal(total);
        return users;
    }
    @RequestMapping("/account")
    public Page<Account> accounts(){
        PageHelper.startPage(1,1);
        return  accountMapper.list();
    }
    @RequestMapping("/insert/user")
    public void test(){
         service.test();
    }
    @RequestMapping("/insert/user1")
    public void test1(){
         service.test1();
    }
    @RequestMapping("/insert/account")
    public void test2(){
         service.test2();
    }
    @RequestMapping("/insert/account1")
    public void test21(){
         service.test21();
    }

}
