package com.admiralcloud.signature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

public class ACSignature {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public SignatureResponse sign(SignatureRequest request) {
        if (request.getAccessSecret() == null || request.getAccessSecret().isEmpty()) {
            throw new IllegalArgumentException("accessSecretMissing");
        }

        long timestamp = request.getTimestamp() != 0 
            ? request.getTimestamp() 
            : Instant.now().getEpochSecond();

        try {
            String valueToHash = buildValueToHash(request, timestamp);
            String hash = calculateHmac(valueToHash, request.getAccessSecret());
            return new SignatureResponse(hash, timestamp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create signature", e);
        }
    }


    private String buildValueToHash(SignatureRequest request, long timestamp) throws Exception {
        StringBuilder valueBuilder = new StringBuilder();
        
        // Add path (lowercase and remove query params)
        String path = request.getPath().toLowerCase().split("\\?")[0];
        valueBuilder.append(path);
        
        // Add timestamp
        valueBuilder.append('\n').append(timestamp);
        
        // Add payload - ensure sorted JSON
        String payload = request.getPayload();
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }

        // Parse JSON, sort it, and convert back to string
        JsonNode jsonNode = objectMapper.readTree(payload);
        JsonNode sortedNode = deepSortJsonNode(jsonNode);
        valueBuilder.append('\n').append(objectMapper.writeValueAsString(sortedNode));

        String result = valueBuilder.toString();
        return result;
    }

    private JsonNode deepSortJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sortedObject = JsonNodeFactory.instance.objectNode();
            List<String> fieldNames = new ArrayList<>();
            node.fieldNames().forEachRemaining(fieldNames::add);
            Collections.sort(fieldNames);  // Sort field names alphabetically
            
            for (String fieldName : fieldNames) {
                sortedObject.set(fieldName, deepSortJsonNode(node.get(fieldName)));
            }
            return sortedObject;
        } 
        else if (node.isArray()) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (JsonNode element : node) {
                arrayNode.add(deepSortJsonNode(element));
            }
            return arrayNode;
        }
        return node;
    }

    private String calculateHmac(String value, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), 
                HMAC_ALGORITHM
            );
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }
}