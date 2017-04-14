package com.rajesh;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * Holds the information about stock
 */
public class StockInfo {

    public final String exchange;
    public final String ticker;
    public final BigDecimal currentPrice;
    public final Long quantity;
    public final BigDecimal holdPrice;

    public StockInfo(final String ticker,final String exchange, final Long quantity, final BigDecimal holdPrice, final BigDecimal currentPrice) {
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        this.quantity = quantity;
        this.holdPrice = holdPrice;
        this.exchange=exchange;
    }

    @Override
    public String toString() {
        return String.format("%-10s(%d):%-8.2f<-%8.2f(%.2f%%  Profit:%8.2f)", ticker,quantity, currentPrice, holdPrice, getPercentage(), profit());

    }


    private BigDecimal profit() {
        return this.currentPrice.multiply(BigDecimal.valueOf(this.quantity)).subtract(this.holdPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    private BigDecimal getPercentage() {

        return this.currentPrice.subtract(this.holdPrice).divide(this.holdPrice, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

    }

    public StockInfo fetch() {
           return new StockInfo(this.ticker,this.exchange, this.quantity, this.holdPrice, GoogleFinance.getPrice(this.ticker,this.exchange));
    }

    public boolean isProfit() {

        return this.currentPrice.compareTo(this.holdPrice) > 0;
    }


}
