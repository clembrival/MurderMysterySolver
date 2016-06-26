package com.clemSP.iteration1.activities;

import android.content.res.Resources;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.clemSP.iteration1.R;

public class PredictionActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        Bundle bundle = getIntent().getExtras();
        String weapon = bundle.getString("prediction");

        ImageView weaponImage = (ImageView)findViewById(R.id.weapon_image);
        TextView weaponCaption = (TextView)findViewById(R.id.weapon_caption);

        if(weapon != null && weaponImage != null && weaponCaption != null)
        {
            if(weapon.contains(" "))
                weapon = weapon.replace(" ", "_");
            Resources resources = this.getResources();
            final int resourceId = resources.getIdentifier(weapon.toLowerCase(), "drawable",
                    this.getPackageName());
            weaponImage.setImageResource(resourceId);
            weaponCaption.setText(weapon);
        }
    }
}
