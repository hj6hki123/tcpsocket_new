package com.example.myapplication.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.home.HomeFragment;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    Button cd;
    Button loginbutton;
    EditText ADDR,PORT;
    EditText user,password;
    NavController navController;
    SharedPreferences pref;
    public View onCreateView( LayoutInflater inflater,
                             ViewGroup containerc, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, containerc, false);
        cd = (Button)view.findViewById(R.id.setfin);
        cd.setOnClickListener(SET);

        loginbutton = (Button)view.findViewById(R.id.login);
        loginbutton.setOnClickListener(login_);

        ADDR=(EditText)view.findViewById(R.id.ADDR);
        PORT=(EditText)view.findViewById(R.id.PORT);

        user=(EditText)view.findViewById(R.id.USER);
        password=(EditText)view.findViewById(R.id.PASSWORD);

        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String ADDRT=pref.getString("ADDR","");
        String PORTT=pref.getString("PORT","");
        ADDR.setText(ADDRT);
        PORT.setText(PORTT);
        navController = findNavController(this);




        return view;

    }

    private View.OnClickListener SET=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("ADDR",ADDR.getText().toString());
            edt.putString("PORT",PORT.getText().toString());
            edt.commit();
            Toast.makeText(getActivity(), "設定完成", Toast.LENGTH_LONG).show();


        }
    };

    private View.OnClickListener login_=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("metername",user.getText().toString());
            edt.putString("metername2",password.getText().toString());
            edt.commit();
            Toast.makeText(getActivity(), "已變更名稱", Toast.LENGTH_LONG).show();


            navController.navigate(R.id.nav_home);



        }
    };



}