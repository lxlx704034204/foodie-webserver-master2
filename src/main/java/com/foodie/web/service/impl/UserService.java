package com.foodie.web.service.impl;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.core.generic.GenericDao;
import com.foodie.core.generic.GenericServiceImpl;
import com.foodie.web.dao.UserMapper;
import com.foodie.web.model.User;
import com.foodie.web.service.IUserService;

/**
 * �û�Serviceʵ����
 *
 * @author StarZou
 * @since 2014��7��5�� ����11:54:24
 */
@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
	public User selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return userMapper.selectByPrimaryKey(id);
	}
    
	@Override
	public User selectByUsername(String username) {
		// TODO Auto-generated method stub
		return userMapper.selectByUsername(username);
	}

	@Override
	public void insert(User user) { 
		userMapper.insert(user);
	}

	@Override
	public User selectByPhone(String phone) {
		// TODO Auto-generated method stub
		return userMapper.selectByPhone(phone);
	}

	@Override
	public int updateByPrimaryKeySelective(User user) {
		// TODO Auto-generated method stub
		return userMapper.updateByPrimaryKeySelective(user);
	}

}
