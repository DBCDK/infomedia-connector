/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import com.github.tomakehurst.wiremock.WireMockServer;
import dk.dbc.httpclient.HttpClient;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class InfomediaConnectorTest {
    private static WireMockServer wireMockServer;
    private static String wireMockHost;
    private static Duration oneDay = Duration.ofHours(23).plusMinutes(59).plusSeconds(59);

    final static Client CLIENT = HttpClient.newClient(new ClientConfig()
            .register(new JacksonFeature()));
    static InfomediaConnector connector;

    private final static Instant theDate = Instant.parse("2019-01-13T00:00:00Z");

    @BeforeAll
    static void startWireMockServer() {
        wireMockServer = new WireMockServer(options().dynamicPort()
                .dynamicHttpsPort());
        wireMockServer.start();
        wireMockHost = "http://localhost:" + wireMockServer.port();
        configureFor("localhost", wireMockServer.port());
    }

    @BeforeAll
    static void setConnector() {
        connector = new InfomediaConnector(CLIENT, wireMockHost, InfomediaConnector.TimingLogLevel.INFO, "username", "password");
    }

    @AfterAll
    static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    void callSearchArticlesNothingFound() throws InfomediaConnectorException {
        Set<String> articleIds = connector.searchArticleIdsByPublishDate(theDate, oneDay, "notfound");
        assertThat(articleIds.size(), is(0));
    }

    @Test
    void callSearchArticlesSingleNewsPaper() throws InfomediaConnectorException {
        Set<String> articleIds = connector.searchArticleIdsByPublishDate(theDate, oneDay, "pol");
        assertThat(articleIds.size(), is(2));
        assertThat(articleIds.contains("e70a7343"), is(true));
        assertThat(articleIds.contains("e70a7334"), is(true));

        ArticleList articles = connector.getArticles(articleIds);

        // Note that this doesn't exactly reflect the real world as in the real world the order is not predictable
        Article articleActual1 = articles.getArticles().get(0);
        assertThat(articleActual1.getArticleId(), is("e70a7343"));
        assertThat(articleActual1.getArticleUrl(), is("https://mediaresearchapi.infomedia.dk/api/v1/article?id=e70a7343"));
        assertThat(articleActual1.getAuthors(), is(Arrays.asList()));
        assertThat(articleActual1.getBodyText(), is("Pol BodyText 1 Full"));
        assertThat(articleActual1.getCaptions(), is(Arrays.asList()));
        assertThat(articleActual1.getHeading(), is("Pol Heading 1 Full"));
        assertThat(articleActual1.getLead(), is(""));
        assertThat(articleActual1.getPageIds(), is(Arrays.asList("7")));
        assertThat(articleActual1.getParagraph(), is("Pol Paragraph 1 Full"));
        assertThat(articleActual1.getPublishDate(), is("2019-01-13T00:00:00Z"));
        assertThat(articleActual1.getSection().getId(), is("1"));
        assertThat(articleActual1.getSection().getName(), is(""));
        assertThat(articleActual1.getSource(), is("Politiken"));
        assertThat(articleActual1.getSubHeading(), is(""));
        assertThat(articleActual1.getWordCount(), is(46));

        Article articleActual2 = articles.getArticles().get(1);
        assertThat(articleActual2.getArticleId(), is("e70a7334"));
        assertThat(articleActual2.getArticleUrl(), is("https://mediaresearchapi.infomedia.dk/api/v1/article?id=e70a7334"));
        assertThat(articleActual2.getAuthors(), is(Arrays.asList()));
        assertThat(articleActual2.getBodyText(), is("Pol BodyText 2 Full"));
        assertThat(articleActual2.getCaptions(), is(Arrays.asList()));
        assertThat(articleActual2.getHeading(), is("Pol Heading 2 Full"));
        assertThat(articleActual2.getLead(), is(""));
        assertThat(articleActual2.getPageIds(), is(Arrays.asList("1")));
        assertThat(articleActual2.getParagraph(), is("Pol Paragraph 2 Full"));
        assertThat(articleActual2.getPublishDate(), is("2019-01-13T00:00:00Z"));
        assertThat(articleActual2.getSection().getId(), is("1"));
        assertThat(articleActual2.getSection().getName(), is(""));
        assertThat(articleActual2.getSource(), is("Politiken"));
        assertThat(articleActual2.getSubHeading(), is(""));
        assertThat(articleActual2.getWordCount(), is(2));
    }

    @Test
    void callSearchArticlesDualNewsPaper() throws InfomediaConnectorException {
        Set<String> sources = new HashSet<>();
        sources.add("pol");
        sources.add("bma");
        Set<String> articleIds = connector.searchArticleIdsByPublishDate(theDate, oneDay, sources);
        assertThat(articleIds.size(), is(2));
        assertThat(articleIds.contains("e70a740d"), is(true));
        assertThat(articleIds.contains("e70a695c"), is(true));

        ArticleList articles = connector.getArticles(articleIds);

        // Note that this doesn't exactly reflect the real world as in the real world the order is not predictable
        Article articleActual1 = articles.getArticles().get(0);
        assertThat(articleActual1.getArticleId(), is("e70a740d"));
        assertThat(articleActual1.getArticleUrl(), is("https://mediaresearchapi.infomedia.dk/api/v1/article?id=e70a740d"));
        assertThat(articleActual1.getAuthors(), is(Arrays.asList()));
        assertThat(articleActual1.getBodyText(), is("Ber BodyText 1 Full"));
        assertThat(articleActual1.getCaptions(), is(Arrays.asList()));
        assertThat(articleActual1.getHeading(), is("Ber Heading 1 Full"));
        assertThat(articleActual1.getLead(), is("Ber Lead 1 Full"));
        assertThat(articleActual1.getPageIds(), is(Arrays.asList("19")));
        assertThat(articleActual1.getParagraph(), is("Ber Paragraph 1 Full"));
        assertThat(articleActual1.getPublishDate(), is("2019-01-13T00:00:00Z"));
        assertThat(articleActual1.getSection().getId(), is("1"));
        assertThat(articleActual1.getSection().getName(), is(""));
        assertThat(articleActual1.getSource(), is("Berlingske"));
        assertThat(articleActual1.getSubHeading(), is(""));
        assertThat(articleActual1.getWordCount(), is(85));

        Article articleActual2 = articles.getArticles().get(1);
        assertThat(articleActual2.getArticleId(), is("e70a695c"));
        assertThat(articleActual2.getArticleUrl(), is("https://mediaresearchapi.infomedia.dk/api/v1/article?id=e70a695c"));
        assertThat(articleActual2.getAuthors(), is(Arrays.asList(",Sune Højrup Bencke")));
        assertThat(articleActual2.getBodyText(), is("Pol BodyText 3 Full"));
        assertThat(articleActual2.getCaptions(), is(Arrays.asList()));
        assertThat(articleActual2.getHeading(), is("Pol Heading 3 Full"));
        assertThat(articleActual2.getLead(), is(""));
        assertThat(articleActual2.getPageIds(), is(Arrays.asList("10")));
        assertThat(articleActual2.getParagraph(), is("Pol Paragraph 3 Full"));
        assertThat(articleActual2.getPublishDate(), is("2019-01-13T00:00:00Z"));
        assertThat(articleActual2.getSection().getId(), is("5"));
        assertThat(articleActual2.getSection().getName(), is("Rejser"));
        assertThat(articleActual2.getSource(), is("Politiken"));
        assertThat(articleActual2.getSubHeading(), is("Pol SubHeading 3 Full"));
        assertThat(articleActual2.getWordCount(), is(872));
    }

    @Test
    void callSearchArticlesOnlinePublication() throws InfomediaConnectorException {
        Set<String> sources = new HashSet<>();
        sources.add("4f1");
        Set<String> articleIds = connector.searchArticleIdsByPublishDate(theDate, oneDay, sources);
        assertThat(articleIds.size(), is(2));
        assertThat(articleIds.contains("e70a806a"), is(true));
        assertThat(articleIds.contains("e70a8818"), is(true));
    }

    @Test
    void callGetArticlesEmptyList() throws InfomediaConnectorException {
        assertThat(connector.getArticles(new HashSet<>()).getArticles().size(), is(0));
        assertThat(connector.getArticles(null).getArticles().size(), is(0));
    }

}
