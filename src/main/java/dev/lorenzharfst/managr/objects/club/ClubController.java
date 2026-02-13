package dev.lorenzharfst.managr.objects.club;

import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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

    /** Retrieve a club by its id. User must have READ or ADMINISTRATION permission to retrieve the club. **/
    @PreAuthorize("hasPermission(#clubId, 'dev.lorenzharfst.managr.objects.club.Club', admin) || hasPermission(#clubId, 'dev.lorenzharfst.managr.objects.club.Club', read)")
    @GetMapping("/clubs/{clubId}")
    ResponseEntity<Club> getClub(@PathVariable long clubId) {
        return ResponseEntity.status(302).body(clubService.getClub(clubId));
    }

    /** Create a club by providing a club name. Authenticated user will be granted ADMINISTRATION permission for that club. **/
    @PostMapping("/clubs")
    long createClub(@RequestParam String name, Principal principal) {
        return clubService.createClub(name, principal);
    }

    /** Add a member to a club by providing their username. Member will be granted READ permission for that club. **/
    @PreAuthorize("hasPermission(#clubId, 'dev.lorenzharfst.managr.objects.club.Club', admin)")
    @PutMapping("/clubs/{clubId}/members/add")
    void addClubMember(@RequestParam String username, @PathVariable long clubId) {
        clubService.addClubMember(username, clubId);
    }

    /** Create a meetup by providing any information for the meetup. Group administrators will automatically be granted ADMINISTRATION permission for the meetup, as well as the creating user. **/
    @PreAuthorize("hasPermission(#meetup.clubId, 'dev.lorenzharfst.managr.objects.club.Club', admin) || hasPermission(#meetup.clubId, 'dev.lorenzharfst.managr.objects.club.Club', create)")
    @PostMapping("/meetups")
    long createMeetup(@P("meetup") @RequestBody MeetupDTO meetup, Principal principal) {
        return clubService.createMeetup(principal, meetup.title, meetup.assignedDate, meetup.attendeeSlots, meetup.location, meetup.description, meetup.clubId);
    }

    /** Retrieve a Meetup by providing its id. **/
    @PreAuthorize("hasPermission(#meetupId, 'dev.lorenzharfst.managr.objects.club.Meetup', read) || hasPermission(#meetupId, 'dev.lorenzharfst.managr.objects.club.Meetup', admin)")
    @GetMapping("/meetups/{meetupId}")
    ResponseEntity<Meetup> getMeetup(@PathVariable long meetupId) {
        return ResponseEntity.ok(clubService.getMeetup(meetupId));
    }

    /** Add attendee to a meetup by providing their member id. **/
    @PreAuthorize("hasPermission(#meetupId, 'dev.lorenzharfst.managr.objects.club.Meetup', admin) || principal.name == #memberUsername")
    @PutMapping("/meetups/{meetupId}/attendees/add")
    void addMeetupAttendee(@PathVariable long meetupId, @RequestParam String memberUsername) {
        clubService.addMeetupAttendee(meetupId, memberUsername);
    }

    /** Remove the attendee of a meetup by providing the member id. **/
    @PreAuthorize("hasPermission(#meetup.id, 'dev.lorenzharfst.managr.objects.club.Meetup', admin) || principal.name == #memberUsername")
    @PutMapping("/meetups/{meetupId}/attendees/remove")
    void removeMeetupAttendee(@PathVariable long meetupId, @RequestParam String memberUsername) {
        clubService.removeMeetupAttendee(meetupId, memberUsername);
    }

    /** Edit a meetup by providing any element of MeetupDTO. **/
    @PreAuthorize("hasPermission(#meetupId, 'dev.lorenzharfst.managr.objects.club.Meetup', admin)")
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
    // TODO: Add authorization
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
