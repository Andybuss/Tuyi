package dong.lan.tuyi.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by 梁桂栋 on 2015/11/2 ： 下午12:58.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: Tuyi
 */
public class Album extends BmobObject {
    private String description;
    private TUser user;
    private List<UserTuyi> tuyis;
    private  String musicType;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public List<UserTuyi> getTuyis() {
        return tuyis;
    }

    public void setTuyis(List<UserTuyi> tuyis) {
        this.tuyis = tuyis;
    }
}
