package com.lol.contacts.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lol.contacts.Dao.ContactsDao;
import com.lol.contacts.R;
import com.lol.contacts.bean.ContactDetailInfo;
import com.lol.contacts.popupwindow.PopupWindowDeleteContact;
import com.makeramen.roundedimageview.RoundedImageView;


public class DetailActivity extends Activity {

    String mContact_id; //contact

    private Button btn_modify;
    private Button btn_delete;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_email;
    private TextView ll_love;
    private TextView tv_address;
    private String contactId;
    private String rawContactId;
    private RoundedImageView iv_head_icon;
    private ContactDetailInfo contactMessage;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        intent = getIntent();
        contactId = intent.getStringExtra("ContactId");
        rawContactId = intent.getStringExtra("RawContactId");
        initView();
        initdata(contactId, rawContactId);
        Log.d("初始ID", contactId);
        Log.d("初始ID", rawContactId);
    }

    /**
     * 从通讯录列表页转到单个联系人的详情页面
     * 详情界面(DetailActivity)要从列表页面Activity中获得ContactId、RawContactId。
     *
     */
    private void initdata(String id,String rawid) {
        //获得该联系人的信息，并展示出来
        ContactsDao contactsDao = new ContactsDao(this);
        contactMessage = contactsDao.getContactMessage(this, id, rawid);
        Log.d("initdata（）显示界面数据，ID",id);
        Log.d("initdata（）显示界面数据，ID",rawid);
        tv_name.setText(contactMessage.getmDisplay_name());
        tv_address.setText(contactMessage.getmAddress());
        tv_email.setText(contactMessage.getmEmail());
        tv_phone.setText(contactMessage.getmPhone_number());
        ll_love.setText( (new Integer(contactMessage.getmScore()).toString()));
        Log.d("love~~~~~~~", (new Integer(contactMessage.getmScore()).toString()));
        iv_head_icon.setImageBitmap(contactMessage.getmContact_icon());
    }



    /**
     * 初始化，给删除联系人、修改联系人信息做OnclickListener
     *
     *
     */
    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phonenum);
        tv_email = (TextView) findViewById(R.id.tv_email);
        ll_love = (TextView) findViewById(R.id.ll_love);
        tv_address = (TextView) findViewById(R.id.tv_address);
        iv_head_icon = (RoundedImageView) findViewById(R.id.iv_head_icon);

        btn_modify = (Button)findViewById(R.id.btn_Detailcontact_modify);
        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                show_delete_popupWindow();
            }
        });
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, ModifyActivity.class);
                intent.putExtra("mContact_id",contactId);
                intent.putExtra("mRawContact_id",rawContactId);
                startActivity(intent);
               finish();
            }
        });
    }


    //点击弹出删除窗口
    public void show_delete_popupWindow(){

        PopupWindowDeleteContact takePhotoPopWin = new PopupWindowDeleteContact(this,contactId,rawContactId);

        //showAtLocation(View parent, int gravity, int x, int y)
        //Gravity.BOTTOM展示在底部
        takePhotoPopWin.showAtLocation(findViewById(R.id.ll_activity_detail),
                Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
        takePhotoPopWin.setOnDismissListener(new popOnDismissListener());

    }
    public class popOnDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }


    //调用系统打电话
    public void ll_jumpSysPhoneCall(View view){

         String number = contactMessage.getmPhone_number();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.addCategory("android.intent.category.DEFAULT");
        //指定要拨打的电话号码
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);

    }
    //调用系统发短信
    public void ll_jumpSysSMS(View view){
        String number = contactMessage.getmPhone_number();
        Uri smsToUri = Uri.parse("smsto:"+number);
        Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri );
        startActivity( mIntent );

    }

    public void btn_return(View view){
        finish();

    }
}
