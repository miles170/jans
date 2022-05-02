/*
 * All rights reserved -- Copyright 2015 Gluu Inc.
 */
package io.jans.ca.server;

import io.jans.ca.common.Command;
import io.jans.ca.common.ErrorResponseCode;
import io.jans.ca.common.params.IParams;
import io.jans.ca.common.response.IOpResponse;
import io.jans.ca.server.op.*;
import io.jans.ca.server.service.*;
import io.jans.ca.server.service.auth.ConfigurationService;
import io.jans.ca.server.utils.Convertor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.WebApplicationException;
import org.slf4j.Logger;

/**
 * oxD operation processor.
 *
 * @author Yuriy Zabrovarnyy
 */
@ApplicationScoped
public class Processor {
    @Inject
    Logger logger;
    @Inject
    ValidationService validationService;
    @Inject
    ConfigurationService configurationService;
    @Inject
    RpSyncService rpSyncService;
    @Inject
    KeyGeneratorService keyGeneratorService;
    @Inject
    DiscoveryService discoveryService;
    @Inject
    RpService rpService;
    @Inject
    StateService stateService;
    @Inject
    UmaTokenService umaTokenService;
    @Inject
    PublicOpKeyService publicOpKeyService;


    public IOpResponse process(Command command) {
        if (command != null) {
            try {
                final IOperation<IParams> operation = (IOperation<IParams>) create(command);
                if (operation != null) {
                    IParams iParams = Convertor.asParams(operation.getParameterClass(), command);
                    validationService.validate(iParams);

                    IOpResponse operationResponse = operation.execute(iParams);
                    if (operationResponse != null) {
                        return operationResponse;
                    } else {
                        logger.error("No response from operation. Command: " + command);
                    }
                } else {
                    logger.error("Operation is not supported!");
                    throw new HttpException(ErrorResponseCode.UNSUPPORTED_OPERATION);
                }
            } catch (ClientErrorException e) {
                throw new WebApplicationException(e.getResponse().readEntity(String.class), e.getResponse().getStatus());
            } catch (WebApplicationException e) {
                logger.error(e.getLocalizedMessage(), e);
                throw e;
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
        throw HttpException.internalError();
    }

    private ServiceProvider getServiceProvider() {
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setRpService(rpService);
        serviceProvider.setConfigurationService(configurationService);
        serviceProvider.setDiscoveryService(discoveryService);
        serviceProvider.setValidationService(validationService);
        serviceProvider.setHttpService(discoveryService.getHttpService());
        serviceProvider.setRpSyncService(rpSyncService);
        return serviceProvider;
    }

    private IOperation<? extends IParams> create(Command command) {

        if (command != null && command.getCommandType() != null) {
            switch (command.getCommandType()) {
                case REGISTER_SITE:
                    return new RegisterSiteOperation(command, rpService, discoveryService);
                case UPDATE_SITE:
                    return new UpdateSiteOperation(command, rpService);
                case REMOVE_SITE:
                    return new RemoveSiteOperation(command, getServiceProvider());
                case GET_CLIENT_TOKEN:
                    return new GetClientTokenOperation(command, discoveryService);
                case GET_ACCESS_TOKEN_BY_REFRESH_TOKEN:
                    return new GetAccessTokenByRefreshTokenOperation(command, discoveryService);
                case INTROSPECT_ACCESS_TOKEN:
                    return new IntrospectAccessTokenOperation(command, getServiceProvider());
                case GET_USER_INFO:
                    return new GetUserInfoOperation(command, getServiceProvider());
                case GET_JWKS:
                    return new GetJwksOperation(command, discoveryService);
                case GET_DISCOVERY:
                    return new GetDiscoveryOperation(command, discoveryService);
                case GET_AUTHORIZATION_URL:
                    return new GetAuthorizationUrlOperation(command, discoveryService, stateService, configurationService);
                case GET_TOKENS_BY_CODE:
                    return new GetTokensByCodeOperation(command, discoveryService, stateService, rpService, keyGeneratorService, publicOpKeyService);
                case RS_PROTECT:
                    return new RsProtectOperation(command, umaTokenService);
                case RS_CHECK_ACCESS:
                    return new RsCheckAccessOperation(command, umaTokenService);
                case INTROSPECT_RPT:
                    return new IntrospectRptOperation(command, getServiceProvider());
                case RP_GET_RPT:
                    return new RpGetRptOperation(command, umaTokenService);
                case RP_GET_CLAIMS_GATHERING_URL:
                    return new RpGetGetClaimsGatheringUrlOperation(command, discoveryService, stateService);
                case GET_RP_JWKS:
                    return new GetRpJwksOperation(command, keyGeneratorService);
            }
            logger.error("Command is not supported. Command: {}", command);
        } else {
            logger.error("Command is invalid. Command: {}", command);
        }
        return null;
    }

}
