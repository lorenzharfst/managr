package dev.lorenzharfst.managr.objects.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;
import java.util.Optional;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    @NativeQuery("SELECT * FROM Meetup m WHERE EXISTS ( SELECT 1 FROM acl_object_identity oid INNER JOIN acl_sid sid ON sid.id = oid.owner_sid WHERE CAST (oid.object_id_identity AS bigint) = m.id AND sid.sid = ?1)")
    public List<Meetup> findListByOwner(String owner);
    @NativeQuery("SELECT * FROM Meetup m WHERE EXISTS ( SELECT 1 FROM acl_object_identity oid INNER JOIN acl_sid sid ON sid.id = oid.owner_sid WHERE CAST (oid.object_id_identity AS bigint) = m.id AND sid.sid = ?1)")
    public Optional<Meetup> findByOwner(String owner);
}
