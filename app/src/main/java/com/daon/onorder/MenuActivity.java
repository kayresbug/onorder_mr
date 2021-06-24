package com.daon.onorder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daon.onorder.Model.MenuModel;
import com.daon.onorder.Model.NotificationModel;
import com.daon.onorder.Model.OrderModel;
import com.daon.onorder.Model.PrintOrderModel;
import com.daon.onorder.assist.AddressRepo;
import com.daon.onorder.assist.ESCPOSSample;
import com.daon.onorder.assist.ResourceInstaller;
import com.daon.onorder.fragment.CartFragment;
import com.daon.onorder.fragment.DetailFragment;
import com.daon.onorder.fragment.menu1Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sam4s.io.OnConnectListener;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sPrint;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.port.android.WiFiPortConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity{
    static TextView order_price;
    static int all_price = 0;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("order");

    TextView menu1;
    TextView menu2;
    TextView menu3;
    TextView menu4;
    TextView menu5;
    TextView menu6;
    TextView menu7;
    TextView menu8;
    TextView menu9;
    TextView table;
    ImageView call;
    ImageView call_driver;
    ImageView cart;
    RecyclerView recyclerView;
    RecyclerView order_recycler;
    ArrayList<TextView> menuBtn = new ArrayList<>();
    ArrayList<Object> menu_list = new ArrayList<>();
    ArrayList<Object> menu_listsize = new ArrayList<>();
    MenuAdapter adapter;
    OrderAdapter orderAdapter;
    LinearLayoutManager layoutManager2;
    RelativeLayout payment_layout;
    LinearLayout bodyLayout;
    String removePrice = "";
    FragmentManager fragmentManager;
    SharedPreferences pref;
    TaskTimer taskTimer = new TaskTimer(); // extends AsyncTask
    int basic = 60;
    int[] array_count1;
    /*
     * 취소거래용 변수
     * 예제는 직전취소이지만 특정 거래의 승인번호와 승인일자를 알고있는경우 이전거래 취소가능
     */
    String prevAuthNum = ""; //취소 거래용 승인번호
    String prevAuthDate = "";//취소 거래용 승인일자

    String prevClassfication = "";

    String vanTr = "";          //VanTr취소용(무카드 취소) 거래거유번호 저장
    String prevCardNo = "";     //VanTr취소용(무카드 취소) 마스킹카드번호 저장

    String cardname = "";
    String company = "";

    ArrayList<OrderModel> order_list = new ArrayList<>();
    ArrayList<OrderModel> cart_list = new ArrayList<>();
    JsonArray categoryArray = new JsonArray();
    JsonArray menuArray;
    menu1Fragment menuFragment;
    Context context;
    boolean isOrder = false;

    Sam4sPrint printer;
    Sam4sPrint printer2;
    private WiFiTask wifiTasks;

    ProgressDialog progressDialog;
    String payMode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        checkPermission();

        context = this;
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        taskTimer.setTime(basic, context);
        taskTimer.execute("");
        table = findViewById(R.id.menuactivity_text_table);
        table.setText(pref.getString("table", ""));
        fragmentManager = getSupportFragmentManager();
        menuFragment = new menu1Fragment();
//        Iterator<String> iter = AddressRepo.getInstance().getIterator();

//            wifiTasks = new WiFiTask();
//            wifiTasks.execute("192.168.20.42", "1");

//        setPrint();
//        printer = AdminApplication.getPrinter();
//        printer2 = AdminApplication.getPrinter2();
        menu1 = findViewById(R.id.menuactivity_text_btn1);
        menu2 = findViewById(R.id.menuactivity_text_btn2);
        menu3 = findViewById(R.id.menuactivity_text_btn3);
        menu4 = findViewById(R.id.menuactivity_text_btn4);
        menu5 = findViewById(R.id.menuactivity_text_btn5);
        menu6 = findViewById(R.id.menuactivity_text_btn6);
        menu7 = findViewById(R.id.menuactivity_text_btn7);
        menu8 = findViewById(R.id.menuactivity_text_btn8);
        menu9 = findViewById(R.id.menuactivity_text_btn9);
        call = findViewById(R.id.menuactivity_img_call);
        call_driver = findViewById(R.id.menuactivity_img_calldriver);
        JSONArray jsonArray = new JSONArray();
        // 리사이클러뷰에 표시할 데이터 리스트 생성.
        ArrayList<TextView> menuBtn = new ArrayList<TextView>();
        menuBtn.add(menu1);
        menuBtn.add(menu2);
        menuBtn.add(menu3);
        menuBtn.add(menu4);
        menuBtn.add(menu5);
        menuBtn.add(menu6);
        menuBtn.add(menu7);
        menuBtn.add(menu8);
        menuBtn.add(menu9);

        bodyLayout = findViewById(R.id.menuactivity_layout_menu);

        Log.d("daon_test", "id1 = "+pref.getString("storecode", ""));

        appendLog("id = "+pref.getString("storecode", ""));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.164.232.164:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);
        interfaceApi.getCategory(pref.getString("storecode", "")).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    categoryArray = response.body();
                    Log.d("daon_test", "responce = "+response.body());
                    for (int i = 0; i < categoryArray.size(); i++) {
                        String aa = categoryArray.get(i).toString();
                        aa = aa.replace("\\", "");
                        aa = aa.substring(2, aa.length()-2);

                        try {
                            JSONObject jsonObject = new JSONObject(aa);
                            menuBtn.get(i).setText(jsonObject.get("name").toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        menuBtn.get(i).setText(jsonObject.get("name").toString().replace("\"", ""));

                    }
                    setList();
                }else{

                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d("daon", "fail = "+t.getMessage());

            }
        });

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CartFragment cartFragment = new CartFragment();
//                Log.d("daon", "size = " + cart_list.size());
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("list", cart_list);
//                cartFragment.setArguments(bundle);
//                fragmentManager.beginTransaction().replace(R.id.menuactivity_layout_menu, cartFragment).commit();
//                setPayment(removePrice,"cancle");
            }
        });
        order_price = findViewById(R.id.menuactivity_text_price);
        payment_layout = findViewById(R.id.menuactivity_layout_payment);
        payment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);
                Log.d("daon_test", "isorder = "+isOrder);
                if (!isOrder) {
                    if (order_list.size() > 0) {
//                        sendData();
//                        setPayment(String.valueOf(all_price), "credit");
                        PaymentDialog paymentDialog = new PaymentDialog(context, new PaymentDialogClickListener() {
                            @Override
                            public void onCardClick() {
                                Log.d("daon_test", "card");
                                setPayment(String.valueOf(all_price), "credit");

                            }

                            @Override
                            public void onPayClick() {
                                Log.d("daon_test", "pay");
                                sendData();
                            }

                            @Override
                            public void onGiftClick() {
                                isOrder = false;
                                Log.d("daon_test", "gift");
//                                payMode = "gift";
//                                setPayment(String.valueOf(all_price), "credit");

//                                sendData();
                            }
                        });
                        paymentDialog.setCanceledOnTouchOutside(false);
                        paymentDialog.setCancelable(false);
                        paymentDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        paymentDialog.show();
                        isOrder = true;
                    }
                }
            }
        });

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
//        recyclerView = findViewById(R.id.recycler1);
//        recyclerView.setHasFixedSize(true);

        order_recycler = findViewById(R.id.order_recycler);
        order_recycler.setHasFixedSize(true);

