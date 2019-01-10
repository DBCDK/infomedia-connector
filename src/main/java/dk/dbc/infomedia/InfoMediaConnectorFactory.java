package dk.dbc.infomedia;

import dk.dbc.infomedia.InfoMediaConnector.TimingLogLevel;
import dk.dbc.httpclient.HttpClient;
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
public class InfoMediaConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoMediaConnectorFactory.class);

    public static InfoMediaConnector create(String recordServiceBaseUrl, String username, String password) throws InfoMediaConnectorException{
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating RecordServiceConnector for: {}", recordServiceBaseUrl);
        return new InfoMediaConnector(client, recordServiceBaseUrl, username, password);
    }

    public static InfoMediaConnector create(String recordServiceBaseUrl, InfoMediaConnector.TimingLogLevel level, String username, String password) throws InfoMediaConnectorException{
        final Client client = HttpClient.newClient(new ClientConfig().register(new JacksonFeature()));
        LOGGER.info("Creating RecordServiceConnector for: {}", recordServiceBaseUrl);
        return new InfoMediaConnector(client, recordServiceBaseUrl, level, username, password);
    }

    @Inject
    @ConfigProperty(name = "INFOMEDIA_URL")
    private String infoMediaBaseUrl;

    @Inject
    @ConfigProperty(name = "INFOMEDIA_TIMING_LOG_LEVEL", defaultValue = "INFO")
    private TimingLogLevel level;

    @Inject
    @ConfigProperty(name = "INFOMEDIA_USERNAME", defaultValue = "not set")
    private String username;

    @Inject
    @ConfigProperty(name = "INFOMEDIA_PASSWORD", defaultValue = "not set")
    private String password;

    InfoMediaConnector connector;

    @PostConstruct
    public void initializeConnector() throws InfoMediaConnectorException{
        connector = InfoMediaConnectorFactory.create(infoMediaBaseUrl, level, username, password);
    }

    @Produces
    public InfoMediaConnector getInstance() {
        return connector;
    }

    @PreDestroy
    public void tearDownConnector() {
        connector.close();
    }
}
