package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.activity.PersonalHomepage;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.SignIn;
import com.yzx.bangbang.utils.netWork.Retro;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FrUser extends Fragment {
    View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_user, container, false);
        init();
        return v;
    }

    private void init() {
        ButterKnife.bind(this, v);
        if (context().user != null) {
            host_name.setText(context().user.getName());
            host_portrait.setImageURI(Retro.get_portrait_uri(context().user.getId()));
        }
    }

    @OnClick({R.id.button_exit_sign_in,
            R.id.button_recent_explore,
            R.id.individual_info_bar,
            R.id.button_balance})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_exit_sign_in:
                context().exit_sign_in_flag = true;
                context().startActivity(new Intent(context(), SignIn.class));
                break;
            case R.id.individual_info_bar:
                context().startActivity(new Intent(context(), PersonalHomepage.class));
                break;
        }
    }

    private Main context() {
        return (Main) getActivity();
    }

    @BindView(R.id.host_portrait)
    SimpleDraweeView host_portrait;
    @BindView(R.id.host_name)
    TextView host_name;
}
