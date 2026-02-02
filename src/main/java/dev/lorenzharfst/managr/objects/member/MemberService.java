package dev.lorenzharfst.managr.objects.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.lorenzharfst.managr.objects.club.ClubRepository;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ClubRepository clubRepository;

}
