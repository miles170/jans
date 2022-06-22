# jans-eleven

Java Web Application providing REST API's for a PKCS#11 interface.

jans-eleven uses the PKCS#11 interface with [SoftHSMv2](https://www.opendnssec.org/softhsm/) without having a Hardware Security Module.

## Supported Algorithms

Algorithm | Family
----------|-------------
none      | -
HS256     | HMAC
HS384     | HMAC
HS512     | HMAC
RS256     | RSA
RS384     | RSA
RS512     | RSA
ES256     | EC
ES384     | EC
ES512     | EC

## Operations

### /generateKey

- **URL:** https://jenkins-dev1.jans.io/jans-eleven/restv1/generateKey
- **Method:** POST
- **Media Type:** application/x-www-form-urlencoded
- **Data Params**
    - accessToken [string]
    - signatureAlgorithm [string]
    - expirationTime [long]

#### Sample Code - RS256

```java
GenerateKeyRequest request = new GenerateKeyRequest();
request.setAccessToken(accessToken);
request.setSignatureAlgorithm(SignatureAlgorithm.RS256);
request.setExpirationTime(expirationTime);

GenerateKeyClient client = new GenerateKeyClient(generateKeyEndpoint);
client.setRequest(request);

GenerateKeyResponse response = client.exec();

assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertNotNull(response.getKeyId());
String rs256Alias = response.getKeyId();
```

#### Sample Request - RS256

```http request
POST /jans-eleven/restv1/generateKey HTTP/1.1
Host: jenkins-dev1.jans.io
Cache-Control: no-cache
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

signatureAlgorithm=RS256&expirationTime=1462916947752
```

#### Sample Response - RS256

```json
{
    "kty": "RSA",
    "kid": "57a6c4fd-f65e-4baa-8a5d-f34812265383",
    "use": "sig",
    "alg": "RS256",
    "exp": 1462916947752
}
```

#### Sample Code - ES256

```java
GenerateKeyRequest request = new GenerateKeyRequest();
request.setAccessToken(accessToken);
request.setSignatureAlgorithm(SignatureAlgorithm.ES256);
request.setExpirationTime(expirationTime);

GenerateKeyClient client = new GenerateKeyClient(generateKeyEndpoint);
client.setRequest(request);

GenerateKeyResponse response = client.exec();

assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertNotNull(response.getKeyId());
String es256Alias = response.getKeyId();
```

#### Sample Request - ES256

```http request
POST /jans-eleven/restv1/generateKey HTTP/1.1
Host: jenkins-dev1.jans.io
Cache-Control: no-cache
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

signatureAlgorithm=ES256&expirationTime=1462916947752
```

#### Sample Response - ES256

```json
{
    "kty": "EC",
    "kid": "f6ade591-4230-4114-8147-316dde969395",
    "use": "sig",
    "alg": "ES256",
    "crv": "P-256",
    "exp": 1462916947752
}
```

### /sign

- **URL:** https://jenkins-dev1.jans.io/jans-eleven/restv1/sign
- **Method:** POST
- **Media Type:** application/json
- **Data Params**
    - accessToken [string]

```json
{
    "signingInput": [string], 
    "signatureAlgorithm": [string],
    "alias": [string],
    "sharedSecret": [string]
}
```

#### Sample Code - HS256

```java
SignRequest request = new SignRequest();
request.setAccessToken(accessToken);
request.getSignRequestParam().setSigningInput(signingInput);
request.getSignRequestParam().setSignatureAlgorithm(SignatureAlgorithm.HS256);
request.getSignRequestParam().setSharedSecret(sharedSecret);

SignClient client = new SignClient(signEndpoint);
client.setRequest(request);

SignResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertNotNull(response.getSignature());
String hs256Signature = response.getSignature();
```

#### Sample Request - HS256

```http request
POST /jans-eleven/restv1/sign HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "Signing Input",
    "signatureAlgorithm": "HS256",
    "sharedSecret": "secret"
}
```

#### Sample Response - HS256

```json
{
    "sig": "CZag3MkkRmJXCnDbE43k6gRit_7ZIPzzpBMHXiNNHBg"
}
```

#### Sample Code - RS256

```java
SignRequest request = new SignRequest();
request.setAccessToken(accessToken);
request.getSignRequestParam().setSigningInput(signingInput);
request.getSignRequestParam().setAlias(rs256Alias);
request.getSignRequestParam().setSignatureAlgorithm(SignatureAlgorithm.RS256);

SignClient client = new SignClient(signEndpoint);
client.setRequest(request);

SignResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertNotNull(response.getSignature());
String rs256Signature = response.getSignature();
```

