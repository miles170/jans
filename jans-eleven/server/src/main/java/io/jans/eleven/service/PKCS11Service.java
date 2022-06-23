/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.eleven.service;

import com.google.common.base.Strings;
import io.jans.eleven.model.JwksRequestParam;
import io.jans.eleven.model.KeyRequestParam;
import io.jans.eleven.model.SignatureAlgorithm;
import io.jans.eleven.model.SignatureAlgorithmFamily;
import io.jans.eleven.util.Base64Util;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.*;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author Javier Rojas Blum
 * @author Yuriy Movchan
 * @version Jun 22, 2022
 */
public class PKCS11Service implements Serializable {

    private static final long serialVersionUID = -2541585376018724618L;

    private final Logger log = LoggerFactory.getLogger(PKCS11Service.class);

    public static final String PROVIDER = "SunPKCS11-SoftHSM";
    public static final String CONFIG_FILE_NAME = "softhsm.cfg";
    public static final String SECURITY_PROVIDER = "SunPKCS11";
    public static final String KEY_STORE = "PKCS11";

    private Provider provider;
    private KeyStore keyStore;
    private char[] pin;

    public PKCS11Service(String pin, Map<String, String> pkcs11Config) {
        log.info("Creating PKCS#11Service service");

        try {
            init(pin, pkcs11Config);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to init PKCS#11. Please fix it!!!.", e);
        }
    }

    public void init(String pin, Map<String, String> pkcs11Config) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.pin = pin.toCharArray();

        initConfig(pkcs11Config);
        Provider pkcs11Provider = Security.getProvider(SECURITY_PROVIDER);
        pkcs11Provider = pkcs11Provider.configure(CONFIG_FILE_NAME);

        if (-1 == Security.addProvider(pkcs11Provider)) {
            log.error("Could not add security provider");
            throw new RuntimeException("Could not add security provider");
        } else {
            log.debug("provider initialized !!!");
        }

        Security.addProvider(pkcs11Provider);
        provider = pkcs11Provider;

