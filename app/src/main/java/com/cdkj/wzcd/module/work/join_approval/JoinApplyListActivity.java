package com.cdkj.wzcd.module.work.join_approval;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.cdkj.baselibrary.api.ResponseInListModel;
import com.cdkj.baselibrary.base.AbsRefreshListActivity;
import com.cdkj.baselibrary.nets.BaseResponseModelCallBack;
import com.cdkj.baselibrary.nets.RetrofitUtils;
import com.cdkj.baselibrary.utils.StringUtils;
import com.cdkj.wzcd.R;
import com.cdkj.wzcd.adapter.MyApplyListAdapter;
import com.cdkj.wzcd.api.MyApiServer;
import com.cdkj.wzcd.model.NodeListModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * 准入申请
 * Created by cdkj on 2018/5/30.
 */

public class JoinApplyListActivity extends AbsRefreshListActivity {


    public static void open(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, JoinApplyListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void afterCreate(Bundle savedInstanceState) {
        mBaseBinding.titleView.setMidTitle(getString(R.string.daiqianzhunru));

//        mBaseBinding.titleView.setRightTitle("ZR");
//        mBaseBinding.titleView.setRightFraClickListener(view -> {
//            JoinApplyActivity.open(this, "");
//        });

        initRefreshHelper(10);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRefreshHelper.onDefaultMRefresh(true);

    }


    @Override
    public RecyclerView.Adapter getListAdapter(List listData) {
        MyApplyListAdapter myApplyListAdapter = new MyApplyListAdapter(listData);
//        myApplyListAdapter.setOnItemClickListener((adapter, view, position) -> {
//            JoinApplyActivity.open(this, myApplyListAdapter.getItem(position).getCode());
//        });
        return myApplyListAdapter;
    }

    @Override
    public void getListRequest(int pageIndex, int limit, boolean isShowDialog) {
        Map<String, String> map = RetrofitUtils.getNodeListMap();

        map.put("start", pageIndex + "");
        map.put("limit", limit + "");
//        map.put("saleUserId", SPUtilHelper.getUserId());

        if (isShowDialog) showLoadingDialog();

        Call call = RetrofitUtils.createApi(MyApiServer.class).getNodeList("632148", StringUtils.getJsonToString(map));
        addCall(call);

        call.enqueue(new BaseResponseModelCallBack<ResponseInListModel<NodeListModel>>(this) {
            @Override
            protected void onSuccess(ResponseInListModel<NodeListModel> data, String SucMessage) {
                mRefreshHelper.setData(data.getList(), "暂无抵押记录", 0);
            }

            @Override
            protected void onFinish() {
                disMissLoading();
            }
        });
    }
}