//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)) ;
//        layoutManager = new LoopingLayoutManager(
//                this,                           // Pass the context.
//                LoopingLayoutManager.HORIZONTAL,  // Pass the orientation. Vertical by default.
//                false                           // Pass whether the views are laid out in reverse.
//                // False by default.
//        );


//        for (int i=0; i<10; i++) {
//            list.add(String.format("TEXT %d", i)) ;
//        }


        findViewById(R.id.menuactivity_layout_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffcf5d"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));

                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                bundle.putInt("position", 0);
                menuFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });

        findViewById(R.id.menuactivity_layout_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                bundle.putInt("position", 13);

                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();

                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffcf5d"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
//                adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.menuactivity_layout_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffcf5d"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                bundle.putInt("position", 16);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffcf5d"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                bundle.putInt("position", 19);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();


            }
        });
        findViewById(R.id.menuactivity_layout_btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffcf5d"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                bundle.putInt("position", 24);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffcf5d"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                bundle.putInt("position", 17);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffcf5d"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                bundle.putInt("position", 24);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffcf5d"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                bundle.putInt("position", array_count1[1]);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffcf5d"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(8));
                menuFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);
                Intent intent = new Intent(MenuActivity.this, CallActivity.class);
                startActivity(intent);
