package dev.lorenzharfst.managr.objects.club;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.lorenzharfst.managr.objects.member.Member;
import jakarta.persistence.*;

@Entity
public class Club {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    long id;
    Date creationDate;
    String name;
    String description;
    @JsonIgnoreProperties("club")
    @ManyToMany(cascade = CascadeType.ALL)
    Set<Member> members = new HashSet<Member>();
    @JsonIgnoreProperties("club")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "club")
    Set<Meetup> meetups = new HashSet<Meetup>();

    public Club(String name, String description) {
        this.name = name;
        this.description = description;
        this.creationDate = new Date();
    }

    public Club(String name) {
        this.name = name;
        this.creationDate = new Date();
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

    public Set<Member> getMembers() {
            return members;
    }

    public void setMembers(Set<Member> members) {
            this.members = members;
    }

    public Set<Meetup> getMeetups() {
            return meetups;
    }

    public void setMeetups(Set<Meetup> meetups) {
            this.meetups = meetups;
    }


}
