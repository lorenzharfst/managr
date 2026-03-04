package dev.lorenzharfst.managr;

import dev.lorenzharfst.managr.objects.club.Club;
import dev.lorenzharfst.managr.objects.club.Meetup;
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
import org.springframework.security.acls.model.AclCache;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

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
    @Transactional
    void cleanup() {
        userDetailsService.deleteUser("meetup_host");
        userDetailsService.deleteUser("club_member");
        userDetailsService.deleteUser("club_owner");
        // Members are deleted automatically as long as ddl-auto = create-drop in properties file
        // Delete all clubs created by club_owner, including ACL entries and their children
        List<Club> clubs = clubRepo.findListByOwner("club_owner");
        // Ids of the meetups we want to delete as we can't fetch them due to lazy load
        List<Long> meetupIdsToDelete = new ArrayList<Long>();
        for (Club club : clubs) {
            // Add the ids of the meetups belonging to this club to a list so we can delete ACLs later on
            meetupIdsToDelete.addAll(clubRepo.findMeetupIdsByClubId(club.getId()));
            clubRepo.deleteById(club.getId());
            aclService.deleteAcl(new ObjectIdentityImpl(Club.class, club.getId()), true);
        }
        for (Long id : meetupIdsToDelete) {
            aclService.deleteAcl(new ObjectIdentityImpl(Meetup.class, id), true);
        }
        // Delete all clubs, all members and all meetups
        try {
            memberRepo.delete(memberRepo.findByUsername("meetup_host").orElseThrow(NoSuchElementException::new));
        } catch (NoSuchElementException ex) {}
        try {
        memberRepo.delete(memberRepo.findByUsername("club_member").orElseThrow(NoSuchElementException::new));
        } catch (NoSuchElementException ex) {}
        try {
        memberRepo.delete(memberRepo.findByUsername("club_owner").orElseThrow(NoSuchElementException::new));
        } catch (NoSuchElementException ex) {}
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
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.members[?(@.username == \"club_member\")].username").value("club_member"));
    }

    @Test
    @Order(4)
    @WithUserDetails("club_owner")
    void addMemberTwoToClubAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/members/add?username=meetup_host"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.members[?(@.username == \"meetup_host\")].username").value("meetup_host"));
    }

    @Test
    @Order(5)
    @WithUserDetails("club_member")
    void getClubAsMember() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.name").value("47638291 TEST CLUB"));
    }

    @Test
    @Order(6)
    @WithUserDetails("meetup_host")
    void createMeetup() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/" + club.getId() + "/meetups")
                        .contentType("application/json")
                        .content("{" +
                                "\"title\": \"47638291 TEST MEETUP\"," +
                                "\"assignedDate\": \"2026-08-12T13:00:00\"," +
                                "\"attendeeSlots\": 4," +
                                "\"location\": \"John's House\"," +
                                "\"description\": \"Casual hanchan\""
                                + "}"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithUserDetails("meetup_host")
    void addAttendeeAsHost() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/meetups/" + meetup.getId() + "/attendees/add?username=club_member"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.attendees[?(@.username == \"club_member\")].username").value("club_member"));
    }

    @Test
    @Order(7)
    @WithUserDetails("club_owner")
    void addAttendeeAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/meetups/" + meetup.getId() + "/attendees/add?username=club_owner"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.attendees[?(@.username == \"club_owner\")].username").value("club_owner"));
    }

    @Test
    @Order(8)
    @WithUserDetails("meetup_host")
    void getMeetupAsHost() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.title").value("47638291 TEST MEETUP"));
    }

    @Test
    @Order(8)
    @WithUserDetails("club_member")
    void getMeetupAsClubMember() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.title").value("47638291 TEST MEETUP"));
    }

    @Test
    @Order(8)
    @WithUserDetails("club_owner")
    void getMeetupAsClubOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.title").value("47638291 TEST MEETUP"));
    }

    @Test
    @Order(9)
    @WithUserDetails("club_owner")
    void editMeetupTitleAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/meetups/" + meetup.getId())
                .contentType("application/json")
                .content("{ \"title\": \"47638291 TEST MEETUP 2\" }"));
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.title").value("47638291 TEST MEETUP 2"));
    }

    @Test
    @Order(9)
    @WithUserDetails("meetup_host")
    void editMeetupDescriptionAsHost() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/meetups/" + meetup.getId())
                .contentType("application/json")
                .content("{ \"description\": \"Test Description\" }"));
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @Order(9)
    @WithUserDetails("club_owner")
    void editMeetupLocationAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/meetups/" + meetup.getId())
                .contentType("application/json")
                .content("{ \"location\": \"Test Location\" }"));
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    @Test
    @Order(9)
    @WithUserDetails("meetup_host")
    void editMeetupAttendeeSlotsAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/meetups/" + meetup.getId())
                .contentType("application/json")
                .content("{ \"attendeeSlots\": 2 }"));
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.attendeeSlots").value("2"));
    }

    @Test
    @Order(9)
    @WithUserDetails("club_owner")
    void editMeetupAssignedDateAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/meetups/" + meetup.getId())
                .contentType("application/json")
                .content("{ \"assignedDate\": \"2026-03-26T16:00:00.000Z\" }"));
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.assignedDate").value("2026-03-26T16:00:00.000Z"));
    }

    @Test
    @Order(9)
    @WithUserDetails("meetup_host")
    void editTwoElementsAsHost() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.put("/clubs/" + club.getId() + "/meetups/" + meetup.getId())
                .contentType("application/json")
                .content("{ \"description\": \"Test Description 2\", \"title\": \"Test Title 2\" }"));
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.description").value("Test Description 2"))
                .andExpect(jsonPath("$.title").value("Test Title 2"));
    }

    @Test
    @Order(10)
    @WithUserDetails("club_owner")
    void removeAttendeeAsOwner() throws Exception {
        Club club = clubRepo.findByOwner("club_owner").orElseThrow(FileNotFoundException::new);
        Meetup meetup = meetupRepo.findByOwner("meetup_host").orElseThrow(FileNotFoundException::new);
        mockMvc.perform(MockMvcRequestBuilders.delete("/clubs/" + club.getId() + "/meetups/" + meetup.getId() + "/attendees/remove?username=club_member"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/meetups/" + meetup.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.attendees[?(@.username == \"club_member\")].username").isEmpty());
    }

}