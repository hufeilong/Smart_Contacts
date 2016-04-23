package com.lol.contactsmanageapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lol.contactsmanageapplication.R;
import com.lol.contactsmanageapplication.bean.ContactDetailInfo;

public class ContactsModifyActivity extends ActionBarActivity {

    private ImageView person_pic;
    private EditText name;
    private EditText phonenum;
    private EditText email;
    private EditText love;
    private EditText address;
    private ContactDetailInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_modify);
        Button back = (Button) findViewById(R.id.btn_return);
        Button save = (Button) findViewById(R.id.btn_save);
        person_pic = (ImageView) findViewById(R.id.iv_pic);
        name = (EditText) findViewById(R.id.et_name);
        phonenum = (EditText) findViewById(R.id.et_phonenum);
        email = (EditText) findViewById(R.id.et_email);
        love = (EditText) findViewById(R.id.et_love);
        address = (EditText) findViewById(R.id.et_address );
        //获得从联系人详情页面传过来的详细数据
        Intent intent = getIntent();
        info = (ContactDetailInfo) intent.getSerializableExtra("modify");
        //加载布局(姓名、手机、邮箱、亲密值、地址)
        name.setText(info.getmDisplay_name());
        phonenum.setText(info.getmPhone_number());
        email.setText(info.getmEmail());
        love.setText(info.getmScore());
        address.setText(info.getmAddress());
        //加载头像
        if(info.getmContact_icon() == null) {
            //没有指定头像时设置为默认头像
            person_pic.setImageResource(R.drawable.person_pic);
        }else{
            //用户有指定头像时直接设置
            person_pic.setImageBitmap(info.getmContact_icon());
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回联系人详情页
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击保存修改结果到数据库
                updateInfo();
//                ContactsDao dao = new ContactsDao(ContactsModifyActivity.this);
//                dao.updateContact(ContactsModifyActivity.this,info);
                //保存完成之后返回联系人详情页，展示修改后的新详情
                finish();
            }
        });

    }

    private ContactDetailInfo updateInfo(){
        //获取修好改的数据进行数据更新
        //图片处理
        info.setmDisplay_name(name.getText().toString());
        info.setmPhone_number(phonenum.getText().toString());
        info.setmEmail(email.getText().toString());
        info.setmScore(Integer.valueOf(love.getText().toString()));
        info.setmAddress(address.getText().toString());
        return info;
    }

}
