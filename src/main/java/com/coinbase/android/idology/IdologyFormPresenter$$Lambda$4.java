package com.coinbase.android.idology;

import android.util.Pair;
import rx.functions.Action1;

final /* synthetic */ class IdologyFormPresenter$$Lambda$4 implements Action1 {
    private final IdologyFormPresenter arg$1;

    private IdologyFormPresenter$$Lambda$4(IdologyFormPresenter idologyFormPresenter) {
        this.arg$1 = idologyFormPresenter;
    }

    public static Action1 lambdaFactory$(IdologyFormPresenter idologyFormPresenter) {
        return new IdologyFormPresenter$$Lambda$4(idologyFormPresenter);
    }

    public void call(Object obj) {
        IdologyFormPresenter.lambda$onShow$3(this.arg$1, (Pair) obj);
    }
}
