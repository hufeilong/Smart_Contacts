package com.lol.contactsmanageapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lol.contactsmanageapplication.R;
import com.lol.contactsmanageapplication.bean.ContactDetailInfo;
import com.lol.contactsmanageapplication.clipCircleImage.ClipHeaderActivity;

import java.io.File;


public class AddContactActivity extends Activity {

    //用于截图
    private static final int RESULT_CAPTURE = 100;
    private static final int RESULT_PICK = 101;
    private static final int CROP_PHOTO = 102;
    private File tempFile;
    ImageView iv_head_icon;

    //一个联系人的详细信息
    private EditText et_name1;
    private EditText et_name2;
    private EditText et_phone;
    private EditText et_address;
    private EditText et_loveScore;
    private EditText et_email;
    private Button save_contact;
    private Button cancel_saveContact;
    private String name1;
    private String name2;
    private String address;
    private String phone;
    private String loveScore;
    private String email;
    private int mHoneyDegre;
    private Uri picUri;

    //用于截图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        //保存联系人按钮
        save_contact = (Button) findViewById(R.id.btn_Detailcontact_modify);
        //取消保存按钮
        cancel_saveContact = (Button) findViewById(R.id.btn_return_and_finishSelf);

        //联系人信息的EditText
        et_name1 = (EditText) findViewById(R.id.et_name1    );
        et_name2 = (EditText) findViewById(R.id.et_name2    );
        et_phone = (EditText) findViewById(R.id.et_phone    );
        et_address =(EditText) findViewById(R.id.et_address  );
        et_loveScore= (EditText) findViewById(R.id.et_loveScore);
        et_email = (EditText) findViewById(R.id.et_email);

        //得到联系人的信息
        name1 = et_name1.getText().toString();
        name2 = et_name2.getText().toString();
        address = et_address.getText().toString();
        phone = et_phone.getText().toString();
        loveScore = et_loveScore.getText().toString();
        mHoneyDegre = Integer.parseInt(loveScore);

        email = et_email.getText().toString();



        initView();
        initData(savedInstanceState);
    }



    private void initView() {

        iv_head_icon = (ImageView) findViewById(R.id.iv_head_icon);

        iv_head_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog();
            }
        });

        //确认保存联系人操作
        save_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到联系人的信息
                name1 = et_name1.getText().toString();
                name2 = et_name2.getText().toString();
                address = et_address.getText().toString();
                phone = et_phone.getText().toString();
                loveScore = et_loveScore.getText().toString();
                mHoneyDegre = Integer.parseInt(loveScore);

                email = et_email.getText().toString();

                //引用接口，将联系人信息插入到数据库中
                ContactDetailInfo contactDetailInfo = new ContactDetailInfo();
                contactDetailInfo.setmAddress(address);
                contactDetailInfo.setmDisplay_name(name1 + name2);
                contactDetailInfo.setmPhone_number(phone);
                contactDetailInfo.setmEmail(email);
                contactDetailInfo.setmScore(mHoneyDegre);
                //处理图片
                contactDetailInfo.setmContact_icon(getBitmapFromUri(picUri));
                ContactsDao dao = new ContactsDao(AddContactActivity.this);
                dao.updateContact(AddContactActivity.this, contactDetailInfo);
                Toast.makeText(AddContactActivity.this,"保存成功",
                        Toast.LENGTH_SHORT).show();

                //跳转到它的详情页面（没有ID，跳不过去，暂时直接回退到联系人列表）
                finish();
            }
        });

        //取消保存联系人操作
        cancel_saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击左上角取消直接回退到联系人列表
                finish();
            }
        });

    }


    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        }else{
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath()+"/clipHeaderLikeQQ/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", tempFile);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {
            case RESULT_CAPTURE:
                if (resultCode == RESULT_OK) {
                    starCropPhoto(Uri.fromFile(tempFile));
                }
                break;
            case RESULT_PICK:
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    starCropPhoto(uri);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (intent != null) {
                        setPicToView(intent);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void showChooseDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                            startActivityForResult(intent, RESULT_CAPTURE);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(intent, "请选择图片"), RESULT_PICK);
                        }
                    }
                }).show();
    }


    /**
     * 打开截图界面
     * @param uri 原图的Uri
     */
    public void starCropPhoto(Uri uri) {

        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipHeaderActivity.class);
        intent.setData(uri);
        intent.putExtra("side_length", 100);//裁剪图片宽高
        startActivityForResult(intent, CROP_PHOTO);

        //调用系统的裁剪
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", crop);
//        intent.putExtra("outputY", crop);
//        intent.putExtra("return-data", true);
//        intent.putExtra("noFaceDetection", true);
//        startActivityForResult(intent, CROP_PHOTO);
    }

    private void setPicToView(Intent picdata) {
        Uri uri = picdata.getData();
        picUri = uri;//獲得截取后圖片
        if (uri == null) {
            return;
        }
        iv_head_icon.setImageURI(uri);
    }


    /**
     *
     * @param dirPath
     * @return
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    //将uri转为bitmap对象
    private Bitmap getBitmapFromUri(Uri uri)
    {
        try
        {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
