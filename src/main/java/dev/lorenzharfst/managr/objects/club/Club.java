package dev.lorenzharfst.managr.objects.club;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.lorenzharfst.managr.objects.club.Meetup;
import dev.lorenzharfst.managr.objects.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Club {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    long id;
    Date creationDate;
    String name;
    String description;
    long ownerId;
    @ManyToMany(mappedBy = "clubs")
    List<Member> members = new ArrayList<Member>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "club")
    List<Meetup> meetups = new ArrayList<Meetup>();

    public Club(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // No-arg constructor for reflection
    public Club() {}

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

    public String getName() {
            return name;
    }

    public void setName(String name) {
            this.name = name;
    }

    public String getDescription() {
            return description;
    }

    public void setDescription(String description) {
            this.description = description;
    }

    public long getOwnerId() {
            return ownerId;
    }

    public void setOwnerId(long ownerId) {
            this.ownerId = ownerId;
    }

    public List<Member> getMembers() {
            return members;
    }

    public void setMembers(List<Member> members) {
            this.members = members;
    }

    public List<Meetup> getMeetups() {
            return meetups;
    }

    public void setMeetups(List<Meetup> meetups) {
            this.meetups = meetups;
    }


}
