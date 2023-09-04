/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import dk.dbc.httpclient.HttpClient;
import dk.dbc.infomedia.InfomediaConnector.TimingLogLevel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InfomediaConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfomediaConnectorFactory.class);

    public static InfomediaConnector create(String infomediaBaseUrl, String username, String password) {
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating InfomediaConnector for: {}", infomediaBaseUrl);
        return new InfomediaConnector(client, infomediaBaseUrl, username, password);
    }

    public static InfomediaConnector create(String infomediaBaseUrl, TimingLogLevel level, String username, String password) {
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating InfomediaConnector for: {}", infomediaBaseUrl);
        return new InfomediaConnector(client, infomediaBaseUrl, level, username, password);
    }

    @Inject
    @ConfigProperty(name = "INFOMEDIA_URL")
    private String infomediaBaseUrl;

    @Inject
    @ConfigProperty(name = "INFOMEDIA_TIMING_LOG_LEVEL", defaultValue = "INFO")
    private String level;

    @Inject
    @ConfigProperty(name = "INFOMEDIA_USERNAME")
    private String username;

    @Inject
    @ConfigProperty(name = "INFOMEDIA_PASSWORD")
    private String password;

    InfomediaConnector connector;

    @PostConstruct
    public void initializeConnector() {
        connector = InfomediaConnectorFactory.create(infomediaBaseUrl, TimingLogLevel.valueOf(level), username, password);
    }

    @Produces
    public InfomediaConnector getInstance() {
        return connector;
    }

    @PreDestroy
    public void tearDownConnector() {
        connector.close();
    }
}
