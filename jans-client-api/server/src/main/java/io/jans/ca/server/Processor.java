/*
 * All rights reserved -- Copyright 2015 Gluu Inc.
 */
package io.jans.ca.server;

import io.jans.ca.common.Command;
import io.jans.ca.common.ErrorResponseCode;
import io.jans.ca.common.params.IParams;
import io.jans.ca.common.response.IOpResponse;
import io.jans.ca.rs.protect.resteasy.Configuration;
import io.jans.ca.server.op.BaseOperation;
import io.jans.ca.server.op.GetRpJwksOperation;
import io.jans.ca.server.op.IOperation;
import io.jans.ca.server.service.KeyGeneratorService;
import io.jans.ca.server.service.RpSyncService;
import io.jans.ca.server.service.ValidationService;
import io.jans.ca.server.service.auth.ConfigurationService;
import io.jans.ca.server.utils.Convertor;
import org.slf4j.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.WebApplicationException;

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

    private IOperation<? extends IParams> create(Command command) {
        BaseOperation operation = null;
        if (command != null && command.getCommandType() != null) {
            switch (command.getCommandType()) {
                case GET_RP_JWKS:
                    operation = new GetRpJwksOperation(command, keyGeneratorService);
            }
            if (operation != null) {
                operation.setValidationService(validationService);
                operation.setRpSyncService(rpSyncService);
                operation.setConfigurationService(configurationService);
                return operation;
            }
            logger.error("Command is not supported. Command: {}", command);
        } else {
            logger.error("Command is invalid. Command: {}", command);
        }
        return null;
    }

}
