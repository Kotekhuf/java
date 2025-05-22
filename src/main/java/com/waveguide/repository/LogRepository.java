package com.waveguide.repository;

import com.waveguide.model.entity.Log;
import com.waveguide.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {
    
    Page<Log> findAllByUser(User user, Pageable pageable);
}