package com.redfox.nimbustodo.ui.fragments;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.data.preferences.common_pref.SPCommonMgr;
import com.redfox.nimbustodo.ui.activity.NoteUpdateActivity;
import com.redfox.nimbustodo.ui.adapters.NoteAdapter;
import com.redfox.nimbustodo.ui.interfaces.AdapterCallBack;
import com.redfox.nimbustodo.util.common_util.UtilCommonConstants;
import com.redfox.nimbustodo.util.common_util.UtilDBOperation;
import com.redfox.nimbustodo.util.common_util.UtilDialog;
import com.redfox.nimbustodo.util.common_util.UtilExtra;


public class FragOne extends Fragment implements AdapterCallBack, UtilDialog.AlertDialogListener {

    private final static String TAG = FragOne.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;
    public static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "image_transition_name";
    private final static String INSTALLED_VERSION_CODE = "3";


    @BindView(R.id.fragOne_recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.intro_card_imv)
    ImageView iC_imv;
    @BindView(R.id.intro_card_title_tv)
    TextView ic_TvTitle;
    @BindView(R.id.intro_card_sub_text_tv)
    TextView iC_TvSubTitle;
    @BindView(R.id.intro_card_dismiss_btn)
    Button iC_BtnDismiss;
    @BindView(R.id.intro_card_root)
    LinearLayout iC_rootLinear;
    @BindView(R.id.fragOne_root)
    LinearLayout fragOneRoot;

    private NoteAdapter noteAdapter;
    private List<NoteModel> noteModelList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private View v;

    private ActionMode mActionMode;
    private List<NoteModel> multiSelectionList = new ArrayList<>();
    private boolean isMultiSelect = false;

    private boolean mDataLoaded = false;
    private UtilDialog utilDialog;

    private SPCommonMgr commonMgr;
    private int mCardVisibility;
    private Handler handlerIntroCard;


    public static FragOne getInstance() {
        return new FragOne();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (LOG_DEBUG) Log.d(TAG, "onCreateView()");
        v = inflater.inflate(R.layout.frag_one, container, false);
        unbinder = ButterKnife.bind(this, v);
        setUpPref();
        setUpDialog();
        setUpRecycler();
        mDataLoaded = true;
        if (mDataLoaded == true) {
            loadData();
        }
        return v;
    }

