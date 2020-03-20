package com.cdkj.wzcd.main.credit.module.yksh;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;

import com.cdkj.baselibrary.appmanager.CdRouteHelper;
import com.cdkj.baselibrary.appmanager.SPUtilHelper;
import com.cdkj.baselibrary.base.AbsBaseLoadActivity;
import com.cdkj.baselibrary.dialog.UITipDialog;
import com.cdkj.baselibrary.model.IsSuccessModes;
import com.cdkj.baselibrary.model.eventmodels.EventBean;
import com.cdkj.baselibrary.nets.BaseResponseModelCallBack;
import com.cdkj.baselibrary.nets.RetrofitUtils;
import com.cdkj.baselibrary.utils.DisplayHelper;
import com.cdkj.baselibrary.utils.StringUtils;
import com.cdkj.wzcd.R;
import com.cdkj.wzcd.api.MyApiServer;
import com.cdkj.wzcd.databinding.ActYkshBinding;
import com.cdkj.wzcd.main.credit.module.CreditDetailFragment;
import com.cdkj.wzcd.main.credit.module.yksh.bean.RwBean;
import com.cdkj.wzcd.main.credit.module.zrzl.bean.ZrzlBean;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * @author : qianLei
 * @since : 2020/1/1 19:07
 */
public class YkshActivity extends AbsBaseLoadActivity {

    private ActYkshBinding mBinding;

    private String code;
    private ZrzlBean bean;

    private List<RwBean> missionList = new ArrayList<>();

    public static void open(Context context, String code) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, YkshActivity.class);
        intent.putExtra(CdRouteHelper.DATA_SIGN, code);
        context.startActivity(intent);
    }

    @Override
    public View addMainView() {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.act_yksh, null, false);
        return mBinding.getRoot();
    }

    @Override
    public void afterCreate(Bundle savedInstanceState) {
        mBaseBinding.titleView.setMidTitle("用款审核");

        init();
        initView();
        initListener();

        getDetail();
    }

    private void init() {
        code = getIntent().getStringExtra(CdRouteHelper.DATA_SIGN);

    }

    private void initView() {

        mBinding.elLoanAmount.setFocusable(false);
        mBinding.elRepointAmount.setFocusable(false);
        mBinding.elHeji.setFocusable(false);

        mBinding.slIsContinueAdvance.setDataIs(null);
        mBinding.slIsPay.setDataIs(null);
        mBinding.slIsPay.getContentView().setPadding(DisplayHelper.dp2px(this, 20), 0, 0, 0);
    }

    private void initListener() {
        mBinding.mlRw.setOnClickListener(view -> {
            RwglActivity.open(this, missionList);
        });

        mBinding.btnConfirm.setOnConfirmListener(view -> {
            doRequest("1");
        });

        mBinding.btnConfirm.setOnConfirmRightListener(view -> {
            doRequest("0");
        });
    }

    private void getDetail() {

        Map<String, Object> map = RetrofitUtils.getRequestMap();
        map.put("code", code);

        showLoadingDialog();

        Call call = RetrofitUtils.createApi(MyApiServer.class)
                .getZrzlDetail("632516", StringUtils.getJsonToString(map));
        addCall(call);

        call.enqueue(new BaseResponseModelCallBack<ZrzlBean>(this) {
            @Override
            protected void onSuccess(ZrzlBean data, String SucMessage) {
                bean = data;
                initDetail();
                setView();
            }

            @Override
            protected void onFinish() {
                disMissLoading();
            }
        });
    }

    private void initDetail() {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.ll_detail, CreditDetailFragment.getInstance(bean));
        mFragmentTransaction.commit();
    }

    private void setView() {

        mBinding.elLoanAmount.setText(bean.getLoanAmount());
        mBinding.elRepointAmount.setText(bean.getRepointAmount());
        mBinding.elHeji.setText(new BigDecimal(bean.getRepointAmount()).add(new BigDecimal(bean.getLoanAmount())).toPlainString());

    }

    private void check() {

        if (mBinding.slIsContinueAdvance.check()) {
            return;
        }
    }

    private void doRequest(String approveResult) {

        check();

        HashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();

        hashMap.put("code", bean.getCode());
        hashMap.put("operator", SPUtilHelper.getUserId());
        hashMap.put("approveResult", approveResult);
        hashMap.put("approveNote", mBinding.rlApproveNote.getText());
        hashMap.put("isContinueAdvance", mBinding.slIsContinueAdvance.getDataKey());
        hashMap.put("isPay", mBinding.slIsPay.getDataKey());
        hashMap.put("missionList", missionList);

        Call call = RetrofitUtils.getBaseAPiService()
                .successRequest("632552", StringUtils.getJsonToString(hashMap));

        addCall(call);

        call.enqueue(new BaseResponseModelCallBack<IsSuccessModes>(this) {
            @Override
            protected void onSuccess(IsSuccessModes data, String SucMessage) {
                UITipDialog.showSuccess(YkshActivity.this, "操作成功", dialogInterface -> {
                    finish();
                });
            }

            @Override
            protected void onFinish() {
                disMissLoading();
            }
        });


    }

    @Subscribe
    public void rw(EventBean bean) {

        if ("yksh_rw_list".equals(bean.getTag())) {
            missionList.clear();
            missionList.addAll((Collection<? extends RwBean>) bean.getValue());

        }

    }
}
