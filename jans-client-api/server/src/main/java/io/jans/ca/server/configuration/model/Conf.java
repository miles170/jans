package io.jans.ca.server.configuration.model;

import io.jans.as.model.config.StaticConfiguration;
import io.jans.orm.annotation.*;

@DataEntry
@ObjectClass(value = "jansAppConf")
public class Conf {

    @DN
    protected String dn;

    @JsonObject
    @AttributeName(name = "jansConfStatic")
    protected StaticConfiguration staticConf;

    @AttributeName(name = "jansRevision")
    protected long revision;

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public StaticConfiguration getStaticConf() {
        return staticConf;
    }

    public void setStaticConf(StaticConfiguration staticConf) {
        this.staticConf = staticConf;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return "Conf [dn=" + dn + ", staticConf=" + staticConf + ", revision=" + revision + "]";
    }
}