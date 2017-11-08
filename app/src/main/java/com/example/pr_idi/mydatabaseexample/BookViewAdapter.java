package com.example.pr_idi.mydatabaseexample;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by vylion on 1/3/17.
 */

public class BookViewAdapter extends RecyclerView.Adapter<BookViewAdapter.BookViewHolder> {

    public static final int COMPARE_BY_TITLE = 0;
    public static final int COMPARE_BY_CATEGORY = 1;

    private final LayoutInflater inflater;
    private List<Book> books;
    private Context context;
    private BookData bookData;
    private int currentOrder;
    private CoordinatorLayout coordinatorLayout;

    public BookViewAdapter(Context c, BookData data, int orderMethod, CoordinatorLayout layout) {
        bookData = data;

        inflater = LayoutInflater.from(c);
        bookData.read();
        books = bookData.getAllBooks();
        bookData.close();
        currentOrder = orderMethod;
        orderBy(currentOrder);
        context = c;
        coordinatorLayout = layout;
    }

    //*****************************************
    //RecyclerViewAdapter implemented functions
    //*****************************************

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.book_view_row, parent, false);
        BookViewHolder holder = new BookViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BookViewHolder holder, int position) {
        Book b = books.get(position);
        holder.setTitleAuthor(b.getTitle() + " - " + b.getAuthor());
        holder.setPublisherYear(b.getPublisher() + ", " + b.getYear());
        holder.setRating(bookData.getRating(b.getPersonalEvaluation()));
        holder.setPersonalEval("\"" + b.getPersonalEvaluation() + "\"");

        if(currentOrder == COMPARE_BY_TITLE) {
            holder.hideCategorySeparator();
            holder.setCategory(b.getCategory());
        }
        else if(currentOrder == COMPARE_BY_CATEGORY) {
            holder.hideCategory();

            int pos = holder.getAdapterPosition();
            BookComparator comparator = new BookComparator(currentOrder);

            if(pos == 0 || comparator.compare(books.get(pos-1), books.get(pos)) != 0) {
                holder.setCategorySeparator(b.getCategory());
            }
            else holder.hideCategorySeparator();
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void add(Book b, int orderMethod) {
        int pos;
        BookComparator comparator = new BookComparator(orderMethod);

        for(pos = 0; pos < books.size(); pos++) {
            if(comparator.compare(b, books.get(pos)) < 1) break;
        }
        bookData.open();
        b = bookData.createBook(b);
        bookData.close();
        books.add(pos, b);
        notifyItemInserted(pos);
    }

    public Book getItem(int pos) {
        return books.get(pos);
    }

    public void remove(Book b) {
        int pos;
        for(pos = 0; pos < books.size(); pos++) {
            if(b == books.get(pos))
                break;
        }
        if(pos < books.size()) {
            bookData.open();
            bookData.deleteBook(books.get(pos));
            bookData.close();
            books.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void remove(int p) {
        bookData.open();
        bookData.deleteBook(books.get(p));
        bookData.close();
        books.remove(p);
        notifyItemRemoved(p);
    }

    //***********************
    //Auxiliary sorting tools
    //***********************

    public void orderBy(int orderMethod) {
        currentOrder = orderMethod;
        BookComparator comparator = new BookComparator(orderMethod);
        Collections.sort(books, comparator);
        notifyDataSetChanged();
    }

    private class BookComparator implements Comparator<Book> {

        private int compareBy;

        public BookComparator(int compareMethod) {
            compareBy = compareMethod;
        }

        @Override
        public int compare(Book lhs, Book rhs) {
            switch (compareBy) {
                case COMPARE_BY_TITLE:
                    return lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase());
                case COMPARE_BY_CATEGORY:
                    return lhs.getCategory().toLowerCase().compareTo(rhs.getCategory().toLowerCase());
                default:
                    return 0;
            }
        }
    }

    //************************
    //Other auxiliary funcions
    //************************

    private Snackbar makeSnackbar(String warning) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, warning, Snackbar.LENGTH_SHORT);

        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        snackbar.show();

        return snackbar;
    }

    private Snackbar makeActionSnackbar(String warning, String action, View.OnClickListener l) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, warning, Snackbar.LENGTH_LONG)
                .setAction(action, l);

        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        snackbar.show();

        return snackbar;
    }

    private void makeDeleteSnackbar(final Book b) {
        Resources res = context.getResources();

        String warning = "\"" + b.getTitle() + "\" " + res.getString(R.string.snackbar_delete_warning);
        String action = res.getString(R.string.snackbar_delete_action);
        final String reverted = "\"" + b.getTitle() + "\" " + res.getString(R.string.snackbar_delete_reverted);

        makeActionSnackbar(warning, action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(b, currentOrder);
                makeSnackbar(reverted);
            }
        });
    }

    private void makeToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    //******************************************************
    //RecyclerViewAdapter's ViewHolder custom implementation
    //******************************************************

    class BookViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {
        private TextView titleAuthor;
        private TextView category;
        private TextView categorySeparator;
        private TextView publisherYear;
        private ImageView menuButton;
        private RatingBar ratingBar;
        private TextView personalEval;

        public BookViewHolder(View itemView) {
            super(itemView);
            titleAuthor = (TextView) itemView.findViewById(R.id.book_view_row_title_author);
            category = (TextView) itemView.findViewById(R.id.book_view_row_category);
            categorySeparator = (TextView) itemView.findViewById(R.id.book_view_row_category_separator);
            publisherYear = (TextView) itemView.findViewById(R.id.book_view_row_publisher_year);
            menuButton = (ImageView) itemView.findViewById(R.id.book_view_row_menu);
            ratingBar = (RatingBar) itemView.findViewById(R.id.book_view_row_rating);
            personalEval = (TextView) itemView.findViewById(R.id.book_view_personal_evaluation);
            menuButton.setOnClickListener(this);
        }

        //*******************
        //Setters and getters
        //*******************

        public void setTitleAuthor(String t) {
            titleAuthor.setText(t);
        }

        public void setCategory(String c) {
            if(category.getVisibility() == View.GONE) category.setVisibility(View.VISIBLE);
            category.setText(c);
        }

        public  void setRating(float rating) {
            ratingBar.setRating(rating);
        }

        public View getMenuButton() {
            return menuButton;
        }

        public void setCategorySeparator(String c) {
            if(categorySeparator.getVisibility() == View.GONE) categorySeparator.setVisibility(View.VISIBLE);
            categorySeparator.setText(c);
        }

        public void setPersonalEval(String personalEval) {
            this.personalEval.setText(personalEval);
        }

        public void setPublisherYear(String publisherYear) {
            this.publisherYear.setText(publisherYear);
        }

        //*************************
        //Other auxiliary functions
        //*************************

        public void hideCategory() {
            category.setVisibility(View.GONE);
        }

        public void hideCategorySeparator() {
            categorySeparator.setVisibility(View.GONE);
        }

        //**********************
        //On click functionality
        //**********************

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.book_view_row_menu:
                    showPopupMenu((ImageView) v);
            }
        }

        public void showPopupMenu(ImageView menu) {
            PopupMenu popup = new PopupMenu(context, menu, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);
            popup.getMenuInflater().inflate(R.menu.book_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.book_menu_modify:
                    makeToast(context.getResources().getString(R.string.feature_not_implemented));
                    return true;
                case R.id.book_menu_delete:
                    Book b = getItem(getAdapterPosition());
                    remove(getAdapterPosition());
                    makeDeleteSnackbar(b);
                    return true;
            }
            return false;
        }
    }
}