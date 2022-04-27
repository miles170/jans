/*
  All rights reserved -- Copyright 2015 Gluu Inc.
 */
package io.jans.ca.server.op;

import io.jans.as.model.crypto.AuthCryptoProvider;
import io.jans.ca.common.Command;
import io.jans.ca.common.ErrorResponseCode;
import io.jans.ca.common.params.HasRpIdParams;
import io.jans.ca.common.params.IParams;
import io.jans.ca.server.HttpException;
import io.jans.ca.server.configuration.model.ApiConf;
import io.jans.ca.server.configuration.model.Rp;
import io.jans.ca.server.service.RpSyncService;
import io.jans.ca.server.service.ValidationService;
import io.jans.ca.server.service.auth.ConfigurationService;
import io.jans.ca.server.utils.Convertor;

/**
 * Base abstract class for all operations.
 *
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 09/08/2013
 *
 */

public abstract class BaseOperation<T extends IParams> implements IOperation<T> {

    private final Command command;
    private final Class<T> parameterClass;
    private final T params;

    private ConfigurationService configurationService;
    private ValidationService validationService;
    private RpSyncService rpSyncService;
    /**
     * Base constructor
     *
     * @param command command
     */
    protected BaseOperation(Command command, Class<T> parameterClass) {
        this.command = command;
        this.parameterClass = parameterClass;
        this.params = Convertor.asParams(parameterClass, command);
    }

    @Override
    public Class<T> getParameterClass() {
        return parameterClass;
    }

    public T getParams() {
        return params;
    }

    public ValidationService getValidationService() {
        return validationService;
    }

    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }

    public RpSyncService getRpSyncService() {
        return rpSyncService;
    }

    public void setRpSyncService(RpSyncService rpSyncService) {
        this.rpSyncService = rpSyncService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public AuthCryptoProvider getCryptoProvider() throws Exception {
        ApiConf conf = getConfigurationService().findConf();
        return new AuthCryptoProvider(conf.getDynamicConf().getCryptProviderKeyStorePath(), conf.getDynamicConf().getCryptProviderKeyStorePassword(), conf.getDynamicConf().getCryptProviderDnName());
    }

    public Rp getRp() {
        if (params instanceof HasRpIdParams) {
            getValidationService().validate((HasRpIdParams) params);
            HasRpIdParams hasRpId = (HasRpIdParams) params;
            return getRpSyncService().getRp(hasRpId.getRpId());
        }
        throw new HttpException(ErrorResponseCode.BAD_REQUEST_NO_RP_ID);
    }

    /**
     * Returns command
     *
     * @return command
     */
    public Command getCommand() {
        return command;
    }
}
