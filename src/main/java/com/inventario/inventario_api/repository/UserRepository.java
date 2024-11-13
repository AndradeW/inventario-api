package com.inventario.inventario_api.repository;

import com.inventario.inventario_api.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {
    public List<User> findAll() {

        List<User> users = new ArrayList<>();

        User user = new User("Pablo", "@", "123456");
        users.add(user);

        return users;
    }
}
