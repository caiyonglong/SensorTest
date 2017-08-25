package com.ckt.test.sensortest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ckt.test.sensortest.R;
import com.ckt.test.sensortest.bean.MHSensor;

import java.util.List;

/**
 * Created by D22434 on 2017/8/24.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private List<MHSensor> mhSensors;
    private Context mContext;


    public MyAdapter(Context context, List<MHSensor> mhSensors) {
        mContext = context;
        this.mhSensors = mhSensors;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.item_list, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTvID.setText(position + 1 + "");
        holder.mTvTest.setText(mhSensors.get(position).getField());
        holder.mTvValue.setText(mhSensors.get(position).getValue() + "");
        holder.mTvResult.setText(mhSensors.get(position).isResult() ? "pass" : "fail");
    }

    @Override
    public int getItemCount() {
        return mhSensors.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvTest, mTvValue, mTvID, mTvResult;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvTest = itemView.findViewById(R.id.tv_test);
            mTvValue = itemView.findViewById(R.id.tv_value);
            mTvID = itemView.findViewById(R.id.tv_id);
            mTvResult = itemView.findViewById(R.id.tv_result);
        }
    }

    public void setMhSensors(List<MHSensor> mhSensors) {
        this.mhSensors = mhSensors;
    }
}