    private void setUpPref() {
        commonMgr = new SPCommonMgr(v.getContext());
        mCardVisibility = commonMgr.getIntroCardVisibility();
        if (mCardVisibility == 0) {
            setUpHandler();
        } else {
            fragOneRoot.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.frag_one_bg_color));
            hideIntroCard(false);
        }
    }

    private void setUpHandler() {
        handlerIntroCard = new Handler();
        handlerIntroCard.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragOneRoot.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.frag_one_overlay_color));
                showIntroCard();
            }
        }, 1200);
        if (commonMgr != null) {
            commonMgr.saveIntroCardVisibility(1);
        }

    }

    private void setUpDialog() {
        utilDialog = new UtilDialog(v.getContext(), FragOne.this);
    }

    private void setUpRecycler() {
        noteAdapter = new NoteAdapter(noteModelList, v.getContext(), FragOne.this, multiSelectionList);
        layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(v.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(noteAdapter);
    }


    @Override
    public void onRowClick(int position, View view, ImageView sharedImv) {
        if (LOG_DEBUG) Log.d(TAG, " ROW clicked at : " + position);

        if (isMultiSelect) {
            multi_select(position);
        } else {
            NoteModel noteModel = noteModelList.get(position);

            Intent intent = new Intent(v.getContext(), NoteUpdateActivity.class);
            Bundle bundle = new Bundle();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                intent.setAction(UtilCommonConstants.FO_TO_NU_ACTION);
                bundle.putParcelable(UtilCommonConstants.FRAGONE_TO_NU_PARCEL_KEY, noteModel);
                bundle.putString(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImv));
                intent.putExtras(bundle);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation
                        (getActivity(), sharedImv, ViewCompat.getTransitionName(sharedImv));
                startActivity(intent, activityOptions.toBundle());

            } else {

                intent.setAction(UtilCommonConstants.FO_TO_NU_ACTION);
                bundle.putParcelable(UtilCommonConstants.FRAGONE_TO_NU_PARCEL_KEY, noteModel);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onLongRowClick(int position, View view) {
        if (LOG_DEBUG) Log.d(TAG, " LONG : clicked at : " + position);

        if (!isMultiSelect) {
            isMultiSelect = true;
            multiSelectionList = new ArrayList<>();

            if (LOG_DEBUG) Log.e(TAG, "Multi " + multiSelectionList.toString());
            if (LOG_DEBUG) Log.e(TAG, "UserList " + noteModelList.toString());


            if (mActionMode == null) {
                mActionMode = getActivity().startActionMode(mActionModeCallback);
            }
        }

        multi_select(position);

    }


    private void multi_select(int position) {
        if (mActionMode != null) {


            if (multiSelectionList.contains(noteModelList.get(position))) {
                if (LOG_DEBUG) Log.w(TAG, " Lost Virginity , 2nd Tap, you will be removed");

                multiSelectionList.remove(noteModelList.get(position));

            } else {
                if (LOG_DEBUG) Log.e(TAG, " Virgin , 1st Tap to Row, ADD TO SELECTION LIST");
                multiSelectionList.add(noteModelList.get(position));

            }

            if (multiSelectionList.size() > 0) {
                if (LOG_DEBUG) Log.i(TAG, " SIZE > 0");
                mActionMode.setTitle("" + multiSelectionList.size());

            } else {

                if (LOG_DEBUG) Log.v(TAG, " NOTHING SELECTED");
                mActionMode.setTitle("" + 0);
                mActionMode.finish();
            }
            refreshAdapter();

        } else {
            //mActionModeNull

        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_mode_menu_frag_one, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_mode_delete:
                    utilDialog.showAlertDialog(UtilCommonConstants.DELETE, "",
                            getString(R.string.DELETE_DIALOG_MSG), "DELETE", "", "CANCEL", false);
                    return true;
                case R.id.action_mode_archive:
                    utilDialog.showAlertDialog(UtilCommonConstants.ARCHIVE, "",
                            getString(R.string.ARCHIEVE_DIALOG_MSG), "ARCHIVE", "", "CANCEL", false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiSelectionList = new ArrayList<>();
            refreshAdapter();
        }
    };

    //vital,
    private void refreshAdapter() {
        noteAdapter.multiSelectionList = multiSelectionList;
        noteAdapter.noteModelList = noteModelList;
        noteAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (LOG_DEBUG) Log.d(TAG, "onResume()       ---");

        if (mDataLoaded == false) {
            if (LOG_DEBUG) Log.d(TAG, "onResume()   : false,  LOAD again    ---");
            loadData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (LOG_DEBUG) Log.d(TAG, "onPause()");

    }

    @Override
    public void onStop() {
        super.onStop();
        if (LOG_DEBUG) Log.d(TAG, "onStop()");
        mDataLoaded = false;
        if (handlerIntroCard != null) {
            handlerIntroCard.removeCallbacks(null);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        noteModelList.clear();
        if (LOG_DEBUG) Log.d(TAG, "onDestroyView()");
    }


    private void loadData() {
        if (LOG_DEBUG) Log.d(TAG, " inside loadData()..");
        noteModelList.clear();
        DBMgr dbManager = new DBMgr(v.getContext());
        dbManager.openDataBase();

        Cursor cursor = dbManager.getCursorForArchived("0");
        noteModelList = UtilDBOperation.extractCommonData(cursor, noteModelList);
        dbManager.closeDataBase();


        if (LOG_DEBUG) {
            Log.d(TAG, "Loaded data (entry only with isArchived 0) to NoteModel Size : " + noteModelList.size());
            Log.d(TAG, "Loaded data (entry only with isArchived 0) to NoteModel List : " + noteModelList.toString());
        }

        if (noteModelList.size() == 0) {
            if (LOG_DEBUG) Log.d(TAG, " OOps, list is Empty..");
        } else {
            noteAdapter = new NoteAdapter(noteModelList, v.getContext(), FragOne.this, multiSelectionList);
            recyclerView.setAdapter(noteAdapter);
            noteAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onPosBtnClicked(String whichOperation) {
        if (multiSelectionList.size() > 0) {

            DBMgr dbManager = new DBMgr(v.getContext());
            dbManager.openDataBase();
            NoteModel noteModel;

            if (whichOperation.contentEquals(UtilCommonConstants.ARCHIVE)) {
                if (LOG_DEBUG) {
                    Log.v(TAG, " noteModelList : list has :" + noteModelList.size());
                    Log.v(TAG, "multi list before clear :" + multiSelectionList.size());
                }

                for (int i = 0; i < multiSelectionList.size(); i++) {
                    noteModel = multiSelectionList.get(i);
                    System.out.println("noteModel has at pos before : " + i + " " + noteModel.toString());
                    int isArchived = noteModel.getIsArchived();
                    if (isArchived == 0) {
                        noteModel.setIsArchived(1);
                    } else {
                        noteModel.setIsArchived(isArchived);
                    }
                    if (LOG_DEBUG)
                        Log.v(TAG, "noteModel has at pos after  : " + i + " " + noteModel.toString());

                    long status = dbManager.updateNote(noteModel);
                    if (LOG_DEBUG) {
                        Log.w(TAG, "update status : " + status);
                        Log.v(TAG, "removing item from list : " + multiSelectionList.get(i));
                    }
                    noteModelList.remove(multiSelectionList.get(i));
                }

                dbManager.closeDataBase();
                multiSelectionList.clear();
                if (LOG_DEBUG) {
                    Log.v(TAG, "multi list after clear :" + multiSelectionList.size());
                    Log.v(TAG, "noteModelList list after clear :" + noteModelList.size());
                }

                noteAdapter.notifyDataSetChanged();

            } else if (whichOperation.contentEquals(UtilCommonConstants.DELETE)) {

                if (LOG_DEBUG) {
                    Log.v(TAG, " noteModelList : list has :" + noteModelList.size());
                    Log.v(TAG, "multi list before clear :" + multiSelectionList.size());
                }

                for (int i = 0; i < multiSelectionList.size(); i++) {
                    int _id = multiSelectionList.get(i).get_id();
                    int status = dbManager.deleteNote(_id);
                    if (LOG_DEBUG) Log.w(TAG, "delete status : " + status);
                    if (LOG_DEBUG)
                        Log.v(TAG, "removing item from list : " + multiSelectionList.get(i));
                    noteModelList.remove(multiSelectionList.get(i));

                }

                multiSelectionList.clear();
                if (LOG_DEBUG) {
                    Log.v(TAG, "multi list after clear :" + multiSelectionList.size());
                    Log.v(TAG, "noteModelList list after clear :" + noteModelList.size());
                }
                noteAdapter.notifyDataSetChanged();
            }
        }
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void onNegBtnClicked() {
    }

    @Override
    public void onNeutralClick() {
        if (mActionMode != null) {
            mActionMode.finish();
        }

    }

    @OnClick(R.id.intro_card_dismiss_btn)
    public void onViewClickedIntro() {
        //slide Right
        hideIntroCard(true);
        fragOneRoot.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.frag_one_bg_color));

    }


    private void showIntroCard() {
        Animation slideLeft = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_left_complete);
        iC_rootLinear.setAnimation(slideLeft);
        iC_rootLinear.setVisibility(View.VISIBLE);

    }

    private void hideIntroCard(boolean withAnimation) {
        if (withAnimation == true) {
            Animation slideRight = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_right_complete);
            iC_rootLinear.setAnimation(slideRight);
            iC_rootLinear.setVisibility(View.GONE);
        } else {
            iC_rootLinear.setVisibility(View.GONE);

        }

    }



}
