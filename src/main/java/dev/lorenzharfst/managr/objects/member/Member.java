package dev.lorenzharfst.managr.objects.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    // Username with which member logs in
    String username;
    // Name that is visible to other users
    String displayname;
}
