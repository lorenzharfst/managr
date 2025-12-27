package dev.lorenzharfst.managr.objects.club;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.lorenzharfst.managr.objects.meetup.Meetup;
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
    @ManyToMany(mappedBy = "club")
    @JoinColumn(nullable = false)
    List<Member> members = new ArrayList<Member>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "club")
    @JoinColumn(nullable = false)
    List<Meetup> meetups = new ArrayList<Meetup>();

    // No-arg constructor for reflection
    public Club() {}
}
