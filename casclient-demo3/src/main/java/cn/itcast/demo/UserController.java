package cn.itcast.demo;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @RequestMapping("/findUserName")
    public void findUserName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
    }
}
