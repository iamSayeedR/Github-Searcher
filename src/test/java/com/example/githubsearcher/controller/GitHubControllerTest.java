package com.example.githubsearcher.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.githubsearcher.dto.GitHubResponse;
import com.example.githubsearcher.dto.GitHubSearchRequest;
import com.example.githubsearcher.service.GitHubService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class GitHubControllerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  @InjectMocks private GitHubController gitHubController;
  @Autowired private MockMvc mockMvc;
  @Mock private GitHubService service;
  private GitHubResponse sampleResponse;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(gitHubController).build();
    sampleResponse = new GitHubResponse();
    sampleResponse.setName("test-repo");
    sampleResponse.setDescription("A test repository");
    sampleResponse.setLanguage("Java");
    sampleResponse.setStars(100);
    sampleResponse.setForks(20);
    sampleResponse.setOwner("octocat");
    sampleResponse.setLastUpdated(Instant.now());
  }

  @Test
  void shouldReturnSearchResultsAndSaveThem() throws Exception {
    GitHubSearchRequest request = new GitHubSearchRequest();
    request.setQuery("spring");
    request.setLanguage("Java");
    request.setSort("stars");

    when(service.searchAndSave(any(GitHubSearchRequest.class))).thenReturn(List.of(sampleResponse));

    mockMvc
        .perform(
            post("/api/github/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Repositories fetched and saved successfully"))
        .andExpect(jsonPath("$.repositories[0].name").value("test-repo"))
        .andExpect(jsonPath("$.repositories[0].owner").value("octocat"));
  }

  @Test
  void shouldReturnFilteredRepositories() throws Exception {
    when(service.getFilteredResults("Java", 50, "stars")).thenReturn(List.of(sampleResponse));

    mockMvc
        .perform(
            get("/api/github/repositories")
                .param("language", "Java")
                .param("minStars", "50")
                .param("sort", "stars"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.repositories[0].name").value("test-repo"))
        .andExpect(jsonPath("$.repositories[0].stars").value(100));
  }
}
