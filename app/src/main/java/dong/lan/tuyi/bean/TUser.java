package dong.lan.tuyi.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 桂栋 on 2015/7/13.
 */
public class TUser extends BmobObject {
    private String username;
    private String pwd;
    private String head;
    private boolean sex;
    private String nick;
    private BmobGeoPoint loginPoint;
    private BmobRelation friends;
    private BmobRelation blacklists;
    private boolean publicMyPoint;
    private String des;
    private BmobRelation favoraite;

    public BmobRelation getFavoraite() {
        return favoraite;
    }

    public void setFavoraite(BmobRelation favoraite) {
        this.favoraite = favoraite;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public TUser(){};

    public TUser(String username,String id,String head,String des,String lat,String lng)
    {
        this.username = username;
        setObjectId(id);
        this.head = head;
        this.des =des;
        this.loginPoint = new BmobGeoPoint(Double.parseDouble(lng),Double.parseDouble(lat));
    }

    public BmobRelation getFriends() {
        return friends;
    }

    public void setFriends(BmobRelation friends) {
        this.friends = friends;
    }

    public BmobRelation getBlacklists() {
        return blacklists;
    }

    public void setBlacklists(BmobRelation blacklists) {
        this.blacklists = blacklists;
    }

    public BmobGeoPoint getLoginPoint() {
        return loginPoint;
    }

    public void setLoginPoint(BmobGeoPoint loginPoint) {
        this.loginPoint = loginPoint;
    }

    public boolean isPublicMyPoint() {
        return publicMyPoint;
    }

    public void setPublicMyPoint(boolean publicMyPoint) {
        this.publicMyPoint = publicMyPoint;
    }



    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
