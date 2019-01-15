# Infomedia Connector

JAR library for retrieving article data from Infomedia.

###Usage

In pom.xml add this dependency:

    <groupId>dk.dbc</groupId>
    <artifactId>infomedia-connector</artifactId>
    <version>1.0-SNAPSHOT</version>

In your EJB add the following inject:

    @Inject
    private InfomediaConnectorFactory infomedia;

You must have the following environment variables in your deployment:

    INFOMEDIA_URL
    INFOMEDIA_USERNAME
    INFOMEDIA_PASSWORD

To get all articles since midnight for Politiken:

            Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
            Set<String> ids = connector.searchArticleIds(today, today, today, "pol");
            ArticleList articles =  connector.getArticles(ids); 


####Caution! 
Pagination doesn't work properly as the order to articles is random.

As long as you are getting articles for one newspaper for one day then it should work just fine. However if you perform a search which has more than 300 hits then you might be missing some result.

The best workaround is to either increase the page size from 300 to something else (setPageSize) or limit your search query to a smaller interval.




  
  
