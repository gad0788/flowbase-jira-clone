package com.flowbase.jira.dto.response;

public class AuthResponse {

    private String token;
    private Long userId;
    private String displayName;
    private String email;

    public AuthResponse(String token, Long userId, String displayName, String email) {
        this.token = token;
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
}
