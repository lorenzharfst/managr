package dev.lorenzharfst.managr.objects.club;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

public interface ClubRepository extends JpaRepository<Club, Long> {

    public Optional<Club> findByName(String name);
    // find a club given its owners username
    @NativeQuery("SELECT * FROM Club c WHERE EXISTS ( SELECT 1 FROM acl_object_identity oid INNER JOIN acl_sid sid ON sid.id = oid.owner_sid WHERE CAST (oid.object_id_identity AS bigint) = c.id AND sid.sid = ?1)")
    public Optional<Club> findByOwner(String owner);
    @NativeQuery("DELETE \n" +
            "FROM Club c \n" +
            "WHERE EXISTS ( \n" +
            "SELECT 1 \n" +
            "FROM acl_object_identity oid \n" +
            "INNER JOIN acl_sid sid ON sid.id = oid.owner_sid \n" +
            "WHERE \n" +
            "\tCAST (oid.object_id_identity AS bigint) = c.id \n" +
            "\tAND sid.sid = ?1\n" +
            ")")
    public void deleteByOwner(String owner);

}
