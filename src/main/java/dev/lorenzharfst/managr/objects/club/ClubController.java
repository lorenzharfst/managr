
package dev.lorenzharfst.managr.objects.club;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClubController {

    @Autowired
    ClubService clubService;

    @GetMapping("/clubs/{clubId}")
    ResponseEntity<Club> getClub(@PathVariable long clubId) {
        return ResponseEntity.ok(clubService.getClub(clubId));
    }

    @PostMapping("/clubs")
    long createClub(@RequestParam String name, Authentication authentication) {
        return clubService.createClub(name, authentication);
    }

    @PutMapping("/clubs/{clubId}/add-member")
    void addClubMember(@RequestParam String memberUsername, @PathVariable long clubId) {
        clubService.addClubMember(memberUsername, clubId);
    }

    @PostMapping("/meetups")
    long createMeetup(@RequestBody MeetupDTO meetup, Authentication authentication) {
        return clubService.createMeetup(authentication, meetup.title, meetup.assignedDate, meetup.attendeeSlots, meetup.location, meetup.description, meetup.clubId);
    }

    @GetMapping("/meetups/{meetupId}")
    ResponseEntity<Meetup> getMeetup(@PathVariable long meetupId) {
        return ResponseEntity.ok(clubService.getMeetup(meetupId));
    }

    @PutMapping("/meetups/{meetupId}/attendees")
    void addMeetupAttendee(@PathVariable long meetupId, @RequestParam String memberUsername) {
        clubService.addMeetupAttendee(meetupId, memberUsername);
    }

    @PutMapping("/meetups/{meetupId}/attendees/{memberId}")
    void removeMeetupAttendee(@PathVariable long meetupId, @PathVariable long memberId) {
        clubService.removeMeetupAttendee(meetupId, memberId);
    }

    @PutMapping("/meetups/{meetupId}")
    void setMeetup(@RequestBody MeetupDTO meetup, @PathVariable long meetupId) {
        // Get the MeetupDTO and only apply changes to the fields that are not 0/null
        if (meetup.title != null) clubService.setMeetupTitle(meetupId, meetup.title);
        if (meetup.description != null) clubService.setMeetupDescription(meetupId, meetup.description);
        if (meetup.location != null) clubService.setMeetupLocation(meetupId, meetup.location);
        if (meetup.attendeeSlots != 0) clubService.setMeetupAttendeeSlots(meetupId, meetup.attendeeSlots);
        if (meetup.assignedDate != null) clubService.setMeetupAssignedDate(meetupId, meetup.assignedDate);
    }

    @DeleteMapping("meetups/{meetupId}")
    void deleteMeetup(@PathVariable long meetupId){
        clubService.deleteMeetup(meetupId);
    }

    static class MeetupDTO {
        public String title;
        public Date assignedDate;
        public int attendeeSlots;
        public String location;
        public String description;
        public long clubId;
    }
}
