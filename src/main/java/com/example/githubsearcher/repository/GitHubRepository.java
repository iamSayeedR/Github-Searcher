package com.example.githubsearcher.repository;

import com.example.githubsearcher.entity.GithubEntity;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitHubRepository extends JpaRepository<GithubEntity, Long> {

  List<GithubEntity> findByLanguageAndStarsGreaterThanEqual(String language, int stars, Sort sort);
}
