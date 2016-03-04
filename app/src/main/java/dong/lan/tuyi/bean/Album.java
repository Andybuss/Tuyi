package dong.lan.tuyi.bean;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * Created by 梁桂栋 on 2015/11/2.
 */
public class Album extends BmobObject {
    TUser user;
    ArrayList<UserTuyi> tuyis;
    String musicType;

    public String getMusicType() {
        return musicType;
    }

    public void setMusicType(String musicType) {
        this.musicType = musicType;
    }

    public TUser getUser() {
        return user;
    }

    public void setUser(TUser user) {
        this.user = user;
    }

    public ArrayList<UserTuyi> getTuyis() {
        return tuyis;
    }

    public void setTuyis(ArrayList<UserTuyi> tuyis) {
        this.tuyis = tuyis;
    }
}
