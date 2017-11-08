package com.example.pr_idi.mydatabaseexample;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    List<String[]> helpSections;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.height = (int) getResources().getDimension(R.dimen.appbar_landscape_height);
            toolbar.setLayoutParams(layoutParams);
        }

        String categoryDefault = getString(R.string.category_default);
        String publisherDefault = getString(R.string.publisher_default);
        String yearDefault = String.valueOf(getResources().getInteger(R.integer.year_default));
        String evalDefault = getResources().getStringArray(R.array.personal_evals)[2];
        String ratingDefault = String.valueOf(3);

        TextView formatable = (TextView) findViewById(R.id.help_formatable);
        formatable.setText(getString(R.string.help_add_book_save_text,
                categoryDefault,
                publisherDefault,
                yearDefault,
                evalDefault,
                ratingDefault));
    }
}
