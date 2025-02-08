package com.admiralcloud.signature;

public class SignatureResponse {
    private final String hash;
    private final long timestamp;

    public SignatureResponse(String hash, long timestamp) {
        this.hash = hash;
        this.timestamp = timestamp;
    }

    public String getHash() { return hash; }
    public long getTimestamp() { return timestamp; }
}