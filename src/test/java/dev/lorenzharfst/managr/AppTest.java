package dev.lorenzharfst.managr;

import dev.lorenzharfst.managr.objects.club.Club;
import dev.lorenzharfst.managr.objects.member.Member;
import dev.lorenzharfst.managr.objects.member.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.lorenzharfst.managr.objects.club.ClubRepository;
import dev.lorenzharfst.managr.objects.club.MeetupRepository;

import java.io.FileNotFoundException;

/**
 * Unit test for simple App.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    MemberRepository memberRepo;
    
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
        Member member = new Member("club_owner");
        memberRepo.save(member);
        // Create a user who's gonna be a club member
        user = User.builder()
            .username("club_member")
            .password(passwordEncoder.encode("club_member"))
            .roles("USER")
            .build();
        userDetailsService.createUser(user);
        member = new Member("club_member");
        memberRepo.save(member);
        // Create a user who's gonna be a meetup host
        user = User.builder()
            .username("meetup_host")
            .password(passwordEncoder.encode("meetup_host"))
            .roles("USER")
            .build();
        userDetailsService.createUser(user);
        member = new Member("meetup_host");
        memberRepo.save(member);
    }

    @AfterAll
    void cleanup() {
        userDetailsService.deleteUser("meetup_host");
        userDetailsService.deleteUser("club_member");
        userDetailsService.deleteUser("club_owner");
        // Members are deleted automatically as long as ddl-auto = create-drop in properties file
        // Delete all clubs created by club_owner, including ACL entries and their children
        Club club = clubRepo.findByOwner("club_owner").orElse(null);
        while (club != null) {
            clubRepo.deleteById(club.getId());
            aclService.deleteAcl(new ObjectIdentityImpl(Club.class, club.getId()), true);
            club = clubRepo.findByOwner("club_owner").orElse(null);
        }
        // TODO: Do the same for meetups, specially with the ACLs since meetups I believe get cascade'd with the club deletions
        // Delete all clubs, all members and all meetups
        memberRepo.delete(memberRepo.findByUsername("meetup_host").orElse(null));
        memberRepo.delete(memberRepo.findByUsername("club_member").orElse(null));
        memberRepo.delete(memberRepo.findByUsername("club_owner").orElse(null));
    }

    @Test
    @Order(1)
    void login() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType("application/x-www-form-urlencoded")
                .content("username=club_owner&password=club_owner")).andExpect(status().isFound());
    }

    @Test
    @Order(2)
    @WithUserDetails("club_owner")
    void createClub() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs?name=47638291 TEST CLUB"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @WithUserDetails("club_owner")
    void getClubAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.name").value("47638291 TEST CLUB"));
    }

    @Test
    @Order(4)
    @WithUserDetails("club_owner")
    void addMemberToClubAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(ChangeSetPersister.NotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/members/add?username=club_member"))
                .andExpect(status().isOk());
    }

}