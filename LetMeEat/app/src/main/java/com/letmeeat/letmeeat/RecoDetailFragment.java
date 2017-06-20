package com.letmeeat.letmeeat;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.letmeeat.letmeeat.adapters.PhotosAdapter;
import com.letmeeat.letmeeat.db.RecosContract;
import com.letmeeat.letmeeat.helpers.IntentHelper;
import com.letmeeat.letmeeat.helpers.Utils;
import com.letmeeat.letmeeat.loaders.RecosLoader;
import com.letmeeat.letmeeat.models.Address;
import com.letmeeat.letmeeat.models.Category;
import com.letmeeat.letmeeat.views.DrawInsetsFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by santhosh on 04/04/2017.
 * Fragment that displays the Details of a Recommendation
 */

public class RecoDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = getClass().getSimpleName();

    private static final String ARG_ITEM_ID = "item_id";
    private static final float PARALLAX_FACTOR = 1.25f;

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private int mMutedColor = 0xFF333333;
    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    private ColorDrawable mStatusBarColorDrawable;

    private GridView mPhotosGridView;
    private PhotosAdapter picturesAdapter;
    private int mTopInset;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private final boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;
    private Gson gson = new Gson();

    private IntentHelper intentHelper;

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

        intentHelper = new IntentHelper(getActivity());

        bindViews();
        updateStatusBar();
        return mRootView;
    }

    private void updateStatusBar() {
        int color = 0;
        mStatusBarColorDrawable.setColor(color);
        mDrawInsetsFrameLayout.setInsetBackground(mStatusBarColorDrawable);
    }

    static float progress(float v, float min, float max) {
        return constrain((v - min) / (max - min), 0, 1);
    }

    private static float constrain(float val, float min, float max) {
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
        WebView mapView = (WebView) mRootView.findViewById(R.id.map_view);

        ImageView phoneIcon = (ImageView) mRootView.findViewById(R.id.phone);
        ImageView linkIcon = (ImageView) mRootView.findViewById(R.id.link);
        ImageView directionsIcon = (ImageView) mRootView.findViewById(R.id.directions);

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            name.setText(mCursor.getString(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_NAME)));

            String imageUrl = mCursor.getString(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_IMAGE_URL));
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(getActivity()).load(imageUrl)
                        .resize(200, 200)
                        .centerCrop()
                        .into(image);
                updateMainPicture(imageUrl);
            }

            byte[] blob = mCursor.getBlob(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_PICTURES));
            String pictureBlob = new String(blob);
            String[] pictures = pictureBlob.split(RecosContract.SPACE);
            if (pictures.length > 1) {
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

            String price = mCursor.getString(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_PRICE_RANGE));
            if (!TextUtils.isEmpty(price)) {
                priceRange.setText(price);
            }

            byte[] jsonBytes = mCursor.getBlob(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CATEGORIES));
            String jsonStr = new String(jsonBytes);
            ArrayList<Category> categories = gson.fromJson(jsonStr, new TypeToken<ArrayList<Category>>() {
            }.getType());
            String cuisines = null;
            if (categories != null && categories.size() > 0) {
                cuisines = categories.toString();
                cuisines = cuisines.substring(1, cuisines.length() - 1);
            }
            if (cuisines != null) {
                cuisine.setText(cuisines);
            }

            Address addressObj = getPrintableAddress(mCursor);
            address.setText(addressObj.getPrintableAddress(null));
            final String printableAddress = addressObj.getPrintableAddress(Address.COMMA);
            String mapUrl = getString(R.string.static_map_url, Utils.urlEncode(addressObj.getCity()), Utils.urlEncode(printableAddress), getResources().getConfiguration().screenWidthDp, 150);
            mapView.loadUrl(mapUrl);
            mapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentHelper.sendMapIntent(printableAddress);
                }
            });

            directionsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentHelper.sendMapIntent(printableAddress);
                }
            });

            final String phoneNumber = mCursor.getString(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_PHONE));
            if (TextUtils.isEmpty(phoneNumber)) {
                phoneIcon.setVisibility(View.GONE);
            } else {
                phoneIcon.setVisibility(View.VISIBLE);
                phoneIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intentHelper.sendDialIntent(phoneNumber);
                    }
                });
            }

            final String link = mCursor.getString(mCursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_URL));
            if (TextUtils.isEmpty(phoneNumber)) {
                linkIcon.setVisibility(View.GONE);
            } else {
                linkIcon.setVisibility(View.VISIBLE);
                linkIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intentHelper.sendWebIntent(link);
                    }
                });
            }

        } else {
            mRootView.setVisibility(View.GONE);
        }
    }

    private void updateMainPicture(String pictureUrl) {
        Picasso.with(getActivity()).load(pictureUrl)
                .into(mPhotoView);
    }

    private Address getPrintableAddress(Cursor cursor) {
        Address address = new Address();
        address.setStreetLine1(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_1)));
        address.setStreetLine2(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_2)));
        address.setCity(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CITY)));
        address.setState(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_STATE)));
        address.setZip(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ZIP)));
        address.setLandmark(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_LANDMARK)));
        return address;
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
        return mIsCard ? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() : mPhotoView.getHeight();
    }
}
