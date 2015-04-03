package com.percyvega.util;

import com.percyvega.model.Carrier;
import com.percyvega.model.CarrierInquiry;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pevega on 3/27/2015.
 */
public class JacksonUtilTest {

    private static String MDN;
    private static CarrierInquiry carrierInquiry;
    private static String txJson;

    @BeforeClass
    public static void setUp() throws Exception {
        MDN = "9547325664";
        carrierInquiry = new CarrierInquiry(MDN, Carrier.ATT);
        txJson = "{\"objid\":143,\"mdn\":\"" + MDN + "\",\"carrierName\":\"ATT\",\"orderType\":\"I\",\"status\":\"PICKED_UP\",\"tryCount\":30,\"creationDate\":1427310044000,\"updateDate\":1427310063142,\"response\":null}";
    }

    @Test
    public void testToJson() throws Exception {
        String json = JacksonUtil.toJson(carrierInquiry);
        assertNotNull(json);
    }

    @Test
    public void testFromJson() throws Exception {
        CarrierInquiry carrierInquiry = JacksonUtil.fromJson(txJson, CarrierInquiry.class);
        assertEquals("Successful", MDN, carrierInquiry.getMdn());
    }
}