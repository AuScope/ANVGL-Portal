package org.auscope.portal.mineraloccurrence;

import org.auscope.portal.server.web.service.HttpServiceCaller;
import org.auscope.portal.server.web.service.MineralOccurrenceService;
import org.auscope.portal.server.web.IWFSGetFeatureMethodMaker;
import org.junit.Before;
import org.junit.Test;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;

import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: Mathew Wyatt
 * Date: Jun 4, 2009
 * Time: 11:41:09 AM
 */
public class TestMineralOccurrenceServiceClient {
    private MineralOccurrenceService mineralOccurrenceService;
    private HttpServiceCaller httpServiceCaller;
    private MineralOccurrencesResponseHandler mineralOccurrencesResponseHandler;
    private HttpClient mockHttpClient;

    private IWFSGetFeatureMethodMaker methodMaker;

    private Mockery context = new Mockery(){{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};


    @Before
    public void setup() {
        this.methodMaker = context.mock(IWFSGetFeatureMethodMaker.class);
        this.mineralOccurrencesResponseHandler = context.mock(MineralOccurrencesResponseHandler.class);
        this.httpServiceCaller = context.mock(HttpServiceCaller.class);
        this.mineralOccurrenceService = new MineralOccurrenceService(this.httpServiceCaller, this.mineralOccurrencesResponseHandler, this.methodMaker);
        this.mockHttpClient = context.mock(HttpClient.class);
    }

    /**
     * The service client ties various different classes togather, so we are not testing its data integrity,
     * rather we are testing that it utilises the other classes properly and send us back the right return object
     *
     * @throws Exception
     */
    @Test
    public void testGetAllMines() throws Exception {
        final String serviceURL = "http://localhost?";

        final GetMethod mockMethod = context.mock(GetMethod.class);
        final String mockMineResponse = new String();
        final List<Mine> mockMines = context.mock(List.class);

        context.checking(new Expectations() {{
            oneOf (methodMaker).makeMethod(serviceURL, "er:Mine", ""); will(returnValue(mockMethod));
            oneOf (httpServiceCaller).getHttpClient();will(returnValue(mockHttpClient));
            oneOf (httpServiceCaller).getMethodResponseAsString(mockMethod, mockHttpClient); will(returnValue(mockMineResponse));
            oneOf (mineralOccurrencesResponseHandler).getMines(mockMineResponse); will(returnValue(mockMines));
        }});

        List<Mine> mines = this.mineralOccurrenceService.getAllMines(serviceURL);
        Assert.assertEquals(mockMines, mines);
    }

    /**
     * The service client ties various different classes togather, so we are not testing its data integrity,
     * rather we are testing that it utilises the other classes properly and send us back the right return object
     *
     * @throws Exception
     */
    @Test
    public void getMineWithSpecifiedName() throws Exception {
        final String serviceURL = "http://localhost?";
        final String mineName = "SomeName";

        final MineFilter mineFilter = new MineFilter(mineName);
        final GetMethod mockMethod = context.mock(GetMethod.class);
        final String mockMineResponse = new String();
        final List<Mine> mockMines = context.mock(List.class);

        context.checking(new Expectations() {{
            oneOf (methodMaker).makeMethod(serviceURL, "er:Mine", mineFilter.getFilterString()); will(returnValue(mockMethod));
            oneOf (httpServiceCaller).getHttpClient();will(returnValue(mockHttpClient));
            oneOf (httpServiceCaller).getMethodResponseAsString(mockMethod, mockHttpClient); will(returnValue(mockMineResponse));
            oneOf (mineralOccurrencesResponseHandler).getMines(mockMineResponse); will(returnValue(mockMines));
        }});

        List<Mine> mines = this.mineralOccurrenceService.getMineWithSpecifiedName(serviceURL, mineName);
        Assert.assertEquals(mockMines, mines);
    }

    /**
     * Test the event that we dont provide a name or group
     *
     * @throws Exception
     */
    @Test
    public void testGetCommodityNoNameOrGroup() throws Exception {
        final String serviceURL = "http://localhost?";
        final String commodityName = "";
        final String commodityGroup = "";

        final GetMethod mockMethod = context.mock(GetMethod.class);
        final String mockCommodityResponse = new String();
        final Collection<Mine> mockCommodities = (Collection<Mine>)context.mock(Collection.class);

        context.checking(new Expectations() {{
            oneOf (methodMaker).makeMethod(serviceURL, "er:Commodity", ""); will(returnValue(mockMethod));
            oneOf (httpServiceCaller).getHttpClient();will(returnValue(mockHttpClient));
            oneOf (httpServiceCaller).getMethodResponseAsString(mockMethod, mockHttpClient); will(returnValue(mockCommodityResponse));
            oneOf (mineralOccurrencesResponseHandler).getCommodities(mockCommodityResponse); will(returnValue(mockCommodities));
        }});

        Collection<Commodity> commodities = this.mineralOccurrenceService.getCommodity(serviceURL, commodityName, commodityGroup);
        Assert.assertEquals(mockCommodities, commodities);
    }

    /**
     * Test the event that we provide a name
     * @throws Exception
     */
    @Test
    public void testGetCommodity() throws Exception {
        final String serviceURL = "http://localhost?";
        final String commodityName = "someName";
        final String commodityGroup = "";

        final CommodityFilter commodityFilter = new CommodityFilter(commodityGroup, commodityName);
        final GetMethod mockMethod = context.mock(GetMethod.class);
        final String mockCommodityResponse = new String();
        final Collection<Commodity> mockCommodities = (Collection<Commodity>)context.mock(Collection.class);

        context.checking(new Expectations() {{
            oneOf (methodMaker).makeMethod(serviceURL, "er:Commodity", commodityFilter.getFilterString()); will(returnValue(mockMethod));
            oneOf (httpServiceCaller).getHttpClient();will(returnValue(mockHttpClient));
            oneOf (httpServiceCaller).getMethodResponseAsString(mockMethod, mockHttpClient); will(returnValue(mockCommodityResponse));
            oneOf (mineralOccurrencesResponseHandler).getCommodities(mockCommodityResponse); will(returnValue(mockCommodities));
        }});

        Collection<Commodity> commodities = this.mineralOccurrenceService.getCommodity(serviceURL, commodityGroup, commodityName);
        Assert.assertEquals(mockCommodities, commodities);
    }

    /**
     * Test for a valid query
     * @throws Exception
     */
    @Test
    public void testGetMineralOccurrenceGML() throws Exception {
        final String serviceURL = "http://localhost?";
        final String commodityName = "someName";
        final String commodityGroup = "";
        final String measureType = "";
        final String minOreAmount = "";
        final String minOreAmountUOM = "";
        final String minCommodityAmount = "";
        final String minCommodityAmountUOM = "";
        final String cutOffGrade = "";
        final String cutOffGradeUOM = "";

        final CommodityFilter commodityFilter = new CommodityFilter(commodityGroup, commodityName);
        final GetMethod mockMethod = context.mock(GetMethod.class);
        final String mockCommodityResponse = new String();
        final Commodity mockCommodity = context.mock(Commodity.class);
        final Collection<Commodity> commodities = Arrays.asList(mockCommodity);

        context.checking(new Expectations() {{
            //this comes from my instantiation of the MineralOccurrenceFilter
            oneOf (mockCommodity).getMineralOccurrenceURI(); will(returnValue("dudURI"));
        }});

        final MineralOccurrenceFilter mineralOccurrenceFilter = new MineralOccurrenceFilter(  commodities,
                                                                                        measureType,
                                                                                        minOreAmount,
                                                                                        minOreAmountUOM,
                                                                                        minCommodityAmount,
                                                                                        minCommodityAmountUOM,
                                                                                        cutOffGrade,
                                                                                        cutOffGradeUOM);

        context.checking(new Expectations() {{
            //this the get commodities part
            oneOf (methodMaker).makeMethod(serviceURL, "er:Commodity", commodityFilter.getFilterString()); will(returnValue(mockMethod));
            oneOf (httpServiceCaller).getHttpClient();will(returnValue(mockHttpClient));
            oneOf (httpServiceCaller).getMethodResponseAsString(mockMethod, mockHttpClient); will(returnValue(mockCommodityResponse));
            oneOf (mineralOccurrencesResponseHandler).getCommodities(mockCommodityResponse); will(returnValue(commodities));

            oneOf (mockCommodity).getMineralOccurrenceURI(); will(returnValue("dudURI"));

            //the mineral occurrence query part
            oneOf (methodMaker).makeMethod(serviceURL, "er:MineralOccurrence", mineralOccurrenceFilter.getFilterString()); will(returnValue(mockMethod));
            oneOf (httpServiceCaller).getHttpClient();will(returnValue(mockHttpClient));
            oneOf (httpServiceCaller).getMethodResponseAsString(mockMethod, mockHttpClient); will(returnValue(mockCommodityResponse));
        }});

        this.mineralOccurrenceService.getMineralOccurrenceGML(serviceURL,
                                                                    commodityName,
                                                                    commodityGroup,
                                                                    measureType,
                                                                    minOreAmount,
                                                                    minCommodityAmountUOM,
                                                                    minCommodityAmount,
                                                                    minCommodityAmountUOM,
                                                                    cutOffGrade,
                                                                    cutOffGradeUOM);
    }

    /**
     * Test for the case that we dont get any results, by mimicking the getCommodity query returning no results
     */
    @Test
    public void testGetMineralOccurrenceGMLNoResults() throws Exception {
        final String serviceURL = "http://localhost?";
        final String commodityName = "someName";
        final String commodityGroup = "";
        final String measureType = "";
        final String minOreAmount = "";
        final String minOreAmountUOM = "";
        final String minCommodityAmount = "";
        final String minCommodityAmountUOM = "";
        final String cutOffGrade = "";
        final String cutOffGradeUOM = "";

        final CommodityFilter commodityFilter = new CommodityFilter(commodityGroup, commodityName);
        final GetMethod mockMethod = context.mock(GetMethod.class);
        final String mockCommodityResponse = new String();
        final Collection<Commodity> commodities = new ArrayList<Commodity>();

        context.checking(new Expectations() {{
            //this the get commodities part
            oneOf (methodMaker).makeMethod(serviceURL, "er:Commodity", commodityFilter.getFilterString()); will(returnValue(mockMethod));
            oneOf (httpServiceCaller).getHttpClient();will(returnValue(mockHttpClient));
            oneOf (httpServiceCaller).getMethodResponseAsString(mockMethod, mockHttpClient); will(returnValue(mockCommodityResponse));
            oneOf (mineralOccurrencesResponseHandler).getCommodities(mockCommodityResponse); will(returnValue(commodities));
        }});

        String returnValue = this.mineralOccurrenceService.getMineralOccurrenceGML(serviceURL,
                                                                    commodityName,
                                                                    commodityGroup,
                                                                    measureType,
                                                                    minOreAmount,
                                                                    minCommodityAmountUOM,
                                                                    minCommodityAmount,
                                                                    minCommodityAmountUOM,
                                                                    cutOffGrade,
                                                                    cutOffGradeUOM);

        Assert.assertEquals("", returnValue);
    }

    @Test
    public void testGetMiningActivity() throws Exception {
        final Mine mockMine = context.mock(Mine.class);
        final List<Mine> mockMineList = Arrays.asList(mockMine);
        final GetMethod mockMethod = context.mock(GetMethod.class);

        context.checking(new Expectations() {{
            ignoring(mockMine);
            oneOf (methodMaker).makeMethod(with(""), with("er:MiningActivity"), with(any(String.class)));will(returnValue(mockMethod));
            oneOf (httpServiceCaller).getHttpClient();will(returnValue(mockHttpClient));
            oneOf(httpServiceCaller).getMethodResponseAsString(mockMethod, mockHttpClient);
        }});

        this.mineralOccurrenceService.getMiningActivityGML("", mockMineList, "", "", "", "", "", "");

    }

    @Test
    public void testGetMiningActivityNoMines() throws Exception {
        final List<Mine> mockMineList = Arrays.asList();

        String response = this.mineralOccurrenceService.getMiningActivityGML("", mockMineList, "", "", "", "", "", "");

        //should get updateCSWRecords blank string back
        Assert.assertEquals("", response);
    }
}
