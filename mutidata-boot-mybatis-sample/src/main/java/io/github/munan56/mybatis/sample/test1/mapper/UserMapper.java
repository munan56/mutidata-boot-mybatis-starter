package io.github.munan56.mybatis.sample.test1.mapper;

import com.github.pagehelper.Page;
import io.github.munan56.mybatis.sample.mode.User;

/**
 * @Author: munan
 * @Date: 2020/7/11 4:34 下午
 */
public interface UserMapper {


    Page<User> listUser();

    int insert(User user);
}