#### Sample Request - RS256

```http request
POST /jans-eleven/restv1/sign HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "Signing Input",
    "signatureAlgorithm": "RS256",
    "alias": "57a6c4fd-f65e-4baa-8a5d-f34812265383"
}
```

#### Sample Response - RS256

```json
{
    "sig": "TharYC_SVPb_PDWyLM2d1_XsAAiePEMom0Wja8R9aWZpP2mRrzMJKuLUcOG7QE7JxnVgQmGGnEV8QPKguGDca5S2EU9NiodFBzg6N4JEFC5FvrpDyZPRhtQP3OKshGWyLKa37KddUWGVRTwfluUhirMRgFmTMYjv6Wuhj_Dx7DoBvMY5KbEkIcBm1tqvqT2U02RNo8ts0PSW3z3hkdygCAcwqmzb0ICBxZ6aCePmVtSXaicEX0Z8FuZY0t4b-PjkuCIUIPLdb5043HFdGX1dwErEi3Y1j-osALnamS8LCqvogjMxbx_MJt6QaUkW952JT0Tk1Xvc_J81ZekzvMpptw"
}
```

#### Sample Code - ES256

```java
SignRequest request = new SignRequest();
request.setAccessToken(accessToken);
request.getSignRequestParam().setSigningInput(signingInput);
request.getSignRequestParam().setAlias(es256Alias);
request.getSignRequestParam().setSignatureAlgorithm(SignatureAlgorithm.ES256);

SignClient client = new SignClient(signEndpoint);
client.setRequest(request);

SignResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertNotNull(response.getSignature());
String es256Signature = response.getSignature();
```

#### Sample Request - ES256

```http request
POST /jans-eleven/restv1/sign HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "Signing Input",
    "signatureAlgorithm": "ES256",
    "alias": "f6ade591-4230-4114-8147-316dde969395"
}
```

#### Sample Response - ES256

```json
{
    "sig": "MEUCIQCe-t-b4ba7OaIBuNKHCCW2GIKPzjTZKCdBAP4EEmVJAQIgXHIW3c9_Ax2DvUHu_tJJzV9LUeYH5uw40m-h2qy-jgM"
}
```

### /verifySignature

- **URL:** https://jenkins-dev1.jans.io/jans-eleven/restv1/verifySignature
- **Method:** POST
- **Media Type:** application/json
- **Data Params**
    - accessToken [string]

```json
{
    "signingInput": [string],
    "signature": [string],
    "alias": [string],
    "jwksRequestParam": { 
        "keyRequestParams": [{
            "alg": [string],
            "kid": [string],
            "use": [string],
            "kty": [string],
            "n": [string],
            "e": [string],
            "crv": [string],
            "x": [string],
            "y": [string]
        }],
    },
    "sharedSecret": [string],
    "signatureAlgorithm": [string]
}
```

#### Sample Code - none

```java
VerifySignatureRequest request = new VerifySignatureRequest();
request.setAccessToken(accessToken);
request.getVerifySignatureRequestParam().setSigningInput(signingInput);
request.getVerifySignatureRequestParam().setSignature(noneSignature);
request.getVerifySignatureRequestParam().setSignatureAlgorithm(SignatureAlgorithm.NONE);

VerifySignatureClient client = new VerifySignatureClient(verifySignatureEndpoint);
client.setRequest(request);

VerifySignatureResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertTrue(response.isVerified());
```

#### Sample Request - none

```http request
POST /jans-eleven/restv1/verifySignature HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "Signing Input",
    "signature": "",
    "signatureAlgorithm": "none"
}
```

#### Sample Response - none

```json
{
    "verified": true
}
```

#### Sample Code - HS256

```java
VerifySignatureRequest request = new VerifySignatureRequest();
request.setAccessToken(accessToken);
request.getVerifySignatureRequestParam().setSigningInput(signingInput);
request.getVerifySignatureRequestParam().setSignature(hs256Signature);
request.getVerifySignatureRequestParam().setSharedSecret(sharedSecret);
request.getVerifySignatureRequestParam().setSignatureAlgorithm(SignatureAlgorithm.HS256);

VerifySignatureClient client = new VerifySignatureClient(verifySignatureEndpoint);
client.setRequest(request);

VerifySignatureResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertTrue(response.isVerified());
```

#### Sample Request - HS256

```http request
POST /jans-eleven/restv1/verifySignature HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "Signing Input",
    "signature": "CZag3MkkRmJXCnDbE43k6gRit_7ZIPzzpBMHXiNNHBg",
    "signatureAlgorithm": "HS256",
    "sharedSecret": "secret"
}
```

