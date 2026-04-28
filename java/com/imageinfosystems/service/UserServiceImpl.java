package com.imageinfosystems.service;

import java.security.MessageDigest;

import com.imageinfosystems.dao.UserDao;
import com.imageinfosystems.model.User;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public RegistractionResult register(RegisterRequest req) {

        long id = 0;

        try {
            String hash = sha256(req.getPassword());

            User user = new User();
            user.setName(req.getName().trim());
            user.setEmail(req.getEmail().trim().toLowerCase());
            user.setPasswordHash(hash);
            user.setGender(safe(req.getGender()));
            user.setCountry(safe(req.getCountry()));
            // ✅ No need to set createdAt here — DAO sets it as Timestamp directly

            id = userDao.insert(user);

            return RegistractionResult.success(
                "Registration Success!! ID " + id
            );

        } catch (Exception e) {
            e.printStackTrace();

            return RegistractionResult.fail(
                "Registration Failed!! Please Try Again!!"
            );
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String sha256(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] b = md.digest(s.getBytes("UTF-8"));

        StringBuilder sb = new StringBuilder();

        for (byte x : b) {
            sb.append(String.format("%02x", x));
        }

        return sb.toString();
    }
}