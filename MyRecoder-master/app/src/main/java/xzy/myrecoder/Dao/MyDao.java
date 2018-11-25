package xzy.myrecoder.Dao;

import xzy.GreebDao.RecoderItemDao;
import xzy.myrecoder.DaoHelper.GreenDaoHelper;
import xzy.myrecoder.Model.RecoderItem;
import xzy.myrecoder.Tool.FileTool;
import xzy.myrecoder.Tool.TimeTool;

public class MyDao {
    private TimeTool timeTool = new TimeTool();
    private FileTool fileTool = new FileTool();
    private RecoderItemDao recoderItemDao = GreenDaoHelper.getDaoSession().getRecoderItemDao();

    /*
           获取音频名称，添加至数据库
     */
    public void defaultInsert(String itemname)
    {
        String date = TimeTool.getDate();
        String size = fileTool.getFileSize(itemname);
        String length = fileTool.getFileLength(itemname);
        RecoderItem recoderItem =new RecoderItem(null,itemname,size,length,date,false);
        recoderItemDao.insert(recoderItem);
    }

}
