# PersonAuthentication External Authn

from io.jans.model.custom.script.type.auth import PersonAuthenticationType
from io.jans.service.cdi.util import CdiUtil
from io.jans.as.server.security import Identity
from io.jans.as.server.service import AuthenticationService
from io.jans.util import StringHelper
from io.jans.as.server.util import ServerUtil
from io.jans.as.server.service import SessionIdService
from io.jans.as.server.service import CookieService
from io.jans.service.cache import CacheProvider
from jakarta.faces.context import ExternalContext

import java
import uuid

class PersonAuthentication(PersonAuthenticationType):
    def __init__(self, currentTimeMillis):
        self.currentTimeMillis = currentTimeMillis

    def init(self, customScript,  configurationAttributes):
        print "PA External Authn. Initialization"
        print "PA External Authn. Initialized successfully"

        self.REDIRECT_URI_STEP1 = None

        # Get Custom Properties
        try:
            self.REDIRECT_URI_STEP1 = configurationAttributes.get("urlstep1").getValue2()
            print "PA External Authn. Get custom property: urlstep1: '%s'" % self.REDIRECT_URI_STEP1
        except:
            print 'Missing required configuration attribute "urlstep1"'

        return True

    def destroy(self, configurationAttributes):
        print "PA External Authn. Destroy"
        print "PA External Authn. Destroyed successfully"
        return True

    def getAuthenticationMethodClaims(self, requestParameters):
        return None

    def getApiVersion(self):
        return 11

    def isValidAuthenticationMethod(self, usageType, configurationAttributes):
        return True

    def getAlternativeAuthenticationMethod(self, usageType, configurationAttributes):
        return None

    def authenticate(self, configurationAttributes, requestParameters, step):
        print "PA External Authn. Authenticate for step: %s" % step

        # authenticationService = CdiUtil.bean(AuthenticationService)

        # # identity = CdiUtil.bean(Identity)
        # # credentials = identity.getCredentials()

        # user_name = 'test_user'
        # user_password = 'test_user_password'

        # logged_in = False
        # if (StringHelper.isNotEmptyString(user_name) and StringHelper.isNotEmptyString(user_password)):
        #     logged_in = authenticationService.authenticate(user_name, user_password)

        # if (not logged_in):
        #     print "PA External Authn. Authenticate Failed to authenticate user '%s'" % (user_name)
        #     return False
        # print "PA External Authn. Authenticate Successfully authenticated user '%s'" % (user_name)

        # Retrieve jansKey from request parameters and validate it
        jansKey = ServerUtil.getFirstValue(requestParameters, "jansKey")
        if (jansKey == None or StringHelper.isEmpty(jansKey)):
            print "PA External Authn. Authenticate jansKey is null or empty"
            return False
        print "PA External Authn. Authenticate jansKey '%s' found in request parameters" % jansKey
        
        # Retrieve jsonValues from cache using jansKey and validate it
        cacheProvider = CdiUtil.bean(CacheProvider)
        jsonValues = cacheProvider.get(jansKey);
        if (jsonValues == None):
            print "PA External Authn. Authenticate jansKey not found in cache"
            return False
        print "PA External Authn. Authenticate jansKey found in cache"

        # Retrieve status from cache using jansKey and validate it
        status = jsonValues["status"]
        if (status == None or StringHelper.isEmpty(status) or status != "OK"):
            print "PA External Authn. Authenticate status is null or empty or not Ok"
            return False
        print "PA External Authn. Authenticate status is OK"

        # Retrieve sessionDn from cache using jansKey and validate it
        sessionDn = jsonValues["sessionDn"]
        if (sessionDn == None or StringHelper.isEmpty(sessionDn)):
            print "PA External Authn. Authenticate sessionDn is null or empty"
            return False
        print "PA External Authn. Authenticate sessionDn '%s' found in cache" % sessionDn

        # Retrieve sessionId from session registry using sessionDn and validate it
        sessionIdService = CdiUtil.bean(SessionIdService)
        sessionId = sessionIdService.getSessionByDn(sessionDn)
        if (sessionId == None):
            print "PA External Authn. Authenticate sessionDn not found in session"
            return False
        print "PA External Authn. Authenticate sessionDn found in session"

        # Write sessionId in cookies
        CdiUtil.bean(CookieService).createSessionIdCookie(sessionId, False)
        print "PA External Authn. Authenticate sessionId '%s' created in cookies" % sessionId.getId()

        # Set sessionId to identity
        CdiUtil.bean(Identity).setSessionId(sessionId)
        print "PA External Authn. Authenticate set sessionId to identity"

        # Remove jansKey from cache
        # cacheProvider.remove(jansKey)
        # print "PA External Authn. Authenticate jansKey removed from cache"

        return True

    def prepareForStep(self, configurationAttributes, requestParameters, step):
        if (step == 1):
            return True
        else:
            return False

    def getExtraParametersForStep(self, configurationAttributes, step):
        return None

    def getCountAuthenticationSteps(self, configurationAttributes):
        return 1

    def getPageForStep(self, configurationAttributes, step):
        print "PA External Authn. GetPageForStep for step: %s" % step
        
        externalContext = CdiUtil.bean(ExternalContext)
        jansKeyAux = ServerUtil.getFirstValue(externalContext.getRequestParameterValuesMap(), "jansKey")
        if (jansKeyAux != None):
            print "PA External Authn. GetPageForStep jansKey '%s' found in request parameters" % jansKeyAux
            return "postlogin.xhtml"

        else:
            redirectUri = ServerUtil.getFirstValue(externalContext.getRequestParameterValuesMap(), "redirect_uri")
            if (redirectUri == None or StringHelper.isEmpty(redirectUri)):
                print "PA External Authn. GetPageForStep redirect_uri is null or empty"
                return ""
            print "PA External Authn. GetPageForStep redirect_uri '%s' found in request parameters" % redirectUri

            # Generate jansKey
            jansKey = str(uuid.uuid4());
            print "PA External Authn. GetPageForStep jansKey '%s' generated" % jansKey

            # Create JSON Values
            jsonValues = {}
            jsonValues["status"] = "Created"
            jsonValues["redirectUri"] = str(redirectUri)

            cacheProvider = CdiUtil.bean(CacheProvider)
            cacheProvider.put(300, jansKey, jsonValues)
            print "PA External Authn. GetPageForStep jansKey '%s' added to cache: %s" % (jansKey, jsonValues)

            #sessionIdService = CdiUtil.bean(SessionIdService)
            identity = CdiUtil.bean(Identity)
            sessionId = identity.getSessionId()
            print "PA External Authn. GetPageForStep sessionId %s" % sessionId
            

            return self.REDIRECT_URI_STEP1 + "?jansKey=" + jansKey

    def getNextStep(self, configurationAttributes, requestParameters, step):
        return -1

    def getLogoutExternalUrl(self, configurationAttributes, requestParameters):
        return None

    def logout(self, configurationAttributes, requestParameters):
        return True
