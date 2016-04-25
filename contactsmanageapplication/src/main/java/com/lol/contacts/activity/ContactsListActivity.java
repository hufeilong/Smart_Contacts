package com.lol.contacts.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lol.contacts.Dao.ContactsDao;
import com.lol.contacts.R;
import com.lol.contacts.Utils;
import com.lol.contacts.bean.ContactListItemInfo;
import com.makeramen.roundedimageview.RoundedImageView;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.util.ArrayList;
import java.util.Map;

public class ContactsListActivity extends Activity {

    private static final String KEY_TRANSITION_EFFECT = "transition_effect";

    private JazzyListView mList;//JazzyListView
    private Map<String, Integer> mEffectMap;//存储代表动画效果种类的字符串和整型常量
    private int mCurrentTransitionEffect = JazzyHelper.ZIPPER;//当前动画效果
    private ArrayList<ContactListItemInfo> mContactsPerson;//存储联系人对象的集合
    private ArrayList<ContactListItemInfo> mPerson;//经处理加入首字母分割线对象的集合
    private ContactsDao dao;
    private Button bt_add;
    private Button bt_record;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        bt_add = (Button) findViewById(R.id.bt_add);
        bt_record = (Button) findViewById(R.id.bt_record);
        mList = (JazzyListView) findViewById(android.R.id.list);//通过空间id查找到JazzyListView
        initData();//从数据库请求数据并进行二次处理
        mList.setAdapter(new JazzylvAdapter(this));//为JazzyListView设置适配器

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //对listview进行点击监听，点击行不是分割栏时跳转到对应联系人详情页
            //携带数据Contact_id、mRawContact_id  ；方便详情页调用数据库方法获取数据
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPerson.get(position).getmContact_id() != "split") {
                    String Contact_id = mPerson.get(position).getmContact_id();
                    String mRawContact_id = mPerson.get(position).getmRawContact_id();
                    Intent intent = new Intent(ContactsListActivity.this, DetailActivity.class);
                    intent.putExtra("ContactId", Contact_id);
                    intent.putExtra("RawContactId", mRawContact_id);
                    startActivity(intent);
                }
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到添加联系人页面
                startActivity(new Intent(ContactsListActivity.this,AddContactActivity.class));
            }
        });

        bt_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到通话记录页面
                startActivity(new Intent(ContactsListActivity.this,CallsRecordActivity.class));
            }
        });

        if (savedInstanceState != null) {//这里用于重新加载动画效果设置
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT, JazzyHelper.ZIPPER);
            setupJazziness(mCurrentTransitionEffect);
        }
    }





    private void initData(){
        initDate();
        mPerson = getDate(mContactsPerson);
    }

    //请求数据库获得数据
    private void initDate() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                //从数据库请求数据
                dao = new ContactsDao(ContactsListActivity.this);
                mContactsPerson = (ArrayList<ContactListItemInfo>) dao.getAllContacts(ContactsListActivity.this);
            }
        }).start();*/
        dao = new ContactsDao(ContactsListActivity.this);
        mContactsPerson = (ArrayList<ContactListItemInfo>) dao.getAllContacts(ContactsListActivity.this);
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
            if(!(personinfo.get(i).getmPhoneBookLabel().equals( personinfo.get(i+1).getmPhoneBookLabel()))){
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
            private final Context mcontext;

            //构造函数
            public JazzylvAdapter(Context context) {
                this.mInflater = LayoutInflater.from(context);
                mcontext = context;
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

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = null;
                View view2 = null;
                if(!(getItem(position).getmContact_id().equals("split")) ){
                    //convertView缓存行布局
                    if (convertView == null) {
                        //开始时所有的view都缓存进convertView
                        view = mInflater.inflate(R.layout.contacts_list_item, null);
                    }else {
                        if(convertView instanceof LinearLayout){
                            view = convertView;
                        }else{
                            view = mInflater.inflate(R.layout.contacts_list_item, null);
                        }

                    }
                    //得到各个控件的对象
                    RoundedImageView imageView1 = (RoundedImageView) view.findViewById(R.id.iv_person_pic);
                    TextView textView1 = (TextView) view.findViewById(R.id.tv_person_name);
                    //设置TextView显示的内容，即我们存放在动态数组中的数据
                    imageView1.setImageBitmap(dao.getBitmap((getItem(position).getmContact_id()), ContactsListActivity.this));//这里暂时全部设置为默认头像
                    textView1.setText(getItem(position).getmName());
                    switch (position%76) {
                        case 0:
                            view.setBackgroundColor(Color.parseColor("#ff0000"));
                            break;
                        case 1:
                            view.setBackgroundColor(Color.parseColor("#ff1100"));
                            break;
                        case 2:
                            view.setBackgroundColor(Color.parseColor("#ff2200"));
                            break;
                        case 3:
                            view.setBackgroundColor(Color.parseColor("#ff3300"));
                            break;
                        case 4:
                            view.setBackgroundColor(Color.parseColor("#ff4400"));
                            break;
                        case 5:
                            view.setBackgroundColor(Color.parseColor("#ff5500"));
                            break;
                        case 6:
                            view.setBackgroundColor(Color.parseColor("#ff6600"));
                            break;
                        case 7:
                            view.setBackgroundColor(Color.parseColor("#ff7700"));
                            break;
                        case 8:
                            view.setBackgroundColor(Color.parseColor("#ff8800"));
                            break;
                        case 9:
                            view.setBackgroundColor(Color.parseColor("#ff9900"));
                            break;
                        case 10:
                            view.setBackgroundColor(Color.parseColor("#ffaa00"));
                            break;
                        case 11:
                            view.setBackgroundColor(Color.parseColor("#ffbb00"));
                            break;
                        case 12:
                            view.setBackgroundColor(Color.parseColor("#ffcc00"));
                            break;
                        case 13:
                            view.setBackgroundColor(Color.parseColor("#ffdd00"));
                            break;
                        case 14:
                            view.setBackgroundColor(Color.parseColor("#ffee00"));
                            break;
                        case 15:
                            view.setBackgroundColor(Color.parseColor("#ffff00"));
                            break;
                        case 16:
                            view.setBackgroundColor(Color.parseColor("#eeff00"));
                            break;
                        case 17:
                            view.setBackgroundColor(Color.parseColor("#ddff00"));
                            break;
                        case 18:
                            view.setBackgroundColor(Color.parseColor("#ccff00"));
                            break;
                        case 19:
                            view.setBackgroundColor(Color.parseColor("#bbff00"));
                            break;
                        case 20:
                            view.setBackgroundColor(Color.parseColor("#aaff00"));
                            break;
                        case 21:
                            view.setBackgroundColor(Color.parseColor("#99ff00"));
                            break;
                        case 22:
                            view.setBackgroundColor(Color.parseColor("#88ff00"));
                            break;
                        case 23:
                            view.setBackgroundColor(Color.parseColor("#77ff00"));
                            break;
                        case 24:
                            view.setBackgroundColor(Color.parseColor("#66ff00"));
                            break;
                        case 25:
                            view.setBackgroundColor(Color.parseColor("#55ff00"));
                            break;
                        case 26:
                            view.setBackgroundColor(Color.parseColor("#44ff00"));
                            break;
                        case 27:
                            view.setBackgroundColor(Color.parseColor("#33ff00"));
                            break;
                        case 28:
                            view.setBackgroundColor(Color.parseColor("#22ff00"));
                            break;
                        case 29:
                            view.setBackgroundColor(Color.parseColor("#11ff00"));
                            break;
                        case 30:
                            view.setBackgroundColor(Color.parseColor("#00ff00"));
                            break;
                        case 31:
                            view.setBackgroundColor(Color.parseColor("#00ff11"));
                            break;
                        case 32:
                            view.setBackgroundColor(Color.parseColor("#00ff22"));
                            break;
                        case 33:
                            view.setBackgroundColor(Color.parseColor("#00ff33"));
                            break;
                        case 34:
                            view.setBackgroundColor(Color.parseColor("#00ff44"));
                            break;
                        case 35:
                            view.setBackgroundColor(Color.parseColor("#00ff55"));
                            break;
                        case 36:
                            view.setBackgroundColor(Color.parseColor("#00ff66"));
                            break;
                        case 37:
                            view.setBackgroundColor(Color.parseColor("#00ff77"));
                            break;
                        case 38:
                            view.setBackgroundColor(Color.parseColor("#00ff88"));
                            break;
                        case 39:
                            view.setBackgroundColor(Color.parseColor("#00ff99"));
                            break;
                        case 40:
                            view.setBackgroundColor(Color.parseColor("#00ffaa"));
                            break;
                        case 41:
                            view.setBackgroundColor(Color.parseColor("#00ffbb"));
                            break;
                        case 42:
                            view.setBackgroundColor(Color.parseColor("#00ffcc"));
                            break;
                        case 43:
                            view.setBackgroundColor(Color.parseColor("#00ffdd"));
                            break;
                        case 44:
                            view.setBackgroundColor(Color.parseColor("#00ffee"));
                            break;
                        case 45:
                            view.setBackgroundColor(Color.parseColor("#00ffff"));
                            break;
                        case 46:
                            view.setBackgroundColor(Color.parseColor("#00eeff"));
                            break;
                        case 47:
                            view.setBackgroundColor(Color.parseColor("#00ddff"));
                            break;
                        case 48:
                            view.setBackgroundColor(Color.parseColor("#00ccff"));
                            break;
                        case 49:
                            view.setBackgroundColor(Color.parseColor("#00bbff"));
                            break;
                        case 50:
                            view.setBackgroundColor(Color.parseColor("#00aaff"));
                            break;
                        case 51:
                            view.setBackgroundColor(Color.parseColor("#0099ff"));
                            break;
                        case 52:
                            view.setBackgroundColor(Color.parseColor("#0088ff"));
                            break;
                        case 53:
                            view.setBackgroundColor(Color.parseColor("#0077ff"));
                            break;
                        case 54:
                            view.setBackgroundColor(Color.parseColor("#0066ff"));
                            break;
                        case 55:
                            view.setBackgroundColor(Color.parseColor("#0055ff"));
                            break;
                        case 56:
                            view.setBackgroundColor(Color.parseColor("#0044ff"));
                            break;
                        case 57:
                            view.setBackgroundColor(Color.parseColor("#0033ff"));
                            break;
                        case 58:
                            view.setBackgroundColor(Color.parseColor("#0022ff"));
                            break;
                        case 59:
                            view.setBackgroundColor(Color.parseColor("#0011ff"));
                            break;
                        case 60:
                            view.setBackgroundColor(Color.parseColor("#0000ff"));
                            break;
                        case 61:
                            view.setBackgroundColor(Color.parseColor("#1100ff"));
                            break;
                        case 62:
                            view.setBackgroundColor(Color.parseColor("#2200ff"));
                            break;
                        case 63:
                            view.setBackgroundColor(Color.parseColor("#3300ff"));
                            break;
                        case 64:
                            view.setBackgroundColor(Color.parseColor("#4400ff"));
                            break;
                        case 65:
                            view.setBackgroundColor(Color.parseColor("#5500ff"));
                            break;
                        case 66:
                            view.setBackgroundColor(Color.parseColor("#6600ff"));
                            break;
                        case 67:
                            view.setBackgroundColor(Color.parseColor("#7700ff"));
                            break;
                        case 68:
                            view.setBackgroundColor(Color.parseColor("#8800ff"));
                            break;
                        case 69:
                            view.setBackgroundColor(Color.parseColor("#9900ff"));
                            break;
                        case 70:
                            view.setBackgroundColor(Color.parseColor("#aa00ff"));
                            break;
                        case 71:
                            view.setBackgroundColor(Color.parseColor("#bb00ff"));
                            break;
                        case 72:
                            view.setBackgroundColor(Color.parseColor("#cc00ff"));
                            break;
                        case 73:
                            view.setBackgroundColor(Color.parseColor("#dd00ff"));
                            break;
                        case 74:
                            view.setBackgroundColor(Color.parseColor("#ee00ff"));
                            break;
                        case 75:
                            view.setBackgroundColor(Color.parseColor("#ff00ff"));
                            break;
                    }
                    return view;
                }else{
                    //convertView缓存行布局
                    if (convertView == null) {
                        view2 = mInflater.inflate(R.layout.contacts_split_item, null);
                    }else {
                        if(convertView instanceof RelativeLayout){
                            view2 = convertView;
                        }else{
                            view2 = mInflater.inflate(R.layout.contacts_split_item, null);

                        }

                    }
                    TextView textView2 = (TextView) view2.findViewById(R.id.tv_split);
                    textView2.setText(getItem(position).getmPhoneBookLabel());
                    return view2;
                }
            }
        }

            //因为是从本地数据库加载，可不设置缓存等优化直接加载
           /* @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (!(getItem(position).getmContact_id().equals("split"))) {//判断是不是分割栏
                    View view = mInflater.inflate(R.layout.contacts_list_item, null);
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_person_pic);
                    TextView textView = (TextView) view.findViewById(R.id.tv_person_name);
                    if( dao.getBitmap(getItem(position).getmContact_id(), ContactsListActivity.this) == null) {
                        imageView.setImageResource(R.drawable.person_pic);//数据库中没有保存的头像时设置为默认头像
                    }else{
                        imageView.setImageBitmap( dao.getBitmap(getItem(position).getmContact_id(), ContactsListActivity.this));
                    }
                    textView.setText(getItem(position).getmName());
                    //设置行颜色渐变特效

                    return view;
                } else {
                    View view2 = mInflater.inflate(R.layout.contacts_split_item, null);
                    TextView tv = (TextView) view2.findViewById(R.id.tv_split);
                    tv.setText(getItem(position).getmPhoneBookLabel());

                    return view2;
                }

            }
        }*/

            /*@Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View view1 = null;
                View view2 = null;

                if(!(getItem(position).getmContact_id().equals("split")) ){
                    ViewHolder holder;
                        //convertView缓存行布局
                        if (convertView == null) {
                            //新建一个ViewHolder对象来接收缓存数据，缓存的是控件的实例
                            holder = new ViewHolder();

                            //开始时所有的view都缓存进convertView
                            view1 = mInflater.inflate(R.layout.contacts_list_item, null);
                        //得到各个控件的对象
                            holder.imageView = (ImageView) view1.findViewById(R.id.iv_person_pic);
                            holder.textView = (TextView) view1.findViewById(R.id.tv_person_name);
                            view1.setTag(holder);//绑定ViewHolder对象
                            //设置TextView显示的内容，即我们存放在动态数组中的数据
                        holder.imageView.setImageResource(R.drawable.person_pic);//这里暂时全部设置为默认头像
                        holder.textView.setText(getItem(position).getmName());
                            convertView = view1;
                        }else if(convertView instanceof LinearLayout) {
                            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
                        }else {
                            //开始时所有的view都缓存进convertView
                            view1 = mInflater.inflate(R.layout.contacts_list_item, null);
                            //得到各个控件的对象
                            holder.imageView = (ImageView) view1.findViewById(R.id.iv_person_pic);
                            holder.textView = (TextView) view1.findViewById(R.id.tv_person_name);
                            view1.setTag(holder);//绑定ViewHolder对象
                        }
                    //设置TextView显示的内容，即我们存放在动态数组中的数据
                    holder.imageView.setImageResource(R.drawable.person_pic);//这里暂时全部设置为默认头像
                    holder.textView.setText(getItem(position).getmName());
                    }else{
                    //convertView缓存行布局
                    if (convertView == null) {
                        view2 = mInflater.inflate(R.layout.contacts_split_item, null);
                    }else {
                        if(convertView instanceof RelativeLayout){
                            view2 = convertView;
                        }else{
                            view2 = mInflater.inflate(R.layout.contacts_split_item, null);

                        }

                    }
                    TextView textView2 = (TextView) view2.findViewById(R.id.tv_split);
                    textView2.setText(getItem(position).getmPhoneBookLabel());
                    convertView = view2;
                }
                return convertView;
        }
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
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