//                sendFirebasecall();
            }
        });
        call_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);
            }
        });
    }

    public void checkPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    public void setList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.164.232.164:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);
        interfaceApi.getMenu(pref.getString("storecode", "")).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray jsonArray1 = response.body();
                    ArrayList<MenuModel> list = new ArrayList<>();
                    array_count1 = new int[categoryArray.size()];
                    for (int z = 0; z < categoryArray.size(); z++) {
                        String aa = categoryArray.get(z).toString();
                        aa = aa.replace("\\", "");
                        aa = aa.substring(2, aa.length()-2);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(aa);
                            int checkcount = 0;
                            for (int i = 0; i < jsonArray1.size(); i++) {

                                MenuModel menuModel = new MenuModel();

                                String bb = jsonArray1.get(i).toString();
                                bb = bb.replace("\\", "");
                                bb = bb.substring(2, bb.length()-2);
                                Log.d("daon_Test", bb);
                                JSONObject menuObject = new JSONObject(bb);
                                if (jsonObject.get("code").equals(menuObject.get("ctgcode"))) {
                                    checkcount++;
                                    menuModel.setName(menuObject.get("name").toString().replace("\"", ""));
                                    menuModel.setPicurl(menuObject.get("picurl").toString().replace("\"", ""));
                                    menuModel.setPrice(menuObject.get("price").toString().replace("\"", ""));
                                    menuModel.setCode(menuObject.get("code").toString().replace("\"", ""));
                                    menuModel.setInfo(menuObject.get("info").toString().replace("\"", ""));
                                    Log.d("daon", menuObject.get("name").toString());
                                    list.add(menuModel);
                                }
                                menu_listsize.add(list);
                            }
                            if (z == 0) {
                                array_count1[z] = checkcount;
                            }else{
                                array_count1[z] = array_count1[z-1] + checkcount;
                            }
                            menu_list.add(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    for (int i = 0; i < array_count1.length; i++){
                        Log.d("daon_test", "array size = "+array_count1[i]);
                    }
                    menu1Fragment menuFragment = new menu1Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", list);
                    menuFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
                Log.d("daon", "error = "+t.getMessage());
            }
        });
    }

    public void setPosition(int count) {
//        ArrayList<MenuModel> list = new ArrayList<>();
//        int menu_count = 0;
//
//        if (count == 3) {
//            menu1.setTextColor(Color.parseColor("#ffcf5d"));
//            menu2.setTextColor(Color.parseColor("#ffffff"));
//            menu3.setTextColor(Color.parseColor("#ffffff"));
//            menu4.setTextColor(Color.parseColor("#ffffff"));
//            menu5.setTextColor(Color.parseColor("#ffffff"));
//            menu6.setTextColor(Color.parseColor("#ffffff"));
//            menu7.setTextColor(Color.parseColor("#ffffff"));
//            menu8.setTextColor(Color.parseColor("#ffffff"));
//            menu9.setTextColor(Color.parseColor("#ffffff"));
//        } else if (count > array_count1[0] && count < array_count1[1]) {
//            menu1.setTextColor(Color.parseColor("#ffffff"));
//            menu2.setTextColor(Color.parseColor("#ffcf5d"));
//            menu3.setTextColor(Color.parseColor("#ffffff"));
//            menu4.setTextColor(Color.parseColor("#ffffff"));
//            menu5.setTextColor(Color.parseColor("#ffffff"));
//            menu6.setTextColor(Color.parseColor("#ffffff"));
//            menu7.setTextColor(Color.parseColor("#ffffff"));
//            menu8.setTextColor(Color.parseColor("#ffffff"));
//            menu9.setTextColor(Color.parseColor("#ffffff"));
//        } else if (count > array_count1[1] && count < array_count1[2]) {
//            menu1.setTextColor(Color.parseColor("#ffffff"));
//            menu2.setTextColor(Color.parseColor("#ffffff"));
//            menu3.setTextColor(Color.parseColor("#ffcf5d"));
//            menu4.setTextColor(Color.parseColor("#ffffff"));
//            menu5.setTextColor(Color.parseColor("#ffffff"));
//            menu6.setTextColor(Color.parseColor("#ffffff"));
//            menu7.setTextColor(Color.parseColor("#ffffff"));
//            menu8.setTextColor(Color.parseColor("#ffffff"));
//            menu9.setTextColor(Color.parseColor("#ffffff"));
//        }else if (count > array_count1[2] && count < array_count1[3]) {
//            menu1.setTextColor(Color.parseColor("#ffffff"));
//            menu2.setTextColor(Color.parseColor("#ffffff"));
//            menu3.setTextColor(Color.parseColor("#ffffff"));
//            menu4.setTextColor(Color.parseColor("#ffcf5d"));
//            menu5.setTextColor(Color.parseColor("#ffffff"));
//            menu6.setTextColor(Color.parseColor("#ffffff"));
//            menu7.setTextColor(Color.parseColor("#ffffff"));
//            menu8.setTextColor(Color.parseColor("#ffffff"));
//            menu9.setTextColor(Color.parseColor("#ffffff"));
//        }else if (count > array_count1[3] && count < array_count1[4]) {
//            menu1.setTextColor(Color.parseColor("#ffffff"));
//            menu2.setTextColor(Color.parseColor("#ffffff"));
//            menu3.setTextColor(Color.parseColor("#ffffff"));
//            menu4.setTextColor(Color.parseColor("#ffffff"));
//            menu5.setTextColor(Color.parseColor("#ffcf5d"));
//            menu6.setTextColor(Color.parseColor("#ffffff"));
//            menu7.setTextColor(Color.parseColor("#ffffff"));
//            menu8.setTextColor(Color.parseColor("#ffffff"));
//            menu9.setTextColor(Color.parseColor("#ffffff"));
//        }else if (count > array_count1[4] && count < array_count1[5]) {
//            menu1.setTextColor(Color.parseColor("#ffffff"));
//            menu2.setTextColor(Color.parseColor("#ffffff"));
//            menu3.setTextColor(Color.parseColor("#ffffff"));
//            menu4.setTextColor(Color.parseColor("#ffffff"));
//            menu5.setTextColor(Color.parseColor("#ffffff"));
//            menu6.setTextColor(Color.parseColor("#ffcf5d"));
//            menu7.setTextColor(Color.parseColor("#ffffff"));
//            menu8.setTextColor(Color.parseColor("#ffffff"));
//            menu9.setTextColor(Color.parseColor("#ffffff"));
//        }else if (count > array_count1[5] && count < array_count1[6]) {
//            menu1.setTextColor(Color.parseColor("#ffffff"));
//            menu2.setTextColor(Color.parseColor("#ffffff"));
//            menu3.setTextColor(Color.parseColor("#ffffff"));
//            menu4.setTextColor(Color.parseColor("#ffffff"));
//            menu5.setTextColor(Color.parseColor("#ffffff"));
//            menu6.setTextColor(Color.parseColor("#ffffff"));
//            menu7.setTextColor(Color.parseColor("#ffcf5d"));
//            menu8.setTextColor(Color.parseColor("#ffffff"));
//            menu9.setTextColor(Color.parseColor("#ffffff"));
//        }

    }
    public void event(){
        if (order_list.size() > 0) {
            removePrice = String.valueOf(all_price);
            orderAdapter.removeData();
            all_price = 0;
            order_price.setText("총 0원 주문하기");
            isOrder = false;
            menu1Fragment menuFragment = new menu1Fragment();
            Bundle bundle = new Bundle();
            menu1.setTextColor(Color.parseColor("#ffcf5d"));
            menu2.setTextColor(Color.parseColor("#ffffff"));
            menu3.setTextColor(Color.parseColor("#ffffff"));
            menu4.setTextColor(Color.parseColor("#ffffff"));
            menu5.setTextColor(Color.parseColor("#ffffff"));
            menu6.setTextColor(Color.parseColor("#ffffff"));
            menu7.setTextColor(Color.parseColor("#ffffff"));
            menu8.setTextColor(Color.parseColor("#ffffff"));
            menu9.setTextColor(Color.parseColor("#ffffff"));

            Log.d("daon", "aaaaaa");

            //put your ArrayList data in bundle
            bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
            bundle.putInt("position", 0);
            menuFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
        }
    }
    public void callCart(ArrayList<OrderModel> array) {
        orderAdapter = new OrderAdapter(MenuActivity.this, array);
        order_recycler.setAdapter(orderAdapter);
        layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        order_recycler.setLayoutManager(layoutManager2);
        Log.d("daon", "remove_item = " + all_price);

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        int price = 0;
        for (int i = 0; i < array.size(); i++) {
            price = price + (Integer.parseInt(array.get(i).getPrice()) * Integer.parseInt(array.get(i).getCount()));
        }
        all_price = price;
        String formattedStringPrice = myFormatter.format(all_price);

        order_price.setText("총 " + formattedStringPrice + "원 주문하기");
    }

    public void closeCart() {
        menu1Fragment menuFragment = new menu1Fragment();
        Bundle bundle = new Bundle();

        //put your ArrayList data in bundle
        bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(1));
        menuFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
    }

    public void callDetail(int i) {
        try {
            Log.d("daon_test", Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/download/logdata.txt");
        } catch (Exception e) {
            Log.d("daon_test ssssss", e.toString());
        }

        appendLog("call detail");

        Log.d("daon_test1 ", "Call = "+ i);
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        //put your ArrayList data in bundle
        bundle.putSerializable("list", (Serializable) (ArrayList<MenuModel>) menu_listsize.get(i));
        bundle.putInt("position", i);
        detailFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.menuactivity_layout_menu, detailFragment).commit();
    }
    public void closeDetail(int position) {
        menu1Fragment menuFragment = new menu1Fragment();
        Bundle bundle = new Bundle();

        //put your ArrayList data in bundle
        bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(1));
        menuFragment.setArguments(bundle);
        bundle.putInt("position", position);
        getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
    }

    public void callItem(String menu, String price, String url, String code, String count) {

        Log.d("daon", "call = " + menu);
        OrderModel order = new OrderModel();
        boolean isCount = false;
        if (order_list.size() > 0) {
            for (int i = 0; i < order_list.size(); i++) {
                Log.d("daon_test","name = "+order_list.get(i).getName());
                if (order_list.get(i).getName().equals(menu)) {
                    order.setCount(String.valueOf(Integer.parseInt(order_list.get(i).getCount()) + Integer.parseInt(count)));
                    order.setName(order_list.get(i).getName());
                    order.setPrice(price);
                    order.setUrl(url);
                    order.setMenuno(code);
                    order_list.set(i, order);
                    isCount = true;
                    break;
                }
            }
            if (!isCount) {
                order.setName(menu);
                order.setCount(count);
                order.setPrice(price);
                order.setUrl(url);
                order.setMenuno(code);
                order_list.add(order);
            }
        } else {
            order.setName(menu);
            order.setCount(count);
            order.setPrice(price);
            order.setUrl(url);
            order.setMenuno(code);
            order_list.add(order);
        }
        cart_list.clear();
        cart_list.addAll(order_list);
        orderAdapter = new OrderAdapter(MenuActivity.this, order_list);
        order_recycler.setAdapter(orderAdapter);
        layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        order_recycler.setLayoutManager(layoutManager2);
        all_price = all_price + Integer.parseInt(price);
        Log.d("daon", "remove_item = " + all_price);

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(all_price);

        order_price.setText("총 " + formattedStringPrice + "원 주문하기");

    }

    public void removeMenu(int price) {
        Log.d("daon_test", "allprice ="+all_price);
        Log.d("daon_test", "allprice ="+price);
        all_price = all_price - price;

        if (all_price > 0) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            String formattedStringPrice = myFormatter.format(all_price);
            order_price.setText("총 " + formattedStringPrice + "원 주문하기");
        } else {
            all_price = 0;
            order_price.setText("총 0원 주문하기");
            isOrder = false;
        }


    }

    public void sendData(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        String sendTime = format.format(calendar.getTime());

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        String order = "";
        String order_json = "{";
//        for (int i = 0; i < order_list.size(); i++) {
//            String order_name = order_list.get(i).getName();
//            order_name = order_name.replace("흑돼지", "");
//            order = order + order_name+" ##"+order_list.get(i).getCount()+"개###";
//        }
        if (order_list.size() > 0) {
            for (int i = 0; i < order_list.size(); i++) {

                order = order + order_list.get(i).getName()+" ##"+order_list.get(i).getCount()+"개###";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

                interfaceApi.postOrder(pref.getString("storecode",""), pref.getString("table",""), order_list.get(i).getMenuno(), order_list.get(i).getPrice(), order_list.get(i).getCount(),
                        String.valueOf(all_price), "Cash", order_list.get(i).getName(), ts,
                        pref.getString("table", "")+sendTime, "prevAuthDate", "vanTr", "prevCardNo").enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.d("daon", "success = ");

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                        Log.d("daon", "error = " + t.getMessage());
                    }
                });
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://15.164.232.164:5000/")
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);
            interfaceApi.payment(pref.getString("storecode", ""), "",
                    "", "", "",
                    "", "O", sendTime, "",
                    "", pref.getString("table", "")+sendTime, "",
                    "", "", "", "",
                    "", "", "", "",
                    "", "", "",
                    "Cash", "", "", "",
                    "", String.valueOf(all_price)).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("daon", "isSuccessful = " + response.isSuccessful());
                    appendLog("isSuccessful = " + response.isSuccessful());
                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    appendLog(t.getMessage());
                }
            });

