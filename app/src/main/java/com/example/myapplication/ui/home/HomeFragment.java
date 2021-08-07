package com.example.myapplication.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.IccOpenLogicalChannelResponse;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.ntt.customgaugeview.library.GaugeView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    TextView textResponse,meter_name,meter_name2;
    Thread threadD;
    GaugeView gaugeView,gaugeView_back;
    GaugeView gaugeView2,gaugeView2_back;
    TextView volt,volt2,current,current2,power,power2,freq,freq2,kwh1,kwh2,pf1,pf2;

    ToggleButton toggleButton2,toggleButton;
    Spinner UDPspinner;

    String spinnerSelect="";
    final String[] values = {"點擊讀取addr"};






    protected void loadGaughView(View root){
        gaugeView = (GaugeView) root.findViewById(R.id.gauge_view);
        gaugeView.setShowRangeValues(true);
        gaugeView.setTargetValue(0);
        gaugeView.setAlpha((float)0.1);

        gaugeView2 = (GaugeView) root.findViewById(R.id.gauge_view2);
        gaugeView2.setShowRangeValues(true);
        gaugeView2.setTargetValue(0);
        gaugeView2.setAlpha((float)0.1);

        gaugeView_back = (GaugeView) root.findViewById(R.id.gauge_view_back);
        gaugeView_back.setShowRangeValues(true);
        gaugeView_back.setTargetValue(0);
        gaugeView_back.setAlpha((float)0.1);

        gaugeView2_back = (GaugeView) root.findViewById(R.id.gauge_view2_back);
        gaugeView2_back.setShowRangeValues(true);
        gaugeView2_back.setTargetValue(0);
        gaugeView2_back.setAlpha((float)0.1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home, container, false);

        loadGaughView(root);

        threadD=new Thread(gogogo);
        threadD.start();



        UDPspinner = (Spinner) root.findViewById(R.id.spinner);
        ArrayAdapter<String> lunchList = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                values);
        UDPspinner.setAdapter(lunchList);
        UDPspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putString("MACaddr",values[position]);
                edt.commit();
                String s=pref.getString("MACaddr","0");
                Toast.makeText(getActivity(), "您選擇了:" + s, Toast.LENGTH_SHORT).show();

                spinnerSelect=values[position];


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        //// 連上之後彩色指針，並給一個值的範例 開始-->
        //
        //
        //
        //
        gaugeView.setAlpha((float)1.0);
        gaugeView.setTargetValue((float)0);

        gaugeView_back.setAlpha((float)0.0);
        gaugeView_back.setTargetValue((float)0);

        gaugeView2.setAlpha((float)1.0);
        gaugeView2.setTargetValue((float)0);

        gaugeView2_back.setAlpha((float)0.0);
        gaugeView2_back.setTargetValue((float)0);

        //
        //
        //
        //// 連上之後彩色指針，並給一個值的範例 結束<--


        meter_name=(TextView)root.findViewById(R.id.textView10);
        meter_name2=(TextView)root.findViewById(R.id.textView17);

        textResponse=(TextView)root.findViewById(R.id.inputs);
        volt=(TextView)root.findViewById(R.id.volt);
        volt2=(TextView)root.findViewById(R.id.volt2);
        current=(TextView)root.findViewById(R.id.curren1);
        current2=(TextView)root.findViewById(R.id.curren2);
        power=(TextView)root.findViewById(R.id.power1);
        power2=(TextView)root.findViewById(R.id.power2);
        freq=(TextView)root.findViewById(R.id.frenq);
        freq2=(TextView)root.findViewById(R.id.frenq2);

        kwh1=(TextView)root.findViewById(R.id.kwh1);
        kwh2=(TextView)root.findViewById(R.id.kwh2);
        pf1=(TextView)root.findViewById(R.id.pf1);
        pf2=(TextView)root.findViewById(R.id.pf2);

        toggleButton2 = (ToggleButton)root.findViewById(R.id.toggleButton2);
        toggleButton2.setOnClickListener(gaugeView_change2);

        toggleButton = (ToggleButton)root.findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(gaugeView_change);


        SharedPreferences pref2 =getActivity().getPreferences(Context.MODE_PRIVATE);
        String metername= pref2.getString("metername","");
        String metername2= pref2.getString("metername2","");
        meter_name.setText(metername);
        meter_name2.setText(metername2);

        return root;
    }




    private  Runnable gogogo=new  Runnable(){
        @Override
        public void run() {
            try {
                while (true){


            Log.e("spinnervalue",spinnerSelect);

            SharedPreferences pref2 =getActivity().getPreferences(Context.MODE_PRIVATE);
            String trys= pref2.getString("DATA","00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00");
            Log.e("trys",trys);
            String[] tokens = trys.split(",");
            float voltd=Integer.valueOf(tokens[0]) + (Integer.valueOf(tokens[1])<<8);
            voltd=voltd/100.0f;
            float current1d=(Integer.valueOf(tokens[2]) + (Integer.valueOf(tokens[3])<<8))/100.0f;
            float current2d=(Integer.valueOf(tokens[4]) + (Integer.valueOf(tokens[5])<<8))/100.0f;
            float power1d=(Integer.valueOf(tokens[6]) + (Integer.valueOf(tokens[7])<<8))/100.0f;
            float power2d=(Integer.valueOf(tokens[8]) + (Integer.valueOf(tokens[9])<<8))/100.0f;
            float frenqd=(Integer.valueOf(tokens[10]) + (Integer.valueOf(tokens[11])<<8))/100.0f;

            float PFd=(Integer.valueOf(tokens[12]))/100.0f;
            float PF2d=(Integer.valueOf(tokens[13]))/100.0f;
            String named=(tokens[14]);
            float kwh1d=(Integer.valueOf(tokens[15]) + (Integer.valueOf(tokens[16])<<8)+(Integer.valueOf(tokens[17])<<16))/10.0f;
            float kwh2d=(Integer.valueOf(tokens[18]) + (Integer.valueOf(tokens[19])<<8)+(Integer.valueOf(tokens[20])<<16))/10.0f;

            int SW1d=Integer.valueOf(tokens[21]);
            int SW2d=Integer.valueOf(tokens[22]);


            volt.setText("電壓:"+String.valueOf(voltd));
            volt2.setText("電壓:"+String.valueOf(voltd));
            current.setText("電流:"+String.valueOf(current1d));
            current2.setText("電流:"+String.valueOf(current2d));
            power.setText("功率:"+String.valueOf(power1d));
            power2.setText("功率:"+String.valueOf(power2d));
            freq.setText("頻率:"+String.valueOf(frenqd));
            freq2.setText("頻率:"+String.valueOf(frenqd));
            pf1.setText("功率因素:"+String.valueOf(PFd));
            pf2.setText("功率因素:"+String.valueOf(PF2d));
            kwh1.setText("千瓦·時:"+String.valueOf(kwh1d));
            kwh2.setText("千瓦·時:"+String.valueOf(kwh2d));


            gaugeView.setTargetValue((float)voltd);
            gaugeView2.setTargetValue((float)voltd);
            gaugeView_back.setTargetValue((float)current1d);
            gaugeView2_back.setTargetValue((float)current2d);

            threadD.sleep(200);
        }


    }
            catch (Exception e)
            {
                Log.e("name","error");
            }


        }
    };


    private View.OnClickListener gaugeView_change=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(toggleButton.isChecked()) //當按鈕狀態為選取時
            {
                gaugeView_back.setAlpha((float)1.0);
                gaugeView.setAlpha((float)0.0);


            }
            else //當按鈕狀態為未選取時
            {

                gaugeView_back.setAlpha((float)0);

                gaugeView.setAlpha((float)1.0);



            }

        }
    };

    private View.OnClickListener gaugeView_change2=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(toggleButton2.isChecked()) //當按鈕狀態為選取時
            {
                gaugeView2_back.setAlpha((float)1.0);

                gaugeView2.setAlpha((float)0.0);

            }
            else //當按鈕狀態為未選取時
            {
                gaugeView2_back.setAlpha((float)0.0);
                gaugeView2.setAlpha((float)1.0);

            }

        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}