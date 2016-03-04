package dong.lan.tuyi.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 桂栋 on 2015/7/23.
 *
 * @author 桂栋
 *         <p/>
 *         图忆的的评论数据类
 */
public class TuyiComment extends BmobObject {

    private String comInfo;         //评论内容
    private UserTuyi userTuyi;      //所评论的图忆
    private TUser comUser;          //评论的用户

    public TUser getComUser() {
        return comUser;
    }

    public void setComUser(TUser comUser) {
        this.comUser = comUser;
    }

    public String getComInfo() {
        return comInfo;
    }

    public void setComInfo(String comInfo) {
        this.comInfo = comInfo;
    }

    public UserTuyi getUserTuyi() {
        return userTuyi;
    }

    public void setUserTuyi(UserTuyi userTuyi) {
        this.userTuyi = userTuyi;
    }
}