//            sendFCM(order);
            sendFirebaseOrder(order, "cash");
        }else{
            isOrder = false;
        }

    }
    public void sendFirebaseOrder(String order, String type){
        String type1 = "번 주문";
        if (!payMode.equals("")){
            type1= "번 포장";
            payMode = "";
        }

        if (type.equals("cash")){
            type1 = "번 현금";
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());

        String time = format.format(calendar.getTime());
        String time2 = format2.format(calendar.getTime());
        PrintOrderModel printOrderModel = new PrintOrderModel(pref.getString("table", "")+type1, order, time, "x", "order", cardname, prevCardNo,prevAuthDate,prevAuthNum,company,company,vanTr,String.valueOf(all_price), type);

        myRef.child(pref.getString("storename", "")).child(time2).push().setValue(printOrderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    removePrice = String.valueOf(all_price);
                    orderAdapter.removeData();
                    all_price = 0;
                    order_price.setText("총 0원 주문하기");
                    isOrder = false;
                    appendLog("sendFirebaseOrder onComplete  금액 : " + removePrice + "  테이블 : " + pref.getString("table", ""));
                    Toast toast = Toast.makeText(MenuActivity.this, "주문이 전달되었습니다.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(MenuActivity.this, "다시 시도해 주세요.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    public String getTime()
    {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);
        return getTime;
    }

    public void appendLog(String text)
    {
        File logFile = null;
        if( Build.VERSION.SDK_INT < 29) logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lolSaveFiles/log.txt");
        else logFile = MenuActivity.this.getExternalFilesDir("/lolSaveFiles");

        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                Log.d("File", "saving error"+e.toString());

                e.printStackTrace();
            }
        }
        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile+"/logdata.txt", true));
            buf.append(getTime()+"    "+text);
            buf.newLine();
            buf.close();
            buf = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendFirebasecall(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());

        String time = format.format(calendar.getTime());
        String time2 = format2.format(calendar.getTime());
        PrintOrderModel printOrderModel = new PrintOrderModel(pref.getString("table", "")+"번 호출", "직원호출", time, "x", "order","","","","","","","","","call");
        DatabaseReference myRef1 = database.getReference("service");

        myRef1.child(pref.getString("storename", "")).child(time2).push().setValue(printOrderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("daon_test", "주문성공");
                Toast toast = Toast.makeText(MenuActivity.this, "주문이 전달되었습니다.", Toast.LENGTH_LONG);

//                finish();
            }
        });
    }
    public void sendOrder() {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        String order = "";
        String order_json = "{";
        for (int i = 0; i < order_list.size(); i++) {

            order = order + order_list.get(i).getName()+" ##"+order_list.get(i).getCount()+"개###";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://15.164.232.164:5000/")
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

            interfaceApi.postOrder(pref.getString("storecode",""), pref.getString("table",""), order_list.get(i).getMenuno(), order_list.get(i).getPrice(), order_list.get(i).getCount(),
                    String.valueOf(all_price), "Card", order_list.get(i).getName(), ts,
                    prevAuthNum, prevAuthDate, vanTr, prevCardNo).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.d("daon", "success = ");

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("daon", "error = " + t.getMessage());
                }
            });
        }
        Log.d("daon_test", "order = "+order);
        sendFirebaseOrder(order, "card");
    }


    public void setPayment(String amount, String type) {
        Log.d("daon", "payment = " + amount);
//        amount = "54000";
//        prevAuthNum = "36217123    ";
//        prevAuthDate = "210523";
        int i_amount = Integer.parseInt(amount);
        int tax = (i_amount/100)*10;
        int aamount = (i_amount/100)*90;
        HashMap<String, byte[]> m_hash = new HashMap<String, byte[]>();
        /*고정 사용필드*/
        m_hash.put("TelegramType", "0200".getBytes());                                    // 전문 구분 ,  승인(0200) 취소(0420)
        m_hash.put("DPTID", "AT0301942A".getBytes());                                     // 단말기번호 , 테스트단말번호 DPT0TEST03
//        m_hash.put("DPTID", "DPT0TEST03".getBytes());                                     // 단말기번호 , 테스트단말번호 DPT0TEST03
        m_hash.put("PosEntry", "S".getBytes());                                           // Pos Entry Mode , 현금영수증 거래 시 키인거래에만 'K'사용
        m_hash.put("PayType", "00".getBytes());                                           // [신용]할부개월수(default '00') [현금]거래자구분
        m_hash.put("TotalAmount", getStrMoneytoTgAmount(amount)); // 총금액
        m_hash.put("Amount", getStrMoneytoTgAmount(String.valueOf(aamount)));      // 공급금액 = 총금액 - 부가세 - 봉사료
        m_hash.put("ServicAmount", getStrMoneytoTgAmount("0"));                           // 봉사료
        m_hash.put("TaxAmount", getStrMoneytoTgAmount(String.valueOf(tax)));                              // 부가세
//        m_hash.put("TaxAmount", getStrMoneytoTgAmount("0"));                              // 부가세
        m_hash.put("FreeAmount", getStrMoneytoTgAmount("0"));                             // 면세 0처리  / 면세 1004원일 경우 총금액 1004원 봉사료(ServiceAmount),부가세(TaxAmount) 0원 공급금액 1004원/ 면세(FreeAmount)  1004원
        m_hash.put("AuthNum", "".getBytes());                                            //원거래 승인번호 , 취소시에만 사용
        m_hash.put("Authdate", "".getBytes());                                           //원거래 승인일자 , 취소시에만 사용
        m_hash.put("Filler", "".getBytes());                                              // 여유필드 - 판매차 필요시에만 입력처리
        m_hash.put("SignTrans", "N".getBytes());                                          // 서명거래 필드, 무서명(N) 50000원 초과시 서명 "N" => "S"변경 필수
        if (Long.parseLong(amount) > 50000)
            m_hash.put("SignTrans", "S".getBytes());                                          // 서명거래 필드, 무서명(N) 50000원 초과시 서명 "N" => "S"변경 필수
        m_hash.put("PlayType", "D".getBytes());                                           // 실행구분,  데몬사용시 고정값(D)
        m_hash.put("CardType", "".getBytes());                                            // 은련선택 여부필드 (현재 사용안함), "" 고정
        m_hash.put("BranchNM", "".getBytes());                                            // 가맹점명 ,관련 개발 필요가맹점만 입력 , 없을시 "" 고정
        m_hash.put("BIZNO", "".getBytes());                                               // 사업자번호 ,KSNET 서버 정의된 가맹정일경우만 사용, 없을 시"" 고정
        m_hash.put("TransType", "".getBytes());                                           // "" 고정
        m_hash.put("AutoClose_Time", "30".getBytes());                                    // 사용자 동작 없을 시 자동 종료 ex)30초 후 종료
        /*선택 사용필드*/
        //m_hash.put("SubBIZNO","".getBytes());                                            // 하위 사업자번호 ,하위사업자 현금영수증 승인 및 취소시 적용
        //m_hash.put("Device_PortName","/dev/bus/usb/001/002".getBytes());                 //리더기 포트 설정 필요 시 UsbDevice 인스턴스의 getDeviceName() 리턴값입력 , 필요없을경우 생략가능
        //m_hash.put("EncryptSign","A!B@C#D4".getBytes());                                 // SignTrans "T"일경우 KSCIC에서 서명 받지않고 해당 사인데이터로 승인진행, 특정업체사용

        ComponentName compName = new ComponentName("ks.kscic_ksr01", "ks.kscic_ksr01.PaymentDlg");

        Intent intent = new Intent(Intent.ACTION_MAIN);
        if (type.equals("credit")) {
            m_hash.put("ReceiptNo", "X".getBytes());  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else if (type.equals("cancle")) {

            //신용취소 호출 부
            m_hash.put("TelegramType", "0420".getBytes());  // 전문 구분 ,  승인(0200) 취소(0420)
            m_hash.put("ReceiptNo", "X".getBytes());        // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            m_hash.put("AuthNum", prevAuthNum.getBytes());
            m_hash.put("Authdate", prevAuthDate.getBytes());
        } else if (type.equals("cancleNocard")) {
            //신용 무카드 취소 호출부
            m_hash.put("TelegramType", "0420".getBytes()); // 전문 구분 ,  승인(0200) 취소(0420)
            m_hash.put("ReceiptNo", "X".getBytes());      // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            m_hash.put("VanTr", "011754615".getBytes());        // 거래고유번호 , 무카드 취소일 경우 필수 필드
            m_hash.put("Cardbin", "465887**********".getBytes());
            m_hash.put("AuthNum", prevAuthNum.getBytes());
            m_hash.put("Authdate", prevAuthDate.getBytes());
        }

        intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(compName);
        intent.putExtra("AdminInfo_Hash", m_hash);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        appendLog("onActivityResult resultCode" + String.valueOf(resultCode));
        if (resultCode == RESULT_OK && data != null) {
            HashMap<String, String> m_hash = (HashMap<String, String>) data.getSerializableExtra("result");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (m_hash.get("VanTr") == null){
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);
                interfaceApi.payment(pref.getString("storecode", ""), m_hash.get("Classification"), m_hash.get("TelegramType"), m_hash.get("Dpt_Id"), m_hash.get("Enterprise_Info"), m_hash.get("Full_Text_Num"),
                        m_hash.get("Status"), m_hash.get("Authdate"), m_hash.get("Message1"), m_hash.get("Message2"), m_hash.get("AuthNum"), m_hash.get("FranchiseID"),
                        m_hash.get("IssueCode"), m_hash.get("CardName"), m_hash.get("PurchaseCode"), m_hash.get("PurchaseName"), m_hash.get("Remain"),
                        m_hash.get("point1"), m_hash.get("point2"), m_hash.get("point3"), m_hash.get("notice1"), m_hash.get("notice2"), m_hash.get("CardType"),
                        m_hash.get("CardNo"), m_hash.get("SWModelNum"), m_hash.get("ReaderModelNum"), m_hash.get("VanTr"), m_hash.get("Cardbin"), String.valueOf(all_price)).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("daon", "isSuccessful = " + response.isSuccessful());
                        appendLog("isSuccessful = " + response.isSuccessful());
                        if (response.isSuccessful()) {
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                        appendLog(t.getMessage());
                    }
                });
                Toast.makeText(this, m_hash.get("Message1"), Toast.LENGTH_LONG).show();

            }else {
                if (m_hash != null) {
                    prevAuthNum = m_hash.get("AuthNum");
                    prevAuthDate = m_hash.get("Authdate");
                    prevClassfication = m_hash.get("Classification");
                    cardname = m_hash.get("CardName");
                    company = m_hash.get("PurchaseName");
                    vanTr = m_hash.get("VanTr");
                    prevCardNo = m_hash.get("CardNo");
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);
                interfaceApi.payment(pref.getString("storecode", ""), m_hash.get("Classification"), m_hash.get("TelegramType"), m_hash.get("Dpt_Id"), m_hash.get("Enterprise_Info"), m_hash.get("Full_Text_Num"),
                        m_hash.get("Status"), m_hash.get("Authdate"), m_hash.get("Message1"), m_hash.get("Message2"), m_hash.get("AuthNum"), m_hash.get("FranchiseID"),
                        m_hash.get("IssueCode"), m_hash.get("CardName"), m_hash.get("PurchaseCode"), m_hash.get("PurchaseName"), m_hash.get("Remain"),
                        m_hash.get("point1"), m_hash.get("point2"), m_hash.get("point3"), m_hash.get("notice1"), m_hash.get("notice2"), m_hash.get("CardType"),
                        m_hash.get("CardNo"), m_hash.get("SWModelNum"), m_hash.get("ReaderModelNum"), m_hash.get("VanTr"), m_hash.get("Cardbin"), String.valueOf(all_price)).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("daon", "isSuccessful = " + response.isSuccessful());
                        appendLog("isSuccessful = " + response.isSuccessful());
                        if (response.isSuccessful()) {
                            sendOrder();
                            prevAuthNum = m_hash.get("AuthNum");
                            prevAuthDate = m_hash.get("Authdate");
                            removePrice = String.valueOf(all_price);
                            orderAdapter.removeData();
                            all_price = 0;
                            order_price.setText("총 0원 주문하기");
                            isOrder = false;
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                        appendLog(t.getMessage());
                    }
                });
            }


        } else if (resultCode == RESULT_FIRST_USER && data != null) {
            //케이에스체크IC 초기버전 이후 가맹점 다운로드 없이 승인 가능
            //Toast.makeText(this, "케이에스체크IC 에서 가맹점 다운로드 후 사용하시기 바랍니다", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
        }
        // 수행을 제대로 하지 못한 경우
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "앱 호출 실패", Toast.LENGTH_LONG).show();
        }
        isOrder = false;

    }

    public byte[] getStrMoneytoTgAmount(String Money) {
        byte[] TgAmount = null;
        if (Money.length() == 0) {
//            Toast.makeText(MainActivity.this, "테스트 금액으로 승인진행", Toast.LENGTH_SHORT).show();
            return "000000001004".getBytes();
        } else {
            Long longMoney = Long.parseLong(Money.replace(",", ""));
            Money = String.format("%012d", longMoney);

            TgAmount = Money.getBytes();
            return TgAmount;
        }
    }

    public void sendFCM(String order){
        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();
        String fcm = pref.getString("fcm", "");
        notificationModel.to = fcm;
        notificationModel.data.title = pref.getString("table", "")+"번 주문";
        notificationModel.data.body = order;
        notificationModel.data.type = "order";
        notificationModel.data.table = pref.getString("table", "")+"번 테이블";


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAAclYgQIg:APA91bGHiG0iXAbhMJvs8pyZpnC7YYtmbBaN6f5adu1uI1GQEZwji8ALkUJXmJ5ttUM2NCCtoDTirvD-WGr9dmhjf7Clq6u4bTvIKf-Lb1JCSso-7a-r3x44Rw9w49byyI_Oxs5vp7vo")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("daon_test", "error = "+e.toString());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                removePrice = String.valueOf(all_price);
                                orderAdapter.removeData();
                                all_price = 0;
                                order_price.setText("총 0원 주문하기");
                                isOrder = false;
                            }
                        });
                    }
                }).start();

            }
        });

    }


    public void setPrint(){
        try {

            if (printer == null) {
                printer = new Sam4sPrint();
                printer2 = new Sam4sPrint();
                AdminApplication.setPrinter(printer, printer2);
                try {

                    printer.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "172.30.1.45", 9100);
                    printer.resetPrinter();
                    printer2.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "172.30.1.54", 9100);
                    printer2.resetPrinter();


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("daon", "print error = " + e.getMessage());
                }
            }

            if (!printer.IsConnected(Sam4sPrint.DEVTYPE_ETHERNET)) {
                try {

                    Log.d("daon", "print error = " + printer.getPrinterStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setPrinter(String order){
            try {
                printer = AdminApplication.getPrinter();
                printer2 = AdminApplication.getPrinter2();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
                String time = format.format(calendar.getTime());
                Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
                try {
//                        builder.addPageBegin();
//                        builder.addPageArea(100, 800, 800, 800);
//                        builder.addPagePosition(50, 30);
                    builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
                    builder.addFeedLine(2);
                    builder.addTextSize(3,3);
                    builder.addText(pref.getString("table", "")+"번 주문");
                    builder.addFeedLine(2);
                    builder.addTextSize(2,2);
                    builder.addTextAlign(builder.ALIGN_RIGHT);
                    builder.addText(order);
                    builder.addFeedLine(2);
                    builder.addTextSize(1,1);
                    builder.addText(time);
                    builder.addFeedLine(1);
                    builder.addCut(Sam4sBuilder.CUT_FEED);
                    printer.sendData(builder);
                    printer2.sendData(builder);
                    printer.closePrinter();
                    removePrice = String.valueOf(all_price);
                    orderAdapter.removeData();
                    all_price = 0;
                    order_price.setText("총 0원 주문하기");
                    Log.d("daon_test", "status = "+printer.getPrinterStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    public void timerReset(){
        taskTimer.setTime(basic);
    }

    public void loading() {
        //로딩
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(MenuActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려 주세요");
                        progressDialog.show();
                    }
                }, 0);
    }

    public void loadingEnd() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 0);
    }

//        @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }

    private void methods(String index, ESCPOSPrinter escp) throws IOException
    {
        ESCPOSSample sample = new ESCPOSSample();

        ResourceInstaller ri = new ResourceInstaller();
        ri.copyAssets(getAssets(), "temp");

        int m_index = Integer.valueOf(index);
        switch(m_index)
        {
            case 0:
                sample.Print_Sample1(escp);
                break;
            case 1:
                sample.Print_Sample2(escp);
                break;
            case 2:
                sample.Print_Image(escp);
                break;
            case 3:
                sample.Print_Bar_1D(escp);
                break;
            case 4:
                sample.Print_Bar_2D(escp);
                break;
            default:
        }
    }

    class WiFiTask extends AsyncTask<String, Void, Integer>
    {
        private WiFiPortConnection connection;
        private String address;

        public void closeConnection() throws InterruptedException, IOException
        {
            if(connection != null)
                connection.close();
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer result = Integer.valueOf(0);
            Log.d("printer_test", "inte = "+result);
            address = params[0];
            try
            {
                // Connection
                connection = WiFiPort.getInstance().open(params[0]);
                // Printing
                methods(params[1], new ESCPOSPrinter(connection));
                Thread.sleep(1000);
                // Disconnection
                connection.close();
            }
            catch (IOException e)
            {
                Log.e("printer_test",e.getMessage(),e);
                result = Integer.valueOf(-1);
            }
            catch (InterruptedException e)
            {
                Log.e("printer",e.getMessage(),e);
                result = Integer.valueOf(-2);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            int retVal = result.intValue();
            if(retVal < 0)
            {
                Toast.makeText(context, "["+address+"] Connection Failed.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        Log.d("daon_test", "on resume!!!"+taskTimer.getStatus());
//        taskTimer.cancel(true);
        if (taskTimer.getStatus() != AsyncTask.Status.RUNNING) {
            taskTimer = new TaskTimer();
            taskTimer.setTime(60, context);
            taskTimer.execute("");
        }

    }
}