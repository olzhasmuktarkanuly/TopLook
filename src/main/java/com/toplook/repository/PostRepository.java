package com.toplook.repository;

import com.toplook.entity.Post;
import com.toplook.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    @Query("SELECT p FROM Post p WHERE p.author IN " +
           "(SELECT f.following FROM Follow f WHERE f.follower = :user) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findFeedForUser(@Param("user") User user, Pageable pageable);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category, Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Post p ORDER BY p.category")
    List<String> findDistinctCategories();
}

