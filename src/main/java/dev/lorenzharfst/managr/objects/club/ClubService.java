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
    public void createClub(String name, String description) {
        Club club = new Club(name, description);
        clubRepository.save(club);
    }

}
