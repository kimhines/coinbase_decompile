package com.coinbase.android.deposits.fiat;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import com.coinbase.android.ControllerScope;
import com.coinbase.android.R;
import com.coinbase.android.confirmation.ConfirmationDetailListAdapter.ConfirmationDetail;
import com.coinbase.android.confirmation.ConfirmationPresenter;
import com.coinbase.android.confirmation.ItemType;
import com.coinbase.android.ui.MainScheduler;
import com.coinbase.android.ui.SnackBarWrapper;
import com.coinbase.android.utils.PaymentMethodUtils;
import com.coinbase.android.utils.TransferUtils;
import com.coinbase.android.utils.Utils;
import com.coinbase.android.utils.analytics.MixpanelTracking;
import com.coinbase.api.LoginManager;
import com.coinbase.v2.models.account.Data;
import com.coinbase.v2.models.transfers.Transfer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Response;
import rx.Scheduler;
import rx.subscriptions.CompositeSubscription;

@ControllerScope
public class DepositConfirmationPresenter implements ConfirmationPresenter {
    private static final String AVAILABLE_DATE_FORMAT = "MMMM dd, yyyy";
    private static final String FALLBACK_FEE_HELP_URL = "https://support.coinbase.com/customer/en/portal/articles/2109597-buy-sell-bank-transfer-fees";
    private Data mAccount;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private final Context mContext;
    private final DepositFiatConfirmationRouter mDepositFiatConfirmationRouter;
    private final DepositFiatConfirmationScreen mDepositFiatConfirmationScreen;
    private CompositeSubscription mDepositFundsSubscription = new CompositeSubscription();
    private final List<ConfirmationDetail> mDetailList = new ArrayList();
    private final LoginManager mLoginManager;
    private final Scheduler mMainScheduler;
    private final MixpanelTracking mMixpanelTracking;
    private com.coinbase.v2.models.paymentMethods.Data mPaymentMethod;
    private List<com.coinbase.v2.models.paymentMethods.Data> mPaymentMethodList;
    private final PaymentMethodUtils mPaymentMethodUtils;
    private com.coinbase.v2.models.transfers.Data mPendingTransferData;
    private final SnackBarWrapper mSnackBarWrapper;
    private final TransferUtils mTransferUtils;

    @Inject
    DepositConfirmationPresenter(Application application, DepositFiatConfirmationRouter depositFiatConfirmationRouter, DepositFiatConfirmationScreen depositFiatConfirmationScreen, LoginManager loginManager, PaymentMethodUtils paymentMethodUtils, TransferUtils transferUtils, MixpanelTracking mixpanelTracking, SnackBarWrapper snackBarWrapper, @MainScheduler Scheduler mainScheduler) {
        this.mContext = application;
        this.mDepositFiatConfirmationRouter = depositFiatConfirmationRouter;
        this.mDepositFiatConfirmationScreen = depositFiatConfirmationScreen;
        this.mLoginManager = loginManager;
        this.mPaymentMethodUtils = paymentMethodUtils;
        this.mTransferUtils = transferUtils;
        this.mMixpanelTracking = mixpanelTracking;
        this.mSnackBarWrapper = snackBarWrapper;
        this.mMainScheduler = mainScheduler;
    }

    void onShow(Bundle args) {
        Gson gson = new Gson();
        this.mPendingTransferData = (com.coinbase.v2.models.transfers.Data) gson.fromJson(args.getString(DepositFiatRouter.TRANSFER), com.coinbase.v2.models.transfers.Data.class);
        this.mAccount = (Data) gson.fromJson(args.getString("selected_account"), Data.class);
        if (this.mPendingTransferData == null || this.mAccount == null) {
            this.mSnackBarWrapper.showGenericError();
            return;
        }
        this.mPaymentMethod = (com.coinbase.v2.models.paymentMethods.Data) gson.fromJson(args.getString(DepositFiatRouter.PAYMENT_METHOD), com.coinbase.v2.models.paymentMethods.Data.class);
        String paymentMethodString = args.getString(DepositFiatRouter.VALID_PAYMENT_METHODS);
        if (paymentMethodString != null) {
            this.mPaymentMethodList = (List) new Gson().fromJson(paymentMethodString, new TypeToken<ArrayList<com.coinbase.v2.models.paymentMethods.Data>>() {
            }.getType());
        }
        createDetailList();
        this.mDepositFiatConfirmationScreen.setDepositAmount(this.mTransferUtils.getCurrencyAmountString(this.mPendingTransferData));
        this.mDepositFiatConfirmationScreen.setDetailList(getDetailList());
        this.mDepositFiatConfirmationScreen.setButtonText(getTitle());
        if (PaymentMethodUtils.shouldShowWorldPayInfo(this.mPaymentMethod)) {
            this.mDepositFiatConfirmationScreen.showWorldPayInfo();
        } else {
            this.mDepositFiatConfirmationScreen.hideWorldPayInfo();
        }
    }

    void onHide() {
        this.mCompositeSubscription.clear();
        this.mDepositFundsSubscription.clear();
    }