#### Sample Response - HS256

```json
{
    "verified": true
}
```

#### Sample Code - RS256

```java
String alias = "RS256SIG";
JwksRequestParam jwksRequestParam = new JwksRequestParam();
KeyRequestParam keyRequestParam = new KeyRequestParam("RSA", "sig", "RS256", alias);
keyRequestParam.setN("AJpGcIVu7fmQJLHXeAClhXaJD7SvuABjYiPcT9IbKFWGWj51GgD-CxtyrQGXT0ctGEEsXOzMZM40q-V7GR-5qkJ_OalVTTc_EeKAHao45bZPsPHLxvusNfrfpyhc6JjF2TQhoOqxbgMgQ9L6W9q9fSjgzx-tPlD0d3X0GZOEQ_NYGstZWRRBwHgsxA2IRYtwSH-v76yPpxF9poLIWdnBKtKfSr6UY7p1BrLmMm0DdMhjQLn6j4S_eB-p2WyBwObvsLqO6FdClpZFtGr82Km2uinpHvZ6KJ_MUEW1sijPPI3rIGbaUbLtQJwX5GVynAP5qU2qRVkcsrKt-GeNoz6QNLM");
keyRequestParam.setE("AQAB");
jwksRequestParam.setKeyRequestParams(Arrays.asList(keyRequestParam));
        
VerifySignatureRequest request = new VerifySignatureRequest();
request.setAccessToken(accessToken);
request.getVerifySignatureRequestParam().setSigningInput(signingInput);
request.getVerifySignatureRequestParam().setSignature(signature);
request.getVerifySignatureRequestParam().setAlias(alias);
request.getVerifySignatureRequestParam().setJwksRequestParam(jwksRequestParam);
request.getVerifySignatureRequestParam().setSignatureAlgorithm(SignatureAlgorithm.RS256);

VerifySignatureClient client = new VerifySignatureClient(verifySignatureEndpoint);
client.setRequest(request);

VerifySignatureResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertTrue(response.isVerified());
```

#### Sample Request - RS256

```http request
POST /jans-eleven/restv1/verifySignature HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlJTMjU2U0lHIn0.eyJpc3MiOiJAITkwQ0MuMkUzOC43NzRDLjYxMEIhMDAwMSFGRDNCLkIwQTAhMDAwOCEzN0JBLkExRjEiLCJzdWIiOiJAITkwQ0MuMkUzOC43NzRDLjYxMEIhMDAwMSFGRDNCLkIwQTAhMDAwOCEzN0JBLkExRjEiLCJhdWQiOiJodHRwczovL2NlLmdsdXUuaW5mbzo4NDQzL3NlYW0vcmVzb3VyY2UvcmVzdHYxL294YXV0aC90b2tlbiIsImp0aSI6Ijc0NWY0N2RmLTY3ZDQtNDBlOC05MzhlLTVlMmI5OWQ5ZTQ3YSIsImV4cCI6MTQ2MTAzMDE5MSwiaWF0IjoxNDYxMDI5ODkxfQ",
    "signature": "RB8KEbzMTovJLGBzxbaxzLvZxj0CjAun1LG1KMuw9t9LBNzA9kxt_QT9qm_vr_SpCFuFhIy6ZeDx4lVPGks6JbWOYxmsCUcxe8l_tkCxOb6fwm3GTttDhHsk1JKPwDVjzXWAyW8i5Wiv39JD57K1SOs3xIOWIp7Uu7lR7HFw52ybT35enxiaGj1H3ROX5dd26GE35McTrEBxPLgAj_yEzAADBqI1nOmDvpzSpo3pkSoxaW8UkncIIdcG8WkPru-exN1nWqnsqA5rX3XxwlWNElq6O9kLOZQKKHbCF0EyZwnave3EdWp56XaZ9V5Y20_NL-aaR7DedZ5xPAyzLFCW2A",
    "signatureAlgorithm": "RS256",
    "alias": "RS256SIG",
    "jwksRequestParam": {
        "keyRequestParams": [{
            "alg": "RS256",
            "kid": "RS256SIG",
            "use": "sig",
            "kty": "RSA",
            "n": "AJpGcIVu7fmQJLHXeAClhXaJD7SvuABjYiPcT9IbKFWGWj51GgD-CxtyrQGXT0ctGEEsXOzMZM40q-V7GR-5qkJ_OalVTTc_EeKAHao45bZPsPHLxvusNfrfpyhc6JjF2TQhoOqxbgMgQ9L6W9q9fSjgzx-tPlD0d3X0GZOEQ_NYGstZWRRBwHgsxA2IRYtwSH-v76yPpxF9poLIWdnBKtKfSr6UY7p1BrLmMm0DdMhjQLn6j4S_eB-p2WyBwObvsLqO6FdClpZFtGr82Km2uinpHvZ6KJ_MUEW1sijPPI3rIGbaUbLtQJwX5GVynAP5qU2qRVkcsrKt-GeNoz6QNLM",
            "e": "AQAB"
        }]
    }
}
```

