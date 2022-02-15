package com.lmaniya.loocall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;




public class MainActivity extends AppCompatActivity {
    private  final int Id_home=1;
    private  final int id_message=2;
    private  final int id_notification=3;
    private  final int id_account=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView selected_page=findViewById(R.id.selected_page);
        MeowBottomNavigation bottomNavigation=findViewById(R.id.negation);
        bottomNavigation.add(new MeowBottomNavigation.Model(Id_home,R.drawable.text_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(id_message,R.drawable.text_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(id_notification,R.drawable.text_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(id_account,R.drawable.text_home));
       bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
           @Override
           public void onClickItem(MeowBottomNavigation.Model item) {
               Toast.makeText(MainActivity.this,"clickeditem" +item.getId(),Toast.LENGTH_SHORT).show();
           }
       });
         bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
             @Override
             public void onShowItem(MeowBottomNavigation.Model item) {
                 String name ;
                 switch (item.getId())
                 {
                     case Id_home:name="home";
                         break;
                     case id_message:name="Message";
                         break;
                     case  id_notification:name="Notification";
                         break;
                     case  id_account:name="Account";
                         break;
                     default:name="";


                 }
                 selected_page.setText("hello"+name);
             }
         });
         bottomNavigation.setCount(id_notification,"4");
         bottomNavigation.show(Id_home,true);
    }
}
