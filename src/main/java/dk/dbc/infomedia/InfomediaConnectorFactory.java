/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import dk.dbc.httpclient.HttpClient;
import dk.dbc.infomedia.InfomediaConnector.TimingLogLevel;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.Client;

@ApplicationScoped
public class InfomediaConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfomediaConnectorFactory.class);

    public static InfomediaConnector create(String informediaBaseUrl, String username, String password) {
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating InfomediaConnector for: {}", informediaBaseUrl);
        return new InfomediaConnector(client, informediaBaseUrl, username, password);
    }

    public static InfomediaConnector create(String informediaBaseUrl, TimingLogLevel level, String username, String password) {
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating InfomediaConnector for: {}", informediaBaseUrl);
        return new InfomediaConnector(client, informediaBaseUrl, level, username, password);
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
