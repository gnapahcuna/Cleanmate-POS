package com.techlogn.cleanmate_pos_v36;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by anucha on 1/7/2018.
 */

public class FragmentSearch extends Fragment {

    View v;
    String dataSearch,dataID,branchID;
    TextView textView;
    ArrayList<CustomItemSearch> items;
    SharedPreferences sharedPreferences;
    int totalCount=0;
    int count_pro = 1,total_price=0;
    TextView text_result1,text_result2,text_result3,textNum ;
    int ID;
    int num=0,sum=0,a=0,aa=0;
    int c=0;
    ListView listView;
    MyAdapterSearch myAdapter;

    private SQLiteHelper mSQLite;
    private SQLiteDatabase mDb;

    int i_num=0;

    private int mScreenWidth;
    private int mScreenHeight;

    private ImageView mImgBall;
    private int mImgHeight;

    private GetIPAPI getIPAPI;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    public FragmentSearch() {

    }
    public static FragmentSearch newInstance() {
        Bundle args = new Bundle();
        FragmentSearch fragment = new FragmentSearch();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_search,container,false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dataSearch = bundle.getString("key");
            dataID=bundle.getString("ID");
            branchID=bundle.getString("branchID");
        }

        getIPAPI=new GetIPAPI();
        //Log
        /*Bugfender.init(getActivity(), "RlG2SafK3kOHo2XvAfqwEZMMOnLl0yGB", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(getActivity().getApplication());*/


        mSQLite=SQLiteHelper.getInstance(getActivity());

