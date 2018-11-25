package xzy.myrecoder.View.Activity;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import xzy.GreebDao.RecoderItemDao;
import xzy.myrecoder.View.Fragment.Deletefragament;
import xzy.myrecoder.View.Fragment.PlayFragment;
import xzy.myrecoder.View.Fragment.Renamefragment;
import xzy.myrecoder.R;
import xzy.myrecoder.DaoHelper.GreenDaoHelper;
import xzy.myrecoder.Adapter.RecoderAdapter;
import xzy.myrecoder.Model.RecoderItem;
import xzy.myrecoder.Tool.ShareTool;

public class ListViewActivity extends AppCompatActivity implements Deletefragament.deleteDialogListener {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ListView listView;
    private RecoderAdapter recoderAdapter;
    private ArrayList<RecoderItem> items = new ArrayList<>();
    private RecoderItemDao recoderItemDao = GreenDaoHelper.getDaoSession().getRecoderItemDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_item);
        init();
    }

    /*
    初始化listview
     */
    public void init()
    {
        listView = findViewById(R.id.list);

        //从数据库拿数据放在数组，再把数组给Adapter去填充Listview
        items = (ArrayList<RecoderItem>) recoderItemDao.loadAll();
        recoderAdapter = new RecoderAdapter(this,items);

        //设置listview适配器和按键监听
        listView.setAdapter(recoderAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnItemLongClickListener(onItemLongClickListener);
    }


/*
listView单击的效果
 */
       private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PlayFragment playFragment = new PlayFragment();
                playFragment.show(fragmentManager, "mediaPlayer_fragment");
                Bundle bundle = new Bundle();
                bundle.putLong("itemnum",items.get(position).getItemNum());
                playFragment.setArguments(bundle);
            }
        };


        /*
            listView长按的效果
         */
       private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String[] opitems = { "删除","重命名","分享" ,"保存到网盘"};
                final int index = position;//被选中的item的下标

                //长按后弹出的dialog
                AlertDialog.Builder listDialog = new AlertDialog.Builder(ListViewActivity.this);
                listDialog.setItems(opitems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case 0://删除
                                Deletefragament deletefragament  = new Deletefragament();
                                deletefragament.show(fragmentManager,"deletefragment");
                               Bundle bundle = new Bundle();
                               bundle.putLong("id",items.get(index).getItemNum());
                                deletefragament.setArguments(bundle);
                                break;
                            case 1://重命名
                                Renamefragment renamefragment = new Renamefragment();
                                renamefragment.show(fragmentManager,"renamefragment");
                                Bundle bundle2 = new Bundle();
                                bundle2.putLong("id",items.get(index).getItemNum());
                                renamefragment.setArguments(bundle2);
                                break;
                            case 2://分享
                                String name1 = items.get(index).getItemName();
                                ShareTool.shareAudio(ListViewActivity.this,name1);
                                break;
                            case 3://保存到网盘
                                String name2 = items.get(index).getItemName();
                                ShareTool.saveToDrive(ListViewActivity.this,0,name2);
                                break;
                            default:
                                break;
                        }
                    }
                });
                listDialog.show();
                return true;
            }
        };


    /*
        删除一个item后
        刷新listView
     */
    public void refresh(){
        items = (ArrayList<RecoderItem>) recoderItemDao.loadAll();
        recoderAdapter = new RecoderAdapter(this,items);
        listView.setAdapter(recoderAdapter);
    }

}

