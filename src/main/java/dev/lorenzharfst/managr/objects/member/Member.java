package dev.lorenzharfst.managr.objects.member;

import dev.lorenzharfst.managr.objects.club.Club;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    // Username with which member logs in
    String username;
    // Name that is visible to other users
    String displayname;
    @ManyToMany
    Club club;
    
    // No-arg constructor for reflection
    public Member() {}

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

    public Club getClub() {
            return club;
    }

    public void setClub(Club club) {
            this.club = club;
    }
}
