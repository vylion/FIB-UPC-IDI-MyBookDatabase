package com.example.pr_idi.mydatabaseexample;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BookData bookData;
    private Toolbar toolbar;
    private RecyclerView bookView;
    private int currentOrder = BookViewAdapter.COMPARE_BY_TITLE;
    private boolean hiddenFab = false;

    private static final String HIDDEN_FAB_TAG = "hidden_fab";
    private static final String ORDER_METHOD_TAG = "order_method";

    public static final String FIRST_TIME_LAUNCH_TAG = "first_time";
    public static final String MODIFY_BOOK_TAG = "modify_book";

    //****************************
    //Activity lifecycle functions
    //****************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bookView = (RecyclerView) findViewById(R.id.main_activity_recycler);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.height = (int) getResources().getDimension(R.dimen.appbar_landscape_height);
            toolbar.setLayoutParams(layoutParams);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(this);
        if(savedInstanceState == null || savedInstanceState.getBoolean(HIDDEN_FAB_TAG)) {
            fab.hide();
            hiddenFab = true;
        }

        bookData = new BookData(this);

        //First time app start code
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean(FIRST_TIME_LAUNCH_TAG, true)) {
            bookData.open();
            bookData.createBook("Miguel Strogoff", "Jules Verne", 1876, "Pierre-Jules Hetzel", getString(R.string.category_action));
            bookData.createBook("Ulysses", "James Joyce", 1922, "Sylvia Beach", getString(R.string.category_modernist));
            bookData.createBook("Don Quijote", "Miguel de Cervantes", 1605, "Francisco de Robles", getString(R.string.category_action));
            bookData.createBook("Metamorphosis", "Kafka", 1915, "René Schickele", getString(R.string.category_magic_realism));
            bookData.close();

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(FIRST_TIME_LAUNCH_TAG, false);
            editor.apply();
        }

        if(savedInstanceState != null)
            currentOrder = savedInstanceState.getInt(ORDER_METHOD_TAG);

        resetBookView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(HIDDEN_FAB_TAG, hiddenFab);
        outState.putInt(ORDER_METHOD_TAG, currentOrder);

        super.onSaveInstanceState(outState);
    }

    //*********************
    //Activity menu options
    //*********************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);

        switch (item.getItemId()) {
            case R.id.main_menu_about:
                String aboutText = getString(R.string.about_text);
                String ok = getString(R.string.about_button);
                showAbout(aboutText, ok, this);
                break;
            case R.id.main_menu_by_category:
                if(currentOrder == BookViewAdapter.COMPARE_BY_TITLE) {
                    BookViewAdapter adapter = (BookViewAdapter) bookView.getAdapter();
                    currentOrder = BookViewAdapter.COMPARE_BY_CATEGORY;
                    adapter.orderBy(currentOrder);
                }
                makeToast(getString(R.string.ordering_category_toast));
                break;
            case R.id.main_menu_by_title:
                if(currentOrder == BookViewAdapter.COMPARE_BY_CATEGORY) {
                    BookViewAdapter adapter = (BookViewAdapter) bookView.getAdapter();
                    currentOrder = BookViewAdapter.COMPARE_BY_TITLE;
                    adapter.orderBy(currentOrder);
                }
                makeToast(getString(R.string.ordering_title_toast));
                break;
            case R.id.main_menu_add_book:
                fab.hide();
                Intent addBook = new Intent(this, AddBookActivity.class);
                hiddenFab = true;
                startActivityForResult(addBook, 0);
                break;
            case R.id.main_menu_help:
                fab.hide();
                hiddenFab = true;
                Intent help = new Intent(this, HelpActivity.class);
                startActivity(help);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //***************************
    //Activity on click functions
    //***************************

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add:
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
                fab.hide();
                Intent i = new Intent(this, AddBookActivity.class);
                hiddenFab = true;
                startActivityForResult(i, 0);
                break;
            default:
                break;
        }
    }

    //*********************************
    //Activity event reaction functions
    //*********************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == AppCompatActivity.RESULT_OK) {
            BookViewAdapter adapter = (BookViewAdapter) bookView.getAdapter();
            Book oldBook = data.getParcelableExtra(AddBookActivity.OLD_BOOK_TAG);
            if(oldBook != null) adapter.remove(oldBook);
            Book newBook = data.getParcelableExtra(AddBookActivity.NEW_BOOK_TAG);
            adapter.add(newBook, currentOrder);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEnterAnimationComplete() {
        if(hiddenFab) {
            expandFab();
            hiddenFab = false;
        }

        super.onEnterAnimationComplete();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //*************************
    //Other auxiliary functions
    //*************************

    private void resetBookView() {
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.main_coordinator);

        BookViewAdapter adapter = new BookViewAdapter(this, bookData, currentOrder, layout);
        bookView.setAdapter(adapter);

        RecyclerView.LayoutManager lm;
        //if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        lm = new LinearLayoutManager(this);
        //else lm = new GridLayoutManager(this, 2);
        bookView.setLayoutManager(lm);
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public static void showAbout(String aboutText, String ok, Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(aboutText)
                .setCancelable(false)
                .setPositiveButton(ok, null)
                .create();

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void makeSnackbar(String warning) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getResources().getLayout(R.layout.main);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, warning, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    private void makeActionSnackbar(String warning, String action, View.OnClickListener l) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getResources().getLayout(R.layout.main);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, warning, Snackbar.LENGTH_LONG)
                .setAction(action, l);

        snackbar.show();
    }

    //*********************************
    //FAB auxiliary animation functions
    //*********************************

    public void startActivityForResultWithFABAnimation(final Intent i, final int requestCode) {
        shrinkFabWithListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivityForResult(i, requestCode);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shrinkFabWithListener(Animation.AnimationListener l) {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);

        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(l);
        fab.startAnimation(shrink);
        fab.hide();
    }

    private void shrinkFab() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);

        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        fab.startAnimation(shrink);
        fab.hide();
    }

    private void expandFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);

        fab.clearAnimation();
        // Scale up animation
        ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        expand.setDuration(100);     // animation duration in milliseconds
        expand.setInterpolator(new AccelerateInterpolator());
        fab.show();
        fab.startAnimation(expand);
    }
}