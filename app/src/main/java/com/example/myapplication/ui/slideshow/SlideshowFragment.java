package com.example.myapplication.ui.slideshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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
import androidx.navigation.Navigation;
import androidx.navigation.NavController;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.home.HomeFragment;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    Button getin;
    NavController navController;
    EditText  usertext,passwordtext;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {


            }
        });



        usertext=(EditText)root.findViewById(R.id.editTextTextUser);
        passwordtext=(EditText)root.findViewById(R.id.editTextTextPassword);
        getin=(Button)root.findViewById(R.id.button2);
        getin.setOnClickListener(click1);
        navController = findNavController(this);

        TextView linktext = (TextView)root.findViewById(R.id.autolink);
        linktext.setMovementMethod(LinkMovementMethod.getInstance());

        SharedPreferences pref2 =getActivity().getPreferences(Context.MODE_PRIVATE);
        String password= pref2.getString("user_password","");
        String username= pref2.getString("user_admin","");
        usertext.setText(username);
        passwordtext.setText(password);

        return root;
    }


    private View.OnClickListener click1=new View.OnClickListener() {
        @Override
        public void onClick(View v) {




            SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("user_admin",usertext.getText().toString());
            edt.putString("user_password",passwordtext.getText().toString());
            edt.putInt("loginflag",1);
            edt.commit();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(pref.getInt("loginaccese",0)==1)
            {
                navController.navigate(R.id.nav_home);
            }
            else
            {
                Toast.makeText(getActivity(), "帳號密碼錯誤", Toast.LENGTH_LONG).show();
            }

        }
    };


}