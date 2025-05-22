package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    Optional<UserDeviceToken> findByUser(User user);
}
