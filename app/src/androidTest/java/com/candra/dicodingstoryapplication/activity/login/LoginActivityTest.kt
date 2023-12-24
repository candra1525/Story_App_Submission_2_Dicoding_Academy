package com.candra.dicodingstoryapplication.activity.login

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.candra.dicodingstoryapplication.R
import com.candra.dicodingstoryapplication.activity.home.HomeActivity
import com.candra.dicodingstoryapplication.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun logInLogOut() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Log In
        onView(withId(R.id.fab_get_started)).perform(click())
        onView(withId(R.id.ed_login_email)).perform(typeText("guestguest@gmail.com"))
        onView(withId(R.id.ed_login_password)).perform(typeText("Candra2003"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withText(context.getString(R.string.login_success_text)))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))

        val okBtn = onView(withText("OK"))
        okBtn.perform(click())

        onView(withId(R.id.rv_list_story)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_list_story)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )
        onView(withId(R.id.back_to_main_from_detail)).perform(click())

        // Log Out
        onView(withId(R.id.settings))
            .perform(click())
        onView(withId(R.id.action_logout))
            .perform(click())
        onView(withText(context.getString(R.string.question_logout))).inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
        onView(withText("OK")).perform(click())
        onView(withText(context.getString(R.string.logout_success_text))).inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
        onView(withText("OK")).perform(click())
    }
}