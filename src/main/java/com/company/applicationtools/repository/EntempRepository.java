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
}
