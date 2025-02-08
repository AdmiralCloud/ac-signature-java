# AC Signature Java

Java implementation of AdmiralCloud's signature helper. You have to use it to make signed calls against AdmiralCloud API.

## Installation

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.admiralcloud</groupId>
    <artifactId>ac-signature</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage

### Create a Signature

```java
ACSignature acSignature = new ACSignature();

SignatureRequest request = new SignatureRequest();
request.setAccessSecret("your-secret-key");
request.setPath("/v5/search");
request.setPayload("{\"searchTerm\": \"My new video\"}");  // Must be valid JSON

SignatureResponse response = acSignature.sign(request);
String hash = response.getHash();
long timestamp = response.getTimestamp();
```

#### With Payload

```java
SignatureRequest request = new SignatureRequest();
request.setAccessSecret("your-secret-key");
request.setPath("/v5/search");
request.setPayload("{\"searchTerm\": \"My new video\"}"); 
request.setIdentifier("123-456"); // before using, please make sure to contact AC team


SignatureResponse response = acSignature.sign(request);

// Using with Spring RestTemplate
HttpHeaders headers = new HttpHeaders();
headers.set("X-AdmiralCloud-ClientId", "clientid-of-your-app"); // please contact AC team if you are unsure about this value
headers.set("X-AdmiralCloud-AccessKey", "your-access-key");
headers.set("X-AdmiralCloud-Hash", signature.getHash());
headers.set("X-AdmiralCloud-RTS", String.valueOf(signature.getTimestamp()));
headers.set("X-AdmiralCloud-Version", "5");

// Optional: for requests "on behalf"
// headers.set("X-AdmiralCloud-Identifier", "user-123");

HttpEntity<String> entity = new HttpEntity<>(request.getPayload(), headers);
RestTemplate restTemplate = new RestTemplate();
ResponseEntity<String> response = restTemplate.exchange(
    "https://api.example.com/v5/search",
    HttpMethod.POST,
    entity,
    String.class
);

// Using with OkHttp
OkHttpClient client = new OkHttpClient();
Request httpRequest = new Request.Builder()
    .url("https://api.example.com/v5/search")
    .addHeader("X-AdmiralCloud-ClientId", "clientid-of-your-app")
    .addHeader("X-AdmiralCloud-AccessKey", "your-access-key")
    .addHeader("X-AdmiralCloud-Hash", signature.getHash())
    .addHeader("X-AdmiralCloud-RTS", String.valueOf(signature.getTimestamp()))
    .addHeader("X-AdmiralCloud-Version", "5")
    .post(RequestBody.create(
        MediaType.parse("application/json"),
        request.getPayload()
    ))
    .build();

Response response = client.newCall(httpRequest).execute();
```
## Technical Details

### JSON Handling

- All payloads must be valid JSON
- Object keys are sorted alphabetically at all levels
- Arrays maintain their order, but objects within arrays are sorted
- `null` values are preserved in the JSON structure

### Timestamp

- Uses Unix timestamp in seconds
- Automatically sets current timestamp if not provided
- Used in signature calculation to ensure request freshness

### Path Handling

- Paths are converted to lowercase
- Query parameters are removed from the path before signature calculation

## Requirements

- Java 17 or higher
- Jackson library for JSON processing
- Maven for dependency management

## Testing

Run the tests using Maven:

```bash
mvn test
```

Or using Docker:

```bash
docker build -t ac-signature-java .
docker run ac-signature-java
```


## License
MIT License
Copyright AdmiralCloud AG. All Rights Reserved.