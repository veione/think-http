package com.think.http.handler;

import com.think.http.annotation.GetMapping;
import com.think.http.annotation.PostMapping;
import com.think.http.annotation.WebHandler;
import com.think.http.constant.StatusCode;
import com.think.http.context.HttpContext;

@WebHandler
public class UserHandler {

    @GetMapping("/user/{uid}")
    public Object getUserInfo(Long uid, HttpContext context) {
        System.out.println("uid = [" + uid + "], context = [" + context + "]");
        String age = context.getParameter("age");
        String name = context.getParameter("name");
        System.out.println("name = [" + name + "], age = [" + age + "]");
        return new User(uid);
    }

    @GetMapping("/user/{uid}/{name}")
    public void updateUser(Long uid, String name) {
        System.out.println("uid = [" + uid + "], name = [" + name + "]");
    }

    @PostMapping("/user/{uid}")
    public Object updateUserInfo(Long uid) {
        System.out.println("uid = [" + uid + "]");
        return StatusCode.SUCCESS;
    }

    static class User {
        public final long uid;

        public User(long uid) {
            this.uid = uid;
        }
    }
}
