# UpdateToken External Authn

from io.jans.model.custom.script.type.token import UpdateTokenType
from io.jans.service.cache import CacheProvider
from io.jans.service.cdi.util import CdiUtil
from io.jans.util import StringHelper

class UpdateToken(UpdateTokenType):
    def __init__(self, currentTimeMillis):
        self.currentTimeMillis = currentTimeMillis

    def init(self, customScript, configurationAttributes):
        print "UT External Authn. Initializing ..."
        print "UT External Authn. Initialized successfully"

        return True

    def destroy(self, configurationAttributes):
        print "UT External Authn. Destroying ..."
        print "UT External Authn. Destroyed successfully"
        return True

    def getApiVersion(self):
        return 11

    def modifyIdToken(self, jsonWebResponse, context):
        
        # Retrieve jansKey from cache
        jansKey = context.getExecutionContext().getHttpRequest().getParameter("jansKey")
        if (jansKey == None or StringHelper.isEmpty(jansKey)):
            print "UT External Authn. ModifyIdToken Could not find jansKey in request"
            return False
        print "UT External Authn. ModifyIdToken jansKey '%s' found in request" % jansKey

        # Retrieve jsonValues from cache using jansKey
        cacheProvider = CdiUtil.bean(CacheProvider)
        jsonValues = cacheProvider.get(jansKey)
        if jsonValues == None:
            print "UT External Authn. ModifyIdToken Could not find jansKey in cache"
            return False
        print "UT External Authn. ModifyIdToken jansKey found in cache"

        # Retrieve redirectUri from cache using jansKey
        jsonValRedirectUri = jsonValues.get("redirectUri")
        if jsonValRedirectUri == None:
            print "UT External Authn. ModifyIdToken Could not find redirectUri in cache"
            return False
        print "UT External Authn. ModifyIdToken redirectUri '%s' found in cache" % jsonValRedirectUri

        # Retrieve sessionId from cache using jansKey
        jsonValSessionId = jsonValues.get("sessionDn")
        if jsonValSessionId == None:
            print "UT External Authn. ModifyIdToken Could not find sessionId in cache"
            return False
        print "UT External Authn. ModifyIdToken sessionId '%s' found in cache" % jsonValSessionId

        # Retrieve authorizeEndpoint from appConfiguration
        authorizeEndpoint = context.getAppConfiguration().getAuthorizationEndpoint()
        if authorizeEndpoint == None:
            print "UT External Authn. ModifyIdToken Could not find authorizeEndpoint in app configuration"
            return False
        print "UT External Authn. ModifyIdToken authorizeEndpoint '%s' found in app configuration" % authorizeEndpoint

        # Retrieve clientId from context
        clientId = context.getClient().getClientId()
        if (clientId == None or StringHelper.isEmpty(clientId)):
            print "UT External Authn. ModifyIdToken Could not find clientId in context"
            return False
        print "UT External Authn. ModifyIdToken clientId '%s' found in context" % clientId

        callbackUri = '%s?response_type=code&client_id=%s&redirect_uri=%s&session_id=%s' % (authorizeEndpoint, clientId, jsonValRedirectUri, jsonValSessionId)
        print "UT External Authn. ModifyIdToken callbackUri '%s'" % callbackUri

        # Decide where to set the callback_uri in header or payload
        jsonWebResponse.getHeader().setClaim("callback_uri", callbackUri)
        jsonWebResponse.getClaims().setClaim("callback_uri", callbackUri)

        # Remove jansKey from cache
        # cacheProvider.remove(jansKey)
        # print "UT External Authn. ModifyIdToken jansKey removed from cache"

        return True

    def modifyRefreshToken(self, refreshToken, context):
        return True

    def modifyAccessToken(self, accessToken, context):
        return True

    def getRefreshTokenLifetimeInSeconds(self, context):
        return 0

    def getIdTokenLifetimeInSeconds(self, context):
        return 0

    def getAccessTokenLifetimeInSeconds(self, context):
        return 0
