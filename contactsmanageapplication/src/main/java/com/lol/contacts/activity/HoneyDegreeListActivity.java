package com.lol.contacts.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.lol.contacts.Dao.ContactsDao;
import com.lol.contacts.Dao.HoneyDegreeDao;
import com.lol.contacts.R;
import com.lol.contacts.Utils;
import com.lol.contacts.bean.HoneyContactInfo;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.util.ArrayList;
import java.util.Map;

public class HoneyDegreeListActivity extends ActionBarActivity {

    private static final String KEY_TRANSITION_EFFECT = "transition_effect";

    private JazzyListView mList;//JazzyListView
    private Map<String, Integer> mEffectMap;//存储代表动画效果种类的字符串和整型常量
    private int mCurrentTransitionEffect = JazzyHelper.ZIPPER;//当前动画效果
    private ArrayList<HoneyContactInfo> mContactsPerson;//存储联系人对象的集合
    private HoneyDegreeDao honeyDegreeDaodao;
    private ContactsDao dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_honey_degree_list);

        if (savedInstanceState != null) {//这里用于重新加载动画效果设置
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT, JazzyHelper.ZIPPER);
            setupJazziness(mCurrentTransitionEffect);
        }
        mList = (JazzyListView) findViewById(R.id.lv_calls);//通过空间id查找到JazzyListView
        initData();
        mList.setAdapter(new JazzylvAdapter(this));//为JazzyListView设置适配器
    }

    /*请求数据库得到数据*/
    private void initData(){
        dao = new ContactsDao(HoneyDegreeListActivity.this);
        honeyDegreeDaodao = new HoneyDegreeDao(HoneyDegreeListActivity.this);
        mContactsPerson = (ArrayList<HoneyContactInfo>) honeyDegreeDaodao.getContactOrderByScore();
        //得到存储数据的动态数组
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
            return mContactsPerson.size();
        }

        @Override
        public HoneyContactInfo getItem(int position) {
            return mContactsPerson.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //因为是从本地数据库加载，可不设置缓存等优化直接加载
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                View view = mInflater.inflate(R.layout.honey_degree_item, null);
            ImageView imageView_love = (ImageView) view.findViewById(R.id.iv_love_pic);
            ImageView imageView_person = (ImageView) view.findViewById(R.id.iv_honey_pic);
                TextView textView_name = (TextView) view.findViewById(R.id.tv_honey_name);
                TextView textView_love = (TextView) view.findViewById(R.id.tv_love_value);

            imageView_love.setImageResource(R.drawable.love);//亲密度红心图标
            if(dao.getBitmap( getItem(position).getContact_id(),HoneyDegreeListActivity.this ) == null) {//设置头像
                    //没有指定头像时设置为默认头像
                    imageView_person.setImageResource(R.drawable.person_pic);
                }else{
                    //用户有指定头像时直接设置
                    imageView_person.setImageBitmap(dao.getBitmap( getItem(position).getContact_id(),HoneyDegreeListActivity.this ));
                }
                textView_name.setText(getItem(position).getName());//姓名
                textView_love.setText(getItem(position).getScore());//亲密度

            return view;
        }
    }


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
