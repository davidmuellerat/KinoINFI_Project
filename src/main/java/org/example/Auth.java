package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public class Auth {
    public static User authenticate(String username, String password, ConnectionSource connectionSource) {
        try {
            Dao<User, Integer> userDao = DaoManager.createDao(connectionSource, User.class);
            User user = userDao.queryForEq("username", username).stream().findFirst().orElse(null);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
