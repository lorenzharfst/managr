package dev.lorenzharfst.managr.objects.member;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.lorenzharfst.managr.objects.club.Club;
import dev.lorenzharfst.managr.objects.club.ClubRepository;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ClubRepository clubRepository;

    /**
     * Given a member's login username, joins them to a club. Only the authenticated user can perform this action.
     * @param memberUsername The actual log in username, which is unique.
     * @param clubId
     */
    public void joinClub(String memberUsername, long clubId) throws NoSuchElementException {
        // Will return null if it didn't find that club
        Club club = clubRepository.findById(clubId).orElseThrow(NoSuchElementException::new);
        Member member = memberRepository.findByUsername(memberUsername).orElseThrow(NoSuchElementException::new);

        club.getMembers().add(member);
        clubRepository.save(club);
    }
}
