package edu.rasmussen.Compatibility;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

/** Used to launch ImageDisplayFragment in a single window.
 * @author Madison Liddell
 * @since 11/20/2015
 */
public class ImageDisplayActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            ImageDisplayFragment displayFragment = new ImageDisplayFragment();
            displayFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, displayFragment).commit();
        }
    }
}