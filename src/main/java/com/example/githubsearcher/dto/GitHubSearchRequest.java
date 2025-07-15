package com.example.githubsearcher.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubSearchRequest {

  private String query;
  private String language;
  private String sort;

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }
}
