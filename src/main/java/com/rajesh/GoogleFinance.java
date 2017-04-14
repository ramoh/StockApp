package com.rajesh;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Fetch stock price using Google finance
 */
public class GoogleFinance {


    public static BigDecimal getPrice(final String ticker, final String exchange) {

        try {
            final URL url = new URL("http://www.google.com/finance/info?q=" + exchange + ":" + ticker);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = reader.lines().collect(Collectors.joining());
            s = s.substring(2);//strip of first two forward slash

            JsonReader reader1 = Json.createReader(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
            JsonArray array = reader1.readArray();
            String price = array.getJsonObject(0).getString("l");
            return BigDecimal.valueOf(NumberFormat.getInstance().parse(price).doubleValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
