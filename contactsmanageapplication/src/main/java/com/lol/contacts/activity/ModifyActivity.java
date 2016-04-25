package com.lol.contacts.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lol.contacts.Dao.ContactsDao;
import com.lol.contacts.R;
import com.lol.contacts.bean.ContactDetailInfo;
import com.lol.contacts.clipCircleImage.ClipHeaderActivity;

import java.io.File;

public class ModifyActivity extends Activity {

    //圆形头像开源部分自带
    private static final int RESULT_CAPTURE = 100;
    private static final int RESULT_PICK = 101;
    private static final int CROP_PHOTO = 102;
    private File tempFile;
    ImageView iv_head_icon;


    private EditText name;
    private EditText phonenum;
    private EditText email;
    private EditText love;
    private EditText address;
    private ContactDetailInfo info;
    private Button back;
    private Button save;
    private Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_modify);
        initView();//初始化布局，设置监听
        initData(savedInstanceState);//初始化数据，从数据库请求数据并加载显示
    }

    private void initView() {
        back = (Button) findViewById(R.id.btn_return);
        save = (Button) findViewById(R.id.btn_save);
        iv_head_icon = (ImageView) findViewById(R.id.iv_head_icon);
        name = (EditText) findViewById(R.id.et_name);
        phonenum = (EditText) findViewById(R.id.et_phonenum);
        email = (EditText) findViewById(R.id.et_email);
        love = (EditText) findViewById(R.id.et_love);
        address = (EditText) findViewById(R.id.et_address );

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
                ContactsDao dao = new ContactsDao(ModifyActivity.this);
                dao.updateContact(ModifyActivity.this,info);
                Log.d("new_love",String.valueOf( info.getmScore()));
                //保存完成之后返回联系人详情页，展示修改后的新详情
                Intent intent = new Intent(ModifyActivity.this,DetailActivity.class);
                intent.putExtra("ContactId",info.getmContact_id());
                intent.putExtra("RawContactId",info.getmRawContact_id());
                startActivity(intent);
                finish();
            }
        });

        LinearLayout ll_icon = (LinearLayout) findViewById(R.id.ll_pic);
        iv_head_icon = (ImageView) findViewById(R.id.iv_head_icon);
        //监听图片背景部分，点击弹出选择框（相册 / 拍照）
        ll_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog();//弹出选择框（相册 / 拍照）
            }
        });

    }

    //从选择图片界面返回时重构恢复页面
    private void initData(Bundle savedInstanceState) {
        //获得从联系人详情页面传过来的联系人ID，然后由ID向数据库请求详细数据
        Intent intent = getIntent();
        String contact_id = intent.getStringExtra("mContact_id");
        String rawContact_id = intent.getStringExtra("mRawContact_id");
         ContactsDao dao = new ContactsDao(ModifyActivity.this);
         info = dao.getContactMessage(ModifyActivity.this, contact_id, rawContact_id);
        //加载布局(姓名、手机、邮箱、亲密值、地址)
        name.setText(info.getmDisplay_name());
        phonenum.setText(info.getmPhone_number());
        email.setText(info.getmEmail());
        love.setText(String.valueOf(info.getmScore()));
        address.setText(info.getmAddress());
        //加载头像
        if(info.getmContact_icon() == null) {
            //没有指定头像时设置为默认头像
            iv_head_icon.setImageResource(R.drawable.person_pic);
        }else{
            //用户有指定头像时直接设置
            iv_head_icon.setImageBitmap(info.getmContact_icon());
        }


        //如果在里面没有重新设置图片则恢复之前的图片，tempFile 变回原来图片的路径
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        }else{
            //获得截取的图片的保存地点路径
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath()+"/clipHeaderLikeQQ/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //页面销毁时保存头像图片路径
        outState.putSerializable("tempFile", tempFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {
            case RESULT_CAPTURE://截取了图片
                if (resultCode == RESULT_OK) {
                    starCropPhoto(Uri.fromFile(tempFile));
                    //这里的tempFile是拍照获得的图片的路径地址，将其转换为uri然后发送到截图模块
                }
                break;
            case RESULT_PICK://从相册中选择了图片
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    //这里的uri是即将要截图的图片的uri,图片来自于相册选择
                    starCropPhoto(uri);
                }

                break;
            case CROP_PHOTO://截图后回传，将回传的图片显示到控件上
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
                .setCancelable(true)//设置为可取消弹窗
                .setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {//开启摄像头拍照获得照片
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                            startActivityForResult(intent, RESULT_CAPTURE);
                        } else {//打开相册选取照片
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
        //这里的uri是即将要截图的图片的uri（来自于相册或者摄像头拍照）
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipHeaderActivity.class);
        intent.setData(uri);
        intent.putExtra("side_length", 100);//裁剪图片宽高
        startActivityForResult(intent, CROP_PHOTO);//请求码为“截图”，跳转到截图

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
        Uri uri = picdata.getData();//通过最终获得的图片的URI显示到控件上
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
    private static String checkDirPath(String dirPath) {//检查文件对象是否存在
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    private ContactDetailInfo updateInfo(){
        //获取修好改的数据进行数据更新
        //图片处理
        info.setmDisplay_name(name.getText().toString());
        info.setmPhone_number(phonenum.getText().toString());
        info.setmEmail(email.getText().toString());
        info.setmScore(Integer.valueOf(love.getText().toString()));
        info.setmAddress(address.getText().toString());
        info.setmContact_icon(getBitmapFromUri(picUri));
        return info;
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
