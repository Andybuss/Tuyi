package dong.lan.tuyi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import dong.lan.tuyi.R;

/**
 * Created by Dooze on 2015/9/26.
 */
public class FragmentImageMatrix extends Fragment {


    private int ids[] = new int[]{R.id.a11,R.id.a12,R.id.a13,R.id.a14,R.id.a15,R.id.a21,R.id.a22,R.id.a23,R.id.a24,R.id.a25
            ,R.id.a31,R.id.a32,R.id.a33,R.id.a34,R.id.a35,R.id.a41,R.id.a42,R.id.a43,R.id.a44,R.id.a45};
    private EditText mItems[] = new EditText[20];
    private float colorMatrixs[] = new float[20];

    public interface onMatrixChangeListener
    {
        void onMatrixChange(float matric[]);

    }

    onMatrixChangeListener changeListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_affect,null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }

    private void getMatric() {
        for (int i = 0; i < 20; i++) {
            if(mItems[i].getText().toString().equals("") )
                mItems[i].setText("0");
             colorMatrixs[i] = Float.parseFloat(mItems[i].getText().toString());
        }
    }

    private void initView() {
        for (int i = 0; i < 20; i++) {
            mItems[i] = (EditText) getView().findViewById(ids[i]);
            mItems[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count>0) {
                        getMatric();
                        changeListener.onMatrixChange(colorMatrixs);
                        System.out.println(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            changeListener = (onMatrixChangeListener) activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() +" should be implement SeekBarChangeListener");
        }
    }
}
