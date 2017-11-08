package com.example.pr_idi.mydatabaseexample;

/**
 * BookData
 * Created by pr_idi on 10/11/16.
 */
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BookData {

    // Database fields
    private SQLiteDatabase database;

    // Helper to manipulate table
    private MySQLiteHelper dbHelper;

    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_AUTHOR,
            MySQLiteHelper.COLUMN_YEAR, MySQLiteHelper.COLUMN_PUBLISHER,
            MySQLiteHelper.COLUMN_CATEGORY, MySQLiteHelper.COLUMN_PERSONAL_EVALUATION
    };

    private String [] personalEvals;
    private String defaultAuthor;
    private String defaultCategory;
    private String defaultPublisher;
    private int defaultYear;

    public BookData(Context context) {
        dbHelper = new MySQLiteHelper(context);
        Resources res = context.getResources();
        personalEvals = res.getStringArray(R.array.personal_evals);
        defaultAuthor = res.getString(R.string.author_default);
        defaultCategory = res.getString(R.string.category_default);
        defaultPublisher = res.getString(R.string.publisher_default);
        defaultYear = res.getInteger(R.integer.year_default);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void read() throws  SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private Book createBook(String title, String author, int year, String publisher, String category, String evaluation) {
        ContentValues values = new ContentValues();
        Log.d("Creating", "Creating " + title + " " + author);

        //Compulsory input; no default value
        values.put(MySQLiteHelper.COLUMN_TITLE, title);

        //default values
        if(author == null) author = defaultAuthor;
        if(publisher == null) publisher = defaultPublisher;
        if(year == -1) year = defaultYear;
        if(category == null) category = defaultCategory;
        if(evaluation == null) evaluation = personalEvals[2];

        values.put(MySQLiteHelper.COLUMN_AUTHOR, author);
        values.put(MySQLiteHelper.COLUMN_PUBLISHER, publisher);
        values.put(MySQLiteHelper.COLUMN_YEAR, year);
        values.put(MySQLiteHelper.COLUMN_CATEGORY, category);
        values.put(MySQLiteHelper.COLUMN_PERSONAL_EVALUATION, evaluation);

        // Actual insertion of the data using the values variable
        long insertId = database.insert(MySQLiteHelper.TABLE_BOOKS, null,
                values);

        // Main activity calls this procedure to create a new book
        // and uses the result to update the listview.
        // Therefore, we need to get the data from the database
        // (you can use this as a query example)
        // to feed the view.

        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Book newBook = cursorToBook(cursor);

        // Do not forget to close the cursor
        cursor.close();

        // Return the book
        return newBook;
    }

    public Book createBook(String title, String author, int year, String publisher, String category, int eval) {
        return createBook(title, author, year, publisher, category, personalEvals[eval]);
    }

    public Book createBook(String title, String author, int year, String publisher, String category) {
        return createBook(title, author, year, publisher, category, null);
    }

    public Book createBook(String title, String author, int year, String publisher) {
        return createBook(title, author, year, publisher, null);
    }

    public Book createBook(String title, String author, int year) {
        return createBook(title, author, year, null);
    }

    public Book createBook(String title, String author, String publisher) {
        return createBook(title, author, -1, publisher);
    }

    public Book createBook(String title, String author) {
        return createBook(title, author, -1, null);
    }

    public Book createBook(String title) {
        return createBook(title, null);
    }

    public Book createBook(Book b) {
        return createBook(b.getTitle(), b.getAuthor(), b.getYear(), b.getPublisher(), b.getCategory(), b.getPersonalEvaluation());
    }

    public void deleteBook(Book book) {
        long id = book.getId();
        System.out.println("Book deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_BOOKS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Book book = cursorToBook(cursor);
            books.add(book);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return books;
    }

    public String getPersonalEvaluation(int r) {
        r--;
        if(r >= 0 && r < personalEvals.length) {
            return personalEvals[r];
        }
        else return personalEvals[5];
    }

    public float getRating(String s) {
        int p;
        for(p = 0; p < personalEvals.length - 1; p++) {
            if(s.equals(personalEvals[p])) break;
        }
        return (float) p+1;
    }

    private Book cursorToBook(Cursor cursor) {
        Book book = new Book(cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6));

        return book;
    }
}