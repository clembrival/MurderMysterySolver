package com.clemSP.iteration1.frontend.class_selection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.frontend.BaseActivity;

/**
 * Activity with buttons to select the type of prediction needed.
 */
public class ClassMenuActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_menu);

        setButton(R.id.class_weapon_button, true);
        setButton(R.id.class_gender_button, false);
    }


    private void setButton(int buttonId, final boolean weapon)
    {
        Button button = (Button) findViewById(buttonId);
        if(button == null)
        {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra("weapon", weapon);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}