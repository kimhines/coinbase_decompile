package com.coinbase.android.paymentmethods;

import android.view.View;
import android.view.View.OnClickListener;
import com.coinbase.v2.models.paymentMethods.Data;

final /* synthetic */ class ConnectedAccountsLayout$$Lambda$3 implements OnClickListener {
    private final ConnectedAccountsLayout arg$1;
    private final Data arg$2;

    private ConnectedAccountsLayout$$Lambda$3(ConnectedAccountsLayout connectedAccountsLayout, Data data) {
        this.arg$1 = connectedAccountsLayout;
        this.arg$2 = data;
    }

    public static OnClickListener lambdaFactory$(ConnectedAccountsLayout connectedAccountsLayout, Data data) {
        return new ConnectedAccountsLayout$$Lambda$3(connectedAccountsLayout, data);
    }

    public void onClick(View view) {
        this.arg$1.mPresenter.onConfirmRemoveConfirmationCicked(this.arg$2);
    }
}
