# jans-auth-server Configuration

Update jans-auth-server JSON configuration ```jansConfDyn``` (ou=jans-auth,ou=configuration,o=jans):

```json
{
  .....
  .....
  .....
  "oxElevenGenerateKeyEndpoint": "http://jenkins-dev1.jans.io/jans-eleven/restv1/generateKey",
  "oxElevenSignEndpoint": "http://jenkins-dev1.jans.io/jans-eleven/restv1/sign",
  "oxElevenVerifySignatureEndpoint": "http://jenkins-dev1.jans.io/jans-eleven/restv1/verifySignature",
  "oxElevenDeleteKeyEndpoint": "http://jenkins-dev1.jans.io/jans-eleven/restv1/deleteKey",
  .....
  .....
  .....
  "webKeysStorage": "pkcs11",
  .....
  .....
  .....
}
```

Generate keys with main class ```io.jans.as.client.util.KeyGenerator```:

```shell
KeyGenerator -sig_keys RS256 RS384 RS512 ES256 ES384 ES512 \
  -ox11 http://jenkins-dev1.jans.io/jans-eleven/restv1/generateKey \
  -expiration 365 \
  -at xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

| Parameter   | Description                       |
-------------|-----------------------------------|
| -sig_keys   | Signature keys to generate        |
| -enc_keys   | Encryption keys to generate       |
| -ox11       | jans-eleven Generate Key Endpoint |
| -expiration | Expiration in days                |
| -at         | jans-eleven Access Token          |
| -h          | Show help                         |

Show command help:

```shell
KeyGenerator -h
```

Update jans-auth-server JSON configuration ```jansConfWebKeys``` with the jwks result of the previous command:

```json
{
  "keys" : [
    .....
    .....
    .....
  ]
}
```