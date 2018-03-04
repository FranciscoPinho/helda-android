package com.organon.helda.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.organon.helda.utils.Utils.convertStreamToString;
import static org.junit.Assert.*;

/**
 * Created by marga on 3/4/2018.
 */
public class UtilsTest {
    @Test
    public void convertStreamToStringTest() throws Exception {
        String str = "flower";
        InputStream inputStream = new ByteArrayInputStream( str.getBytes(StandardCharsets.UTF_8) );
        String result = convertStreamToString(inputStream);
        assertEquals(result, str);
        }


}