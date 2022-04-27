package io.jans.ca.server.configuration.model;

import io.jans.ca.server.configuration.ApiAppConfiguration;
import io.jans.orm.annotation.*;

@DataEntry
@ObjectClass("jansAppConf")
public class ApiConf extends Conf {

    @JsonObject
    @AttributeName(name = "jansConfDyn")
    private ApiAppConfiguration dynamicConf;

    public ApiAppConfiguration getDynamicConf() {
        return dynamicConf;
    }

    public void setDynamicConf(ApiAppConfiguration dynamicConf) {
        this.dynamicConf = dynamicConf;
    }

    @Override
    public String toString() {
        return "ApiConf [dn=" + dn + ", dynamicConf=" + dynamicConf + ", staticConf=" + staticConf + ", revision="
                + revision + "]";
    }
}
