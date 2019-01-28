package com.techlogn.cleanmate_pos_v36;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by anucha on 1/3/2018.
 */

public class FragmentCancelOrderDetailLevel1 extends Fragment {

    View myView;
    TextView Text1,Text2,TextTitle,Text3;
    MyFont myFont;
    ArrayList<MyItemCancelOrderdetailLevel1> items;
    String dataID,branchID,orderNo;

    private GetIPAPI getIPAPI;

    private SwipeMenuListView mSwipeListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_cancel_orederdetail_level1, container, false);

        //Log
       /*Bugfender.init(getActivity(), "RlG2SafK3kOHo2XvAfqwEZMMOnLl0yGB", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(getActivity().getApplication());*/

       getIPAPI=new GetIPAPI();


        myFont=new MyFont(getActivity());
        items=new ArrayList<>();
        TextTitle=myView.findViewById(R.id.textTitle);
        Text1=myView.findViewById(R.id.textTitle5);
        Text2=myView.findViewById(R.id.textTitle6);
        Text3=myView.findViewById(R.id.textTitle7);
        Text1.setTypeface(myFont.setFont());
        Text2.setTypeface(myFont.setFont());
        Text3.setTypeface(myFont.setFont());
        TextTitle.setTypeface(myFont.setFont());



        Bundle bundle = this.getArguments();
        if (bundle != null) {
            orderNo = bundle.getString("orderNo");
            System.out.println("orderNo : "+orderNo);
        }

        TextView text5=myView.findViewById(R.id.textView5);
        text5.setTypeface(myFont.setFont());

        RelativeLayout btn_back=myView.findViewById(R.id.btn_scan);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentCancelOrderDetail fragmentCancelOrderDetail = new FragmentCancelOrderDetail();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame,fragmentCancelOrderDetail)
                        .commit();
            }
        });
        myView.setFocusableInTouchMode(true);
        myView.requestFocus();
        myView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        /*Intent intent = new Intent(getActivity(),MenuActivity.class);
                        startActivity(intent);
                        return true;*/
                    }
                }
                return false;
            }
        });

        mSwipeListView = myView.findViewById(R.id.listView);

        SharedPreferences sharedPreferences2=getActivity().getSharedPreferences("ID", Activity.MODE_PRIVATE);
        Map<String,?> entries2 = sharedPreferences2.getAll();
        Set<String> keys2 = entries2.keySet();
        String[] getData2;
        List<String> list2 = new ArrayList<String>(keys2);
        for (String temp : list2) {
            //System.out.println(temp+" = "+sharedPreferences.getStringSet(temp,null));
            for (int i = 0; i < sharedPreferences2.getStringSet(temp, null).size(); i++) {
                getData2 = sharedPreferences2.getStringSet(temp, null).toArray(new String[sharedPreferences2.getStringSet(temp, null).size()]);
                //System.out.println(temp + " : " + getData2[i]);
                char chk=getData2[i].charAt(1);
                if(chk=='a'){
                    dataID=getData2[i].substring(3);
                }else if(chk=='b'){
                    branchID=getData2[i].substring(3);
                }
            }
        }
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setIcon(R.mipmap.loading);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("กรุณารอสักครู่....");
            dialog.setIndeterminate(true);
            dialog.show();
            Ion.with(getActivity())
                    .load(getIPAPI.IPAddress+"/test%20server%20for%20mobile%20app/CancelOrderDetailList.php")
                    .setBodyParameter("orderNo", ""+orderNo)
                    .setBodyParameter("branchID", branchID)
                    .asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            dialog.dismiss();
                            int index;
                            for (int i = 0; i < result.size(); i++) {

                                JsonObject jsonObject = (JsonObject) result.get(i);
                                index = i + 1;
                                items.add(new MyItemCancelOrderdetailLevel1(""+index,jsonObject.get("ProductNameTH").getAsString(),jsonObject.get("ProductNameEN").getAsString(),jsonObject.get("Num").getAsString()
                                        ,jsonObject.get("ProductID").getAsString()));

                            }

                            MyAdapterCancelOrderdetailLevel1 myAdapter=new MyAdapterCancelOrderdetailLevel1(getActivity(),R.layout.list_cancel_orderdetail_level1,items);
                            mSwipeListView.setAdapter(myAdapter);

                            mSwipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("productID", ""+items.get(i).getProID());
                                    bundle.putString("orderNo", ""+orderNo);
                                    bundle.putString("productName", ""+items.get(i).getNameTH()+"("+items.get(i).getNameEN()+")");
                                    FragmentCancelOrderDetailLevel2 fragmentCancelOrderDetailLevel2 = new FragmentCancelOrderDetailLevel2();
                                    fragmentCancelOrderDetailLevel2.setArguments(bundle);
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.content_frame, fragmentCancelOrderDetailLevel2)
                                            .commit();
                                }
                            });

                        }
                    });
        //}

        return myView;
    }
    public static String dateThai(String strDate)
    {
        String Months[] = {
                "ม.ค", "ก.พ", "มี.ค", "เม.ย",
                "พ.ค", "มิ.ย", "ก.ค", "ส.ค",
                "ก.ย", "ต.ค", "พ.ย", "ธ.ค"};

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        int year=0,month=0,day=0;
        try {
            Date date = df.parse(strDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DATE);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return String.format("%s %s %s", day,Months[month],year+543);
    }

    private void createSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem menuCall = new SwipeMenuItem(getActivity());
                menuCall.setBackground(new ColorDrawable(Color.parseColor("#ff4d4d")));
                menuCall.setWidth(dp2px(80));
                menuCall.setIcon(R.drawable.ic_cancel_black_24dp);
                menu.addMenuItem(menuCall);

            }
        };

        mSwipeListView.setMenuCreator(creator);
    }

    private int dp2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
