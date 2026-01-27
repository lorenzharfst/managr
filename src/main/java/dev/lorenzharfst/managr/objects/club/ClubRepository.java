package dev.lorenzharfst.managr.objects.club;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {

    public Optional<Club> findByName(String name);

}
