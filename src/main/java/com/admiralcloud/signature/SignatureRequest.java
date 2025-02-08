package com.admiralcloud.signature;

public class SignatureRequest {
    private String accessSecret;
    private String path;
    private String identifier;
    private String payload;  // JSON string
    private long timestamp;

    public String getAccessSecret() { return accessSecret; }
    public void setAccessSecret(String accessSecret) { this.accessSecret = accessSecret; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}