        items=new ArrayList<>();
        textView=v.findViewById(R.id.textSearch);
        MyFont myFont=new MyFont(getActivity());
        textView.setTypeface(myFont.setFont());
        listView = v.findViewById(R.id.list_1);
        text_result1=v.findViewById(R.id.text_result1);
        text_result2=v.findViewById(R.id.text_result2);
        text_result3=v.findViewById(R.id.text_result3);
        sharedPreferences = getContext().getSharedPreferences("ORDER", Activity.MODE_PRIVATE);


        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                }
                return false;
            }
        });

        DisplayMetrics display = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
        mScreenWidth = display.widthPixels;
        mScreenHeight = display.heightPixels;

        mImgBall = v.findViewById(R.id.img_sale);
        mImgBall.setVisibility(View.GONE);
        mImgBall.post(new Runnable() {
            @Override
            public void run() {
                mImgHeight = mImgBall.getHeight();
                setStartPosition(100);
            }
        });

        return v;
    }
    private void setStartPosition(int y) {
        mImgBall.setX(mScreenWidth - 400);
        mImgBall.setY(y);
    }
    private void animate(TimeInterpolator interpol, float yBy) {
        mImgBall.animate()
                .setInterpolator(interpol)
                .translationYBy(yBy)
                .setDuration(1000)
                .setStartDelay(50)
                .start();
    }
    @Override
    public void onStart() {
        super.onStart();

        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            mDb = mSQLite.getReadableDatabase();
            String sql = "select product.ProductID,tb_service.ServiceNameTH,tb_service.ServiceNameEN,product.ProductNameTH,product.ProductNameEN,product.ProductPrice,product.ImageFile,product.ColorCode,tb_service.ServiceType,category.CategoryID from product LEFT JOIN tb_service ON product.ServiceType = tb_service.ServiceType LEFT JOIN category ON product.CategoryID=category.CategoryID where (ProductNameTH LIKE '" + dataSearch + "%' OR ProductNameEN LIKE '" + dataSearch + "%')";
            Cursor cursor = mDb.rawQuery(sql, null);

            String str = "";
            while (cursor.moveToNext()) {

                if (cursor.getInt(0) < 11) {
                    str += "0";

                }
                str += "ProductID : " + cursor.getString(0) +
                        "ServiceNameTH : " + cursor.getString(1) +
                        "ServiceNameEN : " + cursor.getString(2) +
                        "ProductNameTH : " + cursor.getString(3) +
                        "ProductNameEN : " + cursor.getString(4) +
                        "Price : " + cursor.getString(5) +
                        "Image : " + cursor.getString(6) +
                        "Color : " + cursor.getString(7) +
                        "ServiceType : " + cursor.getString(8) +
                        "CategoryID : " + cursor.getString(9);
                items.add(new CustomItemSearch(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        "" + 0));
            }

            if (dataSearch.isEmpty()) {
                textView.setText("\u2315 ค้นหา \u1366 " + "ไม่มีคำค้นหา");
            } else {
                textView.setText("\u2315 ค้นหา \u1366 " + dataSearch);

                String sql2 = "SELECT * FROM promotion_sale";
                cursor = mDb.rawQuery(sql2, null);


                while (cursor.moveToNext()) {
                    i_num++;
                }

                setCart();
                myAdapter = new MyAdapterSearch(getActivity(), R.layout.category_list_items1, items, dataID, R.layout.fragment_service_product, listView, text_result1, text_result2, text_result3);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        num++;
                        final String subImg = getIPAPI.IPAddressImage+"/" + items.get(i).mImage.substring(3).trim();
                        Picasso.with(getContext()).load(subImg).into(mImgBall);
                        mImgBall.setVisibility(View.VISIBLE);
                        setStartPosition(100);
                        animate(new AccelerateInterpolator(), mScreenHeight - 100);

                        textNum = view.findViewById(R.id.text_num);
                        final CustomItemSearch item = items.get(i);
                        getItem(i, Integer.parseInt(item.mServiceType), Integer.parseInt(item.mTextProductID));
                    }
                });
                listView.setAdapter(myAdapter);
            }
            cursor.close();
        }else {
            new MyToast(getActivity(), "ไม่มีการเชื่อมต่อ Internet", 0);
        }
    }
    public void getItem(int i,int service,int productID){

        if(service==1) {
            ContentValues cv = new ContentValues();
            cv.put("ServiceType", service);
            cv.put("ProductID", productID);
            mDb.insert("promotion_sale", null, cv);
        }

        int results=0;
        int counts=0;
        ArrayList<Integer>arr1=new ArrayList<>();
        ArrayList<Integer>arr2=new ArrayList<>();
        Map<String,?> entries2 = sharedPreferences.getAll();
        Set<String> keys2 = entries2.keySet();
        String[] getData2;
        List<String> list2 = new ArrayList<String>(keys2);
        for (String temp : list2) {
            for (int k = 0; k < sharedPreferences.getStringSet(temp, null).size(); k++) {
                getData2 = sharedPreferences.getStringSet(temp, null).toArray(new String[sharedPreferences.getStringSet(temp, null).size()]);
                char chk=getData2[k].charAt(1);
                if (chk == 'g') {
                    arr1.add(Integer.parseInt(getData2[k].substring(3)));
                } else if (chk == 'c') {
                    arr2.add(Integer.parseInt(getData2[k].substring(3)));
                }

            }
        }
        for(int k=0;k<arr1.size();k++){
            results+=arr1.get(k)*arr2.get(k);
            counts+=arr1.get(k);
        }
        int sum_counts=counts+1;
        int sum_result=results+Integer.parseInt(items.get(i).mTextPrice);
        count_pro=1;
        Map<String,?> entries3 = sharedPreferences.getAll();
        Set<String> keys3 = entries3.keySet();
        String[] getData3;
        List<String> list3 = new ArrayList<String>(keys3);
        for (String temp : list3) {
            for (int k = 0; k < sharedPreferences.getStringSet(temp, null).size(); k++) {
                getData3 = sharedPreferences.getStringSet(temp, null).toArray(new String[sharedPreferences.getStringSet(temp, null).size()]);
                char chk=getData3[k].charAt(1);
                if(temp.trim().equals(items.get(i).mTextProductID.trim())){
                    if(chk=='c'){
                    }else if(chk=='g'){
                        count_pro=Integer.parseInt(getData3[k].substring(3))+1;

                    }

                }else{
                }
            }
        }
        total_price+=Integer.parseInt(items.get(i).mTextPrice);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<String>arr_proTH=new ArrayList<>();
        arr_proTH.add(items.get(i).mTextProductID);
        StringBuilder sb = new StringBuilder();
        HashSet<String> mSet = new HashSet<>();
        for (int j = 0; j < arr_proTH.size(); j++) {
            mSet.add("<a>"+items.get(i).mTextProductTH);
            mSet.add("<b>"+items.get(i).mTextProductEN);
            mSet.add("<c>"+items.get(i).mTextPrice);
            mSet.add("<d>"+items.get(i).mServiceNameTH);
            mSet.add("<e>"+items.get(i).mServiceNameEN);
            mSet.add("<f>"+getIPAPI.IPAddressImage+"/"+items.get(i).mImage.substring(3).trim());
            mSet.add("<g>"+count_pro);
            mSet.add("<h>"+items.get(i).mTextProductID);
            mSet.add("<i>"+items.get(i).mColor);
            mSet.add("<j>"+items.get(i).mServiceType);
        }
        editor.putStringSet(items.get(i).mTextProductID, mSet);
        editor.apply();

        textNum.setText(""+count_pro);

        text_result1.setText(" "+ sharedPreferences.getAll().size()+" ");
        text_result2.setText(" "+  sum_counts+" ");
        text_result3.setText(" "+ getFormatedAmount(sum_result)+" ");
    }

    public  void setCart(){
        total_price=0;
        sharedPreferences = getContext().getSharedPreferences("ORDER", Activity.MODE_PRIVATE);

        ConstraintLayout cart=v.findViewById(R.id.layout_click_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getAll().size()>0) {
                    SharedPreferences SP = getActivity().getSharedPreferences("K", Context.MODE_PRIVATE);
                    //SP.edit().clear().apply();
                    SharedPreferences.Editor editorSP = SP.edit();
                    HashSet<String> mSet = new HashSet<>();
                    mSet.add("<a>" + "5");
                    mSet.add("<b>" + dataSearch);
                    editorSP.putStringSet("K", mSet);
                    editorSP.apply();

                    Intent i = new Intent(getActivity(), BasketActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.pdlg_anim_fade_in, R.anim.pdlg_anim_fade_out);
                    getActivity().finish();
                }else{
                    new MyToast(getActivity(),"ยังไม่มีสินค้าในตะกร้าสินค้า",0);
                }
            }
        });

        ArrayList<String>arrPrice=new ArrayList<>();
        ArrayList<String>arrCount=new ArrayList<>();
        Map<String,?> entries2 = sharedPreferences.getAll();
        Set<String> keys2 = entries2.keySet();
        String[] getData2;
        List<String> list2 = new ArrayList<String>(keys2);
        for (String temp : list2) {
            for (int k = 0; k < sharedPreferences.getStringSet(temp, null).size(); k++) {
                getData2 = sharedPreferences.getStringSet(temp, null).toArray(new String[sharedPreferences.getStringSet(temp, null).size()]);
                char chk=getData2[k].charAt(1);
                if(chk=='c'){
                    arrPrice.add(getData2[k].substring(3));
                }else if(chk=='g'){
                    totalCount+=Integer.parseInt(getData2[k].substring(3));
                    arrCount.add(getData2[k].substring(3));
                }

            }
        }
        for(int i=0;i<arrPrice.size();i++){
            total_price+=Integer.parseInt(arrPrice.get(i))*Integer.parseInt(arrCount.get(i));
        }
        final TextView text1=v.findViewById(R.id.text_data1);
        final TextView text2=v.findViewById(R.id.text_data2);
        final TextView text3=v.findViewById(R.id.text_data3);
        final TextView text4=v.findViewById(R.id.text_data4);
        final TextView text5=v.findViewById(R.id.text_data5);

        text1.setText("สินค้า");
        text2.setText("ชนิด");
        text3.setText("ชิ้น");
        text4.setText(" ราคารวม");
        text5.setText("บาท");


        text_result1.setText(" "+ sharedPreferences.getAll().size()+" ");
        text_result2.setText(" "+  totalCount+" ");
        text_result3.setText(" "+ getFormatedAmount(total_price)+" ");

        MyFont myFont=new MyFont(getActivity());
        text_result1.setTypeface(myFont.setFont());
        text_result2.setTypeface(myFont.setFont());
        text_result3.setTypeface(myFont.setFont());
        text1.setTypeface(myFont.setFont());
        text2.setTypeface(myFont.setFont());
        text3.setTypeface(myFont.setFont());
        text4.setTypeface(myFont.setFont());
        text5.setTypeface(myFont.setFont());
    }

    private String getFormatedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }
}

