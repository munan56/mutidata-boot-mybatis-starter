package io.github.munan56.mybatis.sample;

import io.github.munan56.mybatis.sample.mode.Account;
import io.github.munan56.mybatis.sample.mode.User;
import io.github.munan56.mybatis.sample.test1.mapper.UserMapper;
import io.github.munan56.mybatis.sample.test2.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: munan
 * @Date: 2020/7/11 5:02 下午
 */
@org.springframework.stereotype.Service
public class Service {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AccountMapper accountMapper;


    @Transactional(rollbackFor = Exception.class,transactionManager = "test1TransactionManager")
    public void test(){
        User user = new User();
        user.setName("11111111");
        int insert = userMapper.insert(user);
        System.out.println(insert);
        int i = 2/ 0;
    }
    @Transactional(rollbackFor = Exception.class,transactionManager = "test1TransactionManager")
    public void test1(){
        User user = new User();
        user.setName("11111111");
        int insert = userMapper.insert(user);
        System.out.println(insert);

    }
    @Transactional(rollbackFor = Exception.class,transactionManager = "test2TransactionManager")
    public void test2(){
        Account account = new Account();
        account.setName("11111111");
        int insert = accountMapper.insert(account);
        System.out.println(insert);
        int i = 2/ 0;
    }
    @Transactional(rollbackFor = Exception.class,transactionManager = "test2TransactionManager")
    public void test21(){
        Account account = new Account();
        account.setName("11111111");
        int insert = accountMapper.insert(account);
        System.out.println(insert);
    }

}
