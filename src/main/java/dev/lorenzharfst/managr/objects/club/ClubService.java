package dev.lorenzharfst.managr.objects.club;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.lorenzharfst.managr.objects.member.MemberRepository;

@Service
public class ClubService {

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    MemberRepository memberRepository;

    /**
     * Create a new Club given a Name and a Description
     */
    public long createClub(String name, String description) {
        if (clubRepository.findByName(name) != null) throw new IllegalArgumentException("Club with that name already exists.");
        Club club = new Club(name, description);
        return clubRepository.save(club).getId();
    }

}
