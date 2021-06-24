package com.daon.onorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daon.onorder.Model.NotificationModel;
import com.daon.onorder.Model.PrintOrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sPrint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class CallActivity extends Activity {

    ImageView call1_minus;
    ImageView call1_plus;
    TextView call1_text;
    RelativeLayout call1_layout;

    ImageView call2_minus;
    ImageView call2_plus;
    TextView call2_text;
    RelativeLayout call2_layout;

    ImageView call3_minus;
    ImageView call3_plus;
    TextView call3_text;
    RelativeLayout call3_layout;

    ImageView call4_minus;
    ImageView call4_plus;
    TextView call4_text;
    RelativeLayout call4_layout;

    ImageView call5_minus;
    ImageView call5_plus;
    TextView call5_text;
    RelativeLayout call5_layout;

    RelativeLayout call6_layout;

    LinearLayout call_btn;
    LinearLayout callcancle_btn;
    LinearLayout water_btn;
    LinearLayout side_btn;
    LinearLayout apron_btn;
    LinearLayout spoon_btn;
    boolean is_layout6 = false;
    boolean is_layout1 = false;
    boolean is_layout2 = false;
    boolean is_layout3 = false;
    boolean is_layout4 = false;

    String service_text;

    String tableNo;
    SharedPreferences pref;
    Sam4sPrint printer;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("order");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        pref = getSharedPreferences("pref", MODE_PRIVATE);


        call_btn = findViewById(R.id.callactivity_call);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_text = "직원호출";
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

                interfaceApi.postService(pref.getString("storecode", ""), pref.getString("table", ""), "직원호출", ts).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            sendFirebaseOrder(service_text);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                finish();
            }
        });

        water_btn = findViewById(R.id.callactivity_water);
        water_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_text = "물 요청";
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

                interfaceApi.postService(pref.getString("storecode", ""), pref.getString("table", ""), "물 요청", ts).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            sendFirebaseOrder(service_text);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                finish();
            }
        });

        side_btn = findViewById(R.id.callactivity_side_dish);
        side_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_text = "반찬 요청";
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);
                interfaceApi.postService(pref.getString("storecode", ""), pref.getString("table", ""), "반찬 요청", ts).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        sendFirebaseOrder(service_text);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
                finish();
            }
        });

        apron_btn = findViewById(R.id.callactivity_apron);
        apron_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_text = "앞치마 요청";
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

                interfaceApi.postService(pref.getString("storecode", ""), pref.getString("table", ""), "앞치마 요청", ts).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            sendFirebaseOrder(service_text);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                finish();
            }
        });

        spoon_btn = findViewById(R.id.callactivity_spoon);
        spoon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service_text = "수저요청";
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

                interfaceApi.postService(pref.getString("storecode", ""), pref.getString("table", ""), "수저 요청", ts).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            sendFirebaseOrder(service_text);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                finish();
            }
        });

        callcancle_btn = findViewById(R.id.callactivity_cancel);
        callcancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void sendFirebaseOrder(String order){
        Log.d("daon_test", order);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());

        String time = format.format(calendar.getTime());
        String time2 = format2.format(calendar.getTime());
        PrintOrderModel printOrderModel = new PrintOrderModel(pref.getString("table", "")+"번 호출", order, time, "x", "service","","","","","","","","","");

        myRef.child(pref.getString("storename", "")).child(time2).push().setValue(printOrderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("daon_test", "주문성공");
                finish();
            }
        });
    }

    public void setPrint(String order){
        Sam4sPrint sam4sPrint = new Sam4sPrint();
        sam4sPrint = AdminApplication.getPrinter();
        try {

            if (!sam4sPrint.IsConnected(Sam4sPrint.DEVTYPE_ETHERNET)) {
                try {
                    setPrint(order);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
                    String time = format.format(calendar.getTime());
                    Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
                    try {
                        builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
                        builder.addFeedLine(2);
                        builder.addTextSize(3,3);
                        builder.addText(pref.getString("table", "")+"번 호출");
                        builder.addFeedLine(2);
                        builder.addTextSize(2,2);
                        builder.addTextAlign(builder.ALIGN_RIGHT);
                        builder.addText(order);
                        builder.addFeedLine(2);
                        builder.addTextSize(1,1);
                        builder.addText(time);
                        builder.addFeedLine(1);
                        builder.addCut(Sam4sBuilder.CUT_FEED);
                        sam4sPrint.sendData(builder);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendFCM(String body){
        Gson gson = new Gson();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = pref.getString("fcm", "");
        notificationModel.data.title = pref.getString("table", "") + "번 호출";
        notificationModel.data.body = body;
        notificationModel.data.type = "service";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAAclYgQIg:APA91bGHiG0iXAbhMJvs8pyZpnC7YYtmbBaN6f5adu1uI1GQEZwji8ALkUJXmJ5ttUM2NCCtoDTirvD-WGr9dmhjf7Clq6u4bTvIKf-Lb1JCSso-7a-r3x44Rw9w49byyI_Oxs5vp7vo")
                .url(" https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

            }
        });
    }
}