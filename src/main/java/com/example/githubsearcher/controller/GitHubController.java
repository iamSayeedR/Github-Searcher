package com.example.githubsearcher.controller;

import com.example.githubsearcher.dto.GitHubResponse;
import com.example.githubsearcher.dto.GitHubSearchRequest;
import com.example.githubsearcher.service.GitHubService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

  private final GitHubService service;

  public GitHubController(GitHubService service) {
    this.service = service;
  }

  @PostMapping("/search")
  public ResponseEntity<?> searchGitHubRepo(@RequestBody GitHubSearchRequest request) {
    List<GitHubResponse> gitHubResponses = service.searchAndSave(request);
    return ResponseEntity.ok(
        Map.of(
            "message",
            "Repositories fetched and saved successfully",
            "repositories",
            gitHubResponses));
  }

  @GetMapping("/repositories")
  public ResponseEntity<Map<String, List<GitHubResponse>>> getFilteredRepos(
      @RequestParam(required = false) String language,
      @RequestParam(required = false) Integer minStars,
      @RequestParam(defaultValue = "stars") String sort) {
    List<GitHubResponse> repos = service.getFilteredResults(language, minStars, sort);
    return ResponseEntity.ok(Map.of("repositories", repos));
  }
}