#### Sample Response - RS256

```json
{
    "verified": true
}
```

#### Sample Code - RS256

```java
VerifySignatureRequest request = new VerifySignatureRequest();
request.setAccessToken(accessToken);
request.getVerifySignatureRequestParam().setSigningInput(signingInput);
request.getVerifySignatureRequestParam().setSignature(rs256Signature);
request.getVerifySignatureRequestParam().setAlias(rs256Alias);
request.getVerifySignatureRequestParam().setSignatureAlgorithm(SignatureAlgorithm.RS256);

VerifySignatureClient client = new VerifySignatureClient(verifySignatureEndpoint);
client.setRequest(request);

VerifySignatureResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertTrue(response.isVerified());
```

#### Sample Request - RS256

```http request
POST /jans-eleven/restv1/verifySignature HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "Signing Input",
    "signature": "TharYC_SVPb_PDWyLM2d1_XsAAiePEMom0Wja8R9aWZpP2mRrzMJKuLUcOG7QE7JxnVgQmGGnEV8QPKguGDca5S2EU9NiodFBzg6N4JEFC5FvrpDyZPRhtQP3OKshGWyLKa37KddUWGVRTwfluUhirMRgFmTMYjv6Wuhj_Dx7DoBvMY5KbEkIcBm1tqvqT2U02RNo8ts0PSW3z3hkdygCAcwqmzb0ICBxZ6aCePmVtSXaicEX0Z8FuZY0t4b-PjkuCIUIPLdb5043HFdGX1dwErEi3Y1j-osALnamS8LCqvogjMxbx_MJt6QaUkW952JT0Tk1Xvc_J81ZekzvMpptw",
    "signatureAlgorithm": "RS256",
    "alias": "57a6c4fd-f65e-4baa-8a5d-f34812265383"
}
```

#### Sample Response - RS256

```json
{
    "verified": true
}
```

#### Sample Code - ES256

```java
String alias = "ES256SIG";
JwksRequestParam jwksRequestParam = new JwksRequestParam();
KeyRequestParam keyRequestParam = new KeyRequestParam("EC", "sig", "ES256", alias);
keyRequestParam.setCrv("P-256");
keyRequestParam.setX("QDpwgxzGm0XdD-3Rgk62wiUnayJDS5iV7nLBwNEX4SI");
keyRequestParam.setY("AJ3IvktOcoICgdFPAvBM44glxcqoHzqyEmj60eATGf5e");
jwksRequestParam.setKeyRequestParams(Arrays.asList(keyRequestParam));

VerifySignatureRequest request = new VerifySignatureRequest();
request.setAccessToken(accessToken);
request.getVerifySignatureRequestParam().setSigningInput(signingInput);
request.getVerifySignatureRequestParam().setSignature(signature);
request.getVerifySignatureRequestParam().setAlias(alias);
request.getVerifySignatureRequestParam().setJwksRequestParam(jwksRequestParam);
request.getVerifySignatureRequestParam().setSignatureAlgorithm(SignatureAlgorithm.ES256);

VerifySignatureClient client = new VerifySignatureClient(verifySignatureEndpoint);
client.setRequest(request);

VerifySignatureResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertTrue(response.isVerified());
```

#### Sample Request - ES256

