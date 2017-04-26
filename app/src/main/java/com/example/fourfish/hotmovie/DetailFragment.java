package com.example.fourfish.hotmovie;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fourfish.hotmovie.Entry.Review;
import com.example.fourfish.hotmovie.adapter.ReviewAdapter;
import com.example.fourfish.hotmovie.data.HotMovieContract;
import com.example.fourfish.hotmovie.tool.ChangeCollectTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fourfish on 2017/2/15.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.image_backdrop)
    ImageView mImageView;

    @BindView(R.id.movie_date)
    TextView mDateText;
    @BindView(R.id.movie_grade)
    TextView mGradeText;
    @BindView(R.id.movie_content)
    TextView mContentText;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.movie_run_time)
    TextView mRunTimeText;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.review_list)
    UnScrollListView mListView;
    @BindView(R.id.collect)
    Button mCollectButton;


    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DEFAULT_LOADER = 0;
    static final String DETAIL_URI = "URI";

    private Uri mUri;

    private ArrayList<Review> mStringArrayList = new ArrayList<>();
    private ReviewAdapter mReviewAdapter;

    private static final String[] MOVIE_COLUMNS = {
            HotMovieContract.MovieEntry.TABLE_NAME + "." + HotMovieContract.MovieEntry._ID,
            HotMovieContract.MovieEntry.COLUMN_OVERVIEW,
            HotMovieContract.MovieEntry.COLUMN_ID,
            HotMovieContract.MovieEntry.COLUMN_VIDEO_SOURCE,
            HotMovieContract.MovieEntry.COLUMN_TITLE,
            HotMovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            HotMovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            HotMovieContract.MovieEntry.COLUMN_RUN_TIME,
            HotMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            HotMovieContract.ReviewEntry.COLUMN_AUTHOR,
            HotMovieContract.ReviewEntry.COLUMN_CONTENT
    };

    private static final int COL_REVIEW_AUTHOR = 9;
    private static final int COL_REVIEW_CONTENT = 10;

    private static final int COL_MOVIE_OVERVIEW = 1;
    private static final int COL_MOVIE_ID = 2;
    private static final int COL_MOVIE_VIDEO_SOURCE = 3;
    private static final int COL_MOVIE_TITLE = 4;
    private static final int COL_MOVIE_RELEASE_DATE = 5;
    private static final int COL_MOVIE_BACKDROP_PATH = 6;
    private static final int COL_MOVIE_RUN_TIME = 7;
    private static final int COL_MOVIE_GRADE = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DEFAULT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mReviewAdapter = new ReviewAdapter(getContext(), R.layout.review_linear, mStringArrayList);
        mListView.setAdapter(mReviewAdapter);
        mListView.setEmptyView(rootView.findViewById(R.id.empty_view));
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri == null) {
            return null;
        }
        return new CursorLoader(getActivity(),
                mUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst() || data == null) {
            return;
        }
        Log.i("onLoadToFirst:", "ARRIVE" + " " + data.getCount());

        String overview = "", title = "", release_data = "", backup = "", grade = "", runtime = "";

        overview = data.getString(COL_MOVIE_OVERVIEW);
        title = data.getString(COL_MOVIE_TITLE);
        final String vedio = data.getString(COL_MOVIE_VIDEO_SOURCE);
        final String id = data.getString(COL_MOVIE_ID);
        release_data = data.getString(COL_MOVIE_RELEASE_DATE);
        backup = data.getString(COL_MOVIE_BACKDROP_PATH);

        grade = data.getString(COL_MOVIE_GRADE);
        runtime = data.getString(COL_MOVIE_RUN_TIME);

        String author = "", content = "";

        do {
            author = data.getString(COL_REVIEW_AUTHOR);
            content = data.getString(COL_REVIEW_CONTENT);
            Review review = new Review(author, content);
            mStringArrayList.add(review);
        } while (data.moveToNext());
        mReviewAdapter.notifyDataSetChanged();

        Log.i("DeatilFragment", author + " " + content);

        mCollapsingToolbarLayout.setTitle(title);
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        Log.v(LOG_TAG, title + " - " + vedio);

        Picasso.with(getActivity())
                .load(backup)
                .placeholder(R.mipmap.ic_launcher)
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT);
                    }
                });

        mRunTimeText.setText("RunTime: " + runtime);
        mGradeText.setText("Grade: " + grade);
        mContentText.setText(overview);
        mDateText.setText("Date: " + release_data);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + vedio));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + vedio));

                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });

        mCollectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeCollectTask changeCollectTask = new ChangeCollectTask(getActivity());
                changeCollectTask.execute(id);
                Log.i("COllECTBUTTON:", "ARRIVE");
            }
        });


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void onPreferenceChanged(String newPreference) {
        Uri uri = mUri;
        if (uri != null) {
            String id = HotMovieContract.ReviewEntry.getMovieIdFromUri(uri);
            Uri upatedUri = HotMovieContract.ReviewEntry.buildReviewWithMovie(Long.parseLong(id));
            mUri = upatedUri;
            getLoaderManager().restartLoader(DEFAULT_LOADER, null, this);
        }
    }
}
