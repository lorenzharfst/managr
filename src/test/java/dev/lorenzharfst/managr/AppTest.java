package dev.lorenzharfst.managr;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.lorenzharfst.managr.objects.club.ClubRepository;
import dev.lorenzharfst.managr.objects.club.MeetupRepository;

/**
 * Unit test for simple App.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class AppTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    JdbcUserDetailsManager userDetailsService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    ClubRepository clubRepo;

    @Autowired
    MeetupRepository meetupRepo;
    
    @Autowired
    JdbcMutableAclService aclService;

    @BeforeAll
    void setup() {
        // Create a user who's gonna be a club owner
        UserDetails user = User.builder()
            .username("club_owner")
            .password(passwordEncoder.encode("club_owner"))
            .roles("USER")
            .build();
        userDetailsService.createUser(user);
        // Create a user who's gonna be a club member
        user = User.builder()
            .username("club_member")
            .password(passwordEncoder.encode("club_member"))
            .roles("USER")
            .build();
        userDetailsService.createUser(user);
        // Create a user who's gonna be a meetup host
        user = User.builder()
            .username("meetup_host")
            .password(passwordEncoder.encode("meetup_host"))
            .roles("USER")
            .build();
        userDetailsService.createUser(user);
    }

    @AfterAll
    void cleanup() {
        userDetailsService.deleteUser("meetup_host");
        userDetailsService.deleteUser("club_member");
        userDetailsService.deleteUser("club_owner");
    }

    @Test
    void login() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType("application/x-www-form-urlencoded")
                .content("username=club_owner&password=club_owner")).andExpect(status().isFound());
    }

    @Test
    @WithUserDetails("club_owner")
    void createClubAsClubOwner() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs?name=Mahjong+Club"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1"));
    }
}