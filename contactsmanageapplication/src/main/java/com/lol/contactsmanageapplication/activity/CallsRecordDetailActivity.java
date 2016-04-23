package com.lol.contactsmanageapplication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lol.contactsmanageapplication.R;
import com.lol.contactsmanageapplication.bean.CallRecordInfo;

public class CallsRecordDetailActivity extends ActionBarActivity {

    private CallRecordInfo recordInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls_record_detail);
        ImageButton call = (ImageButton) findViewById(R.id.ib_call);//拨打电话键
        ImageButton message = (ImageButton) findViewById(R.id.ib_message);//发送信息键
        Button find = (Button) findViewById(R.id.bt_find);//“查看联系人”按钮
        Button update = (Button) findViewById(R.id.bt_update);//“更新联系人”按钮
        TextView time = (TextView) findViewById(R.id.tv_mDateDetail);//通话结束时间
        TextView costtime = (TextView) findViewById(R.id.tv_mCostTime);//通话耗时
        TextView title = (TextView) findViewById(R.id.tv_record_phonenum);//标题栏显示：手机号码+姓名
        TextView date = (TextView) findViewById(R.id.tv_calls_date);//通话日期
        ImageView person = (ImageView) findViewById(R.id.iv_recorddeatail_pic);//联系人头像
        ImageView type = (ImageView) findViewById(R.id.iv_type);//通话类型
        //获得从通话记录页面传过来的详细数据
        Intent intent = getIntent();
        recordInfo = (CallRecordInfo) intent.getSerializableExtra("RecordDetail");
        //加载布局
        time.setText(recordInfo.getmDetailDate());
        costtime.setText(recordInfo.getmTimeDuration());
        date.setText(recordInfo.getmDate());
        title.setText(recordInfo.getmNumber()+" / "+recordInfo.getmName());
        //加载头像
        if(recordInfo.getmContactIcon() == null) {
            //没有指定头像时设置为默认头像
            person.setImageResource(R.drawable.person_pic);
        }else{
            //用户有指定头像时直接设置
            person.setImageBitmap(recordInfo.getmContactIcon());
        }
        //加载通话类型图片
        switch (recordInfo.getmType()){
            case 1:
                type.setImageResource(R.drawable.type_in);
                break;
            case 2:
                type.setImageResource(R.drawable.type_out);
                break;
            case 3:
                type.setImageResource(R.drawable.type_in_norespond);
                break;
            default:break;
        }

        //监听界面中四个button
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接拨打该号码
                Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + recordInfo.getmNumber()));
                startActivity(intentPhone);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到信息编辑界面
                Intent intentMsm = new Intent(Intent.ACTION_VIEW);
                intentMsm.setType("vnd.android-dir/mms-sms");
                intentMsm.setData(Uri.parse("content://mms-sms/conversations/" + recordInfo.getmNumber()));//此为号码
                startActivity(intentMsm);

            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到该联系人详情页面
//                Intent personDetailIntent = new Intent(CallsRecordDetailActivity.this, DetailActivity.class);
//                personDetailIntent.putExtra("ContactId",ContactId);
//                personDetailIntent.putExtra("ContactId",RawContactId);
//                startActivity(personDetailIntent);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到该联系人修改界面
//                Intent personModifyIntent = new Intent(CallsRecordDetailActivity.this, ModifyActivity.class);
//                personModifyIntent.putExtra("ContactId",ContactId);
//                personModifyIntent.putExtra("ContactId",RawContactId);
//                startActivity(personModifyIntent);
            }
        });
    }

}
