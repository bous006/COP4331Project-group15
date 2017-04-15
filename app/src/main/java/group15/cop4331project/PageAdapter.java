package group15.cop4331project;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by bous006 on 3/4/2017.
 */

public class PageAdapter extends FragmentPagerAdapter {

    //The context of the app
    private Context mContext;

    /**
     * @param context, the context of the app
     * @param fm, the fragment manager that will keep each fragment's state in the adapter across swipes
     */
    public PageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * Returns the fragment that should be displayed for the given page number
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MyReportsFragment();
        } else {
            return new ReportsFragment();
        }
    }

    /**
     * Return the total number of pages
     */
    @Override
    public int getCount() { return 2; }

    /**
     * Set the title for each page
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.page_my_reports);
        } else {
            return mContext.getString(R.string.page_reports);
        }
    }
}
