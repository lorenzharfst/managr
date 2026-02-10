package dev.lorenzharfst.managr.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

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
                configurer.authenticationEntryPoint(new DefaultFormLoginAuthenticationEntryPoint());
            });

        return http.build();
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsService = new JdbcUserDetailsManager(dataSource);

        // Create test user, delete in prod
        createTestUser(userDetailsService);
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

    public void createTestUser(JdbcUserDetailsManager userDetailsService) {
        UserDetails user = User.builder() .username("foo")
            .password(passwordEncoder().encode("bar"))
            .roles("USER")
            .build();
        Member member = new Member("foo");
        memberRepository.save(member);
        if (!userDetailsService.userExists("foo")) userDetailsService.createUser(user);
    }

    @Bean
    static MethodSecurityExpressionHandler expressionHandler(AclPermissionEvaluator aclPermissionEvaluator) {
        final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(aclPermissionEvaluator);
        return expressionHandler;
    }

    @Bean
    static AclPermissionEvaluator aclPermissionEvaluator(AclService aclService) {
        return new AclPermissionEvaluator(aclService);
    }

    @Bean
    static JdbcMutableAclService aclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
        return new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
    }

    @Bean
    static LookupStrategy lookupStrategy(DataSource dataSource, AclCache cache,
            AclAuthorizationStrategy aclAuthorizationStrategy, PermissionGrantingStrategy permissionGrantingStrategy) {
        return new BasicLookupStrategy(dataSource, cache, aclAuthorizationStrategy, permissionGrantingStrategy);
    }

    @Bean
    static AclCache aclCache(PermissionGrantingStrategy permissionGrantingStrategy, AclAuthorizationStrategy aclAuthorizationStrategy) {
        Cache cache = new ConcurrentMapCache("aclCache");
        return new SpringCacheBasedAclCache(cache, permissionGrantingStrategy, aclAuthorizationStrategy);
    }

    @Bean
    static AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ADMIN"));
    }

    @Bean
    static PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }
}
