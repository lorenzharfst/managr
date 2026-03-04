package dev.lorenzharfst.managr.objects.member;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.lorenzharfst.managr.objects.club.Club;
import dev.lorenzharfst.managr.objects.club.Meetup;
import jakarta.persistence.*;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    // Username with which member logs in
    String username;
    // Name that is visible to other users
    @Column(unique = true)
    String displayname;
    @ManyToMany(mappedBy = "members")
    Set<Club> clubs = new HashSet<Club>();
    @ManyToMany(mappedBy = "attendees")
    Set<Meetup> meetups = new HashSet<Meetup>();
    
    // No-arg constructor for reflection
    public Member() {}

    public Member(String username) {
        this.username = username;
        this.displayname = username;
    }

    // Getters & Setters
    public long getId() {
            return id;
    }

    public void setId(long id) {
            this.id = id;
    }

    public String getUsername() {
            return username;
    }

    public void setUsername(String username) {
            this.username = username;
    }

    public String getDisplayname() {
            return displayname;
    }

    public void setDisplayname(String displayname) {
            this.displayname = displayname;
    }

    public Set<Club> getClub() {
            return clubs;
    }

    public void setClub(Set<Club> club) {
            this.clubs = club;
    }
}
