package com.example.aoapay.dao;


import com.example.aoapay.config.RedisAnimal;
import com.example.aoapay.table.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;
import java.util.Timer;

@Repository
public class AuthDao extends RedisAnimal<User> {
    public AuthDao(){
        super(User.class);
    }
    public void pushUser(User user){
        super.deleteAllBy("id", user.getId());
        super.save(user);
//        super.save(user,60 * 15);
    }
    public void removeUser(User user){
        super.delete(user);
    }
    public User findUserByToken(String token) {
        return super.findAllByFirst("token",token);
    }
    public User findUserByUserId(String userId) {
        return super.findAllByFirst("id", userId);
    }
}
