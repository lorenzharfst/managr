package dev.lorenzharfst.managr.objects.meetup;

import java.util.Date;
import java.util.List;

import dev.lorenzharfst.managr.objects.club.Club;
import dev.lorenzharfst.managr.objects.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Meetup {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    long id;
    Date creationDate;
    Date assignedDate;
    List<Member> attendees;
    long hostId;
    int attendeeSlots;
    String location;
    String description;
    @ManyToOne
    Club club;

    // No-arg constructor for reflection
    public Meetup() {}
}