    public String getTitle() {
        return this.mContext.getString(R.string.fiat_deposit_title);
    }

    public void onConfirmClicked() {
        this.mDepositFiatConfirmationScreen.disableConfirmButton();
        this.mMixpanelTracking.trackEvent(MixpanelTracking.EVENT_DEPOSIT_TAPPED_CONFIRM, MixpanelTracking.PROPERTY_PAYMENT_METHOD_TYPE, this.mPaymentMethod.getType().toString(), "currency", this.mPendingTransferData.getAmount().getCurrency());
        commitDeposit();
    }

    private void commitDeposit() {
        if (this.mPendingTransferData == null || this.mAccount == null || this.mPendingTransferData.getAmount() == null) {
            this.mDepositFiatConfirmationRouter.routeToError(null, this.mContext.getString(R.string.error_occurred_try_again));
            return;
        }
        this.mDepositFiatConfirmationScreen.showProgressDialog(this.mContext.getString(R.string.deposit_progress));
        this.mDepositFundsSubscription.clear();
        this.mDepositFundsSubscription.add(this.mLoginManager.getClient().commitDepositRx(this.mAccount.getId(), this.mPendingTransferData.getId()).onBackpressureLatest().observeOn(this.mMainScheduler).subscribe(DepositConfirmationPresenter$$Lambda$1.lambdaFactory$(this), DepositConfirmationPresenter$$Lambda$2.lambdaFactory$(this)));
    }

    static /* synthetic */ void lambda$commitDeposit$0(DepositConfirmationPresenter this_, Pair responseRetrofitPair) {
        this_.mDepositFiatConfirmationScreen.hideProgressDialog();
        Response response = responseRetrofitPair.first;
        if (!response.isSuccessful() || ((Transfer) response.body()).getData() == null) {
            this_.mDepositFiatConfirmationRouter.routeToError(null, Utils.getErrorMessage(response));
            this_.mDepositFiatConfirmationScreen.enableConfirmButton();
            return;
        }
        this_.mDepositFiatConfirmationRouter.routeToDepositSuccess(((Transfer) response.body()).getData(), this_.mAccount);
    }

    static /* synthetic */ void lambda$commitDeposit$1(DepositConfirmationPresenter this_, Throwable throwable) {
        this_.mDepositFiatConfirmationScreen.hideProgressDialog();
        this_.mDepositFiatConfirmationScreen.enableConfirmButton();
        this_.mDepositFiatConfirmationRouter.routeToError(null, Utils.getMessage(this_.mContext, throwable));
    }

    public List<ConfirmationDetail> getDetailList() {
        return this.mDetailList;
    }

    private List<ConfirmationDetail> createDetailList() {
        this.mDetailList.clear();
        this.mDetailList.add(new ConfirmationDetail(this.mContext.getString(R.string.confirm_available_header), getAvailableMessage(), ItemType.DETAIL));
        this.mDetailList.add(new ConfirmationDetail(this.mContext.getString(R.string.fiat_deposit_confirm_payment_method_header), this.mPaymentMethodUtils.getShortenedName(this.mPaymentMethod), ItemType.DETAIL));
        if (this.mPendingTransferData.getFee() != null) {
            this.mDetailList.add(new ConfirmationDetail(this.mContext.getString(R.string.confirm_coinbase_fee_header), this.mTransferUtils.getTotalFeeString(this.mPendingTransferData), ItemType.FEE));
        }
        if (this.mPendingTransferData.getPaymentMethodFee() != null) {
            this.mDetailList.add(new ConfirmationDetail(this.mContext.getString(R.string.confirm_payment_method_fee_header), this.mTransferUtils.getPaymentMethodFeeString(this.mPendingTransferData), ItemType.FEE));
        }
        this.mDetailList.add(new ConfirmationDetail(this.mContext.getString(R.string.confirm_credit_total_header), this.mTransferUtils.getSubtotalAmount(this.mPendingTransferData), ItemType.TOTAL));
        return this.mDetailList;
    }

    private String getAvailableMessage() {
        if (this.mTransferUtils.isInstant(this.mPendingTransferData)) {
            return this.mContext.getString(R.string.confirm_available_instantly);
        }
        return Utils.getDateTimeFrom(this.mPendingTransferData.getPayoutAt()).toString(AVAILABLE_DATE_FORMAT);
    }

    public void onFeeHelpClicked() {
        this.mDepositFiatConfirmationScreen.displayFeeHelpUrl(this.mPendingTransferData.getFeeExplanationUrl() != null ? this.mPendingTransferData.getFeeExplanationUrl() : "https://support.coinbase.com/customer/en/portal/articles/2109597-buy-sell-bank-transfer-fees");
    }

    public void onPaymentMethodClicked() {
        if (this.mPaymentMethodList == null) {
            this.mSnackBarWrapper.showGenericError();
        } else {
            this.mDepositFiatConfirmationRouter.routeToLinkedAccountsPicker(this.mPaymentMethodList);
        }
    }
}
