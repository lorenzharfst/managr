package dev.lorenzharfst.managr.objects.meetup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetupRepository extends JpaRepository<Meetup, Long> { }
