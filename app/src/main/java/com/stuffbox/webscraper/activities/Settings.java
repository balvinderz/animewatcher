package com.stuffbox.webscraper.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.stuffbox.webscraper.R;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Toolbar toolbar=findViewById(R.id.settingstoolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SharedPreferences preferences=getSharedPreferences("settings",0);
        final SharedPreferences.Editor editor=preferences.edit();
        SwitchMaterial switchMaterial=findViewById(R.id.onoff);
        int on=preferences.getInt("playtuturu",0);
        if(on==1)
            switchMaterial.setChecked(true);
        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    editor.putInt("playtuturu",1);
                }
                else
                {
                    editor.putInt("playtuturu",0);
                }
                editor.commit();

            }
        });

    }
}
