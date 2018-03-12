package com.example.mustafa.switchtab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Tab1.OnFragmentInteractionListener,Tab2.OnFragmentInteractionListener,Tab3.OnFragmentInteractionListener {

    TextView baslik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baslik = (TextView) findViewById(R.id.main_tvHeader);
        if(FirstActivity.autoLogin.getString("username",null)!=null){
            baslik.setText("KeepNote'a Hoşgeldin, "+FirstActivity.autoLogin.getString("username",null));
        }
        else{
            baslik.setText("KeepNote'a Hoşgeldin");
        }

        //Referanslar
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Notlarım"));
        tabLayout.addTab(tabLayout.newTab().setText("Kişiler"));
        tabLayout.addTab(tabLayout.newTab().setText("Profil"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void cikisYap(View v){
        FirstActivity.autoLogin.edit().remove("email").apply();
        FirstActivity.autoLogin.edit().remove("password").apply();
        FirstActivity.autoLogin.edit().remove("username").apply();
        FirstActivity.autoLogin.edit().remove("girisYapildi").apply();
        FirstActivity.autoLogin.edit().remove("misafir").apply();

        Intent intent = new Intent(this,FirstActivity.class);
        startActivity(intent);
    }

    public void notEkle(View v){
        Intent intent = new Intent(this,NotEkle.class);
        startActivity(intent);
    }
}