```http request
POST /jans-eleven/restv1/verifySignature HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiIsImtpZCI6IkVTMjU2U0lHIn0.eyJpc3MiOiJAITkwQ0MuMkUzOC43NzRDLjYxMEIhMDAwMSFGRDNCLkIwQTAhMDAwOCE3OUIzLjY3MzYiLCJzdWIiOiJAITkwQ0MuMkUzOC43NzRDLjYxMEIhMDAwMSFGRDNCLkIwQTAhMDAwOCE3OUIzLjY3MzYiLCJhdWQiOiJodHRwczovL2NlLmdsdXUuaW5mbzo4NDQzL3NlYW0vcmVzb3VyY2UvcmVzdHYxL294YXV0aC90b2tlbiIsImp0aSI6IjQ0ZjU0NmU0LWRmMmMtNDE5Ny1iNTNjLTIzNzhmY2YwYmRiZSIsImV4cCI6MTQ2MTAzMjgzMiwiaWF0IjoxNDYxMDMyNTMyfQ",
    "signature": "MEQCIGmPSoCExpDu2jPkxttRZ0hjKId9SQM1pP3PLd4CXmt9AiB57tUzvBILyBvHqf3bHVMi0Fsy8M-v-ERib2KVdWJLtg",
    "signatureAlgorithm": "ES256",
    "alias": "ES256SIG",
    "jwksRequestParam": {
        "keyRequestParams": [{
            "alg": "ES256",
            "kid": "ES256SIG",
            "use": "sig",
            "kty": "EC",
            "crv": "P-256",
            "x": "QDpwgxzGm0XdD-3Rgk62wiUnayJDS5iV7nLBwNEX4SI",
            "y": "AJ3IvktOcoICgdFPAvBM44glxcqoHzqyEmj60eATGf5e"
        }]
    }
}
```

#### Sample Response - ES256

```json
{
    "verified": true
}
```

#### Sample Code - ES256

```java
VerifySignatureRequest request = new VerifySignatureRequest();
request.setAccessToken(accessToken);
request.getVerifySignatureRequestParam().setSigningInput(signingInput);
request.getVerifySignatureRequestParam().setSignature(es256Signature);
request.getVerifySignatureRequestParam().setAlias(es256Alias);
request.getVerifySignatureRequestParam().setSignatureAlgorithm(SignatureAlgorithm.ES256);

VerifySignatureClient client = new VerifySignatureClient(verifySignatureEndpoint);
client.setRequest(request);

VerifySignatureResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertTrue(response.isVerified());
```

#### Sample Request - ES256

```http request
POST /jans-eleven/restv1/verifySignature HTTP/1.1
Host: jenkins-dev1.jans.io
Content-Type: application/json
Cache-Control: no-cache
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

{
    "signingInput": "Signing Input",
    "signature": "MEUCIQCe-t-b4ba7OaIBuNKHCCW2GIKPzjTZKCdBAP4EEmVJAQIgXHIW3c9_Ax2DvUHu_tJJzV9LUeYH5uw40m-h2qy-jgM",
    "signatureAlgorithm": "ES256",
    "alias": "f6ade591-4230-4114-8147-316dde969395"
}
```

#### Sample Response - ES256

```json
{
    "verified": true
}
```

### /deleteKey

- **URL:** https://jenkins-dev1.jans.io/jans-eleven/restv1/deleteKey
- **Method:** POST
- **Media Type:** application/x-www-form-urlencoded
- **Data Params**
    - accessToken [string]
    - kid [string]

#### Sample Code - RS256

```java
DeleteKeyRequest request = new DeleteKeyRequest();
request.setAccessToken(accessToken);
request.setAlias(rs256Alias);

DeleteKeyClient client = new DeleteKeyClient(deleteKeyEndpoint);
client.setRequest(request);

DeleteKeyResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertTrue(response.isDeleted());
```

#### Sample Request - RS256

```http request
POST /jans-eleven/restv1/deleteKey HTTP/1.1
Host: jenkins-dev1.jans.io
Cache-Control: no-cache
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

kid=57a6c4fd-f65e-4baa-8a5d-f34812265383
```

#### Sample Response - RS256

```json
{
    "deleted": true
}
```

#### Sample Code - ES256

```java
DeleteKeyRequest request = new DeleteKeyRequest();
request.setAccessToken(accessToken);
request.setAlias(es256Alias);

DeleteKeyClient client = new DeleteKeyClient(deleteKeyEndpoint);
client.setRequest(request);

DeleteKeyResponse response = client.exec();
assertEquals(response.getStatus(), HttpStatus.SC_OK);
assertTrue(response.isDeleted());
```

#### Sample Request - ES256

```http request
POST /jans-eleven/restv1/deleteKey HTTP/1.1
Host: jenkins-dev1.jans.io
Cache-Control: no-cache
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

kid=f6ade591-4230-4114-8147-316dde969395
```

#### Sample Response - ES256

```json
{
    "deleted": true
}
```

## Run Tests

  1. Ensure jans-eleven is deployed and running.
  
  2. Edit the file Client/src/test/Resources/testng.xml to point to your jans-eleven deployment.
  
  3. cd Client.
  
  4. mvn test.

To access Gluu support, please register and open a ticket on [Gluu Support](http://support.gluu.org)
