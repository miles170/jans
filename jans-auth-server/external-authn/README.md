# External Authn

## Design
![](external-authn-diagram.png)

Follow the instructions below to set up:

## Jans App Configuration
Above the **jans-auth** record of the **jansAppConf** table, enable a new attribute on the **jansConfDyn** field

Example:
```
"authorizationRequestCustomAllowedParameters": {
    {
        "paramName": "jansKey",
        "returnInResponse": false
    }
}
```

## Enable Custom Script

- ### Person Authentication - External Authn

Create a new record in table **jansCustomScr**.
```
INSERT INTO jansCustomScr ( doc_id, objectClass, dn, inum, displayName, description, jansScr, jansScrTyp, jansProgLng, jansModuleProperty, jansConfProperty, jansLevel, jansRevision, jansEnabled, jansScrError, jansAlias ) 
VALUES ( 'PA01-EA01', 'jansCustomScr', 'inum=PA01-EA01,ou=scripts,o=jans', 'PA01-EA01', 'pa-external-authn', 'PA External Authn', '', 'person_authentication', 'python', '{"v": ["{\\"value1\\":\\"usage_type\\",\\"value2\\":\\"interactive\\",\\"description\\":\\"\\"}", "{\\"value1\\":\\"location_type\\",\\"value2\\":\\"ldap\\",\\"description\\":\\"\\"}"]}', '{"v": ["{\\"value1\\":\\"urlstep1\\",\\"value2\\":\\"http://demoexample.net:81\\",\\"hide\\":false,\\"description\\":\\"Url to return in step 1\\"}"]}', 1, 1, 0, NULL, '{"v": []}' );
```

Modify the **jansConfProperty** field by replacing **URL_REDIRECT_URI** with the url that you want to return to the first step
```
'{"v": ["{\\"value1\\":\\"urlstep1\\",\\"value2\\":\\"{URL_REDIRECT_URI}\\",\\"hide\\":false,\\"description\\":\\"Url to return in step 1\\"}"]}'
```

Modify the **jansScr** field by adding the content of the following link: [PersonAuthentication Script](pa-external-authn.py)

- ### ROPC (Resource Owner Password Credentials) Script - External Authn

Create a new record in table **jansCustomScr**.
```
INSERT INTO jansCustomScr ( doc_id, objectClass, dn, inum, displayName, description, jansScr, jansScrTyp, jansProgLng, jansModuleProperty, jansConfProperty, jansLevel, jansRevision, jansEnabled, jansScrError, jansAlias ) 
VALUES ( 'ROPC-EA01', 'jansCustomScr', 'inum=ROPC-EA01,ou=scripts,o=jans', 'ROPC-EA01', 'ropc-external-authn', 'ROPC External Authn', '', 'resource_owner_password_credentials', 'python', '{"v": ["{\\"value1\\":\\"location_type\\",\\"value2\\":\\"ldap\\",\\"description\\":\\"\\"}"]}', '{"v": []}', 1, 1, 0, NULL, '{"v": []}' );  
```

Modify the **jansScr** field by adding the content of the following link: [ROPC (Resource Owner Password Credentials) Script](ropc-external-authn.py)

- ### Update Token Script - External Authn

Create a new record in table **jansCustomScr**.
```
INSERT INTO jansCustomScr ( doc_id, objectClass, dn, inum, displayName, description, jansScr, jansScrTyp, jansProgLng, jansModuleProperty, jansConfProperty, jansLevel, jansRevision, jansEnabled, jansScrError, jansAlias ) 
VALUES ( 'UPDT-EA01', 'jansCustomScr', 'inum=UPDT-EA01,ou=scripts,o=jans', 'UPDT-EA01', 'update-token-external-authn', 'Update token External Authn', '', 'update_token', 'python', '{"v": ["{\\"value1\\":\\"location_type\\",\\"value2\\":\\"ldap\\",\\"description\\":\\"\\"}"]}', '{"v": []}', 1, 1, 0, NULL, '{"v": []}' );  
```

Modify the **jansScr** field by adding the content of the following link: [Update Token Script](ut-external-authn.py)

In this script you can choose whether to use the header or payload of the **id_token** for the **callback_uri**:
```
jsonWebResponse.getHeader().setClaim("callback_uri", callbackUri)
jsonWebResponse.getClaims().setClaim("callback_uri", callbackUri)
```
## Call Flow
- ### Step 1: /authorize
Request:
```
curl --location --request GET 'https://jans.localhost/jans-auth/restv1/authorize?response_type=code&client_id=14e36e18-1d51-41ac-a4cf-a7dc677f53a5&scope=openid+profile+address+email&redirect_uri=https://jans.localhost/jans-auth-rp/home.htm&state=a84dd61f-533c-46a4-9315-a66fda3e9a4e&nonce=80e6bd2b-eb78-48b9-be9c-6bb33ef80991&ui_locales=&claims_locales=&request_session_id=false&acr_values='
```
Response: (return the **redirect_uri** with jansKey)
```
http://demoexample.net:81/?jansKey=752877cd-1750-463f-a8de-35c606eff384
```

