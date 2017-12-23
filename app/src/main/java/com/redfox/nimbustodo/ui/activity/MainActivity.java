package com.redfox.nimbustodo.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.SubtitleCollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.data.preferences.common_pref.SPCommonMgr;
import com.redfox.nimbustodo.data.preferences.weather_pref.SPWeatherMgr;
import com.redfox.nimbustodo.job.WeatherJobService;
import com.redfox.nimbustodo.network.NetworkAsync;
import com.redfox.nimbustodo.network.NetworkCallbacks;
import com.redfox.nimbustodo.network.NetworkObserverCallBack;
import com.redfox.nimbustodo.receiver.ConstantNetMonitorReceiver;
import com.redfox.nimbustodo.ui.fragments.FragOne;
import com.redfox.nimbustodo.ui.fragments.FragTwo;
import com.redfox.nimbustodo.ui.interfaces.LocationCallBack;
import com.redfox.nimbustodo.ui.interfaces.TagImageCallBacks;
import com.redfox.nimbustodo.util.common_util.UtilCal;
import com.redfox.nimbustodo.util.common_util.UtilExtra;
import com.redfox.nimbustodo.util.common_util.UtilSnackBar;
import com.redfox.nimbustodo.util.common_util.UtilStorage;
import com.redfox.nimbustodo.util.common_util.UtilTagImage;
import com.redfox.nimbustodo.weather.DisplayImageCallback;
import com.redfox.nimbustodo.weather.model.OpenWeatherModel;
import com.redfox.nimbustodo.weather.weather_util.UtilGetIcon;
import com.redfox.nimbustodo.weather.weather_util.UtilLocationDialog;
import com.redfox.nimbustodo.weather.weather_util.UtilWeatherConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NetworkCallbacks
        , NetworkObserverCallBack, LocationCallBack, DisplayImageCallback, TagImageCallBacks {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private final static String VERSION_CODE = "VERSION_CODE";
    private final static String INSTALLED_VERSION_NAME = "1.4";


    @BindView(R.id.am_toolbar)
    Toolbar toolbar;
    @BindView(R.id.am_frameLayout)
    FrameLayout framelayout;
    @BindView(R.id.am_nav_view)
    NavigationView navView;
    @BindView(R.id.am_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.am_colToolbar)
    SubtitleCollapsingToolbarLayout clpsToolbar;
    @BindView(R.id.am_appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.weather_temperature)
    TextView weatherTemperature;
    @BindView(R.id.weather_image)
    ImageView weatherImageIMV;
    @BindView(R.id.weather_city)
    TextView weatherCityTv;
    @BindView(R.id.weather_country)
    TextView weatherCountryTv;
    @BindView(R.id.am_configBtn)
    Button btnConfig;
    @BindView(R.id.fabBottom)
    FloatingActionButton fabBottom;
    @BindView(R.id.bs_imv_tag)
    CircleImageView bsImvTag;
    @BindView(R.id.bs_Etx)
    EditText bsEtx;
    @BindView(R.id.bs_imv_save)
    ImageView bsImvSave;
    @BindView(R.id.bs_root_bottom_layout)
    LinearLayout bsRootBottomLayout;
    @BindView(R.id.aa_btn_signOut)
    Button btnSignOut;

    private CircleImageView navProfile;
    private TextView navTitle;
    private TextView navEmail;
    private CircleImageView navNetChecker;

    private SPWeatherMgr spWeatherMgr;

    private String city;
    private String country;
    private double tempDouble;
    private String iCon;

    private ConstantNetMonitorReceiver mReceiver;
    private static String respCode = "";
    private boolean isThereNetConstant = false;

    private boolean isBottomShowing = false;
    private boolean isFabShowing = false;

    private Dialog dialogTag;
    private int imageUripath;
    private boolean isLastEntry = false; //to remove entry : based on this value

    private boolean shouldExit = false;  // exit strategy based on this
    private GoogleSignInClient googleSignInClient;
    private static final int REQ_CODE_SIGN_IN = 12;

    private boolean isSignedIn = false;
    private String prefDisplayName;
    private String prefEmail;
    private Bitmap profileBitmap;
    private SPCommonMgr spCommonMgr;
    private Handler handlerToolTips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG_DEBUG) Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FirebaseCrash.setCrashCollectionEnabled(false);
        bsRootBottomLayout.setVisibility(View.INVISIBLE);
        setUpSharedPref();
        setUpToolbar();
        setUpCollapsingToolBar();
        setUpDrawer();
        setUpNavHeader();
        setUpNavigationView();
        setUpGoogleApi();
        setUpBackStackListener();
        setUpReceiver();
        setUpConfigureButton();
        setUpHomeTransaction();
        isFabShowing = true;
        showFab();
        initFireBaseConfig();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }

    }

    private void setUpSharedPref() {
        spWeatherMgr = new SPWeatherMgr(MainActivity.this);
        spCommonMgr = new SPCommonMgr(MainActivity.this);
        loadSignInData();
    }

    private void loadSignInData() {
        int isSignedIn = spCommonMgr.getSignInStatus();
        prefDisplayName = spCommonMgr.getSignInName();
        prefEmail = spCommonMgr.getSignInEmail();

        if (isSignedIn == 1) {
            btnSignOut.setText("SIGN OUT");
            this.isSignedIn = true;
        } else {
            btnSignOut.setText("SIGN IN");
            this.isSignedIn = false;
        }

        if (isSignedIn == 1 && this.isSignedIn == true) {
            profileBitmap = UtilStorage.getProfileImage(MainActivity.this);
        }
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.am_toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpCollapsingToolBar() {

        UtilCal utilCal = new UtilCal();
        int dayOfMonth = utilCal.getDayOfMonth();
        String monthName = utilCal.getMonthName();
        String dayName = utilCal.getDayName();

        String subTitleText = dayName + ", " + String.valueOf(dayOfMonth) + "th " + monthName;

        if (isSignedIn == true) {
            String arr[] = prefDisplayName.split(" ", 0);
            clpsToolbar.setTitle("Hi" + ", " + arr[0]);
        } else {
            clpsToolbar.setTitle(getString(R.string.greetings));
        }
        clpsToolbar.setSubtitle(subTitleText);


        clpsToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarTitle);
        clpsToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarTitle);
        clpsToolbar.setExpandedSubtitleTextAppearance(R.style.ExpandedAppBarSub);
        clpsToolbar.setCollapsedSubtitleTextAppearance(R.style.CollapsedAppBarSub);


        Typeface typeface = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeface = getResources().getFont(R.font.caviar_dreams);
        } else {
            typeface = ResourcesCompat.getFont(this, R.font.caviar_dreams);
        }
        clpsToolbar.setExpandedTitleTypeface(typeface);
        clpsToolbar.setExpandedSubtitleTypeface(typeface);
        clpsToolbar.setCollapsedTitleTypeface(typeface);
        clpsToolbar.setCollapsedSubtitleTypeface(typeface);

    }

    private void setUpDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.am_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpNavHeader() {
        if (navView != null) {
            View navHeaderView = navView.getHeaderView(0);
            navProfile = (CircleImageView) navHeaderView.findViewById(R.id.am_nav_header_profile);
            navTitle = (TextView) navHeaderView.findViewById(R.id.am_nav_header_Title);
            navEmail = (TextView) navHeaderView.findViewById(R.id.am_nav_header_tvEmail);
            navNetChecker = (CircleImageView) navHeaderView.findViewById(R.id.am_nav_header_netCheckImv);

            if (isSignedIn == false) {
                loadDefaultSignInData();
            } else {
                navTitle.setText(prefDisplayName);
                navEmail.setText(prefEmail);
                Glide.with(MainActivity.this).asBitmap().load(profileBitmap).into(navProfile);
            }
        }
    }

    private void setUpNavigationView() {
        navView.setNavigationItemSelectedListener(this);
    }

    private void setUpGoogleApi() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_CODE_SIGN_IN);
    }

    private void setUpBackStackListener() {
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = getFragmentManager().findFragmentById(R.id.am_frameLayout);
                if (currentFragment != null) {

                    int myCount = getFragmentManager().getBackStackEntryCount();
                    if (LOG_DEBUG) Log.e(TAG, "backStackCount : " + myCount);

                    String currentFragName = currentFragment.getClass().getSimpleName();

                    if (currentFragName.equalsIgnoreCase(FragOne.class.getSimpleName())) {
                        shouldExit = true;
                        navView.getMenu().getItem(0).setChecked(true);
                        if (LOG_DEBUG) Log.e(TAG, " whichFragment " + currentFragName);
                        showFab();
                        isBottomShowing = false;
                        isFabShowing = true;


                    } else if (currentFragName.equalsIgnoreCase(FragTwo.class.getSimpleName())) {
                        shouldExit = false;
                        navView.getMenu().getItem(1).setChecked(true);
                        if (LOG_DEBUG) Log.e(TAG, " whichFragment " + currentFragName);
                        hideFab(false);
                        if (isBottomShowing == true) {
                            isFabShowing = true;
                            isBottomShowing = false;
                            hideBottomLinear(false);

                        }
                    }
                }
            }
        });
    }

    private void setUpReceiver() {
        mReceiver = new ConstantNetMonitorReceiver(this);
        registerReceiver();
    }

    private void setUpConfigureButton() {
        if (spWeatherMgr.getSavedStatusForConfigureBtn() == 0) {
            btnConfig.setVisibility(View.VISIBLE);
            hideWeatherPanel();
        } else if (spWeatherMgr.getSavedStatusForConfigureBtn() == 1) {
            btnConfig.setVisibility(View.GONE);
            showWeatherPanel();
            loadPreferenceData();
        }
    }

    private void setUpPeriodicJob() {
        if (LOG_DEBUG) Log.e(TAG, " setUpPeriodicJob()");

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(MainActivity.this));

        Job myJob = dispatcher.newJobBuilder()
                .setService(WeatherJobService.class)
                .setTag(WeatherJobService.class.getSimpleName())
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(5 * 60, 15 * 60))
                .addConstraint(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    private void setUpHomeTransaction() {
        doTransaction(FragOne.getInstance(), true);
    }


    private void setUpHandler() {
        handlerToolTips = new Handler();
        handlerToolTips.postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpToolTip();
            }
        }, 400);
        spCommonMgr.saveStatusToolTipMA(1);

    }

    private void setUpToolTip() {
        new SimpleTooltip.Builder(this)
                .anchorView(bsImvTag)
                .text(" pick a category")
                .backgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .arrowColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .textColor(ContextCompat.getColor(this, R.color.color_white))
                .gravity(Gravity.END)
                .animated(true)
                .transparentOverlay(false)
                .dismissOnOutsideTouch(true)
                .onDismissListener(new SimpleTooltip.OnDismissListener() {
                    @Override
                    public void onDismiss(SimpleTooltip tooltip) {
                        tooltip.dismiss();
                    }
                })
                .build()
                .show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.fragOne_drawer) {
            if (LOG_DEBUG) Log.e(TAG, "fragOne : tapped()");
            doTransaction(FragOne.getInstance(), true);

        } else if (id == R.id.fragTwo_drawer) {
            if (LOG_DEBUG) Log.e(TAG, "fragTwo : tapped()");
            doTransaction(FragTwo.getInstance(), true);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.am_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //adding transaction to backstack based on boolean, for manipulating onBackpressed
    private void doTransaction(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.am_frameLayout, fragment);
        if (addToBackStack == true) {
            transaction.addToBackStack(null);
        }
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.am_menu_settings:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case R.id.am_menu_changeCity:

                if (isThereNetConstant == true) {
                    if (LOG_DEBUG) Log.e(TAG, " menu change city tapped");
                    UtilLocationDialog.locationPicker(MainActivity.this, MainActivity.this);
                } else {
                    UtilSnackBar.showSnackBar(MainActivity.this, "Net Not Available !!", UtilWeatherConstants.NO_NET);
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreExecuteInfY() {
        if (LOG_DEBUG) Log.e(TAG, "callback on Pre..");
    }


    @Override
    public void onPostExecuteInfY(String result) {
        if (LOG_DEBUG) {
            Log.e(TAG, "callback on Post..");
            Log.e(TAG, " ---- " + result);
        }
        UtilLocationDialog.dismissDialog();
        parseWeatherResponse(result);
    }

    private void parseWeatherResponse(String result) {
        if (result != null) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            OpenWeatherModel openWeatherModel = gson.fromJson(result, OpenWeatherModel.class);

            if (LOG_DEBUG) Log.e(TAG, " statusCode " + respCode);

            switch (respCode) {
                case "200":
                    if (LOG_DEBUG) Log.e(TAG, " net yes : 200 : " + respCode);

                    showWeatherPanel();
                    parseSuccess(openWeatherModel);
                    spWeatherMgr.saveDataToPreference(city, country, tempDouble, iCon);

                    if (city != null && city.length() > 0) {
                        if (LOG_DEBUG)
                            Log.e(TAG, " scheduling, city name check :- " + spWeatherMgr.getPrefCity());

                        setUpPeriodicJob();
                    }
                    btnConfig.setVisibility(View.GONE);
                    spWeatherMgr.saveStatusForConfigureBtn(1);

                    break;

                case "404":
                    if (LOG_DEBUG) Log.e(TAG, " net yes : 404 : " + respCode);

                    hideWeatherPanel();
                    btnConfig.setVisibility(View.VISIBLE);
                    spWeatherMgr.saveStatusForConfigureBtn(0);
                    UtilSnackBar.showSnackBar(MainActivity.this, "City Not Found !! Try Another", UtilWeatherConstants.NOT_FOUND);
                    break;

                default:
            }
        }

    }

    private void parseSuccess(OpenWeatherModel openWeatherModel) {
        if (openWeatherModel.getName() != null) {
            city = openWeatherModel.getName();
            weatherCityTv.setText(city);
        }

        if (openWeatherModel.getSys().getCountry() != null) {
            country = openWeatherModel.getSys().getCountry();
            weatherCountryTv.setText(country);
        }
        if (openWeatherModel.getMain().getTemp() != null) {
            tempDouble = openWeatherModel.getMain().getTemp();
            int tempInt = (int) tempDouble;
            weatherTemperature.setText(String.valueOf(tempInt).concat(getString(R.string.celsius)));
        }
        if (openWeatherModel.getWeather().get(0).getIcon() != null) {
            iCon = openWeatherModel.getWeather().get(0).getIcon();
            UtilGetIcon.displayWeatherImage(iCon, MainActivity.this);
        }

    }

    @Override
    public void loadToGlide(int drawable) {
        Glide.with(MainActivity.this).asDrawable()
                .load(drawable).transition(GenericTransitionOptions.with(R.anim.slide_left)).into(weatherImageIMV);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (LOG_DEBUG) Log.e(TAG, "onNewIntent()");

    }

    private void loadPreferenceData() {
        if (LOG_DEBUG) Log.e(TAG, " loadPreferenceData()");

        if (spWeatherMgr.getPrefCity() != null) {
            city = spWeatherMgr.getPrefCity();
            weatherCityTv.setText(city);
        }
        if (spWeatherMgr.getPrefCountry() != null) {
            country = spWeatherMgr.getPrefCountry();
            weatherCountryTv.setText(country);
        }

        if (spWeatherMgr.getPrefTemp() != null) {
            tempDouble = spWeatherMgr.getPrefTemp();
            int tempInt = (int) tempDouble;
            weatherTemperature.setText(String.valueOf(tempInt).concat(getString(R.string.celsius)));
        }
        if (spWeatherMgr.getPrefIcon() != null) {
            iCon = spWeatherMgr.getPrefIcon();
            UtilGetIcon.displayWeatherImage(iCon, MainActivity.this);
        }
        if (LOG_DEBUG)
            Log.e(TAG, " Retrieved Data from Pref :-  " + "\n" + city + "\n" + country + "\n" + String.valueOf(tempDouble));

    }


    public void registerReceiver() {
        if (LOG_DEBUG) Log.e(TAG, " registering..");

        registerReceiver(mReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void unRegisterReceiver() {
        if (LOG_DEBUG) Log.e(TAG, "unregistered.......");

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void isContinuousNetCheck(boolean isThere) {

        isThereNetConstant = isThere;
        if (LOG_DEBUG) Log.e(TAG, " constant netChecker " + isThere);
        UtilExtra.uiCheck(MainActivity.class.getSimpleName());

        if (isThere == true) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navNetChecker.setImageResource(R.color.net_check_available_color);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navNetChecker.setImageResource(R.color.net_check_unavailable_color);
                }
            });
        }
    }


    @OnClick(R.id.am_configBtn)
    public void onViewClicked() {

        if (isThereNetConstant == true) {
            if (LOG_DEBUG) Log.e(TAG, " btn Taped : and Net Status : " + isThereNetConstant);

            UtilLocationDialog.locationPicker(MainActivity.this, MainActivity.this);

        } else if (isThereNetConstant == false) {
            if (LOG_DEBUG) Log.e(TAG, " btn Taped : and Net Status : " + isThereNetConstant);

            hideWeatherPanel();
            btnConfig.setVisibility(View.VISIBLE);
            spWeatherMgr.saveStatusForConfigureBtn(0);
            UtilSnackBar.showSnackBar(MainActivity.this, "Net Not Available !!", UtilWeatherConstants.NO_NET);
        }
    }

    @Override
    public void locationProvider(String locationName) {
        if (LOG_DEBUG) Log.e(TAG, " locationProvider() " + locationName);

        if (locationName.length() > 3) {
            new NetworkAsync(this, MainActivity.this, locationName).execute();
            if (LOG_DEBUG) Log.e(TAG, " typed in editText : " + locationName);
            UtilLocationDialog.dismissDialog();
        }
    }


    private void showWeatherPanel() {
        weatherCityTv.setVisibility(View.VISIBLE);
        weatherCountryTv.setVisibility(View.VISIBLE);
        weatherTemperature.setVisibility(View.VISIBLE);
        weatherImageIMV.setVisibility(View.VISIBLE);
    }

    private void hideWeatherPanel() {
        weatherCityTv.setVisibility(View.GONE);
        weatherCountryTv.setVisibility(View.GONE);
        weatherTemperature.setVisibility(View.GONE);
        weatherImageIMV.setVisibility(View.GONE);
    }

    public static void cityFound(int successCode) {
        respCode = String.valueOf(successCode);
    }

    public static void cityNotFound(String errorCode) {
        respCode = errorCode;
    }

    @OnClick(R.id.fabBottom)
    public void onFabBottomClicked() {
        if (LOG_DEBUG) Log.e(TAG, "fabAdd Tapped");
        if (isFabShowing == true) {
            isFabShowing = false;
            isBottomShowing = true;
            showBottomLinear();
            hideFab(true);

            if (spCommonMgr.getSavedStatusToolTipMA() == 0) {
                setUpHandler();
            }
        }

    }

    private void showBottomLinear() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        bsRootBottomLayout.startAnimation(slideUp);
        bsRootBottomLayout.setVisibility(View.VISIBLE);
    }

    private void hideBottomLinear(boolean withAnimation) {
        if (withAnimation == true) {
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            bsRootBottomLayout.startAnimation(slideDown);
            bsRootBottomLayout.setVisibility(View.INVISIBLE);
        } else {
            bsRootBottomLayout.setVisibility(View.INVISIBLE);
        }

    }

    private void showFab() {
        Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        fabBottom.setAnimation(slideLeft);
        fabBottom.setVisibility(View.VISIBLE);

    }

    private void hideFab(boolean withAnimation) {
        if (withAnimation == true) {
            Animation slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_right);
            fabBottom.setAnimation(slideRight);
            fabBottom.setVisibility(View.INVISIBLE);
        } else {
            fabBottom.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.bs_imv_tag)
    public void bsTagImvClicked() {
        pickTagImage();

    }

    private void pickTagImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Category");
        ListView listView = new ListView(this);
        final String[] stringArray = new String[]{"SPORTS", "EVENTS", "WORK", "EXERCISE", "MEETINGS", "STUDY", "TRAVEL", "SHOPPING", "OTHERS", "ALIEN"};

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
        listView.setAdapter(modeAdapter);

        builder.setView(listView);
        dialogTag = builder.create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String what = stringArray[position];
                if (LOG_DEBUG) Log.e(TAG, what + "  : " + position);
                UtilTagImage.imagePicker(position, MainActivity.this);

            }
        });

        dialogTag.show();

    }


    @Override
    public void imageListener(int drawable) {
        imageUripath = drawable;
        dismissDialogTag();
        bsImvTag.setImageResource(imageUripath);

    }

    private void dismissDialogTag() {
        if (dialogTag != null) {
            dialogTag.dismiss();
        }
    }

    @OnClick(R.id.bs_imv_save)
    public void bsSaveImvClicked() {
        addEntry();
        imageUripath = 0;
        hideBottomLinear(true);
        setUpHomeTransaction();
        showFab();
        UtilExtra.hideKeyboard(MainActivity.this);
        bsEtx.setText("");
        bsImvTag.setImageResource(R.drawable.tag_default);
    }

    private void addEntry() {

        NoteModel noteModel = new NoteModel();

        String noteTitle = bsEtx.getText().toString().trim();
        noteModel.setTitle(noteTitle);
        if (imageUripath == 0) {
            noteModel.setImgUriPath(R.drawable.tag_default);
        } else {
            noteModel.setImgUriPath(imageUripath);

        }
        noteModel.setSub_text("");
        noteModel.setCreateDate(System.currentTimeMillis());
        noteModel.setUpdateDate(System.currentTimeMillis()); //for first time , current time is last visited time
        noteModel.setScheduleTimeLong(0);
        noteModel.setScheduledWhenLong(0);
        noteModel.setScheduledTitle("notSet");
        noteModel.setIsAlarmScheduled(0);
        noteModel.setIsTaskDone(0);
        noteModel.setIsArchived(0);

        if (noteTitle.length() < 0 | noteTitle.isEmpty()) {
            Toast.makeText(this, "Empty Note can't be saved !", Toast.LENGTH_SHORT).show();

        } else {

            DBMgr dbManager = new DBMgr(MainActivity.this);
            dbManager.openDataBase();

            boolean saveStatus = dbManager.insertNote(noteModel);
            if (saveStatus == true) {
                UtilExtra.showToast(this, "Added...");
            } else {
                bsEtx.setFocusable(true);
                bsEtx.setError("something bad happened !!");
            }
            dbManager.closeDataBase();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQ_CODE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            loadSuccessSignInData(account);

        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            loadDefaultSignInData();
        }
    }

    private void loadSuccessSignInData(GoogleSignInAccount account) {
        if (account != null) {

            isSignedIn = true;
            btnSignOut.setText("SIGN OUT");

            String displayName = account.getDisplayName();
            navTitle.setText(displayName);
            String email = account.getEmail();
            navEmail.setText(email);


            String arr[] = displayName.split(" ", 0);
            clpsToolbar.setTitle("Hi" + ", " + arr[0]);

            if (spCommonMgr != null) {
                spCommonMgr.saveSignInStatus(1);
                spCommonMgr.saveSignInName(displayName);
                spCommonMgr.saveSignInEmail(email);
            }

            Uri imageUri = account.getPhotoUrl();
            Glide.with(this).asBitmap().load(imageUri).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    navProfile.setImageBitmap(resource);
                    UtilStorage.saveProfileImage(MainActivity.this, resource);

                }
            });
        }
    }


    private void loadDefaultSignInData() {
        isSignedIn = false;

        String displayName = getString(R.string.profile_default_title);
        navTitle.setText(displayName);
        String email = getString(R.string.profile_default_email);
        navEmail.setText(email);
        clpsToolbar.setTitle(getString(R.string.greetings));
        Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(navProfile);
    }

    @OnClick(R.id.aa_btn_signOut)
    public void onViewClickedSingOut() {

        if (isSignedIn == true) {
            googleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "You have been signed out..", Toast.LENGTH_SHORT).show();
                            loadDefaultSignInData();
                            btnSignOut.setText("SIGN IN");
                            if (spCommonMgr != null) {
                                spCommonMgr.saveSignInStatus(0);
                            }

                        }
                    });
        } else if (isSignedIn == false) {
            signIn();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LOG_DEBUG) Log.e(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LOG_DEBUG) Log.e(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LOG_DEBUG) Log.e(TAG, "onStop()");

        if (handlerToolTips != null) {
            handlerToolTips.removeCallbacks(null);
        }
    }

    @Override
    protected void onDestroy() {
        if (LOG_DEBUG) Log.e(TAG, "onDestroy()");

        super.onDestroy();
        unRegisterReceiver();
        if (mReceiver != null) {
            mReceiver.quitHandlerThread();
        }
    }

    private void initFireBaseConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(remoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        long cacheExpiration = 3600;

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            if (LOG_DEBUG) Log.e(TAG, "Fetch Succeeded");
                            mFirebaseRemoteConfig.activateFetched();

                        } else {
                            if (LOG_DEBUG) Log.e(TAG, "Fetch Failed");
                        }
                        displayWelcomeMessage();
                    }
                });
    }

    private void displayWelcomeMessage() {
        String result = mFirebaseRemoteConfig.getString(VERSION_CODE);
        if (LOG_DEBUG) Log.e(TAG, " remote value  : VERSION_CODE  : " + result);

        if (result.contentEquals(INSTALLED_VERSION_NAME)) {
            if (LOG_DEBUG) Log.e(TAG, " NO UPDATE REQUIRED ");
        } else {
            if (LOG_DEBUG) Log.e(TAG, " UPDATE REQUIRED ---");
        }

    }

    @Override
    public void onBackPressed() {
        if (LOG_DEBUG) Log.e(TAG, "onBackPressed()");

        if (isFabShowing == false) {
            hideBottomLinear(true);
        }


        if (shouldExit == true) {
            if (LOG_DEBUG)
                Log.e(TAG, " Yeah exit douche:" + shouldExit + " isLastEntry : Testing :" + isLastEntry);

            fabBottom.setVisibility(View.VISIBLE);
            if (isBottomShowing == false) {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.am_drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
            isBottomShowing = false;
            isFabShowing = true;


        } else {
            if (LOG_DEBUG)
                Log.e(TAG, " Nope : no exit noob " + shouldExit + " isLastEntry : Testing :" + isLastEntry);
            getFragmentManager().popBackStack();
        }

    }
}
