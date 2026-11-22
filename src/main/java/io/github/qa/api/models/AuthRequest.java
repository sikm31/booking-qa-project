package io.github.qa.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthRequest(@JsonProperty("username") String userName, String password) {
}
