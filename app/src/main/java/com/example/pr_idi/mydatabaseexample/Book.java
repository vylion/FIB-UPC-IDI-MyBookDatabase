package com.example.pr_idi.mydatabaseexample;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Book
 * Created by pr_idi on 10/11/16.
 */
public class Book implements Parcelable {

    // Basic book data manipulation class
    // Contains basic information on the book

    private long id;
    private String title;
    private String author;
    private int year;
    private String publisher;
    private String category;
    private String personal_evaluation;

    public Book(long id, String title, String author, int year, String publisher, String category, String evaluation) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.year = year;
        this.publisher = publisher;
        this.category = category;
        this.personal_evaluation = evaluation;
    }

    public Book(Book b) {
        this(b.title, b.author, b.year, b.publisher, b.category, b.personal_evaluation);
    }

    public Book(String title, String author, int year, String publisher, String category, String personal_evaluation) {
        this(-1, title, author, year, publisher, category, personal_evaluation);
    }

    public Book(String title, String author, int year, String publisher, String category) {
        this(title, author, year, publisher, category, null);
    }

    public Book(String title, String author, int year, String publisher) {
        this(title, author, year, publisher, null);
    }

    public Book(String title, String author, int year) {
        this(title, author, year, null);
    }

    public Book(String title, String author, String publisher) {
        this(title, author, -1, publisher);
    }

    public Book(String title, String author) {
        this(title, author, -1, null);
    }

    protected Book(Parcel in) {
        id = in.readLong();
        title = in.readString();
        author = in.readString();
        year = in.readInt();
        publisher = in.readString();
        category = in.readString();
        personal_evaluation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeInt(year);
        dest.writeString(publisher);
        dest.writeString(category);
        dest.writeString(personal_evaluation);
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title= title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year= year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher= publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPersonalEvaluation() {
        return personal_evaluation;
    }

    public void setPersonalEvaluation(String personal_evaluation) {
        this.personal_evaluation = personal_evaluation;
    }

    // Will be used by the ArrayAdapter in the ListView
    // Note that it only produces the title and the author
    // Extra information should be created by modifying this
    // method or by adding the methods required
    @Override
    public String toString() {
        return String.format("%s - %s", title, author);
    }
}