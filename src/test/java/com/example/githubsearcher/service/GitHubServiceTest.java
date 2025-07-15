package com.example.githubsearcher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.githubsearcher.dto.GitHubResponse;
import com.example.githubsearcher.dto.GitHubSearchRequest;
import com.example.githubsearcher.entity.GithubEntity;
import com.example.githubsearcher.repository.GitHubRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class GitHubServiceTest {

  private RestTemplate restTemplate;
  private GitHubRepository repo;
  private GitHubService service;

  private static Map<String, Object> getStringObjectMap() {
    Map<String, Object> owner = Map.of("login", "octocat");
    Map<String, Object> repoData = new HashMap<>();
    repoData.put("id", 123L);
    repoData.put("name", "test-repo");
    repoData.put("description", "Test description");
    repoData.put("owner", owner);
    repoData.put("language", "Java");
    repoData.put("stargazers_count", 42);
    repoData.put("forks_count", 10);
    repoData.put("updated_at", "2023-07-13T10:00:00Z");

    List<Map<String, Object>> items = List.of(repoData);
    return Map.of("items", items);
  }

  @BeforeEach
  public void setup() {
    restTemplate = mock(RestTemplate.class);
    repo = mock(GitHubRepository.class);
    service = new GitHubService(restTemplate, repo);
  }

  @Test
  public void shouldTestSearchAndSave() {

    Map<String, Object> responseBody = getStringObjectMap();

    when(restTemplate.getForEntity(anyString(), eq(Map.class)))
        .thenReturn(ResponseEntity.ok(responseBody));

    GitHubSearchRequest request = new GitHubSearchRequest();
    request.setQuery("spring");
    request.setLanguage("Java");
    request.setSort("stars");

    List<GitHubResponse> responses = service.searchAndSave(request);

    assertThat(responses).hasSize(1);
    GitHubResponse result = responses.get(0);
    assertThat(result.getName()).isEqualTo("test-repo");
    assertThat(result.getStars()).isEqualTo(42);
    assertThat(result.getOwner()).isEqualTo("octocat");

    ArgumentCaptor<GithubEntity> captor = ArgumentCaptor.forClass(GithubEntity.class);
    verify(repo, times(1)).save(captor.capture());
    assertThat(captor.getValue().getLanguage()).isEqualTo("Java");
  }

  @Test
  public void testGetFilteredResults() {
    GithubEntity entity = new GithubEntity();
    entity.setId(1L);
    entity.setName("sample");
    entity.setLanguage("Java");
    entity.setStars(50);
    entity.setForks(5);
    entity.setLastUpdated(Instant.now());

    when(repo.findByLanguageAndStarsGreaterThanEqual(eq("Java"), eq(10), any(Sort.class)))
        .thenReturn(List.of(entity));

    List<GitHubResponse> results = service.getFilteredResults("Java", 10, "stars");

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("sample");
    assertThat(results.get(0).getStars()).isEqualTo(50);
  }
}
