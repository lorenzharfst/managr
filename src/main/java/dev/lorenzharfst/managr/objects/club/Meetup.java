package dev.lorenzharfst.managr.objects.club;

import java.util.Date;
import java.util.List;

import dev.lorenzharfst.managr.objects.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Meetup {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    long id;
    Date creationDate;
    Date assignedDate;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "meetups")
    List<Member> attendees;
    // Login name of host, decided for keeping the name and not the MemberId since we already have the name with Spring's Authority
    String title;
    int attendeeSlots;
    String location;
    String description;
    @ManyToOne
    Club club;

    public Meetup(String title, Date assignedDate, int attendeeSlots, String location, String description) {
        this.title = title;
        this.assignedDate = assignedDate;
        this.creationDate = new Date();
        this.attendeeSlots = attendeeSlots;
        this.location = location;
        this.description = description;
    }

    // No-arg constructor for reflection
    public Meetup() {}

    // Getters & Setters
    public long getId() {
            return id;
    }

    public void setId(long id) {
            this.id = id;
    }

    public Date getCreationDate() {
            return creationDate;
    }

    public void setCreationDate(Date creationDate) {
            this.creationDate = creationDate;
    }

    public Date getAssignedDate() {
            return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
            this.assignedDate = assignedDate;
    }

    public List<Member> getAttendees() {
            return attendees;
    }

    public void setAttendees(List<Member> attendees) {
            this.attendees = attendees;
    }

    public int getAttendeeSlots() {
            return attendeeSlots;
    }

    public void setAttendeeSlots(int attendeeSlots) {
            this.attendeeSlots = attendeeSlots;
    }

    public String getLocation() {
            return location;
    }

    public void setLocation(String location) {
            this.location = location;
    }

    public String getDescription() {
            return description;
    }

    public void setDescription(String description) {
            this.description = description;
    }

    public Club getClub() {
            return club;
    }

    public void setClub(Club club) {
            this.club = club;
    }
    public String getTitle() {
            return title;
    }

    public void setTitle(String title) {
            this.title = title;
    }
}
