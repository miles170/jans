package io.jans.ca.server.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import io.jans.as.model.configuration.Configuration;
import io.jans.ca.common.proxy.ProxyConfiguration;
import io.jans.ca.server.configuration.model.Rp;
import io.jans.configapi.model.configuration.CorsConfigurationFilter;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiAppConfiguration implements Configuration {

    private boolean configOauthEnabled;
    private List<String> apiApprovedIssuer;
    private String apiProtectionType;
    private String apiClientId;
    private String apiClientPassword;
    private boolean endpointInjectionEnabled;
    private String authIssuerUrl;
    private String authOpenidConfigurationUrl;
    private String authOpenidIntrospectionUrl;
    private String authOpenidTokenUrl;
    private String authOpenidRevokeUrl;
    private String smallryeHealthRootPath;
    private List<String> exclusiveAuthScopes;
    private List<CorsConfigurationFilter> corsConfigurationFilters;
    private String loggingLevel;
    private String loggingLayout;
    private String externalLoggerConfiguration;
    private Boolean disableJdkLogger = true;
    private int maxCount;

    //Jans Client Api properties
    private String registerClientAppType = "web";
    private String registerClientResponesType = "code";
    private Boolean useClientAuthenticationForPat = true;
    private Boolean trustAllCerts;
    private String keyStorePath;
    private String keyStorePassword;
    private Boolean enableJwksGeneration = true;
    private String cryptProviderKeyStorePath;
    private String cryptProviderKeyStorePassword;
    private String cryptProviderDnName;
    private int jwksExpirationInHours = 720;
    private int jwksRegenerationIntervalInHours = 720;
    private Boolean supportGoogleLogout = true;
    private int stateExpirationInMinutes = 5;
    private int nonceExpirationInMinutes = 5;
    private int requestObjectExpirationInMinutes = 5;
    private int dbCleanupIntervalInHours = 1;
    private int rpCacheExpirationInMinutes = 60;
    private int publicOpKeyCacheExpirationInMinutes = 60;
    private Boolean protectCommandsWithAccessToken;
    private Boolean acceptIdTokenWithoutSignature = false;
    private Boolean idTokenValidationCHashRequired = true;
    private Boolean idTokenValidationAtHashRequired = true;
    private Boolean idTokenValidationSHashRequired = false;
    private Boolean validateUserInfoWithIdToken = false;
    private Boolean uma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient;
    private Boolean addClientCredentialsGrantTypeAutomaticallyDuringClientRegistration = true;
    private String migrationSourceFolderPath;
    private List<String> allowedOpHosts = Lists.newArrayList();
    private String storage;
    private JsonNode storageConfiguration;
    private Rp defaultSiteConfig;
    private String tracer;
    private String tracerHost;
    private int tracerPort = 5775;
    private Boolean enableTracing = false;
    private ProxyConfiguration proxyConfiguration;
    private List<String> protectCommandsWithRpId;
    private int persistenceManagerRemoveCount = 1000;
    private List<String> bindIpAddresses;
    private List<String> tlsVersion;
    private List<String> tlsSecureCipher;
    private Boolean mtlsEnabled = false;
    private String mtlsClientKeyStorePath;
    private String mtlsClientKeyStorePassword;
    private Boolean encodeStateFromRequestParameter = false;
    private Boolean encodeNonceFromRequestParameter = false;
    private Boolean fapiEnabled = false;
    private int iatExpirationInHours = 1;
    private Boolean encodeClientIdInAuthorizationUrl = false;


    public boolean isConfigOauthEnabled() {
        return configOauthEnabled;
    }

    public void setConfigOauthEnabled(boolean configOauthEnabled) {
        this.configOauthEnabled = configOauthEnabled;
    }

    public List<String> getApiApprovedIssuer() {
        return apiApprovedIssuer;
    }

    public void setApiApprovedIssuer(List<String> apiApprovedIssuer) {
        this.apiApprovedIssuer = apiApprovedIssuer;
    }

    public String getApiProtectionType() {
        return apiProtectionType;
    }

    public void setApiProtectionType(String apiProtectionType) {
        this.apiProtectionType = apiProtectionType;
    }

    public String getApiClientId() {
        return apiClientId;
    }

    public void setApiClientId(String apiClientId) {
        this.apiClientId = apiClientId;
    }

    public String getApiClientPassword() {
        return apiClientPassword;
    }

    public void setApiClientPassword(String apiClientPassword) {
        this.apiClientPassword = apiClientPassword;
    }

    public boolean isEndpointInjectionEnabled() {
        return endpointInjectionEnabled;
    }

    public void setEndpointInjectionEnabled(boolean endpointInjectionEnabled) {
        this.endpointInjectionEnabled = endpointInjectionEnabled;
    }

    public String getAuthIssuerUrl() {
        return authIssuerUrl;
    }

    public void setAuthIssuerUrl(String authIssuerUrl) {
        this.authIssuerUrl = authIssuerUrl;
    }

    public String getAuthOpenidConfigurationUrl() {
        return authOpenidConfigurationUrl;
    }

    public void setAuthOpenidConfigurationUrl(String authOpenidConfigurationUrl) {
        this.authOpenidConfigurationUrl = authOpenidConfigurationUrl;
    }

    public String getAuthOpenidIntrospectionUrl() {
        return authOpenidIntrospectionUrl;
    }

    public void setAuthOpenidIntrospectionUrl(String authOpenidIntrospectionUrl) {
        this.authOpenidIntrospectionUrl = authOpenidIntrospectionUrl;
    }

    public String getAuthOpenidTokenUrl() {
        return authOpenidTokenUrl;
    }

    public void setAuthOpenidTokenUrl(String authOpenidTokenUrl) {
        this.authOpenidTokenUrl = authOpenidTokenUrl;
    }

    public String getAuthOpenidRevokeUrl() {
        return authOpenidRevokeUrl;
    }

    public void setAuthOpenidRevokeUrl(String authOpenidRevokeUrl) {
        this.authOpenidRevokeUrl = authOpenidRevokeUrl;
    }

    public String getSmallryeHealthRootPath() {
        return smallryeHealthRootPath;
    }

    public void setSmallryeHealthRootPath(String smallryeHealthRootPath) {
        this.smallryeHealthRootPath = smallryeHealthRootPath;
    }

    public List<CorsConfigurationFilter> getCorsConfigurationFilters() {
        if (corsConfigurationFilters == null) {
            corsConfigurationFilters = new ArrayList<>();
        }

        return corsConfigurationFilters;
    }

    public void setCorsConfigurationFilters(List<CorsConfigurationFilter> corsConfigurationFilters) {
        if (corsConfigurationFilters == null) {
            this.corsConfigurationFilters = new ArrayList<>();
        } else {
            this.corsConfigurationFilters = new ArrayList<>();
            this.corsConfigurationFilters.addAll(corsConfigurationFilters);
        }
    }

    public List<String> getExclusiveAuthScopes() {
        if (exclusiveAuthScopes == null) {
            exclusiveAuthScopes = new ArrayList<>();
        }
        return exclusiveAuthScopes;
    }

    public void setExclusiveAuthScopes(List<String> exclusiveAuthScopes) {
        this.exclusiveAuthScopes = exclusiveAuthScopes;
        if (exclusiveAuthScopes == null) {
            this.exclusiveAuthScopes = new ArrayList<>();
        } else {
            this.exclusiveAuthScopes = new ArrayList<>();
            this.exclusiveAuthScopes.addAll(exclusiveAuthScopes);
        }
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public String getLoggingLayout() {
        return loggingLayout;
    }

    public void setLoggingLayout(String loggingLayout) {
        this.loggingLayout = loggingLayout;
    }

    public String getExternalLoggerConfiguration() {
        return externalLoggerConfiguration;
    }

    public void setExternalLoggerConfiguration(String externalLoggerConfiguration) {
        this.externalLoggerConfiguration = externalLoggerConfiguration;
    }

    public Boolean getDisableJdkLogger() {
        return disableJdkLogger;
    }

    public void setDisableJdkLogger(Boolean disableJdkLogger) {
        this.disableJdkLogger = disableJdkLogger;
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }


    public String getRegisterClientAppType() {
        return registerClientAppType;
    }

    public void setRegisterClientAppType(String registerClientAppType) {
        this.registerClientAppType = registerClientAppType;
    }

    public String getRegisterClientResponesType() {
        return registerClientResponesType;
    }

    public void setRegisterClientResponesType(String registerClientResponesType) {
        this.registerClientResponesType = registerClientResponesType;
    }

    public Boolean getUseClientAuthenticationForPat() {
        return useClientAuthenticationForPat;
    }

    public void setUseClientAuthenticationForPat(Boolean useClientAuthenticationForPat) {
        this.useClientAuthenticationForPat = useClientAuthenticationForPat;
    }

    public Boolean getTrustAllCerts() {
        return trustAllCerts;
    }

    public void setTrustAllCerts(Boolean trustAllCerts) {
        this.trustAllCerts = trustAllCerts;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public Boolean getEnableJwksGeneration() {
        return enableJwksGeneration;
    }

    public void setEnableJwksGeneration(Boolean enableJwksGeneration) {
        this.enableJwksGeneration = enableJwksGeneration;
    }

    public String getCryptProviderKeyStorePath() {
        return cryptProviderKeyStorePath;
    }

    public void setCryptProviderKeyStorePath(String cryptProviderKeyStorePath) {
        this.cryptProviderKeyStorePath = cryptProviderKeyStorePath;
    }

    public String getCryptProviderKeyStorePassword() {
        return cryptProviderKeyStorePassword;
    }

    public void setCryptProviderKeyStorePassword(String cryptProviderKeyStorePassword) {
        this.cryptProviderKeyStorePassword = cryptProviderKeyStorePassword;
    }

    public String getCryptProviderDnName() {
        return cryptProviderDnName;
    }

    public void setCryptProviderDnName(String cryptProviderDnName) {
        this.cryptProviderDnName = cryptProviderDnName;
    }

    public int getJwksExpirationInHours() {
        return jwksExpirationInHours;
    }

    public void setJwksExpirationInHours(int jwksExpirationInHours) {
        this.jwksExpirationInHours = jwksExpirationInHours;
    }

    public int getJwksRegenerationIntervalInHours() {
        return jwksRegenerationIntervalInHours;
    }

    public void setJwksRegenerationIntervalInHours(int jwksRegenerationIntervalInHours) {
        this.jwksRegenerationIntervalInHours = jwksRegenerationIntervalInHours;
    }

    public Boolean getSupportGoogleLogout() {
        return supportGoogleLogout;
    }

    public void setSupportGoogleLogout(Boolean supportGoogleLogout) {
        this.supportGoogleLogout = supportGoogleLogout;
    }

    public int getStateExpirationInMinutes() {
        return stateExpirationInMinutes;
    }

    public void setStateExpirationInMinutes(int stateExpirationInMinutes) {
        this.stateExpirationInMinutes = stateExpirationInMinutes;
    }

    public int getNonceExpirationInMinutes() {
        return nonceExpirationInMinutes;
    }

    public void setNonceExpirationInMinutes(int nonceExpirationInMinutes) {
        this.nonceExpirationInMinutes = nonceExpirationInMinutes;
    }

    public int getRequestObjectExpirationInMinutes() {
        return requestObjectExpirationInMinutes;
    }

    public void setRequestObjectExpirationInMinutes(int requestObjectExpirationInMinutes) {
        this.requestObjectExpirationInMinutes = requestObjectExpirationInMinutes;
    }

    public int getDbCleanupIntervalInHours() {
        return dbCleanupIntervalInHours;
    }

    public void setDbCleanupIntervalInHours(int dbCleanupIntervalInHours) {
        this.dbCleanupIntervalInHours = dbCleanupIntervalInHours;
    }

    public int getRpCacheExpirationInMinutes() {
        return rpCacheExpirationInMinutes;
    }

    public void setRpCacheExpirationInMinutes(int rpCacheExpirationInMinutes) {
        this.rpCacheExpirationInMinutes = rpCacheExpirationInMinutes;
    }

    public int getPublicOpKeyCacheExpirationInMinutes() {
        return publicOpKeyCacheExpirationInMinutes;
    }

    public void setPublicOpKeyCacheExpirationInMinutes(int publicOpKeyCacheExpirationInMinutes) {
        this.publicOpKeyCacheExpirationInMinutes = publicOpKeyCacheExpirationInMinutes;
    }

    public Boolean getProtectCommandsWithAccessToken() {
        return protectCommandsWithAccessToken;
    }

    public void setProtectCommandsWithAccessToken(Boolean protectCommandsWithAccessToken) {
        this.protectCommandsWithAccessToken = protectCommandsWithAccessToken;
    }

    public Boolean getAcceptIdTokenWithoutSignature() {
        return acceptIdTokenWithoutSignature;
    }

    public void setAcceptIdTokenWithoutSignature(Boolean acceptIdTokenWithoutSignature) {
        this.acceptIdTokenWithoutSignature = acceptIdTokenWithoutSignature;
    }

    public Boolean getIdTokenValidationCHashRequired() {
        return idTokenValidationCHashRequired;
    }

    public void setIdTokenValidationCHashRequired(Boolean idTokenValidationCHashRequired) {
        this.idTokenValidationCHashRequired = idTokenValidationCHashRequired;
    }

    public Boolean getIdTokenValidationAtHashRequired() {
        return idTokenValidationAtHashRequired;
    }

    public void setIdTokenValidationAtHashRequired(Boolean idTokenValidationAtHashRequired) {
        this.idTokenValidationAtHashRequired = idTokenValidationAtHashRequired;
    }

    public Boolean getIdTokenValidationSHashRequired() {
        return idTokenValidationSHashRequired;
    }

    public void setIdTokenValidationSHashRequired(Boolean idTokenValidationSHashRequired) {
        this.idTokenValidationSHashRequired = idTokenValidationSHashRequired;
    }

    public Boolean getValidateUserInfoWithIdToken() {
        return validateUserInfoWithIdToken;
    }

    public void setValidateUserInfoWithIdToken(Boolean validateUserInfoWithIdToken) {
        this.validateUserInfoWithIdToken = validateUserInfoWithIdToken;
    }

    public Boolean getUma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient() {
        return uma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient;
    }

    public void setUma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient(Boolean uma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient) {
        this.uma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient = uma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient;
    }

    public Boolean getAddClientCredentialsGrantTypeAutomaticallyDuringClientRegistration() {
        return addClientCredentialsGrantTypeAutomaticallyDuringClientRegistration;
    }

    public void setAddClientCredentialsGrantTypeAutomaticallyDuringClientRegistration(Boolean addClientCredentialsGrantTypeAutomaticallyDuringClientRegistration) {
        this.addClientCredentialsGrantTypeAutomaticallyDuringClientRegistration = addClientCredentialsGrantTypeAutomaticallyDuringClientRegistration;
    }

    public String getMigrationSourceFolderPath() {
        return migrationSourceFolderPath;
    }

    public void setMigrationSourceFolderPath(String migrationSourceFolderPath) {
        this.migrationSourceFolderPath = migrationSourceFolderPath;
    }

    public List<String> getAllowedOpHosts() {
        return allowedOpHosts;
    }

    public void setAllowedOpHosts(List<String> allowedOpHosts) {
        this.allowedOpHosts = allowedOpHosts;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public JsonNode getStorageConfiguration() {
        return storageConfiguration;
    }

    public void setStorageConfiguration(JsonNode storageConfiguration) {
        this.storageConfiguration = storageConfiguration;
    }

    public Rp getDefaultSiteConfig() {
        return defaultSiteConfig;
    }

    public void setDefaultSiteConfig(Rp defaultSiteConfig) {
        this.defaultSiteConfig = defaultSiteConfig;
    }

    public String getTracer() {
        return tracer;
    }

    public void setTracer(String tracer) {
        this.tracer = tracer;
    }

    public String getTracerHost() {
        return tracerHost;
    }

    public void setTracerHost(String tracerHost) {
        this.tracerHost = tracerHost;
    }

    public int getTracerPort() {
        return tracerPort;
    }

    public void setTracerPort(int tracerPort) {
        this.tracerPort = tracerPort;
    }

    public Boolean getEnableTracing() {
        return enableTracing;
    }

    public void setEnableTracing(Boolean enableTracing) {
        this.enableTracing = enableTracing;
    }

    public ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }

    public void setProxyConfiguration(ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;
    }

    public List<String> getProtectCommandsWithRpId() {
        return protectCommandsWithRpId;
    }

    public void setProtectCommandsWithRpId(List<String> protectCommandsWithRpId) {
        this.protectCommandsWithRpId = protectCommandsWithRpId;
    }

    public int getPersistenceManagerRemoveCount() {
        return persistenceManagerRemoveCount;
    }

    public void setPersistenceManagerRemoveCount(int persistenceManagerRemoveCount) {
        this.persistenceManagerRemoveCount = persistenceManagerRemoveCount;
    }

    public List<String> getBindIpAddresses() {
        return bindIpAddresses;
    }

    public void setBindIpAddresses(List<String> bindIpAddresses) {
        this.bindIpAddresses = bindIpAddresses;
    }

    public List<String> getTlsVersion() {
        return tlsVersion;
    }

    public void setTlsVersion(List<String> tlsVersion) {
        this.tlsVersion = tlsVersion;
    }

    public List<String> getTlsSecureCipher() {
        return tlsSecureCipher;
    }

    public void setTlsSecureCipher(List<String> tlsSecureCipher) {
        this.tlsSecureCipher = tlsSecureCipher;
    }

    public Boolean getMtlsEnabled() {
        return mtlsEnabled;
    }

    public void setMtlsEnabled(Boolean mtlsEnabled) {
        this.mtlsEnabled = mtlsEnabled;
    }

    public String getMtlsClientKeyStorePath() {
        return mtlsClientKeyStorePath;
    }

    public void setMtlsClientKeyStorePath(String mtlsClientKeyStorePath) {
        this.mtlsClientKeyStorePath = mtlsClientKeyStorePath;
    }

    public String getMtlsClientKeyStorePassword() {
        return mtlsClientKeyStorePassword;
    }

    public void setMtlsClientKeyStorePassword(String mtlsClientKeyStorePassword) {
        this.mtlsClientKeyStorePassword = mtlsClientKeyStorePassword;
    }

    public Boolean getEncodeStateFromRequestParameter() {
        return encodeStateFromRequestParameter;
    }

    public void setEncodeStateFromRequestParameter(Boolean encodeStateFromRequestParameter) {
        this.encodeStateFromRequestParameter = encodeStateFromRequestParameter;
    }

    public Boolean getEncodeNonceFromRequestParameter() {
        return encodeNonceFromRequestParameter;
    }

    public void setEncodeNonceFromRequestParameter(Boolean encodeNonceFromRequestParameter) {
        this.encodeNonceFromRequestParameter = encodeNonceFromRequestParameter;
    }

    public Boolean getFapiEnabled() {
        return fapiEnabled;
    }

    public void setFapiEnabled(Boolean fapiEnabled) {
        this.fapiEnabled = fapiEnabled;
    }

    public int getIatExpirationInHours() {
        return iatExpirationInHours;
    }

    public void setIatExpirationInHours(int iatExpirationInHours) {
        this.iatExpirationInHours = iatExpirationInHours;
    }

    public Boolean getEncodeClientIdInAuthorizationUrl() {
        return encodeClientIdInAuthorizationUrl;
    }

    public void setEncodeClientIdInAuthorizationUrl(Boolean encodeClientIdInAuthorizationUrl) {
        this.encodeClientIdInAuthorizationUrl = encodeClientIdInAuthorizationUrl;
    }

    @Override
    public String toString() {
        return "ApiAppConfiguration{" +
                "configOauthEnabled=" + configOauthEnabled +
                ", apiApprovedIssuer=" + apiApprovedIssuer +
                ", apiProtectionType='" + apiProtectionType + '\'' +
                ", apiClientId='" + apiClientId + '\'' +
                ", apiClientPassword='" + apiClientPassword + '\'' +
                ", endpointInjectionEnabled=" + endpointInjectionEnabled +
                ", authIssuerUrl='" + authIssuerUrl + '\'' +
                ", authOpenidConfigurationUrl='" + authOpenidConfigurationUrl + '\'' +
                ", authOpenidIntrospectionUrl='" + authOpenidIntrospectionUrl + '\'' +
                ", authOpenidTokenUrl='" + authOpenidTokenUrl + '\'' +
                ", authOpenidRevokeUrl='" + authOpenidRevokeUrl + '\'' +
                ", smallryeHealthRootPath='" + smallryeHealthRootPath + '\'' +
                ", exclusiveAuthScopes=" + exclusiveAuthScopes +
                ", corsConfigurationFilters=" + corsConfigurationFilters +
                ", loggingLevel='" + loggingLevel + '\'' +
                ", loggingLayout='" + loggingLayout + '\'' +
                ", externalLoggerConfiguration='" + externalLoggerConfiguration + '\'' +
                ", disableJdkLogger=" + disableJdkLogger +
                ", maxCount=" + maxCount +
                ", registerClientAppType='" + registerClientAppType + '\'' +
                ", registerClientResponesType='" + registerClientResponesType + '\'' +
                ", useClientAuthenticationForPat=" + useClientAuthenticationForPat +
                ", trustAllCerts=" + trustAllCerts +
                ", keyStorePath='" + keyStorePath + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", enableJwksGeneration=" + enableJwksGeneration +
                ", cryptProviderKeyStorePath='" + cryptProviderKeyStorePath + '\'' +
                ", cryptProviderKeyStorePassword='" + cryptProviderKeyStorePassword + '\'' +
                ", cryptProviderDnName='" + cryptProviderDnName + '\'' +
                ", jwksExpirationInHours=" + jwksExpirationInHours +
                ", jwksRegenerationIntervalInHours=" + jwksRegenerationIntervalInHours +
                ", supportGoogleLogout=" + supportGoogleLogout +
                ", stateExpirationInMinutes=" + stateExpirationInMinutes +
                ", nonceExpirationInMinutes=" + nonceExpirationInMinutes +
                ", requestObjectExpirationInMinutes=" + requestObjectExpirationInMinutes +
                ", dbCleanupIntervalInHours=" + dbCleanupIntervalInHours +
                ", rpCacheExpirationInMinutes=" + rpCacheExpirationInMinutes +
                ", publicOpKeyCacheExpirationInMinutes=" + publicOpKeyCacheExpirationInMinutes +
                ", protectCommandsWithAccessToken=" + protectCommandsWithAccessToken +
                ", acceptIdTokenWithoutSignature=" + acceptIdTokenWithoutSignature +
                ", idTokenValidationCHashRequired=" + idTokenValidationCHashRequired +
                ", idTokenValidationAtHashRequired=" + idTokenValidationAtHashRequired +
                ", idTokenValidationSHashRequired=" + idTokenValidationSHashRequired +
                ", validateUserInfoWithIdToken=" + validateUserInfoWithIdToken +
                ", uma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient=" + uma2AuthRegisterClaimsGatheringEndpointAsRedirectUriOfClient +
                ", addClientCredentialsGrantTypeAutomaticallyDuringClientRegistration=" + addClientCredentialsGrantTypeAutomaticallyDuringClientRegistration +
                ", migrationSourceFolderPath='" + migrationSourceFolderPath + '\'' +
                ", allowedOpHosts=" + allowedOpHosts +
                ", storage='" + storage + '\'' +
                ", storageConfiguration=" + storageConfiguration +
                ", defaultSiteConfig=" + defaultSiteConfig +
                ", tracer='" + tracer + '\'' +
                ", tracerHost='" + tracerHost + '\'' +
                ", tracerPort=" + tracerPort +
                ", enableTracing=" + enableTracing +
                ", proxyConfiguration=" + proxyConfiguration +
                ", protectCommandsWithRpId=" + protectCommandsWithRpId +
                ", persistenceManagerRemoveCount=" + persistenceManagerRemoveCount +
                ", bindIpAddresses=" + bindIpAddresses +
                ", tlsVersion=" + tlsVersion +
                ", tlsSecureCipher=" + tlsSecureCipher +
                ", mtlsEnabled=" + mtlsEnabled +
                ", mtlsClientKeyStorePath='" + mtlsClientKeyStorePath + '\'' +
                ", mtlsClientKeyStorePassword='" + mtlsClientKeyStorePassword + '\'' +
                ", encodeStateFromRequestParameter=" + encodeStateFromRequestParameter +
                ", encodeNonceFromRequestParameter=" + encodeNonceFromRequestParameter +
                ", fapiEnabled=" + fapiEnabled +
                ", iatExpirationInHours=" + iatExpirationInHours +
                ", encodeClientIdInAuthorizationUrl=" + encodeClientIdInAuthorizationUrl +
                '}';
    }
}
