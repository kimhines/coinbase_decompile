package com.coinbase.android.pricechart;

import rx.functions.Action1;

final /* synthetic */ class PriceChartPresenter$$Lambda$6 implements Action1 {
    private final PriceChartPresenter arg$1;

    private PriceChartPresenter$$Lambda$6(PriceChartPresenter priceChartPresenter) {
        this.arg$1 = priceChartPresenter;
    }

    public static Action1 lambdaFactory$(PriceChartPresenter priceChartPresenter) {
        return new PriceChartPresenter$$Lambda$6(priceChartPresenter);
    }

    public void call(Object obj) {
        this.arg$1.handleGetPricesFailure();
    }
}
