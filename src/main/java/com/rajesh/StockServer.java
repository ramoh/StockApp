package com.rajesh;

import rx.Observable;
import rx.Subscriber;

import java.math.BigDecimal;
import java.util.List;

/**
 *  Stockserver which returns a Observable to listen to
 */

public class StockServer {

    public static Observable<StockInfo> getFeed(List<StockInfo> stocks) {
        return Observable.unsafeCreate((Subscriber<? super StockInfo> subscriber) -> {

            try {
                while (!subscriber.isUnsubscribed()) {
                    stocks.stream()
                            .map(StockInfo::fetch)
                            .forEach(subscriber::onNext);


                }
            } catch (Exception ex) {
                subscriber.onError(ex);
            }

        });
    }

    public static Observable<BigDecimal> getCurrentTotalPrice(List<StockInfo> stocks) {
        return Observable.unsafeCreate(subscriber -> {

            try {
                while (!subscriber.isUnsubscribed()) {
                    BigDecimal currentTotalPrice = stocks.stream()
                            .map(StockInfo::fetch)
                            .map(stockInfo -> stockInfo.currentPrice.multiply(BigDecimal.valueOf(stockInfo.quantity)))
                            .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
                    subscriber.onNext(currentTotalPrice);
                }
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }


}
