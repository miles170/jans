package io.jans.ca.server.arquillian;

import com.fasterxml.jackson.databind.JsonNode;
import io.jans.as.model.uma.UmaConstants;
import io.jans.ca.client.ClientInterface;
import io.jans.ca.client.GetTokensByCodeResponse2;
import io.jans.ca.client.RsProtectParams2;
import io.jans.ca.common.Jackson2;
import io.jans.ca.common.introspection.CorrectRptIntrospectionResponse;
import io.jans.ca.common.params.*;
import io.jans.ca.common.response.*;
import io.jans.ca.server.TestUtils;
import io.jans.ca.server.tests.PathTestEndPoint;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ClientIterfaceImpl implements ClientInterface {

    protected String targeHostUrl = "";

    public static ClientIterfaceImpl getInstanceClient(String targeHostUrl) {
        ClientIterfaceImpl result = new ClientIterfaceImpl();
        result.targeHostUrl = targeHostUrl;
        return result;
    }

    private WebTarget webTarget(String pathEndPoint) {
        return ResteasyClientBuilder.newClient().target(targeHostUrl + pathEndPoint);
    }

    private Invocation.Builder requestBuilder(String pathEndPoint) {
        return webTarget(pathEndPoint).request();
    }

    private Entity<?> toPostParam(Object param) {
        String json = null;
        try {
            json = Jackson2.asJson(param);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        return Entity.json(json);
    }

    private String readResponse(String endPoint, Response response) {
        String entity = response.readEntity(String.class);
        showResponse(endPoint, response, entity);
        assertEquals(response.getStatus(), 200, "Unexpected response code.");
        return entity;
    }

    @Override
    public String healthCheck() {
        Invocation.Builder builder = requestBuilder(PathTestEndPoint.HEALT_CHECK);
        Response response = builder.get();
        String entity = response.readEntity(String.class);

        showResponse("healthCheck", response, entity);
        assertEquals(response.getStatus(), 200, "Unexpected response code.");
        return entity;
    }

    @Override
    public JsonNode getRpJwks() {
        return null;
    }

    @Override
    public String getRequestObject(String value) {
        return null;
    }

    @Override
    public GetClientTokenResponse getClientToken(GetClientTokenParams params) {
        WebTarget webTarget = webTarget(PathTestEndPoint.GET_CLIENT_TOKEN);
        Invocation.Builder builder = webTarget.request();
        builder.header("Accept", UmaConstants.JSON_MEDIA_TYPE);
        builder.header("Content-Type", UmaConstants.JSON_MEDIA_TYPE);
        Response response = builder.post(toPostParam(params));
        String json = readResponse(webTarget.getUri().toString(), response);
        try {
            return Jackson2.createJsonMapper().readValue(json, GetClientTokenResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        return null;
    }

    @Override
    public IntrospectAccessTokenResponse introspectAccessToken(String authorization, String authorizationRpId, IntrospectAccessTokenParams params) {
        return null;
    }

    @Override
    public CorrectRptIntrospectionResponse introspectRpt(String authorization, String authorizationRpId, IntrospectRptParams params) {
        return null;
    }

    @Override
    public RegisterSiteResponse registerSite(RegisterSiteParams params) {
        WebTarget webTarget = ResteasyClientBuilder.newClient().target(targeHostUrl + PathTestEndPoint.REGISTER_SITE);
        Invocation.Builder builder = webTarget.request();
        builder.header("Accept", UmaConstants.JSON_MEDIA_TYPE);
        builder.header("Content-Type", UmaConstants.JSON_MEDIA_TYPE);
        Response response = builder.post(toPostParam(params));
        String json = readResponse(webTarget.getUri().toString(), response);
        try {
            return Jackson2.createJsonMapper().readValue(json, RegisterSiteResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        return null;
    }

    @Override
    public UpdateSiteResponse updateSite(String authorization, String authorizationRpId, UpdateSiteParams params) {
        return null;
    }

    @Override
    public RemoveSiteResponse removeSite(String authorization, String authorizationRpId, RemoveSiteParams params) {
        return null;
    }

    @Override
    public GetAuthorizationUrlResponse getAuthorizationUrl(String authorization, String authorizationRpId, GetAuthorizationUrlParams params) {
        return null;
    }

    @Override
    public GetAuthorizationCodeResponse getAuthorizationCode(String authorization, String authorizationRpId, GetAuthorizationCodeParams params) {
        return null;
    }

    @Override
    public GetTokensByCodeResponse2 getTokenByCode(String authorization, String authorizationRpId, GetTokensByCodeParams params) {
        return null;
    }

    @Override
    public JsonNode getUserInfo(String authorization, String authorizationRpId, GetUserInfoParams params) {
        return null;
    }

    @Override
    public GetLogoutUriResponse getLogoutUri(String authorization, String authorizationRpId, GetLogoutUrlParams params) {
        return null;
    }

    @Override
    public GetClientTokenResponse getAccessTokenByRefreshToken(String authorization, String authorizationRpId, GetAccessTokenByRefreshTokenParams params) {
        return null;
    }

    @Override
    public RsProtectResponse umaRsProtect(String authorization, String authorizationRpId, RsProtectParams2 params) {
        return null;
    }

    @Override
    public RsModifyResponse umaRsModify(String authorization, String authorizationRpId, RsModifyParams params) {
        return null;
    }

    @Override
    public RsCheckAccessResponse umaRsCheckAccess(String authorization, String authorizationRpId, RsCheckAccessParams params) {
        return null;
    }

    @Override
    public RpGetRptResponse umaRpGetRpt(String authorization, String authorizationRpId, RpGetRptParams params) {
        return null;
    }

    @Override
    public RpGetClaimsGatheringUrlResponse umaRpGetClaimsGatheringUrl(String authorization, String authorizationRpId, RpGetClaimsGatheringUrlParams params) {
        return null;
    }

    @Override
    public AuthorizationCodeFlowResponse authorizationCodeFlow(String authorization, String authorizationRpId, AuthorizationCodeFlowParams params) {
        return null;
    }

    @Override
    public CheckAccessTokenResponse checkAccessToken(String authorization, String authorizationRpId, CheckAccessTokenParams params) {
        return null;
    }

    @Override
    public CheckIdTokenResponse checkIdToken(String authorization, String authorizationRpId, CheckIdTokenParams params) {
        return null;
    }

    @Override
    public String getRp(String authorization, String authorizationRpId, GetRpParams params) {
        return null;
    }

    @Override
    public GetJwksResponse getJwks(String authorization, String authorizationRpId, GetJwksParams params) {
        return null;
    }

    @Override
    public GetDiscoveryResponse getDiscovery(GetDiscoveryParams params) {
        return null;
    }

    @Override
    public GetIssuerResponse getIssuer(GetIssuerParams params) {
        return null;
    }

    @Override
    public GetRequestObjectUriResponse getRequestObjectUri(String authorization, String authorizationRpId, GetRequestObjectUriParams params) {
        return null;
    }


    public static void showResponse(String title, Response response, Object entity) {
        System.out.println(" ");
        System.out.println("RESPONSE FOR: " + title);
        System.out.println(response.getStatus());
        for (Map.Entry<String, List<Object>> headers : response.getHeaders().entrySet()) {
            String headerName = headers.getKey();
            System.out.println(headerName + ": " + headers.getValue());
        }

        if (entity != null) {
            System.out.println(entity.toString().replace("\\n", "\n"));
        }
        System.out.println(" ");
        System.out.println("Status message: " + response.getStatus());
    }

}
