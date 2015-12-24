package edu.rasmussen.Compatibility;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;

/** Handles the list of image names. Updates frame with image if in landscape else launches new activity to display it.
 * @author Madison Liddell
 * @since 11/19/2015
 */
public class ImageListFragment extends ListFragment
{
    private boolean mDualPane;
    private int mCurCheckPosition = 0;
    private Activity mActivity;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        return inflater.inflate(R.layout.image_list_fragment, container, false);
//    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        showList();

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.imageFrame);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showImage(mCurCheckPosition, null);
        }

        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                showList();
            }
        });
    }

    /**
     * Populates cursoradapter with image file names and binds it to the list adapter
     */
    public void showList()
    {
        // columns to query
        String[] columns = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.TITLE,
                MediaStore.Images.Media.DATA};
        // xml views to use
        int[] to = new int[] {R.id.id_placeholder ,R.id.name_entry};
        // query for results
        cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                                 columns, null, null, null);
        // Setup cursor adapter
        // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        adapter = new SimpleCursorAdapter(
                mActivity,
                R.layout.name_entry,
                cursor,
                columns,
                to,
                0);

        // Bind to list adapter.
        setListAdapter(adapter);
        //cursor.getColumnName()
        //cursor.close();
    }

    @Override
    public void onResume()
    {
        showList();
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity)
    {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    // find the selected image and display it
    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {
        // get reference to selected item
        Cursor c = (Cursor) list.getItemAtPosition(position);
        int columnIndex = c.getColumnIndex(MediaStore.Images.Media.TITLE);
        String name = c.getString(columnIndex);
        // custom media dir goes to app's image save location
        File customMediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                        "CompatibilityCamera");
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // normal dir goes to user's gallery media
        File mediaStorageDir = new File(uri.getPath());
        File mediaFile = new File(customMediaStorageDir.getPath() + "/" + name + ".jpg");
        if (!mediaFile.exists()) // if we cant find the selected file try the other more general dir
            mediaFile = new File(mediaStorageDir.getPath() + "/" + name + ".jpg");

        showImage(position, mediaFile.getPath());
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showImage(int index, String image) {
        mCurCheckPosition = index;

        if (mDualPane) {// for landscape
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            ImageDisplayFragment details;

            //if (details == null ){//|| details.getShownIndex().compareTo(image) != 0) {
                // Make new fragment to show this selection.
                details = ImageDisplayFragment.newInstance(image);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.imageFrame, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            //}
        } else { // for portrait
            // launch activity in its own window
            Intent intent = new Intent();
            intent.setClass(getActivity(), ImageDisplayActivity.class);
            intent.putExtra("image name", image);
            startActivity(intent);
        }
    }
}