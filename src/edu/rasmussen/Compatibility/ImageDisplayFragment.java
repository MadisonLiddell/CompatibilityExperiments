package edu.rasmussen.Compatibility;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Called by MainActivity when needed for double pane display, and also used for single pane display
 * @author Madison Liddell
 * @since 11/19/2015
 */
public class ImageDisplayFragment extends Fragment
{
    private Activity mActivity;
    private static String NAME_KEY = "image name";

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the image at 'index'.
     */
    public static ImageDisplayFragment newInstance(String image)
    {
        ImageDisplayFragment f = new ImageDisplayFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(NAME_KEY, image);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Activity activity)
    {
        mActivity = activity;
        super.onAttach(activity);
    }

    public String getShownIndex()
    {
        return getArguments().getString(NAME_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container == null)
            return null;// no need to load image if fragment container doesn't exist
        return loadImageFromStorage(getArguments().getString(NAME_KEY, "Unnamed"));
    }

    private ImageView loadImageFromStorage(String imageName)
    {
        File imageFile = new File(imageName);
        // add image to imageview
        ImageView img = new ImageView(mActivity);
        if (imageFile.exists()) {
            Bitmap b = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            img.setImageBitmap(b);
        }
        return img;
    }
}