package dev.lorenzharfst.managr.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {
    
    @PostMapping("/login")
    public void login(){
        // User should get through this endpoint whether they are
        // loging in or registering.
        throw new UnsupportedOperationException();
    }

    @PostMapping("/logout")
    public void logout(){
        throw new UnsupportedOperationException();
    }
}
