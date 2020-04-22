package com.cdkj.wzcd.main.message;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.cdkj.baselibrary.appmanager.CdRouteHelper;
import com.cdkj.baselibrary.base.AbsBaseLoadActivity;
import com.cdkj.baselibrary.utils.DateUtil;
import com.cdkj.wzcd.R;
import com.cdkj.wzcd.databinding.ActMessageBinding;
import com.cdkj.wzcd.main.message.bean.MessageBean;
import com.cdkj.wzcd.main.message.bean.NoticeBean;

/**
 * @author : qianLei
 * @since : 2020/1/3 17:09
 */
public class NoticeActivity extends AbsBaseLoadActivity {

    private ActMessageBinding mBinding;

    private NoticeBean bean;

    public static void open(Context context, NoticeBean bean) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, NoticeActivity.class);
        intent.putExtra(CdRouteHelper.DATA_SIGN, bean);
        context.startActivity(intent);
    }

    @Override
    public View addMainView() {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.act_message, null, false);
        return mBinding.getRoot();
    }

    @Override
    public void afterCreate(Bundle savedInstanceState) {

        mBaseBinding.titleView.setMidTitle("公告详情");

        init();
        setView();
    }

    private void init() {
        bean = (NoticeBean) getIntent().getSerializableExtra(CdRouteHelper.DATA_SIGN);
    }

    private void setView() {

        mBinding.tvType.setText("消息");

        mBinding.tvTitle.setText(bean.getTitle());
        mBinding.tvTime.setText(DateUtil.formatStringData(bean.getUpdateDatetime()));
        mBinding.wvContent.loadData(bean.getContent(), "text/html;charset=UTF-8", "UTF-8");

    }
}