package com.admiralcloud.signature;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ACSignatureTest {
    private final ACSignature acSignature = new ACSignature();
    private static final String TEST_SECRET = "test-secret-key-2024";

    @Test
    public void testBasicSignature() throws InterruptedException {
        SignatureRequest request = new SignatureRequest();
        request.setAccessSecret(TEST_SECRET);
        request.setPath("/api/v1/test");
        request.setPayload("{}");
        
        // First signature
        SignatureResponse response1 = acSignature.sign(request);
        assertNotNull(response1.getHash());
        assertTrue(response1.getTimestamp() > 0);
        
        // Wait for 1 second to ensure different timestamp
        Thread.sleep(1000);
        
        // Second signature - will be different due to new timestamp
        SignatureResponse response2 = acSignature.sign(request);
        assertNotNull(response2.getHash());
        assertTrue(response2.getTimestamp() > 0);
        
        // Verify timestamps are different
        assertNotEquals(response1.getTimestamp(), response2.getTimestamp());
        
        // Verify hashes are different (because timestamps are different)
        assertNotEquals(response1.getHash(), response2.getHash());
    }
    
    @Test
    public void testSignatureWithSimplePayload() {
        SignatureRequest request = new SignatureRequest();
        request.setAccessSecret(TEST_SECRET);
        request.setPath("/api/v1/test");
        request.setPayload("{\"key2\":\"value2\",\"key1\":\"value1\"}");
        
        SignatureResponse response = acSignature.sign(request);
        assertNotNull(response.getHash());
        assertTrue(response.getTimestamp() > 0);
    }
    
    @Test
    public void testSignatureWithComplexPayload() {
        SignatureRequest request = new SignatureRequest();
        request.setAccessSecret(TEST_SECRET);
        request.setPath("/api/v1/complex");
        
        String complexJson = "{"
            + "\"nested\":{\"c\":3,\"a\":1,\"b\":2},"
            + "\"array\":[{\"z\":\"last\",\"y\":\"middle\",\"x\":\"first\"}],"
            + "\"simple\":\"value\""
            + "}";
        request.setPayload(complexJson);
        
        SignatureResponse response = acSignature.sign(request);
        assertNotNull(response.getHash());
        assertTrue(response.getTimestamp() > 0);
    }
    
    @Test
    public void testSignatureWithNullValues() {
        SignatureRequest request = new SignatureRequest();
        request.setAccessSecret(TEST_SECRET);
        request.setPath("/api/v1/nulls");
        
        String jsonWithNulls = "{"
            + "\"nullValue\":null,"
            + "\"regularValue\":\"test\","
            + "\"nestedNull\":{\"key\":null}"
            + "}";
        request.setPayload(jsonWithNulls);
        
        SignatureResponse response = acSignature.sign(request);
        assertNotNull(response.getHash());
        assertTrue(response.getTimestamp() > 0);
    }

    @Test
    public void testMissingAccessSecret() {
        SignatureRequest request = new SignatureRequest();
        request.setPath("/api/v1/test");
        request.setPayload("{}");
        
        assertThrows(IllegalArgumentException.class, () -> {
            acSignature.sign(request);
        });
    }
    
    @Test
    public void testPathNormalization() {
        SignatureRequest request = new SignatureRequest();
        request.setAccessSecret(TEST_SECRET);
        request.setPath("/API/v1/TEST?param=value");
        request.setPayload("{}");
        
        SignatureResponse response = acSignature.sign(request);
        assertNotNull(response.getHash());
        assertTrue(response.getTimestamp() > 0);
    }
}