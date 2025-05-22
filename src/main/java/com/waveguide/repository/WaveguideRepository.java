package com.waveguide.repository;

import com.waveguide.model.entity.User;
import com.waveguide.model.entity.Waveguide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaveguideRepository extends JpaRepository<Waveguide, UUID> {
    
    Page<Waveguide> findAllByUser(User user, Pageable pageable);
    
    Optional<Waveguide> findByIdAndUser(UUID id, User user);
    
    boolean existsByIdAndUser(UUID id, User user);
    
    void deleteByIdAndUser(UUID id, User user);
}