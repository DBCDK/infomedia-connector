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

    public static InfomediaConnector create(String recordServiceBaseUrl, String username, String password) {
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating InfomediaConnector for: {}", recordServiceBaseUrl);
        return new InfomediaConnector(client, recordServiceBaseUrl, username, password);
    }

    public static InfomediaConnector create(String recordServiceBaseUrl, InfomediaConnector.TimingLogLevel level, String username, String password) {
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating InfomediaConnector for: {}", recordServiceBaseUrl);
        return new InfomediaConnector(client, recordServiceBaseUrl, level, username, password);
    }

    @Inject
    @ConfigProperty(name = "INFOMEDIA_URL")
    private String infomediaBaseUrl;

    // TODO Implement log level injection
//    @Inject
//    @ConfigProperty(name = "INFOMEDIA_TIMING_LOG_LEVEL", defaultValue = "INFO")
//    private TimingLogLevel level;

    @Inject
    @ConfigProperty(name = "INFOMEDIA_USERNAME")
    private String username;

    @Inject
    @ConfigProperty(name = "INFOMEDIA_PASSWORD")
    private String password;

    InfomediaConnector connector;

    @PostConstruct
    public void initializeConnector() {
        connector = InfomediaConnectorFactory.create(infomediaBaseUrl, TimingLogLevel.INFO, username, password);
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
