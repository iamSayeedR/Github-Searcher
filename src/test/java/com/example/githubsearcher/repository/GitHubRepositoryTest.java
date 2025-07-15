package com.example.githubsearcher.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.githubsearcher.entity.GithubEntity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GitHubRepositoryTest {

  @Autowired private GitHubRepository repository;

  @Test
  @DisplayName("Should return repositories filtered by language and stars with sorting")
  public void testFindByLanguageAndStarsGreaterThanEqual() {
    GithubEntity repo1 = new GithubEntity();
    repo1.setId(1L);
    repo1.setName("Alpha");
    repo1.setLanguage("Java");
    repo1.setStars(50);
    repo1.setForks(10);
    repo1.setOwner("user1");
    repo1.setLastUpdated(Instant.now());

    GithubEntity repo2 = new GithubEntity();
    repo2.setId(2L);
    repo2.setName("Beta");
    repo2.setLanguage("Java");
    repo2.setStars(100);
    repo2.setForks(20);
    repo2.setOwner("user2");
    repo2.setLastUpdated(Instant.now());

    repository.saveAll(List.of(repo1, repo2));

    List<GithubEntity> results =
        repository.findByLanguageAndStarsGreaterThanEqual(
            "Java", 60, Sort.by(Sort.Direction.DESC, "stars"));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Beta");
    assertThat(results.get(0).getStars()).isGreaterThanOrEqualTo(60);
  }
}
