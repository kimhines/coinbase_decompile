package com.coinbase.android.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.widget.NestedScrollView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.coinbase.android.R;
import com.coinbase.android.idology.IdologyAddressFormLayout;

public class ControllerStateIdologyAddressFormBinding extends ViewDataBinding {
    private static final IncludedLayouts sIncludes = null;
    private static final SparseIntArray sViewsWithIds = new SparseIntArray();
    public final TextView btnIdologyContinue;
    public final View btnIdologyContinueDivider;
    public final IdologyAddressFormLayout idologyFormLayout;
    private long mDirtyFlags = -1;
    private final LinearLayout mboundView0;
    public final NestedScrollView nsvIdologyFormContainer;
    public final RelativeLayout progress;

    static {
        sViewsWithIds.put(R.id.progress, 1);
        sViewsWithIds.put(R.id.nsvIdologyFormContainer, 2);
        sViewsWithIds.put(R.id.idologyFormLayout, 3);
        sViewsWithIds.put(R.id.btnIdologyContinueDivider, 4);
        sViewsWithIds.put(R.id.btnIdologyContinue, 5);
    }

    public ControllerStateIdologyAddressFormBinding(DataBindingComponent bindingComponent, View root) {
        super(bindingComponent, root, 0);
        Object[] bindings = ViewDataBinding.mapBindings(bindingComponent, root, 6, sIncludes, sViewsWithIds);
        this.btnIdologyContinue = (TextView) bindings[5];
        this.btnIdologyContinueDivider = (View) bindings[4];
        this.idologyFormLayout = (IdologyAddressFormLayout) bindings[3];
        this.mboundView0 = (LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.nsvIdologyFormContainer = (NestedScrollView) bindings[2];
        this.progress = (RelativeLayout) bindings[1];
        setRootTag(root);
        invalidateAll();
    }

    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 1;
        }
        requestRebind();
    }

    public boolean hasPendingBindings() {
        synchronized (this) {
            if (this.mDirtyFlags != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean setVariable(int variableId, Object variable) {
        return false;
    }

    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        return false;
    }

    protected void executeBindings() {
        synchronized (this) {
            long dirtyFlags = this.mDirtyFlags;
            this.mDirtyFlags = 0;
        }
    }

    public static ControllerStateIdologyAddressFormBinding inflate(LayoutInflater inflater, ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
    }

    public static ControllerStateIdologyAddressFormBinding inflate(LayoutInflater inflater, ViewGroup root, boolean attachToRoot, DataBindingComponent bindingComponent) {
        return (ControllerStateIdologyAddressFormBinding) DataBindingUtil.inflate(inflater, R.layout.controller_state_idology_address_form, root, attachToRoot, bindingComponent);
    }

    public static ControllerStateIdologyAddressFormBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, DataBindingUtil.getDefaultComponent());
    }

    public static ControllerStateIdologyAddressFormBinding inflate(LayoutInflater inflater, DataBindingComponent bindingComponent) {
        return bind(inflater.inflate(R.layout.controller_state_idology_address_form, null, false), bindingComponent);
    }

    public static ControllerStateIdologyAddressFormBinding bind(View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    public static ControllerStateIdologyAddressFormBinding bind(View view, DataBindingComponent bindingComponent) {
        if ("layout/controller_state_idology_address_form_0".equals(view.getTag())) {
            return new ControllerStateIdologyAddressFormBinding(bindingComponent, view);
        }
        throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
    }
}