- ### Step 2: /token
Request: (**Authorization** = Basic base64(client_id:client_secret))
```
curl --location --request POST 'https://jans.localhost/jans-auth/restv1/token' \
--header 'Authorization: Basic MTRlMzZlMTgtMWQ1MS00MWFjLWE0Y2YtYTdkYzY3N2Y1M2E1Ojk5NzE4NWU1LTc2NGUtNGE4Yi1hNjYwLTdjZmQ4NzJhNjc0Ng==' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=test_user' \
--data-urlencode 'password=test_user_password' \
--data-urlencode 'scope=openid' \
--data-urlencode 'jansKey=752877cd-1750-463f-a8de-35c606eff384'
```
Response: (id_token contains in header or payload callback_uri)
```
{
    "access_token": "a0878887-b998-4da4-aa0b-4e74bd9a4441",
    "refresh_token": "d8b618ac-9d9c-4b90-9cac-aafb1e38e82e",
    "scope": "openid",
    "id_token": "eyJraWQiOiIxNmEyMmIwMy03YjUzLTQxY2QtYWE3OS01YjI1ZjNlY2QzOGNfc2lnX3JzMjU2IiwiY2FsbGJhY2tfdXJpIjoiaHR0cHM6Ly9qYW5zLmxvY2FsaG9zdC9qYW5zLWF1dGgvcmVzdHYxL2F1dGhvcml6ZT9yZXNwb25zZV90eXBlPWNvZGUmY2xpZW50X2lkPTE0ZTM2ZTE4LTFkNTEtNDFhYy1hNGNmLWE3ZGM2NzdmNTNhNSZyZWRpcmVjdF91cmk9aHR0cHM6Ly9qYW5zLmxvY2FsaG9zdC9qYW5zLWF1dGgtcnAvaG9tZS5odG0mc2Vzc2lvbl9pZD1qYW5zSWQ9NWFkMDhkZjYtZjQ0ZS00NDZkLWI3YzUtNTc0OGRmYTYzZTkwLG91PXNlc3Npb25zLG89amFucyIsInR5cCI6Imp3dCIsImFsZyI6IlJTMjU2In0.eyJhdWQiOiIxNGUzNmUxOC0xZDUxLTQxYWMtYTRjZi1hN2RjNjc3ZjUzYTUiLCJhY3IiOiJzaW1wbGVfcGFzc3dvcmRfYXV0aCIsInN1YiI6InpMM3FrWFVwZE9OOXoxRDNNVUkyRG1CTHd3MzA1RUt4eWY3al8zb0oyaDQiLCJjb2RlIjoiY2FlNmU2NTQtYTI0YS00NDM0LWJjNTYtYzI0OTRlZWVjMjUxIiwiYW1yIjpbXSwiY2FsbGJhY2tfdXJpIjoiaHR0cHM6Ly9qYW5zLmxvY2FsaG9zdC9qYW5zLWF1dGgvcmVzdHYxL2F1dGhvcml6ZT9yZXNwb25zZV90eXBlPWNvZGUmY2xpZW50X2lkPTE0ZTM2ZTE4LTFkNTEtNDFhYy1hNGNmLWE3ZGM2NzdmNTNhNSZyZWRpcmVjdF91cmk9aHR0cHM6Ly9qYW5zLmxvY2FsaG9zdC9qYW5zLWF1dGgtcnAvaG9tZS5odG0mc2Vzc2lvbl9pZD1qYW5zSWQ9NWFkMDhkZjYtZjQ0ZS00NDZkLWI3YzUtNTc0OGRmYTYzZTkwLG91PXNlc3Npb25zLG89amFucyIsImlzcyI6Imh0dHBzOi8vamFucy5sb2NhbGhvc3QiLCJleHAiOjE2NTk2NjQxNjMsImdyYW50IjoicGFzc3dvcmQiLCJpYXQiOjE2NTk2NjA1NjMsInNpZCI6IjE1NzVlZTI4LWRjYmItNDAyMC05Y2FhLTlkOGFiNjE5NDhmYiIsIm94T3BlbklEQ29ubmVjdFZlcnNpb24iOiJvcGVuaWRjb25uZWN0LTEuMCJ9.UX8vvuh_7ldTGLUUu0_i-AOYqGouTlNdK-h-X2gNnxNDmJH9Q_jmZlF5n1APUwpWc563vw_2TBce2HYlfUyCukCrRW30Njx2Bb8Q62yhIA4p7ZxDDT6MBTtbnoA1TniQMUZ3mt6a_kI9F_x4wSZl1aefwUkm3pZj5swGmsnaR6kpH7P4tH_PUOnYaYczXxg1fuShHaIeHd90_Kh36E6nuqMu6DcGK5Gy4UFeHgWhKfx3rFhHvDjvYGgJz37zSF0Enm3SpjqURHdVvvVxEKVkq3spAdUMwno_5dfnjWmfugRVuDV7DdiMb9DLoHvLAbzFD7LW6Ing2mprGpEZIwsFrQ",
    "token_type": "Bearer",
    "expires_in": 299
}
```

- ### Step 3: callback_uri (/authorize)

Request:
```
curl --location --request GET 'https://jans.localhost/jans-auth/restv1/authorize?response_type=code&client_id=14e36e18-1d51-41ac-a4cf-a7dc677f53a5&redirect_uri=https://jans.localhost/jans-auth-rp/home.htm&jansKey=752877cd-1750-463f-a8de-35c606eff384'
```

Response: (return to the **redirect_uri**)
```
https://jans.localhost/jans-auth-rp/home.htm&code=752877cd-1750-463f-a8de-35c606eff384
```