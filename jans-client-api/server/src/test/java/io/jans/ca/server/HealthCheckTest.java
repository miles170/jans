package io.jans.ca.server;

import io.jans.ca.common.Jackson2;
import io.jans.ca.server.arquillian.BaseTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class HealthCheckTest extends BaseTest {

    @ArquillianResource
    private URI url;

    @Parameters({"testPathHealthCheck"})
    @Test
    public void testHealthCheck(String testPathHealthCheck) throws IOException {
        showTitle("testHealthCheck");

        String targetUrl = url.toString() + testPathHealthCheck;
        System.out.println("-----------TARGET URL------------------------" + targetUrl);
        String resp = Tester.newClient(targetUrl).healthCheck();

        assertNotNull(resp);
        Map<String, String> map = Jackson2.createRpMapper().readValue(resp, Map.class);

        assertEquals(map.get("application"), "oxd");
        assertEquals(map.get("status"), "running");
        assertEquals(map.get("version"), System.getProperty("projectVersion"));

    }
}
