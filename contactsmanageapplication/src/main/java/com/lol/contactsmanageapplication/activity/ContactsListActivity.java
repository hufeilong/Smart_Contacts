package com.lol.contactsmanageapplication.activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.lol.contactsmanageapplication.R;
import com.lol.contactsmanageapplication.Utils;
import com.lol.contactsmanageapplication.bean.ContactListItemInfo;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.util.ArrayList;
import java.util.Map;

public class ContactsListActivity extends ActionBarActivity {

    private static final String KEY_TRANSITION_EFFECT = "transition_effect";

    private JazzyListView mList;//JazzyListView
    private Map<String, Integer> mEffectMap;//存储代表动画效果种类的字符串和整型常量
    private int mCurrentTransitionEffect = JazzyHelper.ZIPPER;//当前动画效果
    private ArrayList<ContactListItemInfo> mContactsPerson;//存储联系人对象的集合
    private ArrayList<ContactListItemInfo> mPerson;//经处理加入首字母分割线对象的集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        mList = (JazzyListView) findViewById(android.R.id.list);//通过空间id查找到JazzyListView
        initData();//从数据库请求数据并进行二次处理
        mList.setAdapter(new JazzylvAdapter(this));//为JazzyListView设置适配器


        if (savedInstanceState != null) {//这里用于重新加载动画效果设置
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT, JazzyHelper.ZIPPER);
            setupJazziness(mCurrentTransitionEffect);
        }
    }



    /*模拟添加一个得到数据的方法，方便使用*/
    /*private ArrayList<ContactListItemInfo> initDate(){

        mContactsPerson = new ArrayList<ContactListItemInfo>();
      *//*为动态数组添加数据*//*
        for(int i=0;i<100;i++)
        {
            //创建测试数据，ID为 i,姓名为我是小 + i，头像均为默认头像
            ContactListItemInfo contactInfo = new ContactListItemInfo("" + i, "我是小" + i, null);
            if(i<10){
                contactInfo.first_letter = "A";
            }else if(i < 20){
                contactInfo.first_letter = "B";
            }else if(i < 30){
                contactInfo.first_letter = "C";
            }else if(i < 40){
                contactInfo.first_letter = "D";
            }else if(i < 50){
                contactInfo.first_letter = "H";
            }else {
                contactInfo.first_letter = "M";
            }
            mContactsPerson.add(contactInfo);
        }
        return mContactsPerson;
        //得到存储数据的动态数组
    }*/

    private void initData(){
        mContactsPerson = initDate();
        mPerson = getDate(mContactsPerson);
    }

    //请求数据库获得数据
    private ArrayList<ContactListItemInfo> initDate() {
        ArrayList<ContactListItemInfo> infos = new ArrayList<>();
        //从数据库请求数据
//      ContactsDao dao = new ContactsDao(ContactsListActivity.this);
//      infos = dao.getAllContacts(ContactsListActivity.this);
        return infos;
    }




    //对联系人集合进行二次处理，方便适配器绑定数据时添加首字母分割栏
    private ArrayList<ContactListItemInfo> getDate(ArrayList<ContactListItemInfo> personinfo){
        ArrayList<ContactListItemInfo> infos = new ArrayList<>();
        //首先在最前面加入第一个联系人的姓名首字母分割栏
        ContactListItemInfo splitA = new ContactListItemInfo("split","split",null, null, null, null, null, 0);
        splitA.setmPhoneBookLabel(personinfo.get(0).getmPhoneBookLabel());
        infos.add(splitA);
        int size = personinfo.size();
        for(int i=0;i<size-1;i++){
        //在联系人姓名首字母变化的地方插入一个特别的联系人对象，便于ListView加载保定数据时插入分割栏
            if(personinfo.get(i).getmPhoneBookLabel() != personinfo.get(i+1).getmPhoneBookLabel()){
                ContactListItemInfo split = new ContactListItemInfo("split","split",null, null, null, null, null, 0);
                split.setmPhoneBookLabel(personinfo.get(i + 1).getmPhoneBookLabel());
                //先加当前联系人信息，再在后面添加特别对象
                infos.add(personinfo.get(i));
                infos.add(split);
            }else{
                infos.add(personinfo.get(i));
            }
        }
        infos.add(personinfo.get(size-1));
        return infos;
    }

        //新建一个JazzylvAdapter适配器继承于BaseAdapter，实现数据的绑定
    class JazzylvAdapter extends BaseAdapter {
            private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

            //构造函数
            public JazzylvAdapter(Context context) {
                this.mInflater = LayoutInflater.from(context);
            }

            @Override
            public int getCount() {
                return mPerson.size();
            }

            @Override
            public ContactListItemInfo getItem(int position) {
                return mPerson.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            //因为是从本地数据库加载，可不设置缓存等优化直接加载
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (getItem(position).getmContact_id() != "split") {//判断是不是分割栏
                    View view = mInflater.inflate(R.layout.contacts_list_item, null);
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_person_pic);
                    TextView textView = (TextView) view.findViewById(R.id.tv_person_name);
                    if(getItem(position).getmContact_icon() == null) {
                        imageView.setImageResource(R.drawable.person_pic);//数据库中没有保存的头像时设置为默认头像
                    }else{
                        imageView.setImageBitmap(getItem(position).getmContact_icon());
                    }
                    textView.setText(getItem(position).getmName());
                    return view;
                } else {
                    View view2 = mInflater.inflate(R.layout.contacts_split_item, null);
                    TextView tv = (TextView) view2.findViewById(R.id.tv_split);
                    tv.setText(getItem(position).getmPhoneBookLabel());
                    return view2;
                }

            }
        }

          /*  @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                    if(!(getDate(initDate()).get(position).contact_id.equals("split")) ){
                           ViewHolder holder;
                           View view;
                        //convertView缓存行布局
                        if (convertView == null) {
                            //开始时所有的view都缓存进convertView
                            view = mInflater.inflate(R.layout.contacts_list_item, null);
                            //新建一个ViewHolder对象来接收缓存数据，缓存的是控件的实例
                            holder = new ViewHolder();
                        //得到各个控件的对象
                            holder.imageView = (ImageView) view.findViewById(R.id.iv_person_pic);
                            holder.textView = (TextView) view.findViewById(R.id.tv_person_name);
                            view.setTag(holder);//绑定ViewHolder对象
                        }else {
                            view = convertView;
                            holder = (ViewHolder) view.getTag();//取出ViewHolder对象
                        }
                        //设置TextView显示的内容，即我们存放在动态数组中的数据
//                holder.imageView.setImageBitmap(getDate().get(position).contact_icon);
                        holder.imageView.setImageResource(R.drawable.person_pic);//这里暂时全部设置为默认头像
                        holder.textView.setText(getDate(initDate()).get(position).display_name);
                        return view;

                    }else{
                        View view2 = mInflater.inflate(R.layout.contacts_split_item, null);
                        TextView tv = (TextView) view2.findViewById(R.id.tv_split);
                        tv.setText(getDate(initDate()).get(position).first_letter);
                        return view2;
                    }
        }
    }

    public final  class ViewHolder{
        public ImageView imageView;
        public TextView textView;
    }*/



    //呼出菜单栏时
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mEffectMap = Utils.buildEffectMap(this);//获得由LinkedHashMap实现存储的动画种类字符串和对应整型常量
        Utils.populateEffectMenu(menu, new ArrayList<>(mEffectMap.keySet()), this);//在菜单中展示动画特效字符串名供选择
        return true;
    }

    //左键菜单栏设置动画效果,选择后Toast弹出提示所选动画效果
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String strEffect = item.getTitle().toString();
        Toast.makeText(this, strEffect, Toast.LENGTH_SHORT).show();
        setupJazziness(mEffectMap.get(strEffect));//由菜单中选中的动画进行设置动画设置
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TRANSITION_EFFECT, mCurrentTransitionEffect);
    }

    //设置动画效果
    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        mList.setTransitionEffect(mCurrentTransitionEffect);
    }
}
