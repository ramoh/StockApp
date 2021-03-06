package com.rajesh;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Stock server which returns a Observable to listen to
 */

class StockServer {

    static Observable<StockInfo> getFeed(final StockInfo stock) {
        return Observable.create((Subscriber<? super StockInfo> subscriber) -> {

            try {

                while (!subscriber.isUnsubscribed()) {
                    TimeUnit.SECONDS.sleep(3);
                    subscriber.onNext(stock.fetch());
                }
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        }).doOnSubscribe(() -> System.out.println("subscribed " + stock.ticker))
                .doOnUnsubscribe(() -> System.out.println("Unsubscribed " + stock.ticker))
                .subscribeOn(Schedulers.io()).distinctUntilChanged(stockInfo -> stockInfo.currentPrice);
    }

    static Observable<BigDecimal> getCurrentTotalPrice(final List<StockInfo> stocks) {
        return Observable.create((Subscriber<? super BigDecimal> subscriber) -> {
            try {
                while (!subscriber.isUnsubscribed()) {
                    TimeUnit.SECONDS.sleep(3);
                    BigDecimal currentTotalPrice = stocks.stream()
                            .map(StockInfo::fetch)
                            .map(stockInfo -> stockInfo.currentPrice.multiply(BigDecimal.valueOf(stockInfo.quantity)))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    subscriber.onNext(currentTotalPrice);
                }
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        }).subscribeOn(Schedulers.io())
                .distinctUntilChanged();
    }


}
