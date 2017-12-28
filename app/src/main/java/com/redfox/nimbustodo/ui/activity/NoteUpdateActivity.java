package com.redfox.nimbustodo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.data.preferences.common_pref.SPCommonMgr;
import com.redfox.nimbustodo.ui.fragments.FragOne;
import com.redfox.nimbustodo.ui.interfaces.DateTimeCallback;
import com.redfox.nimbustodo.ui.interfaces.TagImageCallBacks;
import com.redfox.nimbustodo.util.alarm_util.UtilAlarmManager;
import com.redfox.nimbustodo.util.common_util.UtilCal;
import com.redfox.nimbustodo.util.common_util.UtilCommonConstants;
import com.redfox.nimbustodo.util.common_util.UtilDBOperation;
import com.redfox.nimbustodo.util.common_util.UtilDateTimePicker;
import com.redfox.nimbustodo.util.common_util.UtilDialog;
import com.redfox.nimbustodo.util.common_util.UtilExtra;
import com.redfox.nimbustodo.util.common_util.UtilLogger;
import com.redfox.nimbustodo.util.common_util.UtilTagImage;
import com.uniquestudio.library.CircleCheckBox;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;


public class NoteUpdateActivity extends AppCompatActivity implements TagImageCallBacks, View.OnClickListener
        , UtilDialog.AlertDialogListener, DateTimeCallback {

    private final static String TAG = NoteUpdateActivity.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.au_fabSheet)
    FloatingActionButton fab;
    @BindView(R.id.au_etxTitle)
    EditText etxTitle;
    @BindView(R.id.au_editIcon)
    ImageView imvSaveTitle;
    @BindView(R.id.au_imv_alarm)
    ImageView imvAlarm;
    @BindView(R.id.au_tv_alarm)
    TextView tvAlarm;
    @BindView(R.id.au_imv_alarm_cancel)
    ImageView imvAlarmCancel;
    @BindView(R.id.au_imvTag)
    ImageView imvTag;
    @BindView(R.id.au_imv_extraNote)
    ImageView imvExtra;
    @BindView(R.id.au_etx_extra_note_add)
    EditText etxExtra;
    @BindView(R.id.au_linear2)
    LinearLayout auLinear2;
    @BindView(R.id.au_tv_updateDate)
    TextView tvUpdateDate;
    @BindView(R.id.au_bottom_separator)
    TextView tvSeparator;
    @BindView(R.id.au_tv_createDate)
    TextView tvCreateDate;
    @BindView(R.id.au_btn_archive)
    ImageView btnArchive;
    @BindView(R.id.au_imv_due)
    ImageView imvDueDate;
    @BindView(R.id.au_tv_dueDate)
    TextView tvDueDate;
    @BindView(R.id.au_relative_taskDone)
    RelativeLayout relativeTaskDone;
    @BindView(R.id.au_cBox_taskdone)
    CircleCheckBox cBoxTaskdone;
    @BindView(R.id.au_tv_taskProgress)
    TextView tvTaskProgress;

    private TagImageCallBacks tagImageCallBacks;
    private Dialog dialogTag;

    private Toolbar toolbar;

    private boolean isEditOn = true;
    private boolean isEditOnExtra = true;
    private Handler handlerToolTips;

    private SPCommonMgr spCommonMgr;
    private Calendar mCalendar;
    private String alarmTimSet;
    private boolean alarmSchBoolean = false;

    private String title;
    private int recordPosId;
    private int imageUriPath;
    private String subText;
    private long dateCreation;
    private long dateUpdated;
    private long scheduledTimeLong;
    private long scheduledWhenLong;
    private String scheduleTitle;
    private int isAlarmScheduled;
    private int isTaskDone;
    private int isArchived;

    private DBMgr dbMgr;
    private BottomSheetDialog dialogBottom;
    private ImageView bsImvShare;
    private CircleCheckBox bsCboxMark;
    private Button bsDismissBtn;
    private UtilDialog utilDialog;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private SimpleTooltip simpleTooltip;

    private Handler taskHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG_DEBUG) Log.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_update);
        ButterKnife.bind(this);
        setUpSharedPref();
        setUpToolbar();
        processData(getIntent());
        setUpDialog();
        setUpDb();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.nav_header_bg_color));
        }

        if (spCommonMgr.getSavedStatusToolTip() == 0) {
            setUpHandler();
        }
        showScheduledAlarms();
        setUpTaskDoneOperations();
        setUpBottomSheetDialog();


    }


    private void setUpSharedPref() {
        spCommonMgr = new SPCommonMgr(NoteUpdateActivity.this);
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.fav_note_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //Having crash....now fine
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));

            } else {
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_check));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (LOG_DEBUG) Log.v(TAG, " onNetIntent()---------------");
        setIntent(intent);
        processData(intent);
    }

    private void processData(Intent intent) {
        if (LOG_DEBUG) Log.v(TAG, " inside processData() ");

        if (intent != null && intent.getAction() != null) {

            if (intent.getAction().equalsIgnoreCase(UtilCommonConstants.FO_TO_NU_ACTION)) {
                if (LOG_DEBUG) Log.v(TAG, " intent action FO_TO_NU_ACTION");

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    NoteModel noteModel = bundle.getParcelable(UtilCommonConstants.FRAGONE_TO_NU_PARCEL_KEY);

                    if (noteModel != null) {

                        if (LOG_DEBUG) Log.w(TAG, " rcv NoteModel has : " + noteModel.toString());
                        commonBundleTask(bundle, noteModel, false);
                    }
                }
            } else if (intent.getAction().equalsIgnoreCase(UtilCommonConstants.DB_TO_NU_ACTION)) {
                if (LOG_DEBUG) Log.v(TAG, " panel action coming from Dummy Broadcast");

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    NoteModel noteModel = bundle.getParcelable(UtilCommonConstants.DB_TO_NU_PARCEL_KEY);
                    if (noteModel != null) {
                        if (LOG_DEBUG) Log.v(TAG, " rcv NoteModel has : " + noteModel.toString());
                        commonBundleTask(bundle, noteModel, true);
                    }
                }
            }
        }
    }

    private void commonBundleTask(Bundle bundle, NoteModel noteModel, boolean isFromNotification) {
        if (LOG_DEBUG) Log.v(TAG, " inside commonBundleTask()  ");


        recordPosId = noteModel.get_id();
        title = noteModel.getTitle();
        etxTitle.setText(title);
        imageUriPath = noteModel.getImgUriPath();
        imageTagOperation(imageUriPath);
        subText = noteModel.getSub_text();
        if (subText.equalsIgnoreCase("")) {
            etxExtra.setHint(getString(R.string.add_extra_hint));
        } else {
            etxExtra.setText(subText);

        }
        dateCreation = noteModel.getCreateDate();
        tvCreateDate.setText("created on : " + UtilCal.formatDate(dateCreation));
        dateUpdated = noteModel.getUpdateDate();
        tvUpdateDate.setText("last visited : " + UtilCal.formatDate(dateUpdated));
        scheduledTimeLong = noteModel.getScheduleTimeLong();
        scheduledWhenLong = noteModel.getScheduledWhenLong();
        scheduleTitle = noteModel.getScheduledTitle();
        isAlarmScheduled = noteModel.getIsAlarmScheduled();
        isTaskDone = noteModel.getIsTaskDone();
        isArchived = noteModel.getIsArchived();

        if (LOG_DEBUG)
            UtilLogger.logWhatWeHave(TAG, recordPosId, title, imageUriPath, subText, UtilCal.formatDate(dateCreation),
                    UtilCal.formatDate(dateUpdated), scheduledTimeLong, scheduledWhenLong, scheduleTitle, isAlarmScheduled, isTaskDone, isArchived);

        if (isFromNotification == false) {
            String imvTransitionName = bundle.getString(FragOne.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imvTag.setTransitionName(imvTransitionName);
            }
        }
    }

    private void imageTagOperation(int drawableTag) {
        Glide.with(getApplicationContext()).load(drawableTag).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                supportStartPostponedEnterTransition();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                supportStartPostponedEnterTransition();
                return false;
            }
        }).into(imvTag);
    }

    private void setUpDialog() {
        utilDialog = new UtilDialog(this, NoteUpdateActivity.this);
    }

    private void setUpDb() {
        dbMgr = new DBMgr(NoteUpdateActivity.this);
        dbMgr.openDataBase();
    }

    private void setUpHandler() {
        handlerToolTips = new Handler();
        handlerToolTips.postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpToolTip();
            }
        }, 1500);
        spCommonMgr.saveStatusToolTip(1);
    }

    private void setUpToolTip() {
        SimpleTooltip.Builder builder = new SimpleTooltip.Builder(this);
        builder.anchorView(imvSaveTitle)
                .text(" toggle edit or save")
                .backgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .arrowColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .textColor(ContextCompat.getColor(this, R.color.color_white))
                .gravity(Gravity.START)
                .animated(false)
                .dismissOnOutsideTouch(true)
                .dismissOnInsideTouch(true)
                .transparentOverlay(false)
                .dismissOnOutsideTouch(true);
        simpleTooltip = builder.build();
        if (simpleTooltip != null)
            simpleTooltip.show();
    }

    private void setUpTaskDoneOperations() {
        cBoxTaskdone.setClickable(false);
        if (isTaskDone == 1) {
            relativeTaskDone.setVisibility(View.VISIBLE);
            taskHandler = new Handler();
            taskHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cBoxTaskdone.setChecked(true);

                }
            }, 1500);
            tvTaskProgress.setText("Task Completed... ");
            etxTitle.setPaintFlags(etxTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            relativeTaskDone.setVisibility(View.GONE);
            etxTitle.setPaintFlags(etxTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private void showScheduledAlarms() {
        if (LOG_DEBUG) Log.v(TAG, " showScheduledAlarms()");
        if (isAlarmScheduled == 1) {
            if (LOG_DEBUG)
                Log.v(TAG, " status isAlarmScheduled " + isAlarmScheduled + " fireAt " + scheduledTimeLong);

            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(scheduledTimeLong);

            int selectedHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int selectedMinute = mCalendar.get(Calendar.MINUTE);
            String AM_PM = UtilCal.getAMPM(selectedHour);


            if (selectedHour > 12) {
                selectedHour = selectedHour - 12;
            } else {
                //intentional
                selectedHour = selectedHour;
            }
            String hour = UtilCal.getDoubleDigits(selectedHour);
            String minute = UtilCal.getDoubleDigits(selectedMinute);

            String alarmTimSet = hour + ":" + minute + " " + AM_PM;

            if (LOG_DEBUG) Log.v(TAG, " showScheduled Alarms : alarmTimeSet : " + alarmTimSet);
            tvAlarm.setText(" notify @ " + alarmTimSet);
            tvAlarm.setTextColor(ContextCompat.getColor(this, R.color.color_amber));
            imvAlarmCancel.setVisibility(View.VISIBLE);
            imvAlarm.setImageResource(R.drawable.ic_alarm_on);
            tvDueDate.setVisibility(View.VISIBLE);
            imvDueDate.setVisibility(View.VISIBLE);
            tvDueDate.setText(UtilCal.formatDatePattern2(scheduledTimeLong));

        } else {
            imvDueDate.setVisibility(View.GONE);
            tvDueDate.setVisibility(View.GONE);
        }

    }

    public void setUpBottomSheetDialog() {
        View viewBottom = getLayoutInflater().inflate(R.layout.bottomsheet, null);

        dialogBottom = new BottomSheetDialog(this);
        dialogBottom.setContentView(viewBottom);
        dialogBottom.setCanceledOnTouchOutside(false);
        dialogBottom.setCancelable(true);


        bsImvShare = (ImageView) viewBottom.findViewById(R.id.au_bs_imv_share);
        bsCboxMark = (CircleCheckBox) viewBottom.findViewById(R.id.au_bs_cBox_mark);
        bsDismissBtn = (Button) viewBottom.findViewById(R.id.au_bs_btn_cancel);
        bsImvShare.setOnClickListener(this);
        bsDismissBtn.setOnClickListener(this);

        bsCboxMark.setListener(new CircleCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                if (isChecked) {
                    isTaskDone = 1;
                    relativeTaskDone.setVisibility(View.VISIBLE);
                    tvTaskProgress.setText("Task Completed... ");
                    etxTitle.setPaintFlags(etxTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    isTaskDone = 0;
                    relativeTaskDone.setVisibility(View.GONE);
                    etxTitle.setPaintFlags(etxTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

            }
        });
    }

    @OnClick(R.id.au_editIcon)
    public void onEditTitleIconClicked() {
        if (LOG_DEBUG) Log.v(TAG, "onEditIcon clicked : Note Title");
        if (isEditOn == true) {
            if (LOG_DEBUG) Log.v(TAG, " isEditOn : " + isEditOn);

            isEditOn = false;
            imvSaveTitle.setImageResource(R.drawable.ic_save_note);
            imvSaveTitle.animate().rotation(0).start();
            etxTitle.setEnabled(true);
            etxTitle.setSelection(etxTitle.getText().toString().trim().length());
            etxTitle.requestFocus();
            UtilExtra.showKeyboard(NoteUpdateActivity.this);

        } else if (isEditOn == false) {
            if (LOG_DEBUG) Log.v(TAG, " isEditOn : " + isEditOn);

            isEditOn = true;
            imvSaveTitle.setImageResource(R.drawable.ic_edit_note);
            imvSaveTitle.animate().rotation(360).start();
            etxTitle.setText(etxTitle.getText().toString().trim());
            etxTitle.setEnabled(false);
            etxTitle.clearFocus();
        }

    }

    @OnClick(R.id.au_imv_alarm)
    public void onAlarmImvClicked() {
        Log.v(TAG, "onAlarmIcon clicked");

        alarmSchBoolean = true;
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake_booty);
        imvAlarm.startAnimation(animation);

        datePicker();

    }


    private void datePicker() {
        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        //now pick time
        UtilDateTimePicker.datePicker(this, mCalendar, mYear, mMonth, mDay, this);
        //interface : result
    }

    @Override
    public void dateCallBacks(int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);

        //request TimePicker
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        UtilDateTimePicker.timePicker(this, mHour, mMinute, this);


    }

    @Override
    public void timeCallBacks(int selectedHour, int selectedMinute) {

        mCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        mCalendar.set(Calendar.MINUTE, selectedMinute);

        Calendar currentTime = Calendar.getInstance();

        if (mCalendar.getTimeInMillis() >= currentTime.getTimeInMillis()) {
            String AM_PM = UtilCal.getAMPM(selectedHour);

            String hour = UtilCal.getDoubleDigits(UtilCal.format12Hour(selectedHour));
            String minute = UtilCal.getDoubleDigits(selectedMinute);

            alarmTimSet = hour + ":" + minute + " " + AM_PM;

            if (LOG_DEBUG) Log.v(TAG, " alarmTimeSet : " + alarmTimSet);
            scheduledTimeLong = mCalendar.getTimeInMillis();
            isAlarmScheduled = 1;
            alarmSchBoolean = true;
            if (LOG_DEBUG) Log.v(TAG, " alarmTimeSet : long time " + scheduledTimeLong);
            tvAlarm.setText(" notify @ " + alarmTimSet);
            tvAlarm.setTextColor(ContextCompat.getColor(NoteUpdateActivity.this, R.color.color_amber));
            imvAlarmCancel.setVisibility(View.VISIBLE);
            imvAlarm.setImageResource(R.drawable.ic_alarm_on);
            tvDueDate.setVisibility(View.VISIBLE);
            imvDueDate.setVisibility(View.VISIBLE);
            tvDueDate.setText(UtilCal.formatDatePattern2(scheduledTimeLong));

        } else {
            //wrong time, ask again for valid.
            UtilDateTimePicker.timePicker(this, mHour, mMinute, this);
        }
    }

    @Override
    public void cancelCallBacks(boolean isDialogCancelled) {
        alarmSchBoolean = false;
        isAlarmScheduled = 0;

    }

    @OnClick(R.id.au_imv_alarm_cancel)
    public void onImbAlarmCancelClicked() {

        if (isAlarmScheduled == 1) {
            scheduledTimeLong = 0;
            isAlarmScheduled = 0;
            alarmSchBoolean = false;
            if (LOG_DEBUG) Log.v(TAG, " isAlarmScheduled = 1 : " + isAlarmScheduled);
            UtilAlarmManager.cancelAlarm(NoteUpdateActivity.this, recordPosId);
        } else {
            if (LOG_DEBUG) Log.v(TAG, " isAlarmScheduled = 0 : " + isAlarmScheduled);
        }
        scheduledTimeLong = 0;
        alarmSchBoolean = false;
        tvAlarm.setText(getString(R.string.add_reminder));
        tvDueDate.setVisibility(View.GONE);
        imvDueDate.setVisibility(View.GONE);
        if (mCalendar != null && mCalendar.getTime() != null) {
            mCalendar = null;

        }
        tvAlarm.setTextColor(ContextCompat.getColor(NoteUpdateActivity.this, R.color.alarm_text_default));
        imvAlarmCancel.setVisibility(View.GONE);
        imvAlarm.setImageResource(R.drawable.ic_alarm_default);
        if (LOG_DEBUG)
            Log.v(TAG, "--------------------" + alarmSchBoolean + " , " + isAlarmScheduled + " ," + scheduledTimeLong);

    }

    @OnClick(R.id.au_imv_extraNote)
    public void onImvExtraClicked() {
        if (LOG_DEBUG) Log.v(TAG, "onIconExtraNote CLicked");

        if (isEditOnExtra == true) {
            if (LOG_DEBUG) Log.v(TAG, " isEditOnExtra : " + isEditOnExtra);
            isEditOnExtra = false;
            imvExtra.setImageResource(R.drawable.ic_save_note);
            imvExtra.animate().rotation(0).start();
            etxExtra.setEnabled(true);
            etxExtra.setSelection(etxExtra.getText().toString().trim().length());
            etxExtra.requestFocus();
            UtilExtra.showKeyboard(NoteUpdateActivity.this);

        } else if (isEditOnExtra == false) {
            if (LOG_DEBUG) Log.v(TAG, " isEditOnExtra : " + isEditOnExtra);
            isEditOnExtra = true;
            imvExtra.setImageResource(R.drawable.ic_docs);
            imvExtra.animate().rotation(360).start();
            etxExtra.setText(etxExtra.getText().toString().trim());
            etxExtra.setEnabled(false);
            etxExtra.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onSaveTapped();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveTapped() {
        String noteTitle = etxTitle.getText().toString().trim();
        String extraNote = etxExtra.getText().toString().trim();

        if (alarmSchBoolean == true && isAlarmScheduled == 1) {
            updateEntry(noteTitle, extraNote);

            UtilAlarmManager.scheduleAlarm(
                    this, noteTitle, scheduledTimeLong, System.currentTimeMillis(), recordPosId);
            finish();
        } else {
            updateEntry(noteTitle, extraNote);
            finish();
        }
    }

    private void updateEntry(String noteTitle, String extraNote) {
        int emptyCheck = UtilDBOperation.updateEntry(dbMgr, isAlarmScheduled, scheduledTimeLong, recordPosId,
                noteTitle, imageUriPath, extraNote, dateCreation, isTaskDone, isArchived, alarmTimSet);
        if (emptyCheck == 0) {
            etxTitle.setError("Can't left Empty..");
        }
    }

    @OnClick(R.id.au_imvTag)
    public void onImvTagClicked() {
        pickTagImage(NoteUpdateActivity.this);


    }

    private void pickTagImage(TagImageCallBacks tagImageCallBacks) {
        this.tagImageCallBacks = tagImageCallBacks;

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
                UtilTagImage.imagePicker(position, NoteUpdateActivity.this);

            }
        });

        dialogTag.show();

    }


    @Override
    public void imageListener(int drawable) {
        imageUriPath = drawable;
        dismissDialogTag();
        imvTag.setImageResource(imageUriPath);

    }

    private void dismissDialogTag() {
        if (dialogTag != null) {
            dialogTag.dismiss();
        }
    }


    @OnClick(R.id.au_btn_archive)
    public void onImvArchived() {

        utilDialog.showAlertDialog(UtilCommonConstants.ARCHIVE, "",
                "Do you want to archive selected note or notes ?", "ARCHIVE", "", "CANCEL", false);


    }

    @OnClick(R.id.au_fabSheet)
    public void onFabClicked() {
        if (dialogBottom != null) {
            dialogBottom.show();
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.au_bs_imv_share:
                UtilExtra.shareDataOnClick(NoteUpdateActivity.this, title, etxExtra.getText().toString().trim());
                break;
            case R.id.au_bs_btn_cancel:
                if (dialogBottom != null) {
                    dialogBottom.dismiss();
                }
                break;
            default:
        }

    }

    @Override
    public void onPosBtnClicked(String whichOperation) {
        if (whichOperation.contentEquals(UtilCommonConstants.ARCHIVE)) {
            NoteModel noteModel = new NoteModel();

            noteModel.set_id(recordPosId);
            noteModel.setTitle(etxTitle.getText().toString().trim());
            noteModel.setImgUriPath(imageUriPath);
            noteModel.setSub_text(etxExtra.getText().toString().trim());
            noteModel.setCreateDate(dateCreation);
            noteModel.setUpdateDate(dateUpdated); //for first time , current time is last visited time
            noteModel.setScheduleTimeLong(scheduledTimeLong);
            noteModel.setScheduledWhenLong(scheduledWhenLong);
            noteModel.setScheduledTitle(scheduleTitle);
            noteModel.setIsAlarmScheduled(isAlarmScheduled);
            noteModel.setIsTaskDone(isTaskDone);
            if (isArchived == 0) {
                noteModel.setIsArchived(1);
            } else {
                noteModel.setIsArchived(isArchived);
            }

            DBMgr dbManager = new DBMgr(NoteUpdateActivity.this);
            dbManager.openDataBase();

            long updateStatus = dbManager.updateNote(noteModel);
            if (updateStatus == 1) {
            }
            dbManager.closeDataBase();
            finish();
        }

    }

    @Override
    public void onNegBtnClicked() {
    }

    @Override
    public void onNeutralClick() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LOG_DEBUG) Log.v(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LOG_DEBUG) Log.v(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LOG_DEBUG) Log.v(TAG, "onStop()");

        if (simpleTooltip != null) {
            simpleTooltip.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (LOG_DEBUG) Log.v(TAG, "onDestroy()");
        super.onDestroy();
        if (handlerToolTips != null) {
            handlerToolTips.removeCallbacks(null);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
