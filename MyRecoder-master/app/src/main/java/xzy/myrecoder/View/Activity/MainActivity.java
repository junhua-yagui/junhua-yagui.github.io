package xzy.myrecoder.View.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import xzy.GreebDao.RecoderItemDao;
import xzy.myrecoder.Dao.MyDao;
import xzy.myrecoder.R;
import xzy.myrecoder.Tool.FileTool;
import xzy.myrecoder.DaoHelper.GreenDaoHelper;
import xzy.myrecoder.Tool.PermissionTool;
import xzy.myrecoder.Tool.RecoderTool;

public class MainActivity extends AppCompatActivity {


        private Button playBtn;
        private Button stopInterimBtn;
        private Button stopBtn;
        private Button fileBtn;
        private RecoderTool recoderTool = new RecoderTool();
        private MyDao myDao = new MyDao();
        private Animation animation;
        private Animation transAnimationone;
        private Animation transAnimationtwo;
        private ImageView musicPlayerImg;
        private ImageView transparentImgone;
        private ImageView transparentImgTwo;
        private boolean isStoping=true;

        @Override
        protected void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            init();
        }

        /*
        初始化
         */
        public void init()
        {
            stopInterimBtn = findViewById(R.id.stopInterimbtn);
            stopBtn = findViewById(R.id.stopbtn);
            playBtn = findViewById(R.id.playbtn);
            fileBtn = findViewById(R.id.filebtn);
            musicPlayerImg = findViewById(R.id.musicplayer);
            transparentImgone = findViewById(R.id.transparentone);
            transparentImgTwo = findViewById(R.id.transparenttwo);
            stopBtn.setEnabled(false);
            stopBtn.setAlpha((float) 0.5);

            //动态申请权限
            PermissionTool.applypermission(getApplicationContext(),MainActivity.this);
            //默认在SD根目录创建资源文件夹recoreder_res
            FileTool.createFile();

            setListener();
        }

        /*
        给各个按钮设置监听器
         */
        public void setListener()
        {
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playBtn.setVisibility(View.INVISIBLE);
                    stopInterimBtn.setVisibility(View.VISIBLE);
                    stopBtn.setEnabled(true);
                    stopBtn.setAlpha((float) 1);

                    rotateAnimation();
                    transparentAnimation();
                    recoderTool.startRecord();
                }
            });

            stopInterimBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playBtn.setVisibility(View.VISIBLE);
                    stopInterimBtn.setVisibility(View.INVISIBLE);
                    closeAnimation();

                    recoderTool.pauseRecord();
                }
            });

            stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String itemname=FileTool.getDefaultName();
                    //点击stop按钮就停止录音
                    recoderTool.stopRecord();
                    closeAnimation();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("是否保存？");
                    builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            playBtn.setVisibility(View.VISIBLE);
                            stopInterimBtn.setVisibility(View.INVISIBLE);

                            //将生成的文件信息存入数据库
                            myDao.defaultInsert(itemname);
                        }
                    });
                    builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            playBtn.setVisibility(View.VISIBLE);
                            stopInterimBtn.setVisibility(View.INVISIBLE);

                            //将已录的音频的文件删除
                            FileTool.deleteFile(itemname);
                        }
                    });
                    builder.create().show();

                    stopBtn.setAlpha((float) 0.5);
                    stopBtn.setEnabled(false);
                }
            });

            /*
            fileBtn的功能实现
            跳转至ItemListView
             */
            fileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAnimation();
                    Intent intent = new Intent(MainActivity.this,ListViewActivity.class);
                    startActivity(intent);
                }
            });

        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu)
        {
            getMenuInflater().inflate(R.menu.main_menu,menu);
            return true;
        }


        /*
        位于菜单栏的About
        跳转至AboutActivity
         */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.about)
        {
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, permissions[i] + "已授权", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, permissions[i] + "拒绝授权", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //旋转
    public void rotateAnimation()
    {
        //动画
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        musicPlayerImg.startAnimation(animation);
    }

    //虚化图片移动
    public void transparentAnimation()
    {
        isStoping=false;
        transAnimationone = AnimationUtils.loadAnimation(this,R.anim.transparent_animation);
        transAnimationtwo = AnimationUtils.loadAnimation(this,R.anim.transparenttwo_animation);
        transparentImgone.startAnimation(transAnimationone);
        transparentImgTwo.startAnimation(transAnimationtwo);
        transAnimationone.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isStoping==false)
                    transparentImgone.startAnimation(transAnimationone);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        transAnimationtwo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isStoping==false)
                    transparentImgTwo.startAnimation(transAnimationtwo);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //关闭动画
    public void closeAnimation()
    {
        if(musicPlayerImg.getAnimation()!=null && transparentImgone.getAnimation()!=null && transparentImgTwo.getAnimation()!=null) {
            musicPlayerImg.clearAnimation();//停止旋转
            transparentImgone.clearAnimation();
            transparentImgTwo.clearAnimation();
            isStoping=true;
        }
    }
}


