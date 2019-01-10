package dk.dbc.infomedia;

import dk.dbc.httpclient.FailSafeHttpClient;
import dk.dbc.httpclient.HttpPost;
import dk.dbc.invariant.InvariantUtil;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class InfoMediaConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoMediaConnector.class);

    public enum TimingLogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private static final String URL_OAUTH_TOKEN = "/oauth/token";
    private static final String URL_INFOMEDIA_FETCH = "/api/articles/fetch";
    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
            .retryOn(Collections.singletonList(ProcessingException.class))
            .retryIf((Response response) -> response.getStatus() == 404
                    || response.getStatus() == 500
                    || response.getStatus() == 502)
            .withDelay(10, TimeUnit.SECONDS)
            .withMaxRetries(6);

    private final FailSafeHttpClient failSafeHttpClient;
    private final String baseUrl;
    private final String username;
    private final String password;
    private final LogLevelMethod logger;

    private Calendar tokenExpiryDate;
    private String bearerToken;

    /**
     * Returns new instance with default retry policy
     *
     * @param httpClient web resources client
     * @param baseUrl    base URL for infomedia api endpoint
     * @param username   the username for the infomedia service
     * @param password   the password for the infomedia service
     */
    public InfoMediaConnector(Client httpClient, String baseUrl, String username, String password) {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl, TimingLogLevel.INFO, username, password);
    }

    /**
     * Returns new instance with default retry policy
     *
     * @param httpClient web resources client
     * @param baseUrl    base URL for infomedia api endpoint
     * @param level      log level
     * @param username   the username for the infomedia service
     * @param password   the password for the infomedia service
     */
    public InfoMediaConnector(Client httpClient, String baseUrl, TimingLogLevel level, String username, String password) {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl, level, username, password);
    }

    /**
     * Returns new instance with custom retry policy
     *
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl            base URL for infomedia api endpoint
     * @param username           the username for the infomedia service
     * @param password           the password for the infomedia service
     */
    public InfoMediaConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl, String username, String password) {
        this(failSafeHttpClient, baseUrl, TimingLogLevel.INFO, username, password);
    }

    /**
     * Returns new instance with custom retry policy
     *
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl            base URL for infomedia api endpoint
     * @param level              log level
     * @param username           the username for the infomedia service
     * @param password           the password for the infomedia service
     */
    public InfoMediaConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl, TimingLogLevel level, String username, String password) {
        this.failSafeHttpClient = InvariantUtil.checkNotNullOrThrow(
                failSafeHttpClient, "failSafeHttpClient");
        this.baseUrl = InvariantUtil.checkNotNullNotEmptyOrThrow(
                baseUrl, "baseUrl");
        this.username = InvariantUtil.checkNotNullOrThrow(
                username, "username");
        this.password = InvariantUtil.checkNotNullOrThrow(
                password, "password");
        switch (level) {
            case TRACE:
                logger = LOGGER::trace;
                break;
            case DEBUG:
                logger = LOGGER::debug;
                break;
            case INFO:
                logger = LOGGER::info;
                break;
            case WARN:
                logger = LOGGER::warn;
                break;
            case ERROR:
                logger = LOGGER::error;
                break;
            default:
                logger = LOGGER::info;
                break;
        }
    }

    /**
     * This function is responsible for keeping the bearer token up to date
     *
     * @throws InfoMediaConnectorException
     */
    private void authenticate() throws InfoMediaConnectorException {
        synchronized (this) {
            if (!authTokenIsValid()) {
                final String data = "grant_type=password&username=" + username + "&password=" + password;
                final HttpPost httpPost = new HttpPost(failSafeHttpClient)
                        .withBaseUrl(baseUrl)
                        .withPathElements(URL_OAUTH_TOKEN)
                        .withData(data, MediaType.APPLICATION_JSON)
                        .withHeader("Content-type", "application/x-www-form-urlencoded");

                final Response response = httpPost.execute();
                assertResponseStatus(response, Response.Status.OK);
                AuthToken auth = readResponseEntity(response, AuthToken.class);

                updateAuthToken(auth);
            }
        }
    }

    private void updateAuthToken(AuthToken authToken) {
        this.bearerToken = authToken.getAccessToken();
        this.tokenExpiryDate.add(Calendar.SECOND, authToken.getExpiresIn());
    }

    private boolean authTokenIsValid() {
        // No auth token has been retrieved before
        if (this.tokenExpiryDate == null) {
            return false;
        }

        return Calendar.getInstance().before(this.tokenExpiryDate);
    }

    public void close() {
        failSafeHttpClient.getClient().close();
    }

    private <T> T readResponseEntity(Response response, Class<T> type)
            throws InfoMediaConnectorException {
        final T entity = response.readEntity(type);
        if (entity == null) {
            throw new InfoMediaConnectorException(
                    String.format("infomedia service returned with null-valued %s entity",
                            type.getName()));
        }
        return entity;
    }

    private void assertResponseStatus(Response response, Response.Status expectedStatus)
            throws InfoMediaConnectorUnexpectedStatusCodeException {
        final Response.Status actualStatus =
                Response.Status.fromStatusCode(response.getStatus());
        if (actualStatus != expectedStatus) {
            throw new InfoMediaConnectorUnexpectedStatusCodeException(
                    String.format("infomedia service returned with unexpected status code: %s",
                            actualStatus),
                    actualStatus.getStatusCode());
        }
    }

    @FunctionalInterface
    interface LogLevelMethod {
        void log(String format, Object... objs);
    }

}
