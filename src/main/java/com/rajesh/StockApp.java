package com.rajesh;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stock tracker
 */
public class StockApp {

    public static void main(String[] args) throws Exception {

        List<StockInfo> myStocks = processInputFile("D:\\stock.csv");

        final BigDecimal totalHoldPrice = myStocks.stream()
                .map(stockInfo -> stockInfo.holdPrice.multiply(BigDecimal.valueOf(stockInfo.quantity)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Observable<StockInfo>> infos = myStocks.stream().map(StockServer::getFeed).collect(Collectors.toList());

        System.out.println(infos.size());
        final Observable<BigDecimal> finalPriceFeed = StockServer.getCurrentTotalPrice(myStocks);
        AppFeed stockAppFeed = new AppFeed("Stock Feed");
        stockAppFeed.update("Initializing the app", true);


        finalPriceFeed.subscribeOn(Schedulers.newThread())
                .distinctUntilChanged()
                .subscribe(
                        price -> stockAppFeed.updateTitle(String.format("HP : %.2f CP: %.2f", totalHoldPrice, price), price.compareTo(totalHoldPrice) > 0)
                        , ex -> stockAppFeed.updateTitle(ex.getMessage(), false));
        infos.forEach(feed -> {
            feed.subscribeOn(Schedulers.io()).distinctUntilChanged(stockInfo -> stockInfo.currentPrice)
                    . subscribe(s ->
                            stockAppFeed.update(s.toString() + "\n", s.isProfit())
                    , ex ->
                            stockAppFeed.update(ex.getMessage() + "\n", false)
            );
        });
    }


    private static List<StockInfo> processInputFile(final String location) {

        Function<String, StockInfo> converter = (String stockStr) -> {
            try {
                final String tokens[] = stockStr.split(",");
                final Long quant = Long.valueOf(tokens[2]);
                final BigDecimal holdPrice = new BigDecimal(tokens[3]);
                return new StockInfo(tokens[0], tokens[1], quant, holdPrice, BigDecimal.ZERO);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };

        List<StockInfo> list = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(location))) {

            list = lines.skip(1)
                    .filter(s -> !s.startsWith("#"))
                    .map(converter)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

    }
}
