package com.letmeeat.letmeeat;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.letmeeat.letmeeat.adapters.PhotosAdapter;
import com.letmeeat.letmeeat.db.RecosContract;
import com.letmeeat.letmeeat.loaders.RecosLoader;
import com.letmeeat.letmeeat.models.Address;
import com.letmeeat.letmeeat.views.DrawInsetsFrameLayout;
import com.letmeeat.letmeeat.views.ObservableScrollView;
import com.squareup.picasso.Picasso;

/**
 * Created by santhosh on 04/04/2017.
 * Fragment that displays the Details of a Recommendation
 */

public class RecoDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = getClass().getSimpleName();

    public static final String ARG_ITEM_ID = "item_id";
    private static final float PARALLAX_FACTOR = 1.25f;

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private int mMutedColor = 0xFF333333;
    private ObservableScrollView mScrollView;
    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    private ColorDrawable mStatusBarColorDrawable;

    private GridView mPhotosGridView;
    private PhotosAdapter picturesAdapter;
    private int mTopInset;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecoDetailFragment() {
    }

    public static RecoDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        RecoDetailFragment fragment = new RecoDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(R.dimen.detail_card_top_margin);
        setHasOptionsMenu(true);
    }

    public RecoDetailsActivity getActivityCast() {
        return (RecoDetailsActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_reco_detail, container, false);
        mDrawInsetsFrameLayout = (DrawInsetsFrameLayout)
                mRootView.findViewById(R.id.draw_insets_frame_layout);
        mDrawInsetsFrameLayout.setOnInsetsCallback(new DrawInsetsFrameLayout.OnInsetsCallback() {
            @Override
            public void onInsetsChanged(Rect insets) {
                mTopInset = insets.top;
            }
        });


        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);
        mPhotoContainerView = mRootView.findViewById(R.id.photo_container);
        mPhotosGridView = (GridView) mRootView.findViewById(R.id.photos_gridview);
        mPhotosGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                updateMainPicture((String) picturesAdapter.getItem(position));
            }
        });
        mStatusBarColorDrawable = new ColorDrawable(0);

        bindViews();
        updateStatusBar();
        return mRootView;
    }

    private void updateStatusBar() {
        int color = 0;
        if (mPhotoView != null && mTopInset != 0 && mScrollY > 0) {
            float f = progress(mScrollY,
                    mStatusBarFullOpacityBottom - mTopInset * 3,
                    mStatusBarFullOpacityBottom - mTopInset);
            color = Color.argb((int) (255 * f),
                    (int) (Color.red(mMutedColor) * 0.9),
                    (int) (Color.green(mMutedColor) * 0.9),
                    (int) (Color.blue(mMutedColor) * 0.9));
        }
        mStatusBarColorDrawable.setColor(color);
        mDrawInsetsFrameLayout.setInsetBackground(mStatusBarColorDrawable);
    }

    static float progress(float v, float min, float max) {
        return constrain((v - min) / (max - min), 0, 1);
    }

    static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        TextView name = (TextView) mRootView.findViewById(R.id.reco_name);
        ImageView image = (ImageView) mRootView.findViewById(R.id.reco_image);
        TextView priceRange = (TextView) mRootView.findViewById(R.id.price_range);
        TextView reviewsCount = (TextView) mRootView.findViewById(R.id.reviews_count);
        ImageView ratingStar1 = (ImageView) mRootView.findViewById(R.id.rating_star_1);
        ImageView ratingStar2 = (ImageView) mRootView.findViewById(R.id.rating_star_2);
        ImageView ratingStar3 = (ImageView) mRootView.findViewById(R.id.rating_star_3);
        ImageView ratingStar4 = (ImageView) mRootView.findViewById(R.id.rating_star_4);
        ImageView ratingStar5 = (ImageView) mRootView.findViewById(R.id.rating_star_5);
        TextView cuisine = (TextView) mRootView.findViewById(R.id.cuisine_type);
        TextView address = (TextView) mRootView.findViewById(R.id.address);
//        ImageView seeMore = (ImageView) mRootView.findViewById(R.id.see_more);
//        seeMore.setVisibility(View.GONE);

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            name.setText(mCursor.getString(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_NAME)));

            byte[] blob = mCursor.getBlob(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_PICTURES));

            String pictureBlob = new String(blob);
            String[] pictures = pictureBlob.split(RecosContract.SPACE);

            if (pictures[0] != null) {
                Picasso.with(getActivity()).load(pictures[0])
                        .resize(200, 200)
                        .centerCrop()
                        .into(image);

                updateMainPicture(pictures[0]);

                picturesAdapter = new PhotosAdapter(getActivity(), pictures);
                mPhotosGridView.setAdapter(picturesAdapter);
                picturesAdapter.notifyDataSetChanged();
            }
            reviewsCount.setText(getActivity().getString(R.string.reviews_count, mCursor.getInt(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_REVIEWS_COUNT))));

            float ratings = mCursor.getFloat(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_RATINGS));
            ratingStar1.setImageResource(ratings >= 1 ? R.drawable.star : (ratings < 1 && ratings > 0) ? R.drawable.star_half : R.drawable.star_outline);
            ratingStar2.setImageResource(ratings >= 2 ? R.drawable.star : (ratings < 2 && ratings > 1) ? R.drawable.star_half : R.drawable.star_outline);
            ratingStar3.setImageResource(ratings >= 3 ? R.drawable.star : (ratings < 3 && ratings > 2) ? R.drawable.star_half : R.drawable.star_outline);
            ratingStar4.setImageResource(ratings >= 4 ? R.drawable.star : (ratings < 4 && ratings > 3) ? R.drawable.star_half : R.drawable.star_outline);
            ratingStar5.setImageResource(ratings == 5 ? R.drawable.star : (ratings < 5 && ratings > 4) ? R.drawable.star_half : R.drawable.star_outline);

            priceRange.setText(getActivity().getString(R.string.pricerange, mCursor.getInt(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_START_PRICE)), mCursor.getInt(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_END_PRICE))));
            cuisine.setText(mCursor.getString(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CUISINE)));
            address.setText(getPrintableAddress(mCursor));

        } else {
            mRootView.setVisibility(View.GONE);
        }
    }

    private void updateMainPicture(String pictureUrl) {
        Picasso.with(getActivity()).load(pictureUrl)
                .into(mPhotoView);
    }

    private String getPrintableAddress(Cursor cursor) {
        Address address = new Address();
        address.setStreetLine1(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_1)));
        address.setStreetLine2(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_2)));
        address.setCity(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CITY)));
        address.setState(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_STATE)));
        address.setZip(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ZIP)));
        address.setLandmark(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_LANDMARK)));
        return address.getPrintableAddress(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return RecosLoader.newInstanceForRecoId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

    public int getUpButtonFloor() {
        if (mPhotoContainerView == null || mPhotoView.getHeight() == 0) {
            return Integer.MAX_VALUE;
        }

        // account for parallax
        return mIsCard ? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() - mScrollY : mPhotoView.getHeight() - mScrollY;
    }
}