package com.percyvega.util;

import com.percyvega.model.Carrier;
import com.percyvega.model.IntergateTransaction;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pevega on 3/27/2015.
 */
public class JacksonUtilTest {

    private static String MDN;
    private static IntergateTransaction intergateTransaction;
    private static String txJson;

    @BeforeClass
    public static void setUp() throws Exception {
        MDN = "9547325664";
        intergateTransaction = new IntergateTransaction(MDN, Carrier.ATT);
        txJson = "{\"objid\":143,\"mdn\":\"" + MDN + "\",\"carrierName\":\"ATT\",\"orderType\":\"I\",\"status\":\"PICKED_UP\",\"tryCount\":30,\"creationDate\":1427310044000,\"updateDate\":1427310063142,\"response\":null}";
    }

    @Test
    public void testToJson() throws Exception {
        String json = JacksonUtil.fromTransactionToJson(intergateTransaction);
        assertNotNull(json);
    }

    @Test
    public void testFromJson() throws Exception {
        IntergateTransaction intergateTransaction = JacksonUtil.fromJsonToTransaction(txJson);
        assertEquals("Successful", MDN, intergateTransaction.getMdn());
    }
}