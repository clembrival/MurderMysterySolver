package com.clemSP.iteration1.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.clemSP.iteration1.R;


/**
 * Class containing the methods shared between multiple activities.
 */
public abstract class BaseActivity extends AppCompatActivity
{
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_settings: return true;
            case R.id.action_prototypes:
                startActivity(new Intent(this, ManagerActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    protected void printErrorToast(int errorRes)
    {
        Toast.makeText(this, errorRes, Toast.LENGTH_SHORT).show();
    }
}
