package com.collabrize.features.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.collabrize.features.user.domain.User;
import com.collabrize.user.UserRepo;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepo userRepo;

    @GetMapping
    public String getUser() {
        var user = User.builder().email("email").build();
        return userRepo.save(user).toString();
    }

}
