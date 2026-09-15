package com.vehicle.backend.modules.messaging.repository;

import com.vehicle.backend.modules.messaging.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByBookingIdOrderByTimestampAsc(Long bookingId);

    @Query("SELECT m FROM Message m WHERE (m.senderId = :u1 AND m.receiverId = :u2) OR (m.senderId = :u2 AND m.receiverId = :u1) ORDER BY m.timestamp ASC")
    List<Message> findConversationBetweenUsers(@Param("u1") Long u1, @Param("u2") Long u2);

    @Query("SELECT m FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId ORDER BY m.timestamp DESC")
    List<Message> findUserMessages(@Param("userId") Long userId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    @Query("SELECT m FROM Message m WHERE m.senderId = :otherUserId AND m.receiverId = :currentUserId AND m.isRead = false")
    List<Message> findUnreadFromUser(@Param("otherUserId") Long otherUserId, @Param("currentUserId") Long currentUserId);
}