        keyStore = KeyStore.getInstance(KEY_STORE, pkcs11Provider);
        keyStore.load(null, pin.toCharArray());
    }

    private void initConfig(Map<String, String> pkcs11Config) {
        try {
            String library = pkcs11Config.get("library");
            String name = pkcs11Config.get("name");
            String slot = pkcs11Config.get("slot");

            //Create config file
            FileWriter fw = new FileWriter(CONFIG_FILE_NAME);
            fw.write("name = " + name + "\n");
            fw.write("library = " + library + "\n");
            fw.write("slot = " + slot + "\n");
            fw.write("attributes(generate, *, *) = {\n");
            fw.write("\t CKA_TOKEN = true\n}\n" + "attributes(generate, CKO_CERTIFICATE, *) = {\n");
            fw.write("\t CKA_PRIVATE = false\n}\n" + "attributes(generate, CKO_PUBLIC_KEY, *) = {\n");
            fw.write("\t CKA_PRIVATE = false\n}\n");
            fw.close();
        } catch (IOException e) {
            log.debug(e.getMessage());
            e.printStackTrace();
        }
    }

    public String generateKey(String dnName, SignatureAlgorithm signatureAlgorithm, Long expirationTime)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, CertificateException,
            KeyStoreException, IOException, OperatorCreationException {
        KeyPairGenerator keyGen;

        if (signatureAlgorithm == null) {
            throw new RuntimeException("The signature algorithm parameter cannot be null");
        } else if (SignatureAlgorithmFamily.RSA.equals(signatureAlgorithm.getFamily())) {
            keyGen = KeyPairGenerator.getInstance(signatureAlgorithm.getFamily());
            keyGen.initialize(2048, new SecureRandom());
        } else if (SignatureAlgorithmFamily.EC.equals(signatureAlgorithm.getFamily())) {
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(signatureAlgorithm.getCurve().getAlias());
            keyGen = KeyPairGenerator.getInstance(signatureAlgorithm.getFamily());
            keyGen.initialize(ecGenParameterSpec, new SecureRandom());
        } else {
            throw new RuntimeException("The provided signature algorithm parameter is not supported");
        }

        // Generate the key
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey pk = keyPair.getPrivate();

        // Java API requires a certificate chain
        Certificate[] chain = generateV3Certificate(keyPair, dnName, signatureAlgorithm, expirationTime);

        String alias = UUID.randomUUID().toString();

        keyStore.setKeyEntry(alias, pk, pin, chain);
        keyStore.store(null);

        return alias;
    }

    public String getSignature(byte[] signingInput, String alias, String sharedSecret, SignatureAlgorithm signatureAlgorithm) throws UnrecoverableEntryException,
            NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, SignatureException {
        if (signatureAlgorithm == SignatureAlgorithm.NONE) {
            return null;
        } else if (SignatureAlgorithmFamily.HMAC.equals(signatureAlgorithm.getFamily())) {
            SecretKey secretKey = new SecretKeySpec(
                    sharedSecret.getBytes(StandardCharsets.UTF_8),
                    signatureAlgorithm.getAlgorithm());
            Mac mac = Mac.getInstance(signatureAlgorithm.getAlgorithm());
            mac.init(secretKey);
            byte[] sig = mac.doFinal(signingInput);
            return Base64Util.base64UrlEncode(sig);
        } else { // EC or RSA
            PrivateKey privateKey = getPrivateKey(alias);

            Signature signature = Signature.getInstance(signatureAlgorithm.getAlgorithm(), provider);
            signature.initSign(privateKey);
            signature.update(signingInput);

            return Base64Util.base64UrlEncode(signature.sign());
        }
    }

    public boolean verifySignature(String signingInput, String encodedSignature, String alias, String sharedSecret,
                                   JwksRequestParam jwksRequestParam, SignatureAlgorithm signatureAlgorithm)
            throws InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, SignatureException,
            UnrecoverableEntryException {
        boolean verified;

        if (signatureAlgorithm == SignatureAlgorithm.NONE) {
            return Strings.isNullOrEmpty(encodedSignature);
        } else if (SignatureAlgorithmFamily.HMAC.equals(signatureAlgorithm.getFamily())) {
            String expectedSignature = getSignature(signingInput.getBytes(), null, sharedSecret, signatureAlgorithm);
            return expectedSignature.equals(encodedSignature);
        } else { // EC or RSA
            PublicKey publicKey;

            try {
                if (jwksRequestParam == null) {
                    publicKey = getPublicKey(alias);
                } else {
                    publicKey = getPublicKey(alias, jwksRequestParam);
                }
                if (publicKey == null) {
                    return false;
                }

                byte[] signature = Base64Util.base64UrlDecode(encodedSignature);

                Signature verifier = Signature.getInstance(signatureAlgorithm.getAlgorithm());
                verifier.initVerify(publicKey);
                verifier.update(signingInput.getBytes());
                verified = verifier.verify(signature);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                verified = false;
            }
        }

        return verified;
    }

    public void deleteKey(String alias) throws KeyStoreException {
        keyStore.deleteEntry(alias);
    }

    public PublicKey getPublicKey(String alias, JwksRequestParam jwksRequestParam) throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeySpecException {
        PublicKey publicKey = null;

        for (KeyRequestParam key : jwksRequestParam.getKeyRequestParams()) {
            if (alias.equals(key.getKid())) {
                SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.fromName(key.getAlg());
                if (signatureAlgorithm != null) {
                    if (signatureAlgorithm.getFamily().equals(SignatureAlgorithmFamily.RSA)) {
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
                                new BigInteger(1, Base64Util.base64UrlDecode(key.getN())),
                                new BigInteger(1, Base64Util.base64UrlDecode(key.getE())));
                        publicKey = keyFactory.generatePublic(pubKeySpec);
                    } else if (signatureAlgorithm.getFamily().equals(SignatureAlgorithmFamily.EC)) {
                        AlgorithmParameters parameters = AlgorithmParameters.getInstance(SignatureAlgorithmFamily.EC);
                        parameters.init(new ECGenParameterSpec(signatureAlgorithm.getCurve().getAlias()));
                        ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);

                        publicKey = KeyFactory.getInstance(SignatureAlgorithmFamily.EC).generatePublic(new ECPublicKeySpec(
                                new ECPoint(
                                        new BigInteger(1, Base64Util.base64UrlDecode(key.getX())),
                                        new BigInteger(1, Base64Util.base64UrlDecode(key.getY()))
                                ), ecParameters));
                    }
                }
            }
        }

        return publicKey;
    }

    public PublicKey getPublicKey(String alias) {
        PublicKey publicKey = null;

        try {
            if (Strings.isNullOrEmpty(alias)) {
                return null;
            }

            Certificate certificate = getCertificate(alias);
            if (certificate == null) {
                return null;
            }
            publicKey = certificate.getPublicKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return publicKey;
    }

    public Certificate getCertificate(String alias) throws KeyStoreException {
        return keyStore.getCertificate(alias);
    }

    private PrivateKey getPrivateKey(String alias)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        if (Strings.isNullOrEmpty(alias)) {
            return null;
        }

        Key key = keyStore.getKey(alias, pin);
        if (key == null) {
            return null;
        }

        if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        }

        return null;
    }

    private Certificate[] generateV3Certificate(KeyPair pair, String dnName, SignatureAlgorithm signatureAlgorithm,
                                                Long expirationTime)
            throws OperatorCreationException, CertificateException {
        X500Name owner = new X500Name(dnName);
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 10000);
        Date notAfter = new Date(expirationTime);

        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                owner,
                serialNumber,
                notBefore,
                notAfter,
                owner,
                SubjectPublicKeyInfo.getInstance(pair.getPublic().getEncoded())
        );

        ContentSigner signer = new JcaContentSignerBuilder(signatureAlgorithm.getAlgorithm())
                .setProvider(PROVIDER).build(pair.getPrivate());
        final X509CertificateHolder holder = builder.build(signer);

        Certificate cert = new JcaX509CertificateConverter().getCertificate(holder);

        return new Certificate[]{cert};
    }
}
