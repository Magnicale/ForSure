package ru.itis.timecapsule.model;

public class User {
    private long id;
    private String email;
    private String displayName;
    private String passwordHash;
    private Role role;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
