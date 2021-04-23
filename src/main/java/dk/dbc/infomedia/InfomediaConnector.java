/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import dk.dbc.httpclient.FailSafeHttpClient;
import dk.dbc.httpclient.HttpPost;
import dk.dbc.invariant.InvariantUtil;
import dk.dbc.util.Stopwatch;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class InfomediaConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfomediaConnector.class);

    public enum TimingLogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private static final String URL_OAUTH_TOKEN = "/oauth/token";
    private static final String URL_INFOMEDIA_SEARCH = "/api/v1/article/search";
    private static final String URL_INFOMEDIA_FETCH = "/api/v1/article/fetch";
    private static final RetryPolicy<Response> RETRY_POLICY = new RetryPolicy<Response>()
            .handle(ProcessingException.class)
            .handleResultIf(response -> response.getStatus() == 404
                    || response.getStatus() == 502)
            .withDelay(Duration.ofSeconds(10))
            .withMaxRetries(6);

    private final FailSafeHttpClient failSafeHttpClient;
    private final String baseUrl;
    private final String username;
    private final String password;
    private final LogLevelMethod logger;

    private Instant tokenExpiryDate = Instant.now();
    private String bearerToken;
    private int pageSize = 300;

    /**
     * Returns new instance with default retry policy
     *
     * @param httpClient web resources client
     * @param baseUrl    base URL for infomedia api endpoint
     * @param username   the username for the infomedia service
     * @param password   the password for the infomedia service
     */
    public InfomediaConnector(Client httpClient, String baseUrl, String username, String password) {
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
    public InfomediaConnector(Client httpClient, String baseUrl, TimingLogLevel level, String username, String password) {
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
    public InfomediaConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl, String username, String password) {
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
    public InfomediaConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl, TimingLogLevel level, String username, String password) {
        this.failSafeHttpClient = InvariantUtil.checkNotNullOrThrow(failSafeHttpClient, "failSafeHttpClient");
        this.baseUrl = InvariantUtil.checkNotNullNotEmptyOrThrow(baseUrl, "baseUrl");
        this.username = InvariantUtil.checkNotNullOrThrow(username, "username");
        this.password = InvariantUtil.checkNotNullOrThrow(password, "password");
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

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * This function is responsible for keeping the bearer token up to date
     *
     * @throws InfomediaConnectorException
     */
    private void authenticate() throws InfomediaConnectorException {
        synchronized (this) {
            if (!authTokenIsValid()) {
                final Stopwatch stopwatch = new Stopwatch();
                try {
                    logger.log("Token expired - getting new one");
                    final String data = String.format("grant_type=password&username=%s&password=%s", username, password);
                    final HttpPost httpPost = new HttpPost(failSafeHttpClient)
                            .withBaseUrl(baseUrl)
                            .withPathElements(URL_OAUTH_TOKEN)
                            .withData(data, MediaType.TEXT_PLAIN);


                    final Response response = httpPost.execute();
                    assertResponseStatus(response, Response.Status.OK);
                    final AuthToken auth = readResponseEntity(response, AuthToken.class);

                    updateAuthToken(auth);
                } finally {
                    logger.log("POST {} took {} milliseconds", URL_OAUTH_TOKEN,
                            stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));
                }
            }
        }
    }

    private void updateAuthToken(AuthToken authToken) {
        this.bearerToken = authToken.getAccessToken();
        this.tokenExpiryDate = Instant.now().plusSeconds(authToken.getExpiresIn());
        logger.log("Bearer token renewed. New expire time is {}", this.tokenExpiryDate.toString());
    }

    private boolean authTokenIsValid() {
        return Instant.now().compareTo(this.tokenExpiryDate) < 0;
    }

    public void close() {
        failSafeHttpClient.getClient().close();
    }

    /**
     * This function is used for finding articles for a single source
     *
     * @param publishDate     The date the article was published
     * @param publishDuration The amount of time since publishDate to look for articles
     * @param source          Name of article source (e.g. newspapers)
     * @return A list of article ids
     * @throws InfomediaConnectorException On failure to read result entity from response
     */
    public Set<String> searchArticleIdsByPublishDate(Instant publishDate, Duration publishDuration, String source)
            throws InfomediaConnectorException {
        return searchArticleIdsByPublishDate(publishDate, publishDuration, new HashSet<>(Collections.singletonList(source)));
    }

    /**
     * This function is used for finding articles for sources limited by an interval
     *
     * @param publishDate     The date the article was published
     * @param publishDuration The amount of time since publishDate to look for articles
     * @param sources         Name of article sources (e.g. newspapers)
     * @return A list of article ids
     * @throws InfomediaConnectorException On failure to read result entity from response
     */
    public Set<String> searchArticleIdsByPublishDate(Instant publishDate, Duration publishDuration, Set<String> sources)
            throws InfomediaConnectorException {
        final Set<String> result = new HashSet<>();
        int count = 0;

        /*
         * A thing to note about pagination: The order of the articles seems to be in random order in each request.
         * That means each page will grab the items from different lists thus not resulting in a complete list.
         * For now we just ignore that problem as a pagesize of 300 seems to be fine.
         */
        ArticleSearchResult reply;
        do {
            final ArticleSearchRequest body = new ArticleSearchRequest();
            body.setIqlQuery(String.format("sourcecode:[%s] AND publishdate:[%s..%s]",
                    String.join(",", sources), publishDate.toString(), publishDate.plus(publishDuration).toString()));
            body.setSearchRange(new SearchRange(publishDate, publishDate.plus(publishDuration)));
            body.setPagingParameter(new PagingParameter(count, this.pageSize));
            reply = postRequest(URL_INFOMEDIA_SEARCH, body, ArticleSearchResult.class);
            count += this.pageSize;
            result.addAll(reply.getArticleIds());
        } while (count < reply.getNumFound());

        return result;
    }

    /**
     * This function is used for getting full article data from Informedia
     *
     * @param articleIds The list of article ids
     * @return List of articles with full data from Infomedia
     * @throws InfomediaConnectorException On failure to read result entity from response
     */
    public ArticleList getArticles(Set<String> articleIds) throws InfomediaConnectorException {
        // Infomedia returns a different DTO when request is an empty list. To avoid that situation we simple return an
        // empty list if the articleIds is empty
        if (articleIds == null || articleIds.size() == 0) {
            ArticleList result = new ArticleList();
            result.setArticles(new ArrayList<>());

            return result;
        }

        final String body = "[\"" + String.join("\",\"", articleIds) + "\"]";

        return postRequest(URL_INFOMEDIA_FETCH, body, ArticleList.class);
    }

    private <S, T> T postRequest(String path, S data, Class<T> returnType) throws InfomediaConnectorException {
        authenticate(); // Make sure we have a token
        logger.log("POST {} with data {}", path, data);
        final Stopwatch stopwatch = new Stopwatch();
        try {
            final HttpPost httpPost = new HttpPost(failSafeHttpClient)
                    .withBaseUrl(baseUrl)
                    .withPathElements(path)
                    .withJsonData(data)
                    .withHeader("Accept", "application/json")
                    .withHeader("Content-type", "application/json")
                    .withHeader("Authorization", "bearer " + this.bearerToken);
            final Response response = httpPost.execute();
            assertResponseStatus(response, Response.Status.OK);
            return readResponseEntity(response, returnType);
        } finally {
            logger.log("POST {} took {} milliseconds", path,
                    stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));
        }
    }

    private <T> T readResponseEntity(Response response, Class<T> type)
            throws InfomediaConnectorException {
        final T entity = response.readEntity(type);
        if (entity == null) {
            throw new InfomediaConnectorException(
                    String.format("infomedia service returned with null-valued %s entity",
                            type.getName()));
        }
        return entity;
    }

    private void assertResponseStatus(Response response, Response.Status expectedStatus)
            throws InfomediaConnectorUnexpectedStatusCodeException {
        final Response.Status actualStatus =
                Response.Status.fromStatusCode(response.getStatus());
        if (actualStatus != expectedStatus) {
            throw new InfomediaConnectorUnexpectedStatusCodeException(
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
