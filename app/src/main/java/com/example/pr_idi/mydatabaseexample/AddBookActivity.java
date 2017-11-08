package com.example.pr_idi.mydatabaseexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.renderscript.BaseObj;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddBookActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener, View.OnClickListener {

    private static final int NO_TITLE = 1;
    private static final int NO_AUTHOR = 2;

    public static final String HIDDEN_FAB_TAG = "hidden_fab";
    public static final String NEW_BOOK_TAG = "new_book";
    public static final String OLD_BOOK_TAG = "old_book";

    private Book book;
    private BookData bookData;
    private boolean hiddenFab = false;

    private EditText titleBox;
    private EditText authorBox;
    private EditText categoryBox;
    private EditText publisherBox;
    private EditText yearBox;
    private RatingBar ratingBar;
    private TextView personalEval;

    //*****************************
    //Activity life cycle functions
    //*****************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_book);

        ViewStub content = (ViewStub) findViewById(R.id.add_book_stub);
        if(content != null) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                content.setLayoutResource(R.layout.content_add_book_landscape);
            }
            else {
                content.setLayoutResource(R.layout.content_add_book);
            }

            content.inflate();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);
        fab.setOnClickListener(this);
        if(savedInstanceState == null || savedInstanceState.getBoolean(HIDDEN_FAB_TAG)) {
            fab.hide();
            hiddenFab = true;
        }

        Intent i = getIntent();
        book = i.getParcelableExtra(MainActivity.MODIFY_BOOK_TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        bookData = new BookData(this);

        ratingBar = (RatingBar) findViewById(R.id.edit_book_rating);
        ratingBar.setOnRatingBarChangeListener(this);

        titleBox = (EditText) findViewById(R.id.edit_book_title_box);
        authorBox = (EditText) findViewById(R.id.edit_book_author_box);
        categoryBox = (EditText) findViewById(R.id.edit_book_category_box);
        publisherBox = (EditText) findViewById(R.id.edit_book_publisher_box);
        yearBox = (EditText) findViewById(R.id.edit_book_year_box);
        personalEval = (TextView) findViewById(R.id.edit_book_eval_title);
        ratingBar.setRating(3);

        categoryBox.setHint(getString(R.string.edit_book_category_hint) + " " + getString(R.string.category_default));

        if(savedInstanceState != null) {
            book = savedInstanceState.getParcelable(MainActivity.MODIFY_BOOK_TAG);

            titleBox.setText(book.getTitle());
            authorBox.setText(book.getAuthor());

            if(book.getCategory() != null) categoryBox.setText(book.getCategory());
            if(book.getYear() != -1) yearBox.setText(book.getYear());
            if(book.getPublisher() != null) publisherBox.setText(book.getPublisher());
            if(book.getPersonalEvaluation() != null) {
                personalEval.setText(getString(R.string.edit_book_eval) + " \"" + book.getPersonalEvaluation() + "\"");
                ratingBar.setRating(bookData.getRating(book.getPersonalEvaluation()));
            }
        }
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
        book = saveBook();
        outState.putParcelable(MainActivity.MODIFY_BOOK_TAG, book);

        super.onSaveInstanceState(outState);
    }

    //***********************
    //Activity menu functions
    //***********************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_book_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_book_menu_about:
                String aboutText = getString(R.string.about_text);
                String ok = getString(R.string.about_button);
                MainActivity.showAbout(aboutText, ok, this);
                break;
            case R.id.add_book_clear:
                askToEmptyFields();
                break;
            case R.id.add_book_help:
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);
                fab.hide();
                hiddenFab = true;
                Intent help = new Intent(this, HelpActivity.class);
                startActivity(help);
        }

        return super.onOptionsItemSelected(item);
    }

    //***************************
    //Activity on click functions
    //***************************

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_done:
                Intent attempt = attemptToSave();
                if(attempt != null) {
                    setResult(AppCompatActivity.RESULT_OK, attempt);
                    finishWithFABAnimation();
                }
                break;
            default:
                break;
        }
    }

    //*********************************
    //Activity event reaction functions
    //*********************************

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        ratingBar.setRating(rating);
        personalEval.setText(getString(R.string.edit_book_eval) + " \"" + bookData.getPersonalEvaluation((int) rating) + "\"");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onEnterAnimationComplete() {
        if(hiddenFab) {
            expandFab();
            hiddenFab = false;
        }

        super.onEnterAnimationComplete();
    }

    //*************************
    //Other auxiliary functions
    //*************************

    private String ratingToString(float rating) {
        return bookData.getPersonalEvaluation((int) rating);
    }

    private Intent attemptToSave() {
        switch (checkInput()) {
            case NO_TITLE:
                Toast.makeText(this, getString(R.string.error_no_title), Toast.LENGTH_SHORT).show();
                return null;
            case NO_AUTHOR:
                Toast.makeText(this, getString(R.string.error_no_author), Toast.LENGTH_SHORT).show();
                return null;
            default:
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra(NEW_BOOK_TAG, saveBook());
                if(book != null) i.putExtra(OLD_BOOK_TAG, book);
                return i;
        }
    }

    private int checkInput() {
        EditText title = (EditText) findViewById(R.id.edit_book_title_box);
        if(title.getText().length() <= 0) return NO_TITLE;

        EditText author = (EditText) findViewById(R.id.edit_book_author_box);
        if(author.getText().length() <= 0) return NO_AUTHOR;

        return 0;
    }

    private Book saveBook() {
        String title = titleBox.getText().toString();
        String author = authorBox.getText().toString();

        Book newBook = new Book(title, author);

        if(yearBox.getText().length() > 0) newBook.setYear(Integer.parseInt(yearBox.getText().toString()));
        if(publisherBox.getText().length() > 0) newBook.setPublisher(publisherBox.getText().toString());
        if(categoryBox.getText().length() > 0) newBook.setCategory(categoryBox.getText().toString());
        newBook.setPersonalEvaluation(ratingToString(ratingBar.getRating()));

        return newBook;
    }

    private void askToEmptyFields() {
        String question = getResources().getString(R.string.dialog_empty_fields);
        String yes = getResources().getString(R.string.dialog_yes);
        String no = getResources().getString(R.string.dialog_no);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(question)
                .setCancelable(false)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        titleBox.setText("");
                        authorBox.setText("");
                        categoryBox.setText("");
                        publisherBox.setText("");
                        yearBox.setText("");
                        ratingBar.setRating(3);
                    }
                })
                .setNegativeButton(no, null)
                .create();

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    //*********************************
    //FAB auxiliary animation functions
    //*********************************

    public void shrinkFabWithListener(Animation.AnimationListener l) {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);

        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(l);
        fab.startAnimation(shrink);
        fab.hide();
    }

    public void finishWithFABAnimation() {
        shrinkFabWithListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shrinkFab() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);

        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        fab.startAnimation(shrink);
        fab.hide();
    }

    private void expandFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);

        fab.clearAnimation();
        // Scale up animation
        ScaleAnimation expand =  new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        expand.setDuration(100);     // animation duration in milliseconds
        expand.setInterpolator(new AccelerateInterpolator());
        fab.show();
        fab.startAnimation(expand);
    }
}
