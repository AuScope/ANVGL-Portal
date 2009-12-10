package org.auscope.portal.mineraloccurrence;

import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Collection;

import junit.framework.Assert;

/**
 * User: Mathew Wyatt
 * Date: 24/03/2009
 * Time: 10:24:29 AM
 */
public class TestMineralOccurrenceResponseHandler {
    MineralOccurrencesResponseHandler mineralOccurrencesResponseHandler = new MineralOccurrencesResponseHandler();

    @Test
    public void testHandleMineResponse() throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        File mineGetFeatureResponse = new File("src/test/resources/mineGetFeatureResponse.xml");
        BufferedReader reader = new BufferedReader( new FileReader(mineGetFeatureResponse) );
        StringBuffer mineGetFeatureResponseXML = new StringBuffer();

        String str;
        while ((str = reader.readLine()) != null) {
            mineGetFeatureResponseXML.append(str);
        }
        reader.close();

        Collection<Mine> mines = mineralOccurrencesResponseHandler.getMines(mineGetFeatureResponseXML.toString());

        Assert.assertEquals("There are 2 mines", 2, mines.size());
        Assert.assertEquals("The first one is Good Hope", "Good Hope", ((Mine)mines.toArray()[0]).getMineNamePreffered());
        Assert.assertEquals("The second one is Sons of Freedom Reef", "Sons of Freedom Reef", ((Mine)mines.toArray()[1]).getMineNamePreffered());
    }

    @Test
    public void testHandleCommodityResponse() throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        File commodityGetFeatureResponse = new File("src/test/resources/commodityGetFeatureResponse.xml");
        BufferedReader reader = new BufferedReader( new FileReader(commodityGetFeatureResponse) );
        StringBuffer commodityGetFeatureResponseXML = new StringBuffer();

        String str;
        while ((str = reader.readLine()) != null) {
            commodityGetFeatureResponseXML.append(str);
        }
        reader.close();

        Collection<Commodity> commodities = 
            mineralOccurrencesResponseHandler.getCommodities(commodityGetFeatureResponseXML.toString());

        Assert.assertEquals("There are 2 commodities", 2, commodities.size());
        Assert.assertEquals("The first one's name is Gold", "Gold", ((Commodity)commodities.toArray()[0]).getCommodityName());
        Assert.assertEquals("The second one's MineralOccurrence source is urn:cgi:feature:GSV:MineralOccurrence:361170", "urn:cgi:feature:GSV:MineralOccurrence:361170", ((Commodity)commodities.toArray()[1]).getMineralOccurrenceURI());
    }
    
    @Test
    public void testGetNumberOfFeaturesTwo() throws IOException, ParserConfigurationException, SAXException {
        File commodityGetFeatureResponse = new File("src/test/resources/commodityGetFeatureResponse.xml");
        BufferedReader reader = new BufferedReader( new FileReader(commodityGetFeatureResponse) );
        StringBuffer commodityGetFeatureResponseXML = new StringBuffer();

        String str;
        while ((str = reader.readLine()) != null) {
            commodityGetFeatureResponseXML.append(str);
        }
        reader.close();

        int numberOfFeatures =
            mineralOccurrencesResponseHandler.getNumberOfFeatures(commodityGetFeatureResponseXML.toString());
        
        Assert.assertEquals("There are 2 features", 2, numberOfFeatures);
    }

    @Test
    public void testGetNumberOfFeaturesZero() throws IOException, ParserConfigurationException, SAXException {
        File getFeatureResponse = new File("src/test/resources/mineralOccurrenceNoFeaturesResponse.xml");
        BufferedReader reader = new BufferedReader( new FileReader(getFeatureResponse) );
        StringBuffer getFeatureResponseXML = new StringBuffer();

        String str;
        while ((str = reader.readLine()) != null) {
            getFeatureResponseXML.append(str);
        }
        reader.close();

        int numberOfFeatures =
            mineralOccurrencesResponseHandler.getNumberOfFeatures(getFeatureResponseXML.toString());
        
        Assert.assertEquals("There are 0 features", 0, numberOfFeatures);
    }
}
