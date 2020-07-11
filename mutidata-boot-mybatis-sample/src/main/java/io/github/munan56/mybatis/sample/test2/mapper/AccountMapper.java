package io.github.munan56.mybatis.sample.test2.mapper;

import com.github.pagehelper.Page;
import io.github.munan56.mybatis.sample.mode.Account;

/**
 * @Author: munan
 * @Date: 2020/7/11 4:34 下午
 */
public interface AccountMapper {


    Page<Account> list();

    int insert(Account account);
}
