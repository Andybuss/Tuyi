package dong.lan.tuyi.utils;

public interface PermissionListener {
    public void  onGranted();
    
    public void  onDenied();
    
    public void onShowRationale(String[] permissions);
}

