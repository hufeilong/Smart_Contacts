package com.lol.contactsmanageapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lol.contactsmanageapplication.R;
import com.lol.contactsmanageapplication.Utils;
import com.lol.contactsmanageapplication.bean.CallRecordInfo;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.util.ArrayList;
import java.util.Map;

public class CallsRecordActivity extends ActionBarActivity {

    private static final String KEY_TRANSITION_EFFECT = "transition_effect";

    private JazzyListView mList;//JazzyListView
    private Map<String, Integer> mEffectMap;//存储代表动画效果种类的字符串和整型常量
    private int mCurrentTransitionEffect = JazzyHelper.ZIPPER;//当前动画效果
    private ArrayList<CallRecordInfo> mContactsPerson;//存储联系人对象的集合
    private ArrayList<CallRecordInfo> mPerson;//经处理加入时间分割线对象的集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsrecord_list);
        mList = (JazzyListView) findViewById(R.id.lv_calls);//通过空间id查找到JazzyListView
        initData();//从数据库获取数据并进行二次处理
        mList.setAdapter(new JazzylvAdapter(this));//为JazzyListView设置适配器

        if (savedInstanceState != null) {//这里用于重新加载动画效果设置
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT, JazzyHelper.ZIPPER);
            setupJazziness(mCurrentTransitionEffect);
        }

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mPerson.get(position).getmName() != "split"){
                    CallRecordInfo recordInfo = new CallRecordInfo();
                    recordInfo = mPerson.get(position);//将点击到的信息对象打包发到详情页面
                    Intent intent = new Intent(CallsRecordActivity.this,CallsRecordDetailActivity.class);
                    intent.putExtra("RecordDetail",recordInfo);
                    startActivity(intent);
                }
            }
        });
    }

    private void initData(){
        mContactsPerson = initDate();
        mPerson = getDate(mContactsPerson);
    }

    /*从数据库请求获得数据，方便使用，避免反复请求数据库*/
    private ArrayList<CallRecordInfo> initDate(){

        ArrayList<CallRecordInfo> infos = new ArrayList<>();

        //从数据库请求数据
//      ContactsDao dao = new ContactsDao(CallsRecordActivity.this);
//      infos = dao.getCallLogMessage(CallsRecordActivity.this);

        return infos;
    }

    //对联系人集合进行二次处理，方便适配器绑定数据时添加时间分割栏
    private ArrayList<CallRecordInfo> getDate(ArrayList<CallRecordInfo> personinfo){
        ArrayList<CallRecordInfo> infos = new ArrayList<CallRecordInfo>();
        //首先在最前面加入时间分割栏
        CallRecordInfo splitA = new CallRecordInfo("split");
        splitA.setmDate("今天");
        infos.add(splitA);
        int size = personinfo.size();
        for(int i=0;i<size-1;i++){
            //在通话记录日期变化的地方插入一个特别的通话记录对象，便于ListView加载保定数据时插入分割栏
            if(personinfo.get(i).getmDate() != personinfo.get(i+1).getmDate()){
                CallRecordInfo split = new CallRecordInfo("split");
                split.setmDate(personinfo.get(i + 1).getmDate());
                //先加当前通话记录，再在后面添加特别对象
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
        public CallRecordInfo getItem(int position) {
            return mPerson.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //因为是从本地数据库加载，可不设置缓存等优化直接加载
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(getItem(position).getmName() !="split") {
                View view = mInflater.inflate(R.layout.callsrecord_list_item, null);
                ImageView imageView_person = (ImageView) view.findViewById(R.id.iv_record_person);
                ImageView imageView_type = (ImageView) view.findViewById(R.id.iv_record_type);
                TextView textView_name = (TextView) view.findViewById(R.id.tv_record_name);
                TextView textView_phonenum = (TextView) view.findViewById(R.id.tv_record_phonenum);
                TextView textView_time = (TextView) view.findViewById(R.id.tv_record_time);
                if(getItem(position).getmContactIcon() == null) {
                    //没有指定头像时设置为默认头像
                    imageView_person.setImageResource(R.drawable.person_pic);
                }else{
                    //用户有指定头像时直接设置
                    imageView_person.setImageBitmap(getItem(position).getmContactIcon());
                }
                textView_name.setText(getItem(position).getmName());
                textView_phonenum.setText(getItem(position).getmNumber());
                textView_time.setText(getItem(position).getmDetailDate());
                switch (getItem(position).getmType()){
                    case 1:
                        imageView_type.setImageResource(R.drawable.type_in);
                        break;
                    case 2:
                        imageView_type.setImageResource(R.drawable.type_out);
                        break;
                    case 3:
                        imageView_type.setImageResource(R.drawable.type_in_norespond);
                        break;
                    default:break;
                }
                return view;
            }else{
                View view_split = mInflater.inflate(R.layout.callsrecord_split_item, null);
                TextView tv = (TextView) view_split.findViewById(R.id.tv_record_split);
                tv.setText(getItem(position).getmDate());
                return view_split;
            }

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
