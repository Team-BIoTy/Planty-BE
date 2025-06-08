package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByUserOrderByReceivedAtDesc(User user);
    List<UserNotification> findByUserAndIsReadFalse(User user);
}
