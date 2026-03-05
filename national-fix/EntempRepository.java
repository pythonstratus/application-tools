package com.company.applicationtools.repository;

import com.company.applicationtools.entity.Entemp;
import com.company.applicationtools.entity.EntempId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntempRepository extends JpaRepository<Entemp, EntempId> {

    Optional<Entemp> findById(EntempId id);

    @Query("SELECT DISTINCT e.org FROM Entemp e WHERE e.org IS NOT NULL ORDER BY e.org")
    List<String> findDistinctOrgs();

    // =========================================================================
    // ROID Generation - Find first available gap in ROID values
    // MAX is already 99999999 (NUMBER(8) ceiling), so we find gaps instead.
    // This query finds the smallest ROID + 1 where that value doesn't already exist.
    // =========================================================================

    @Query(value = """
        SELECT MIN(e.ROID) + 1
        FROM ENTEMP e
        WHERE e.ROID + 1 NOT IN (SELECT e2.ROID FROM ENTEMP e2)
          AND e.ROID + 1 <= 99999999
        """, nativeQuery = true)
    Long getNextAvailableRoid();
}
