package dong.lan.tuyi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import dong.lan.tuyi.R;

/**
 * Created by Dooze on 2015/9/26.
 */
public class FragmentColorAjust extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private SeekBar hueBar, staturationBar, lumBar;
    public static int MAX_VALUES = 255;
    public static int MID_VALUES = 127;
    private float hue = 0;
    private float staturation = 1;
    private float lum = 1;

    public interface seekBarChangeListener
    {
        void onSeekBarChange(float hue,float statu,float lum);
    }

    seekBarChangeListener changeListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            changeListener = (seekBarChangeListener) activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() +" should be implement SeekBarChangeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.color_adjust,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hueBar = (SeekBar) getView().findViewById(R.id.hueSeekbar);
        staturationBar = (SeekBar) getView().findViewById(R.id.saturationSeekbar);
        lumBar = (SeekBar) getView().findViewById(R.id.lumSeekbar);
        hueBar.setOnSeekBarChangeListener(this);
        staturationBar.setOnSeekBarChangeListener(this);
        lumBar.setOnSeekBarChangeListener(this);
        lumBar.setMax(MAX_VALUES);
        staturationBar.setMax(MAX_VALUES);
        hueBar.setMax(MAX_VALUES);
        hueBar.setProgress(MID_VALUES);
        staturationBar.setProgress(MID_VALUES);
        lumBar.setProgress(MID_VALUES);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.hueSeekbar:
                hue = (progress - MID_VALUES) * 1.0f / MID_VALUES * 180;
                break;
            case R.id.saturationSeekbar:
                staturation = progress * 1.0f / MID_VALUES;
                break;
            case R.id.lumSeekbar:
                lum = progress * 1.0f / MID_VALUES;
                break;
        }
        changeListener.onSeekBarChange(hue,staturation,lum);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
