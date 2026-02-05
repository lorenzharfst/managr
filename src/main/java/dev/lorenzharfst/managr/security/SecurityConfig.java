package dev.lorenzharfst.managr.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

import dev.lorenzharfst.managr.objects.member.Member;
import dev.lorenzharfst.managr.objects.member.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // Temporarily disabled csrf until I get around to configuring it
        http.csrf((csrf) -> csrf.disable())
            .logout((logout) -> logout.logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)))
            .formLogin((login) -> {
                // Use a custom AuthenticationSuccessHandler so it doesn't redirect to a new page and so on, same with Failure below
                login.successHandler(new SuccessfulFormLoginHandler());
                login.failureHandler(new FailureFormLoginHandler());
            })
            .authorizeHttpRequests((authorize) -> authorize
                .anyRequest().authenticated()
            )
            .exceptionHandling((configurer) -> {
                // Just respond with a status code instead of a page redirect when unauthorized access
                configurer.accessDeniedHandler(new AccessDeniedFormLoginHandler());
                configurer.authenticationEntryPoint(new FormLoginAuthenticationEntryPoint());
            });

        return http.build();
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsService(DataSource dataSource) {
        // Creating a test user. Delete in prod.
        UserDetails user = User.builder()
            .username("foo")
            .password(passwordEncoder().encode("bar"))
            .roles("USER")
            .build();
        Member member = new Member("foo");
        memberRepository.save(member);
        JdbcUserDetailsManager userDetailsService = new JdbcUserDetailsManager(dataSource);
        // Create the test user if it doesn't exist yet. Delete this in prod too.
        if (!userDetailsService.userExists("foo")) userDetailsService.createUser(user);

        return userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService jdbcUserDetailsService, BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider(jdbcUserDetailsService);
        dao.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(dao);
    }
}
