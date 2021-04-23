package dk.dbc.infomedia;

public class InfomediaConnectorTestWireMockRecorder {
    
        /*
        Steps to reproduce wiremock recording:

        * Start standalone runner
            java -jar wiremock-standalone-{WIRE_MOCK_VERSION}.jar --proxy-all="{INFOMEDIA_BASE_URL}" --record-mappings --verbose

        * Run the main method of this class

        * Replace content of src/test/resources/{__files|mappings} with that produced by the standalone runner
     */

    public static void main(String[] args) throws InfomediaConnectorException {
        InfomediaConnectorTest.connector = new InfomediaConnector(
                InfomediaConnectorTest.CLIENT, "http://localhost:8080", "username", "password");
        final InfomediaConnectorTest InfomediaConnectorTest = new InfomediaConnectorTest();
        allTests(InfomediaConnectorTest);
    }

    private static void allTests(InfomediaConnectorTest connectorTest)
            throws InfomediaConnectorException {
        connectorTest.callSearchArticlesNothingFound();
        connectorTest.callSearchArticlesSingleNewsPaper();
        connectorTest.callSearchArticlesDualNewsPaper();
        connectorTest.callSearchArticlesOnlinePublication();
    }
    
}
