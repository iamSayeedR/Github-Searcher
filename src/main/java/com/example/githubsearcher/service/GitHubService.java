package com.example.githubsearcher.service;

import com.example.githubsearcher.dto.GitHubResponse;
import com.example.githubsearcher.dto.GitHubSearchRequest;
import com.example.githubsearcher.entity.GithubEntity;
import com.example.githubsearcher.repository.GitHubRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {
  private final RestTemplate restTemplate;
  private final GitHubRepository repo;

  public GitHubService(RestTemplate restTemplate, GitHubRepository repo) {
    this.restTemplate = restTemplate;
    this.repo = repo;
  }

  public List<GitHubResponse> searchAndSave(GitHubSearchRequest request) {
    String url =
        String.format(
            "https://api.github.com/search/repositories?q=%s+language:%s&sort=%s",
            request.getQuery(), request.getLanguage(), request.getSort());

    ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
    List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

    List<GitHubResponse> savedRepos = new ArrayList<>();
    for (Map<String, Object> item : items) {
      GithubEntity entity = new GithubEntity();
      entity.setId(((Number) item.get("id")).longValue());
      entity.setName((String) item.get("name"));
      entity.setDescription((String) item.get("description"));
      entity.setOwner(((Map<String, Object>) item.get("owner")).get("login").toString());
      entity.setLanguage((String) item.get("language"));
      entity.setStars(((Number) item.get("stargazers_count")).intValue());
      entity.setForks(((Number) item.get("forks_count")).intValue());
      entity.setLastUpdated(Instant.parse((String) item.get("updated_at")));

      repo.save(entity);

      GitHubResponse dto = new GitHubResponse();
      BeanUtils.copyProperties(entity, dto);
      savedRepos.add(dto);
    }

    return savedRepos;
  }

  public List<GitHubResponse> getFilteredResults(String language, Integer minStars, String sort) {
    Sort sorting =
        Sort.by(
            Sort.Direction.DESC,
            switch (sort) {
              case "forks" -> "forks";
              case "updated" -> "lastUpdated";
              default -> "stars";
            });
    List<GithubEntity> entities =
        repo.findByLanguageAndStarsGreaterThanEqual(
            language, minStars != null ? minStars : 0, sorting);

    return entities.stream()
        .map(
            entity -> {
              GitHubResponse dto = new GitHubResponse();
              BeanUtils.copyProperties(entity, dto);
              return dto;
            })
        .collect(Collectors.toList());
  }
}
