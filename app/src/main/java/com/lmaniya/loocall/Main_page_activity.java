package com.lmaniya.loocall;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.add_room.see_room_activity;
import com.lmaniya.loocall.direct.Direct_product_activity;
import com.lmaniya.loocall.fcm.fcm;
import com.lmaniya.loocall.fetch_shop.fetch_product.customer.my_cart_activity;
import com.lmaniya.loocall.login.helper.DatabaseHandler;
import com.lmaniya.loocall.login.helper.SessionManager;
import com.lmaniya.loocall.login.login_activity;
import com.lmaniya.loocall.login.profile.user_profile_activity;
import com.lmaniya.loocall.shell.shell_activity;
import com.lmaniya.loocall.super_admin.bank.bank_activity;
import com.lmaniya.loocall.super_admin.hospital.hospital_activity;
import com.lmaniya.loocall.super_admin.school.school_activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main_page_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    TextView login;
    private DatabaseHandler db;
    SessionManager sessionManager;
    private HashMap<String,String> user = new HashMap<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<categories_data> C_data;
    private Categories_adapter categories_adapter;
    private categories_api_interface categories_api_interface;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    BottomNavigationBehavior bottomNavigationBehavior;
    String role;
    String user_id,user_name;

    // bottom
    private  final int Id_home=1;
    private  final int id_message=2;
    private  final int id_fast_business=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View header = navigationView.getHeaderView(0);
        login=header.findViewById(R.id.login_user);
        role=getIntent().getStringExtra("role");
        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        user_id=user.get("uid");
        user_name=user.get("name");
        setTitle("Main Page");
// bottom


        MeowBottomNavigation bottomNavigation=findViewById(R.id.negation);
        bottomNavigation.add(new MeowBottomNavigation.Model(Id_home,R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(id_message,R.drawable.ic_chat));
        bottomNavigation.add(new MeowBottomNavigation.Model(id_fast_business,R.drawable.ic_fast));





        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name ;
                switch (item.getId())
                {
                    case Id_home:name="Home";
                        break;
                    case id_message:name="Message";
                        break;
                    case  id_fast_business:name="Direct Product";
                        break;

                    default:name="";


                }

            }
        });

        bottomNavigation.show(Id_home,true);




        sessionManager= new SessionManager(getApplicationContext());
        if (sessionManager.isroller()==0)
        {
            if (user_id!=null)
            {
                login.setText("Hello:"+user_name);
            }
            else {
                login.setText("Hello please login");
            }


        }
        else {
            login.setText("Hello please login");
        }




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main_page_activity.this, login_activity.class));
            }
        });

        relativeLayout=findViewById(R.id.main_page_layout);
        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.categories_data_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout=findViewById(R.id.swipe);
        progressDialog=new ProgressDialog(this);

//        bottomNavigationBehavior=new BottomNavigationBehavior();
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
//        layoutParams.setBehavior(bottomNavigationBehavior);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnected())
                {

                    swipeRefreshLayout.setRefreshing(false);

                    getData("users", "");

                } else {
                    Toast.makeText(Main_page_activity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                }




            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && dy > 0)
                {

                    //scrolled to bottom
                }else if (!recyclerView.canScrollVertically(-1) && dy < 0)
                {
                    swipeRefreshLayout.setEnabled(true);
                }
                else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });





        getData("users", "");


//






    }
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    /**
     *  Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     *  Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }


    private void setSnackbarMessage(String status,boolean showBar) {
        String internetStatus = "";
        if (status.equalsIgnoreCase("Wifi enabled") || status.equalsIgnoreCase("Mobile data enabled")) {
            internetStatus = "Internet Connected";
        } else {
            internetStatus = "Lost Internet Connection";
        }
        snackbar = Snackbar
            .make(relativeLayout, internetStatus, Snackbar.LENGTH_LONG)
            .setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        if (internetStatus.equalsIgnoreCase("Lost Internet Connection")) {
            if (internetConnected) {
                snackbar.show();
                internetConnected = false;
            }
        } else {
            if (!internetConnected) {
                internetConnected = true;
                snackbar.show();
            }
        }
    }




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_checkout:
                    startActivity(new Intent(Main_page_activity.this, Direct_product_activity.class));
                    return true;



            }
            return false;
        }
    };




    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
            || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
//        if (id==R.id.nav_emergency)
//        {
//            startActivity(new Intent(Main_page_activity.this, help_and_support.class));
//        }
        if (id==R.id.nav_gallery)
        {
            startActivity(new Intent(Main_page_activity.this, fcm.class));
        }
        if (id==R.id.nav_my_profile)
        {
            if (sessionManager.isroller()==0) {


                startActivity(new Intent(Main_page_activity.this, user_profile_activity.class));
            }
            else {
                Toast.makeText(Main_page_activity.this,"You don't have permission please login",Toast.LENGTH_SHORT).show();
            }
        }
        if (id==R.id.nav_my_cart)
        {
            if (sessionManager.isroller()==0) {


                startActivity(new Intent(Main_page_activity.this, my_cart_activity.class));
            }
            else {
                Toast.makeText(Main_page_activity.this,"You don't have permission please login",Toast.LENGTH_SHORT).show();
            }
        }
        if(id==R.id.nav_gallery)
        {
            startActivity(new Intent(Main_page_activity.this,fcm.class));
        }
        if (id==R.id.nav_bank)
        {
            startActivity(new Intent(Main_page_activity.this, bank_activity.class));
        }
        if (id==R.id.nav_school)
        {
            startActivity(new Intent(Main_page_activity.this, hospital_activity.class));
        }
        if (id==R.id.nav_hospital)
        {
            startActivity(new Intent(Main_page_activity.this, school_activity.class));
        }
        if (id==R.id.nav_room)
        {
            startActivity(new Intent(Main_page_activity.this, see_room_activity.class));
        }
        if (id==R.id.nav_sale)
        {
            startActivity(new Intent(Main_page_activity.this, shell_activity.class));
        }


        if (id==R.id.about_us)
        {
            startActivity(new Intent(Main_page_activity.this,about_activity.class));
        }
        if (id==R.id.contact_us)
        {
            startActivity(new Intent(Main_page_activity.this,contact_us_activity.class));
        }
        if (id==R.id.nav_ime)
        {
            startActivity(new Intent(Main_page_activity.this,MainActivity.class));
        }


        return false;
    }

    public void getData(String type, String key) {
        progressDialog.setTitle("Loading...");
        progressDialog.show();


        categories_api_interface = categories_api.getApiClient().create(categories_api_interface.class);

        Call<List<categories_data>> call = categories_api_interface.getData(type, key);
        call.enqueue(new Callback<List<categories_data>>() {
            @Override
            public void onResponse(Call<List<categories_data>> call, Response<List<categories_data>> response) {
                progressDialog.dismiss();

                swipeRefreshLayout.setRefreshing(false);
                C_data = response.body();

                categories_adapter = new Categories_adapter(C_data, Main_page_activity.this);
                recyclerView.setAdapter(categories_adapter);
                categories_adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<categories_data>> call, Throwable t) {
                progressDialog.dismiss();

                swipeRefreshLayout.setRefreshing(false);
                // Toast.makeText(main_page_activity.this, "Error\n" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_main, menu);





        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                getData("users", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getData("users", newText);

                return false;
            }


        });

        return true;

    }
}
