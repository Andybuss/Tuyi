package dong.lan.tuyi.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 梁桂栋 on 2015/11/10.
 */
public class Interested extends BmobObject {

    private TUser user;

    public String geusername() {
        return username;
    }

    public void seusername(String username) {
        this.username = username;
    }

    private String username;
    private Integer zaji;
    private Integer lewan;
    private Integer renqing;
    private Integer meishi;
    private Integer meijing;
    private Boolean zj;
    private Boolean lw;
    private Boolean rq;
    private Boolean ms;
    private Boolean mj;

    public Boolean getZj() {
        return zj;
    }

    public void setZj(Boolean zj) {
        this.zj = zj;
    }

    public Boolean getLw() {
        return lw;
    }

    public void setLw(Boolean lw) {
        this.lw = lw;
    }

    public Boolean getRq() {
        return rq;
    }

    public void setRq(Boolean rq) {
        this.rq = rq;
    }

    public Boolean getMs() {
        return ms;
    }

    public void setMs(Boolean ms) {
        this.ms = ms;
    }

    public Boolean getMj() {
        return mj;
    }

    public void setMj(Boolean mj) {
        this.mj = mj;
    }

    public Interested()
    {}
    public Interested(TUser user,String username, Integer zaji, Integer lewan, Integer renqing, Integer meishi, Integer meijing) {
        this.user = user;
        this.username = username;
        this.zaji = zaji;
        this.lewan = lewan;
        this.renqing = renqing;
        this.meishi = meishi;
        this.meijing = meijing;
    }


    public TUser getUser() {

        return user;
    }

    public void setUser(TUser user) {
        this.user = user;
    }

    public Integer getZaji() {
        return zaji;
    }

    public void setZaji(Integer zaji) {
        this.zaji = zaji;
    }

    public Integer getLewan() {
        return lewan;
    }

    public void setLewan(Integer lewan) {
        this.lewan = lewan;
    }

    public Integer getRenqing() {
        return renqing;
    }

    public void setRenqing(Integer renqing) {
        this.renqing = renqing;
    }

    public Integer getMeishi() {
        return meishi;
    }

    public void setMeishi(Integer meishi) {
        this.meishi = meishi;
    }

    public Integer getMeijing() {
        return meijing;
    }

    public void setMeijing(Integer meijing) {
        this.meijing = meijing;
    }
}
