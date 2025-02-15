# swagger-java-client

oxd-server
- API version: 4.2

oxd-server


*Automatically generated by the [Swagger Codegen](https://github.com/swagger-api/swagger-codegen)*


## Requirements

Building the API client library requires:
1. Java 1.7+
2. Maven/Gradle

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn clean install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn clean deploy
```

Refer to the [OSSRH Guide](http://central.sonatype.org/pages/ossrh-guide.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>io.swagger</groupId>
  <artifactId>swagger-java-client</artifactId>
  <version>1.0.1</version>
  <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

```shell
mvn clean package
```

Then manually install the following JARs:

* `target/swagger-java-client-1.0.0.jar`
* `target/lib/*.jar`

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        GetAccessTokenByRefreshTokenParams body = new GetAccessTokenByRefreshTokenParams(); // GetAccessTokenByRefreshTokenParams | 
        String authorization = "authorization_example"; // String | 
        try {
            GetAccessTokenByRefreshTokenResponse result = apiInstance.getAccessTokenByRefreshToken(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#getAccessTokenByRefreshToken");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        GetAuthorizationUrlParams body = new GetAuthorizationUrlParams(); // GetAuthorizationUrlParams | 
        String authorization = "authorization_example"; // String | 
        try {
            GetAuthorizationUrlResponse result = apiInstance.getAuthorizationUrl(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#getAuthorizationUrl");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        GetClientTokenParams body = new GetClientTokenParams(); // GetClientTokenParams | 
        try {
            GetClientTokenResponse result = apiInstance.getClientToken(body);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#getClientToken");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        GetDiscoveryParams body = new GetDiscoveryParams(); // GetDiscoveryParams | 
        try {
            GetDiscoveryResponse result = apiInstance.getDiscovery(body);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#getDiscovery");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        GetJwksParams body = new GetJwksParams(); // GetJwksParams | 
        String authorization = "authorization_example"; // String | 
        try {
            GetJwksResponse result = apiInstance.getJsonWebKeySet(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#getJsonWebKeySet");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        GetLogoutUriParams body = new GetLogoutUriParams(); // GetLogoutUriParams | 
        String authorization = "authorization_example"; // String | 
        try {
            GetLogoutUriResponse result = apiInstance.getLogoutUri(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#getLogoutUri");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        GetTokensByCodeParams body = new GetTokensByCodeParams(); // GetTokensByCodeParams | 
        String authorization = "authorization_example"; // String | 
        try {
            GetTokensByCodeResponse result = apiInstance.getTokensByCode(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#getTokensByCode");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        GetUserInfoParams body = new GetUserInfoParams(); // GetUserInfoParams | 
        String authorization = "authorization_example"; // String | 
        try {
            Map<String, Object> result = apiInstance.getUserInfo(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#getUserInfo");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        try {
            apiInstance.healthCheck();
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#healthCheck");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        IntrospectAccessTokenParams body = new IntrospectAccessTokenParams(); // IntrospectAccessTokenParams | 
        String authorization = "authorization_example"; // String | 
        try {
            IntrospectAccessTokenResponse result = apiInstance.introspectAccessToken(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#introspectAccessToken");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        IntrospectRptParams body = new IntrospectRptParams(); // IntrospectRptParams | 
        String authorization = "authorization_example"; // String | 
        try {
            IntrospectRptResponse result = apiInstance.introspectRpt(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#introspectRpt");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        RegisterSiteParams body = new RegisterSiteParams(); // RegisterSiteParams | 
        try {
            RegisterSiteResponse result = apiInstance.registerSite(body);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#registerSite");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        RemoveSiteParams body = new RemoveSiteParams(); // RemoveSiteParams | 
        String authorization = "authorization_example"; // String | 
        try {
            RemoveSiteResponse result = apiInstance.removeSite(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#removeSite");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        UmaRpGetClaimsGatheringUrlParams body = new UmaRpGetClaimsGatheringUrlParams(); // UmaRpGetClaimsGatheringUrlParams | 
        String authorization = "authorization_example"; // String | 
        try {
            UmaRpGetClaimsGatheringUrlResponse result = apiInstance.umaRpGetClaimsGatheringUrl(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#umaRpGetClaimsGatheringUrl");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        UmaRpGetRptParams body = new UmaRpGetRptParams(); // UmaRpGetRptParams | 
        String authorization = "authorization_example"; // String | 
        try {
            UmaRpGetRptResponse result = apiInstance.umaRpGetRpt(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#umaRpGetRpt");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        UmaRsCheckAccessParams body = new UmaRsCheckAccessParams(); // UmaRsCheckAccessParams | 
        String authorization = "authorization_example"; // String | 
        try {
            UmaRsCheckAccessResponse result = apiInstance.umaRsCheckAccess(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#umaRsCheckAccess");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        UmaRsModifyParams body = new UmaRsModifyParams(); // UmaRsModifyParams | 
        String authorization = "authorization_example"; // String | 
        try {
            UmaRsModifyResponse result = apiInstance.umaRsModify(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#umaRsModify");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        UmaRsProtectParams body = new UmaRsProtectParams(); // UmaRsProtectParams | 
        String authorization = "authorization_example"; // String | 
        try {
            UmaRsProtectResponse result = apiInstance.umaRsProtect(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#umaRsProtect");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DevelopersApi;

import java.io.File;
import java.util.*;

public class DevelopersApiExample {

    public static void main(String[] args) {
        
        DevelopersApi apiInstance = new DevelopersApi();
        UpdateSiteParams body = new UpdateSiteParams(); // UpdateSiteParams | 
        String authorization = "authorization_example"; // String | 
        try {
            UpdateSiteResponse result = apiInstance.updateSite(body, authorization);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DevelopersApi#updateSite");
            e.printStackTrace();
        }
    }
}
```

## Documentation for API Endpoints

All URIs are relative to *https://gluu.org/*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DevelopersApi* | [**getAccessTokenByRefreshToken**](docs/DevelopersApi.md#getAccessTokenByRefreshToken) | **POST** /get-access-token-by-refresh-token | Get Access Token By Refresh Token
*DevelopersApi* | [**getAuthorizationUrl**](docs/DevelopersApi.md#getAuthorizationUrl) | **POST** /get-authorization-url | Get Authorization Url
*DevelopersApi* | [**getClientToken**](docs/DevelopersApi.md#getClientToken) | **POST** /get-client-token | Get Client Token
*DevelopersApi* | [**getDiscovery**](docs/DevelopersApi.md#getDiscovery) | **POST** /get-discovery | Get OP Discovery Configuration
*DevelopersApi* | [**getJsonWebKeySet**](docs/DevelopersApi.md#getJsonWebKeySet) | **POST** /get-jwks | Get JSON Web Key Set
*DevelopersApi* | [**getLogoutUri**](docs/DevelopersApi.md#getLogoutUri) | **POST** /get-logout-uri | Get Logout URL
*DevelopersApi* | [**getTokensByCode**](docs/DevelopersApi.md#getTokensByCode) | **POST** /get-tokens-by-code | Get Tokens By Code
*DevelopersApi* | [**getUserInfo**](docs/DevelopersApi.md#getUserInfo) | **POST** /get-user-info | Get User Info
*DevelopersApi* | [**healthCheck**](docs/DevelopersApi.md#healthCheck) | **GET** /health-check | Health Check
*DevelopersApi* | [**introspectAccessToken**](docs/DevelopersApi.md#introspectAccessToken) | **POST** /introspect-access-token | Introspect Access Token
*DevelopersApi* | [**introspectRpt**](docs/DevelopersApi.md#introspectRpt) | **POST** /introspect-rpt | Introspect RPT
*DevelopersApi* | [**registerSite**](docs/DevelopersApi.md#registerSite) | **POST** /register-site | Register Site
*DevelopersApi* | [**removeSite**](docs/DevelopersApi.md#removeSite) | **POST** /remove-site | Remove Site
*DevelopersApi* | [**umaRpGetClaimsGatheringUrl**](docs/DevelopersApi.md#umaRpGetClaimsGatheringUrl) | **POST** /uma-rp-get-claims-gathering-url | UMA RP Get Claims Gathering URL
*DevelopersApi* | [**umaRpGetRpt**](docs/DevelopersApi.md#umaRpGetRpt) | **POST** /uma-rp-get-rpt | UMA RP Get RPT
*DevelopersApi* | [**umaRsCheckAccess**](docs/DevelopersApi.md#umaRsCheckAccess) | **POST** /uma-rs-check-access | UMA RS Check Access
*DevelopersApi* | [**umaRsModify**](docs/DevelopersApi.md#umaRsModify) | **POST** /uma-rs-modify | UMA RS Modify Resources
*DevelopersApi* | [**umaRsProtect**](docs/DevelopersApi.md#umaRsProtect) | **POST** /uma-rs-protect | UMA RS Protect Resources
*DevelopersApi* | [**updateSite**](docs/DevelopersApi.md#updateSite) | **POST** /update-site | Update Site

## Documentation for Models

 - [Condition](docs/Condition.md)
 - [ErrorResponse](docs/ErrorResponse.md)
 - [GetAccessTokenByRefreshTokenParams](docs/GetAccessTokenByRefreshTokenParams.md)
 - [GetAccessTokenByRefreshTokenResponse](docs/GetAccessTokenByRefreshTokenResponse.md)
 - [GetAuthorizationUrlParams](docs/GetAuthorizationUrlParams.md)
 - [GetAuthorizationUrlResponse](docs/GetAuthorizationUrlResponse.md)
 - [GetClientTokenParams](docs/GetClientTokenParams.md)
 - [GetClientTokenResponse](docs/GetClientTokenResponse.md)
 - [GetDiscoveryParams](docs/GetDiscoveryParams.md)
 - [GetDiscoveryResponse](docs/GetDiscoveryResponse.md)
 - [GetJwksParams](docs/GetJwksParams.md)
 - [GetJwksResponse](docs/GetJwksResponse.md)
 - [GetLogoutUriParams](docs/GetLogoutUriParams.md)
 - [GetLogoutUriResponse](docs/GetLogoutUriResponse.md)
 - [GetTokensByCodeParams](docs/GetTokensByCodeParams.md)
 - [GetTokensByCodeResponse](docs/GetTokensByCodeResponse.md)
 - [GetUserInfoParams](docs/GetUserInfoParams.md)
 - [IntrospectAccessTokenParams](docs/IntrospectAccessTokenParams.md)
 - [IntrospectAccessTokenResponse](docs/IntrospectAccessTokenResponse.md)
 - [IntrospectRptParams](docs/IntrospectRptParams.md)
 - [IntrospectRptResponse](docs/IntrospectRptResponse.md)
 - [JsonWebKey](docs/JsonWebKey.md)
 - [RegisterSiteParams](docs/RegisterSiteParams.md)
 - [RegisterSiteResponse](docs/RegisterSiteResponse.md)
 - [RemoveSiteParams](docs/RemoveSiteParams.md)
 - [RemoveSiteResponse](docs/RemoveSiteResponse.md)
 - [RsResource](docs/RsResource.md)
 - [UmaRpGetClaimsGatheringUrlParams](docs/UmaRpGetClaimsGatheringUrlParams.md)
 - [UmaRpGetClaimsGatheringUrlResponse](docs/UmaRpGetClaimsGatheringUrlResponse.md)
 - [UmaRpGetRptParams](docs/UmaRpGetRptParams.md)
 - [UmaRpGetRptResponse](docs/UmaRpGetRptResponse.md)
 - [UmaRsCheckAccessParams](docs/UmaRsCheckAccessParams.md)
 - [UmaRsCheckAccessResponse](docs/UmaRsCheckAccessResponse.md)
 - [UmaRsModifyParams](docs/UmaRsModifyParams.md)
 - [UmaRsModifyResponse](docs/UmaRsModifyResponse.md)
 - [UmaRsProtectParams](docs/UmaRsProtectParams.md)
 - [UmaRsProtectResponse](docs/UmaRsProtectResponse.md)
 - [UpdateSiteParams](docs/UpdateSiteParams.md)
 - [UpdateSiteResponse](docs/UpdateSiteResponse.md)

## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author

yuriyz@gluu.org
