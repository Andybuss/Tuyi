package dong.lan.tuyi.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 桂栋 on 2015/7/18.
 */
public class UserTuyi extends BmobObject {

    private TUser tUser;                 //图忆的所属用户
    private BmobGeoPoint tPoint;        //图忆的添加经纬度
    private String tPic;               //上传后的到的图忆图片的链接
    private String tContent;          //图忆的内容
    private boolean isPublic;        //图忆是否是公开的
    private String tUri;            //本地Uri地址
    private String time;           //发布时间，搭配createAt()使用
    private Integer zan;          //喜欢的计数器
    private BmobRelation likes;  //一对多关系，保存所有喜欢这个图忆的用户关系
    private String offlineNmae;
    private String locDes;
    private String TAG;

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getLocDes() {
        return locDes;
    }

    public void setLocDes(String locDes) {
        this.locDes = locDes;
    }

    public String getOfflineNmae() {
        return offlineNmae;
    }

    public void setOfflineNmae(String offlineNmae) {
        this.offlineNmae = offlineNmae;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public Integer getZan() {
        return zan;
    }

    public void setZan(Integer zan) {
        this.zan = zan;
    }

    public String gettUri() {
        return tUri;
    }

    public void settUri(String tUri) {
        this.tUri = tUri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TUser gettUser() {
        return tUser;
    }


    public void settUser(TUser tUser) {
        this.tUser = tUser;
    }

    public BmobGeoPoint gettPoint() {
        return tPoint;
    }

    public void settPoint(BmobGeoPoint tPoint) {
        this.tPoint = tPoint;
    }

    public String gettPic() {
        return tPic;
    }

    public void settPic(String tPic) {
        this.tPic = tPic;
    }

    public String gettContent() {
        return tContent;
    }

    public void settContent(String tContent) {
        this.tContent = tContent;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
