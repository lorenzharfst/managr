
package dev.lorenzharfst.managr.objects.club;

import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClubController {

    @Autowired
    ClubService clubService;

    /** Retrieve a club by its id. User must have READ permission to retrieve the club. **/
    // TODO: PreAuthorize that the person has read permission on that club
    @GetMapping("/clubs/{clubId}")
    ResponseEntity<Club> getClub(@PathVariable long clubId) {
        return ResponseEntity.ok(clubService.getClub(clubId));
    }

    /** Create a club by providing a club name. Authenticated user will be granted ADMINISTRATION permission for that club. **/
    @PostMapping("/clubs")
    long createClub(@RequestParam String name, Principal principal) {
        return clubService.createClub(name, principal);
    }

    /** Add a member to a club by providing their username. Member will be granted READ permission for that club. **/
    @PutMapping("/clubs/{clubId}/add-member")
    void addClubMember(@RequestParam String memberUsername, @PathVariable long clubId) {
        clubService.addClubMember(memberUsername, clubId);
    }

    /** Create a meetup by providing any information for the meetup. Group administrators will automatically be granted ADMINISTRATION permission for the meetup, as well as the creating user. **/
    // TODO: Authorize only users that have ADMINISTRATOR or CREATE permissions in the club the meetup belongs to
    @PostMapping("/meetups")
    long createMeetup(@RequestBody MeetupDTO meetup, Principal principal) {
        return clubService.createMeetup(principal, meetup.title, meetup.assignedDate, meetup.attendeeSlots, meetup.location, meetup.description, meetup.clubId);
    }

    /** Retrieve a Meetup by providing its id. **/
    // TODO: PreAuthorize that the user has READ permission on the club this meetup belongs to.
    @GetMapping("/meetups/{meetupId}")
    ResponseEntity<Meetup> getMeetup(@PathVariable long meetupId) {
        return ResponseEntity.ok(clubService.getMeetup(meetupId));
    }

    /** Add attendee to a meetup by providing their member id. **/
    // TODO: Authorize only administrators of the meetup or the same member who the id belongs to
    @PostMapping("/meetups/{meetupId}/attendees")
    void addMeetupAttendee(@PathVariable long meetupId, @RequestParam long memberId) {
        clubService.addMeetupAttendee(meetupId, memberId);
    }

    /** Remove the attendee of a meetup by providing the member id. **/
    // TODO: Authorize only administrators of the meetup or the same member who the id belongs to
    @PutMapping("/meetups/{meetupId}/attendees/{memberId}")
    void removeMeetupAttendee(@PathVariable long meetupId, @PathVariable long memberId) {
        clubService.removeMeetupAttendee(meetupId, memberId);
    }

    /** Edit a meetup by providing any element of MeetupDTO. **/
    // TODO: Authorize only administrators of the meetup.
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
