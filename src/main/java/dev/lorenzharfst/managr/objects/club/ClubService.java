package dev.lorenzharfst.managr.objects.club;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    JdbcMutableAclService aclService;

    /**
     * Get a Club given a Club ID
     */
    public Club getClub(long clubId){
        return clubRepository.findById(clubId).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Create a new Club given a Name and a Description.
     * @param name The club name
     * @param description
     * @return The newly created Club id
     */
    @Transactional
    public long createClub(String name, Principal principal) {
        Club club = new Club(name);
        club = clubRepository.save(club);

        // We set the authenticated user to be the administrator of the club
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Club.class, club.getId());
        Sid sid = new PrincipalSid(principal.getName());

        MutableAcl acl = null;
        try {
            acl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(objectIdentity);
        }
        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, sid, true);

        return club.getId();
    }

    /**
     * Given a member's login username, joins them to a club. Only the authenticated user can perform this action.
     * @param memberUsername The actual log in username, which is unique.
     * @param clubId
     */
    public void addClubMember(String memberUsername, long clubId) throws NoSuchElementException {
        // Will return null if it didn't find that club
        Club club = clubRepository.findById(clubId).orElseThrow(NoSuchElementException::new);
        Member member = memberRepository.findByUsername(memberUsername).orElseThrow(NoSuchElementException::new);

        club.getMembers().add(member);
        club = clubRepository.save(club);

        // We give read permission to the added member
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Club.class, club.getId());
        Sid sid = new PrincipalSid(memberUsername);
        
        MutableAcl acl = null;
        try {
            acl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(objectIdentity);
        }
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, sid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, sid, true);
    }

    /**
     * Create a new Meetup given a Club id. The authenticated user is automatically set as the administrator.
     * @param assignedDate When the meetup is taking place
     * @param attendeeSlots Maximum number of Members that can confirm assistance to the Meetup
     * @param location Location where it's taking place
     * @param description Additional information for the Meetup
     * @param clubId The club to which this Meetup is tied to
     * @return The id of the newly created Meetup
     */
    @Transactional
    public long createMeetup(Principal principal, String title, Date assignedDate, int attendeeSlots, String location, String description, long clubId) {
        Meetup meetup = new Meetup(title, assignedDate, attendeeSlots, location, description);
        Club club = clubRepository.findById(clubId).orElseThrow(NoSuchElementException::new);
        meetup.setClub(club);
        meetup = meetupRepository.save(meetup);

        ObjectIdentity objectIdentity = new ObjectIdentityImpl(Meetup.class, meetup.getId());
        Sid sid = new PrincipalSid(principal.getName());

        MutableAcl meetupAcl = null;
        try {
            meetupAcl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            meetupAcl = aclService.createAcl(objectIdentity);
        }

        // We set the authenticated user as the administrator of this meetup
        meetupAcl.insertAce(meetupAcl.getEntries().size(), BasePermission.ADMINISTRATION, sid, true);
        // We set the administrators of the group to also become administrators of the meetup
        MutableAcl clubAcl = (MutableAcl) aclService.readAclById(new ObjectIdentityImpl(Club.class, club.getId()));
        for (AccessControlEntry ace : clubAcl.getEntries()) {
            if (ace.getPermission() == BasePermission.ADMINISTRATION) {
                meetupAcl.insertAce(meetupAcl.getEntries().size(), BasePermission.ADMINISTRATION, ace.getSid(), true);
            }
        }
        aclService.updateAcl(meetupAcl);

        return meetup.getId();
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
     * @param memberId Member id
     */
    public void addMeetupAttendee(long meetupId, long memberId) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        meetup.getAttendees().add(member);
        meetupRepository.save(meetup);
    }

    /**
     * Join a meetup given a Meetup id and a Member's login name.
     * @param meetupId
     * @param memberUsername Login name of that member
     */
    public void addMeetupAttendee(long meetupId, String memberUsername) {
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
     * Remove a Member from the Attendees of a Meetup.
     * @param meetupId
     * @param memberUsername Login name of that member
     */
    public void removeMeetupAttendee(long meetupId, String memberUsername) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        meetup.getAttendees().removeIf((member) -> member.getUsername() == memberUsername);
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

    /** Remove a Meetup 
     * @param meetupId
     */
    public void deleteMeetup(long meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(NoSuchElementException::new);
        meetupRepository.delete(meetup);
    }

}
