package dev.lorenzharfst.managr.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dev.lorenzharfst.managr.objects.member.Member;
import dev.lorenzharfst.managr.objects.member.MemberRepository;

@Controller
public class AuthenticationController {
    @Autowired
    JdbcUserDetailsManager userDetailsService;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    MemberRepository memberRepository;

    @PostMapping("/register")
    public void register(@RequestParam String username, @RequestParam String password) {
        UserDetails user = User.builder()
            .username(username)
            .password(encoder.encode(password))
            .roles("USER")
            .build();
        Member member = new Member("username");

        userDetailsService.createUser(user);
        memberRepository.save(member);
    }
}
