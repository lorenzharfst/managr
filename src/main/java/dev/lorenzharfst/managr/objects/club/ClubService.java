package dev.lorenzharfst.managr.objects.club;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Create a new Club given a Name and a Description
     * @param name The club name
     * @param description
     * @return The newly created Club id
     */
    public long createClub(String name, String description) {
        if (clubRepository.findByName(name) != null) throw new IllegalArgumentException("Club with that name already exists.");
        Club club = new Club(name, description);
        return clubRepository.save(club).getId();
    }

    /**
     * Create a new Meetup given a Club id
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
        Club club = clubRepository.findById(clubId).orElseThrow(IllegalArgumentException::new);
        meetup.setClub(club);
        return meetupRepository.save(meetup).getId();
    }

}
