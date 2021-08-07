package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.MacAddress;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Delayed;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    NavController navController;

    private HomeFragment HFragment;

    public Thread threadA;
    private Socket clientSocket;
    private InputStreamReader dis;
    String tmp;
    boolean connectfrag;
    String ADDRT;
    String PORTT;
    InetAddress serverIp;
    int serverPort;
    boolean CONEACCEPT_FLAG=false;


    String datatofrag;
    SharedPreferences pref2;



    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //////////// test !!!!!!!!!!!! show gauge
        //startActivity(new Intent(this, GsActivity.class));


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_slideshow,R.id.nav_gallery, R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


//////////讀取檔案獲取IP與PORTD////////////////////////////////////////

        SharedPreferences pref2 = getPreferences(Context.MODE_PRIVATE);
        ADDRT = pref2.getString("ADDR","");
        PORTT = pref2.getString("PORT","");

//////////讀取檔案獲取IP與PORTD////////////////////////////////////////

        threadA=new Thread(Connection);
        threadA.start();

        Bundle bundle=new Bundle();
        bundle.putString("data","hello");

        SharedPreferences.Editor edt99 = pref2.edit();
        edt99.putInt("loginflag",0);
        edt99.commit();




    }

////////////////執行續////////////////////////////////////
    private  Runnable Connection=new  Runnable(){
                @Override
                public void run()
                {
                    while (true)
                {

                Log.e("thread","while");
                connectfrag=CheckConnect();

                if(connectfrag==false)
                {

                    continue;
                }
                else if(connectfrag==true)
                {
                    try
                    {

                        dis = new InputStreamReader(clientSocket.getInputStream());
                        OutputStreamWriter dwt=new OutputStreamWriter(clientSocket.getOutputStream());

                        BufferedReader br = new BufferedReader(dis);
                        BufferedWriter bt= new BufferedWriter(dwt);


                        while (clientSocket.isConnected())
                        {
                            int loginAccese=0;
                            String user_name= pref2.getString("user_admin","");
                            String user_password= pref2.getString("user_password","");
                            int logflag=pref2.getInt("loginflag",0);


                            if(logflag==1)
                            {
                                bt.write(user_name+"|"+user_password+"\r\n");
                                bt.flush();
                                SharedPreferences.Editor edt99 = pref2.edit();
                                edt99.putInt("loginflag",0);
                                edt99.commit();
                            }
                            else{continue;}

                            tmp = br.readLine();
                            Log.e("qqqqqqqqq",tmp);
                            int stattmp= Integer.parseInt(tmp);

                            if(stattmp==1)
                            {
                              loginAccese=1;
                              SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                              SharedPreferences.Editor edt = pref.edit();
                              edt.putInt("loginaccese",loginAccese);
                              edt.commit();
                              bt.write("ok"+"\r\n");
                              bt.flush();


                            }
                            else if(stattmp==0)
                            {
                               loginAccese=0;
                                SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor edt = pref.edit();
                                edt.putInt("loginaccese",loginAccese);
                                edt.commit();

                            }
                            Log.e("LLLLLLLL", String.valueOf(loginAccese));
                            String macaddress=pref2.getString("MACaddr","0");


                            while(loginAccese==1)
                            {
                                bt.write("MACADDR||"+macaddress);
                                bt.flush();
                                if(br.readLine()=="MA1") {
                                    while (macaddress==pref2.getString("MACaddr","0"))//判斷是否有修改
                                    {
                                        tmp = br.readLine();   //持續讀取資料
                                        pref2 = getPreferences(Context.MODE_PRIVATE);
                                        SharedPreferences.Editor edt;
                                        edt = pref2.edit();
                                        edt.putString("DATA", tmp);
                                        edt.commit();
                                        Log.e("socket", tmp);
                                    }
                                    bt.write("MAChange");   //讓server知道改變了macaddress
                                    bt.flush();
                                }
                                else if(br.readLine()=="MA0")
                                {
                                    macaddress=pref2.getString("MACaddr","0");
                                    bt.write("MACADDR||"+macaddress);
                                    bt.flush();

                                }
                            }
                        }

                    }
                    catch (java.io.IOException e)
                    {

                        clientSocket=null;
                        connectfrag=false;

                    }
                }

            }
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean CheckConnect()
    {
        try {
            Log.e("llllllllllllllll", "1");

                pref2 = getPreferences(Context.MODE_PRIVATE);

                ADDRT = pref2.getString("ADDR", "120.114.67.238");
                PORTT = pref2.getString("PORT", "9997");

                serverIp = InetAddress.getByName(ADDRT);
                serverPort = Integer.valueOf(PORTT);

                Log.e("ADDR", serverIp.toString());
                Log.e("llllllllllllllll", "2");


                clientSocket=new Socket();
                InetSocketAddress isa = new InetSocketAddress(serverIp,serverPort);
                clientSocket.connect(isa,5000);

                Log.e("llllllllllllllll", "3");

                if (clientSocket != null) {
                    CONEACCEPT_FLAG=true;

                }
                else
                {
                    CONEACCEPT_FLAG=false;

                    Log.e("llllllllllllllll", "4");
                    //???????????????????製作跳至設定ip之fragment?????????????
                }


        } catch (java.io.IOException e) {



            Log.e("msg1", "Socket連線有問題 !");
            Log.e("msg1", "IOException :" + e.toString());
            //???????????????????製作跳至設定ip之fragment?????????????
            /*HomeFragment fm1= new HomeFragment();
            FragmentManager FM = getSupportFragmentManager();
            FM.beginTransaction().replace(R.id.nav_gallery,fm1).commit();*/

            return false;


        }



        if(CONEACCEPT_FLAG == false)
        {
          return false;
        }
        else
        {
            return true;
        }

    }






}