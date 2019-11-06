package cn.com.heaton.avertisetest.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.com.heaton.avertisetest.R;
import cn.com.heaton.avertisetest.base.BaseAdapter;
import cn.com.heaton.avertisetest.model.SendRecord;

/**
 *
 * Created by LiuLei on 2017/12/26.
 */

public class SendAdvertiserAdapter extends BaseAdapter<SendRecord> {

    public SendAdvertiserAdapter(Context context, List<SendRecord> list) {
        super(context, list);
    }

    @Override
    public int getContentView() {
        return R.layout.item_send;
    }

    @Override
    public void onInitView(View view, int position) {
        SendRecord record = getItem(position);

        TextView tv_payload = getAdapterView(view, R.id.tv_payload);
        TextView tv_time = getAdapterView(view, R.id.tv_time);

        tv_payload.setText(record.getHexPayload());
        tv_time.setText(record.getTime());
    }
}
