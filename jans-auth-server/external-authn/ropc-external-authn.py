# ResourceOwnerPasswordCredentials External Authn

from io.jans.model.custom.script.type.owner import ResourceOwnerPasswordCredentialsType
from io.jans.as.server.service import AuthenticationService, SessionIdService
from io.jans.as.server.model.common import SessionIdState
from io.jans.as.server.security import Identity
from io.jans.service.cdi.util import CdiUtil
from io.jans.as.model.authorize import AuthorizeRequestParam
from io.jans.as.server.model.config import Constants
from io.jans.util import StringHelper
from java.lang import String
from java.util import Date, HashMap
from io.jans.service.cache import CacheProvider

class ResourceOwnerPasswordCredentials(ResourceOwnerPasswordCredentialsType):
    def __init__(self, currentTimeMillis):
        self.currentTimeMillis = currentTimeMillis

    def init(self, customScript, configurationAttributes):
        print "ROPC External Authn. Initializing ..."
        print "ROPC External Authn. Initialized successfully"
        return True

    def destroy(self, configurationAttributes):
        print "ROPC External Authn. Destroying ..."
        print "ROPC External Authn. Destroyed successfully"
        return True

    def getApiVersion(self):
        return 11

    def authenticate(self, context):
        print "ROPC External Authn. Authenticate"

        # Retrieve jansKey from cache
        jansKey = context.getHttpRequest().getParameter("jansKey")
        if (jansKey == None or StringHelper.isEmpty(jansKey)):
            print "ROPC External Authn. Authenticate. jansKey is empty"
            return False
        cacheProvider = CdiUtil.bean(CacheProvider)
        jsonValues = cacheProvider.get(jansKey)
        if jsonValues == None:
            print "ROPC External Authn. Authenticate. Could not find jansKey in cache"
            return False

        # Do generic authentication
        authenticationService = CdiUtil.bean(AuthenticationService)

        username = context.getHttpRequest().getParameter("username")
        password = context.getHttpRequest().getParameter("password")
        result = authenticationService.authenticate(username, password)
        if not result:
            print "ROPC External Authn. Authenticate. Could not authenticate user '%s' " % username
            return False

        print "ROPC External Authn. Authenticate. JANS values: '%s'" % (jsonValues)

        context.setUser(authenticationService.getAuthenticatedUser())
        print "ROPC External Authn. Authenticate. User '%s' authenticated successfully" % username
        
        # Get cusom parameters from request
        customParam1Value = context.getHttpRequest().getParameter("custom1")
        customParam2Value = context.getHttpRequest().getParameter("custom2")

        customParameters = {}
        customParameters["custom1"] = customParam1Value
        customParameters["custom2"] = customParam2Value
        print "ROPC External Authn. Authenticate. User '%s'. Creating authenticated session with custom attributes: '%s'" % (username, customParameters)

        session = self.createNewAuthenticatedSession(context, customParameters)

        # This is needed to allow store in token entry sessionId
        authenticationService.configureEventUser(session)
        print "ROPC External Authn. Authenticate. User '%s'. Created authenticated session: '%s'" % (username, customParameters)

        # Set session id cache
        jsonValues["status"] = "OK"
        # jsonValues["sessionId"] = str(session.getId())
        jsonValues["sessionDn"] = str(session.getDn())
        cacheProvider.put(300, jansKey, jsonValues)
        print "ROPC External Authn. Authenticate. Stored sessionDn: '%s' and status: '%s' in cache" % (jsonValues.get("sessionDn"), jsonValues.get("status"))
        
        return True

    def createNewAuthenticatedSession(self, context, customParameters={}):
        sessionIdService = CdiUtil.bean(SessionIdService)

        user = context.getUser()
        client = CdiUtil.bean(Identity).getSessionClient().getClient()

        # Add mandatory session parameters
        sessionAttributes = HashMap()
        sessionAttributes.put(Constants.AUTHENTICATED_USER, user.getUserId())
        sessionAttributes.put(AuthorizeRequestParam.CLIENT_ID, client.getClientId())
        sessionAttributes.put(AuthorizeRequestParam.PROMPT, "")

        # Add custom session parameters
        for key, value in customParameters.iteritems():
            if StringHelper.isNotEmpty(value):
                sessionAttributes.put(key, value)

        # Generate authenticated session
        sessionId = sessionIdService.generateAuthenticatedSessionId(context.getHttpRequest(), user.getDn(), sessionAttributes)

        print "ROPC External Authn. Generated session id. DN: '%s'" % sessionId.getDn()

        return sessionId
    
    # def createCallBackRedirect(self, context, sessionId, jsonValues):
        
    #     # Retrieve authorizeEndpoint from appConfiguration
    #     authorizeEndpoint = context.getAppConfiguration().getAuthorizationEndpoint()
    #     if authorizeEndpoint == None:
    #         print "ROPC External Authn. CreateCallBackRedirect Could not find authorizeEndpoint in app configuration"
    #         return ""
    #     print "ROPC External Authn. CreateCallBackRedirect authorizeEndpoint '%s' found in app configuration" % authorizeEndpoint

    #     clientId = CdiUtil.bean(Identity).getSessionClient().getClient().getClientId()
    #     if (clientId == None or StringHelper.isEmpty(clientId)):
    #         print "ROPC External Authn. CreateCallBackRedirect Could not find clientId in session"
    #         return ""
    #     print "ROPC External Authn. CreateCallBackRedirect clientId '%s' found in session" % clientId

    #     # Retrieve redirectUri from cache using jansKey
    #     jsonValRedirectUri = jsonValues.get("redirectUri")
    #     if (jsonValRedirectUri == None or StringHelper.isEmpty(jsonValRedirectUri)):
    #         print "ROPC External Authn. CreateCallBackRedirect Could not find redirectUri in cache or is empty"
    #         return ""
    #     print "ROPC External Authn. CreateCallBackRedirect '%s' found in cache" % jsonValRedirectUri

    #     callbackUri = '%s?response_type=code&client_id=%s&redirect_uri=%s&session_id=%s' % (authorizeEndpoint, clientId, jsonValRedirectUri, sessionId.getId())
    #     print "ROPC External Authn. CreateCallBackRedirect callbackUri '%s'" % callbackUri

    #     return callbackUri