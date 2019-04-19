package com.example.unforgettable;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SetActivity extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review);
        View view = inflater.inflate(R.layout.activity_set, container, false);

        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
