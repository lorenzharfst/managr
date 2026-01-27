package dev.lorenzharfst.managr.objects.club;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.lorenzharfst.managr.objects.member.Member;
import dev.lorenzharfst.managr.objects.member.MemberRepository;

@Service
public class ClubService {

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MeetupRepository meetupRepository;

    /**
     * Create a new Club given a Name and a Description.
     * @param name The club name
     * @param description
     * @return The newly created Club id
     */
    public long createClub(String name, String description) {
        if (clubRepository.findByName(name).isEmpty()) throw new IllegalArgumentException("Club with that name already exists.");
        Club club = new Club(name, description);
        return clubRepository.save(club).getId();
    }

    /**
     * Create a new Meetup given a Club id.
     * @param hostName The login username of the person creating the club
     * @param title The title of the Meetup
     * @param assignedDate When the meetup is taking place
     * @param attendeeSlots Maximum number of Members that can confirm assistance to the Meetup
     * @param location Location where it's taking place
     * @param description Additional information for the Meetup
     * @param clubId The club to which this Meetup is tied to
     * @return The id of the newly created Meetup
     */
    public long createMeetup(String hostName, String title, Date assignedDate, int attendeeSlots, String location, String description, long clubId) {
        Meetup meetup = new Meetup(hostName, title, assignedDate, attendeeSlots, location, description);
        Club club = clubRepository.findById(clubId).orElseThrow(NoSuchElementException::new);
        meetup.setClub(club);
        return meetupRepository.save(meetup).getId();
    }

    /** 
     * Get a Meetup object given a Meetup id.
     * @param meetupId
     * @return Meetup object
     */
    public Meetup getMeetup(long meetupId) {
        return meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Join a meetup given a Meetup id and a Member's login name.
     * @param meetupId
     * @param memberUsername Login name of that member
     */
    public void joinMeetup(long meetupId, String memberUsername) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        Member member = memberRepository.findByUsername(memberUsername).orElseThrow(NoSuchElementException::new);
        meetup.getAttendees().add(member);
        meetupRepository.save(meetup);
    }

    /**
     * Remove a Member from the Attendees of a Meetup.
     * @param meetupId
     * @param memberId
     */
    public void removeMeetupAttendee(long meetupId, long memberId) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        meetup.getAttendees().removeIf((member) -> member.getId() == memberId);
    }

    /**
     * Get the Attendees of a Meetup.
     * @param meetupId
     */
    public List<Member> getMeetupAttendees(long meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        return meetup.getAttendees();
    }

    /** Set the title of a Meetup */
    public void setMeetupTitle(long meetupId, String title) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        meetup.setTitle(title);
        meetupRepository.save(meetup);
    }

    /** Set the description of a Meetup */
    public void setMeetupDescription(long meetupId, String description) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        meetup.setDescription(description);
        meetupRepository.save(meetup);
    }

    /** Set the location of a Meetup */
    public void setMeetupLocation(long meetupId, String location) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        meetup.setLocation(location);
        meetupRepository.save(meetup);
    }

    /** Set the attendee slots of a Meetup */
    public void setMeetupAttendeeSlots(long meetupId, int attendeeSlots) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        meetup.setAttendeeSlots(attendeeSlots);
        meetupRepository.save(meetup);
    }

    /** Set the assigned date of a Meetup */
    public void setMeetupAssignedDate(long meetupId, Date assignedDate) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        meetup.setAssignedDate(assignedDate);
        meetupRepository.save(meetup);
    }

    /** Remove a Meetup from a Club
     * @param clubId
     * @param meetupId
     */
    public void removeMeetup(long clubId, long meetupId) {
        Club club = clubRepository.findById(clubId).orElseThrow(NoSuchElementException::new);
        club.getMeetups().removeIf((meetup) -> meetup.getId() == meetupId);
    }

}
