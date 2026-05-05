package com.toplook.repository;

import com.toplook.entity.Follow;
import com.toplook.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    boolean existsByFollowerAndFollowing(User follower, User following);
    long countByFollowing(User following);
    long countByFollower(User follower);
}
