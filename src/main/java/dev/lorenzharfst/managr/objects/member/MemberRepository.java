package dev.lorenzharfst.managr.objects.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Optional<Member> findByUsername(String username);
    @Query("DELETE FROM Member m WHERE m.username = :username")
    public void deleteByUsername(String username);
}
