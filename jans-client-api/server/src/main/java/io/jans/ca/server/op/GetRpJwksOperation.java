package io.jans.ca.server.op;

import io.jans.ca.common.Command;
import io.jans.ca.common.params.GetJwksParams;
import io.jans.ca.common.response.IOpResponse;
import io.jans.ca.common.response.POJOResponse;
import io.jans.ca.server.HttpException;
import io.jans.ca.server.service.KeyGeneratorService;

public class GetRpJwksOperation extends BaseOperation<GetJwksParams> {

    private KeyGeneratorService keyGeneratorService;

    public GetRpJwksOperation(Command command, KeyGeneratorService keyGeneratorService) {
        super(command, GetJwksParams.class);
        this.keyGeneratorService = keyGeneratorService;
    }

    @Override
    public IOpResponse execute(GetJwksParams params) {

        try {
            return new POJOResponse(keyGeneratorService.getKeys());
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
