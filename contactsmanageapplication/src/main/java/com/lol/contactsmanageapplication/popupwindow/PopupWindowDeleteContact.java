package com.lol.contactsmanageapplication.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lol.contactsmanageapplication.Dao.ContactsDao;
import com.lol.contactsmanageapplication.R;


/**
 * Created by MSI on 2016/4/20.
 */
public class PopupWindowDeleteContact extends PopupWindow {
    private Context mContext;

    private View view;

    private Button btn_take_photo, btn_pick_photo, btn_cancel;


    public PopupWindowDeleteContact(final Context mContext, final String mContact_id, final String mRawContact_id) {
        this.view = LayoutInflater.from(mContext).inflate(R.layout.inflate_popoup_delete, null);
        final Button btn_confrim = (Button) view.findViewById(R.id.btn_confrim);
        final Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);


        //“取消删除联系人”的监听
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });

        //“确认删除联系人”的监听
        btn_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过mContact_id，调用删除接口删除联系人。
                ContactsDao contactsDao = new ContactsDao(mContext);
                contactsDao.deleteContact(mContext,mContact_id,mRawContact_id);
                Toast.makeText(mContext,"已删除",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.rl_delete_contact).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.popup_delete);
        //添加pop窗口关闭事件

    }



}
