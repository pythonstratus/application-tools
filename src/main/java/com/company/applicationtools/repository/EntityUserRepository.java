package com.company.applicationtools.repository;

import com.company.applicationtools.entity.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EntityUserRepository extends JpaRepository<EntityUser, Long> {

    @Query("SELECT eu.loginDate FROM EntityUser eu WHERE TRIM(eu.userSeid) = :seid ORDER BY eu.loginDate DESC")
    Optional<LocalDate> findLatestLoginDateBySeid(@Param("seid") String seid);
}
