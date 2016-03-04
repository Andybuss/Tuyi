package dong.lan.tuyi.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Dooze on 2015/9/29.
 */
public class FeedBack extends BmobObject{
    private TUser user;
    private String feek;
    private String reply;

    public TUser getUser() {
        return user;
    }

    public void setUser(TUser user) {
        this.user = user;
    }

    public String getFeek() {
        return feek;
    }

    public void setFeek(String feek) {
        this.feek = feek;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
