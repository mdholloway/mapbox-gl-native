package com.mapbox.mapboxsdk.testapp.render;

import android.graphics.Bitmap;

import android.support.test.espresso.Espresso;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResourceTimeoutException;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mapbox.mapboxsdk.testapp.activity.render.RenderTestActivity;
import com.mapbox.mapboxsdk.testapp.utils.SnapshotterIdlingResource;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumentation test to run render tests
 */
@RunWith(AndroidJUnit4.class)
public class RenderTest {

  private SnapshotterIdlingResource idlingResource;

  @Rule
  public ActivityTestRule<RenderTestActivity> rule = new ActivityTestRule<>(RenderTestActivity.class);

  @Before
  public void beforeTest() {
    try {
      Timber.e("@Before test: register idle resource");
      IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.MINUTES);
      Espresso.registerIdlingResources(idlingResource = new SnapshotterIdlingResource(rule.getActivity()));
    } catch (IdlingResourceTimeoutException idlingResourceTimeoutException) {
      throw new RuntimeException("Idling out!");
    }
  }

  @Test
  public void testRender() {
    onView(withId(android.R.id.content)).check(matches(isDisplayed()));
    Timber.e("RenderTestActivity : getting bitmap");
    Bitmap bitmap = rule.getActivity().getBitmap();
    Assert.assertNotNull(bitmap);
  }

  @After
  public void afterTest() {
    Timber.e("@After test: unregister idle resource");
    Espresso.unregisterIdlingResources(idlingResource);
  }

  private void checkViewIsDisplayed(int id) {
    onView(withId(id))
      .check(matches(isDisplayed()));
  }
}