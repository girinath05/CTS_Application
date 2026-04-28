package com.imageinfosystems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;                        

import com.imageinfosystems.model.User;
import com.imageinfosystems.util.Db;

public class UserDaoImpl implements UserDao {

    @Override
    public long insert(User user) throws Exception {

        String sql = "insert into users(name,email,password_hash,gender,country,created_at) values(?,?,?,?,?,?)";

        try (
            Connection con = Db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getCountry());
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // ✅ fixed

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return 0;
    }
}