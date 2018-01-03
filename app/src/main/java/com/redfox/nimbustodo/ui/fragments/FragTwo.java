package com.redfox.nimbustodo.ui.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.data.db.DBHelperSingleton;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.data.preferences.common_pref.SPCommonMgr;
import com.redfox.nimbustodo.ui.adapters.NoteAdapter;
import com.redfox.nimbustodo.ui.interfaces.AdapterCallBack;
import com.redfox.nimbustodo.util.common_util.UtilCommonConstants;
import com.redfox.nimbustodo.util.common_util.UtilDBOperation;
import com.redfox.nimbustodo.util.common_util.UtilDialog;
import com.redfox.nimbustodo.util.common_util.UtilExtra;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class FragTwo extends Fragment implements AdapterCallBack, UtilDialog.AlertDialogListener {

    private final static String TAG = FragTwo.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;

    @BindView(R.id.intro_card2_imv)
    ImageView iC_2Imv;
    @BindView(R.id.intro_card2_title_tv)
    TextView iC_2TitleTv;
    @BindView(R.id.intro_card2_sub_text_tv)
    TextView iC_2SubTextTv;
    @BindView(R.id.intro_card2_dismiss_btn)
    Button iC_2DismissBtn;
    @BindView(R.id.intro_card2_root)
    LinearLayout iC_2rootLinear;
    @BindView(R.id.fragTwo_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fragTwo_root)
    LinearLayout fragTwoRoot;
    Unbinder unbinder;
    @BindView(R.id.fragTwo_archive_imv_info)
    ImageView archiveImvInfo;

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

    private AlertDialog alertDialog;
    private TextView titleTv;
    private TextView subTv;

    public static FragTwo getInstance() {
        return new FragTwo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_two, container, false);
        unbinder = ButterKnife.bind(this, v);
        setUpPref();
        setUpDialog();
        setUpRecycler();
        mDataLoaded = true;
        if (mDataLoaded == true) {
            loadData();
        }
        setUpArchiveDialog();
        return v;
    }

    private void setUpPref() {
        commonMgr = new SPCommonMgr(v.getContext());
        mCardVisibility = commonMgr.getIntroCardVisibility2();
        if (mCardVisibility == 0) {
            setUpHandler();
        } else {
            fragTwoRoot.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.frag_one_bg_color));
            hideIntroCard(false);
        }
    }

    private void setUpHandler() {
        handlerIntroCard = new Handler();
        handlerIntroCard.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragTwoRoot.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.frag_one_overlay_color));
                showIntroCard();
            }
        }, 500);
        if (commonMgr != null) {
            commonMgr.saveIntroCardVisibility2(1);
        }

    }

    private void setUpDialog() {
        utilDialog = new UtilDialog(v.getContext(), FragTwo.this);
    }

    private void setUpRecycler() {
        noteAdapter = new NoteAdapter(noteModelList, v.getContext(), FragTwo.this, multiSelectionList);
        layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(v.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    public void onRowClick(int position, View view, ImageView sharedImv) {
        if (LOG_DEBUG) Log.d(TAG, " ROW clicked at : " + position);
        titleTv.setText(noteModelList.get(position).getTitle());
        subTv.setText(noteModelList.get(position).getSub_text());

        if (isMultiSelect) {
            multi_select(position);
        } else {
            if (alertDialog != null) {
                alertDialog.show();
            }
        }
    }

    @Override
    public void onLongRowClick(int position, View view) {
        if (LOG_DEBUG) Log.d(TAG, " LONG : clicked at : " + position);

        if (!isMultiSelect) {
            isMultiSelect = true;
            multiSelectionList = new ArrayList<>();

            if (mActionMode == null) {
                mActionMode = getActivity().startActionMode(mActionModeCallback);
            }
        }
        multi_select(position);
    }

    private void multi_select(int position) {
        if (mActionMode != null) {

            if (multiSelectionList.contains(noteModelList.get(position))) {
                multiSelectionList.remove(noteModelList.get(position));

            } else {
                multiSelectionList.add(noteModelList.get(position));
            }
            if (multiSelectionList.size() > 0) {
                mActionMode.setTitle("" + multiSelectionList.size());

            } else {
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
            inflater.inflate(R.menu.action_mode_menu_frag_two, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.am_fragTwo_restore:
                    utilDialog.showAlertDialog(UtilCommonConstants.RESTORE, "",
                            getString(R.string.RESTORE_DIALOG_MSG), "RESTORE", "", "CANCEL", false);
                    return true;
                case R.id.am_fragTwo_delete:
                    utilDialog.showAlertDialog(UtilCommonConstants.DELETE, "",
                            getString(R.string.DELETE_DIALOG_MSG), "DELETE", "", "CANCEL", false);
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
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private void loadData() {
        if (LOG_DEBUG) Log.d(TAG, " inside loadData()..");
        noteModelList.clear();

        Cursor cursor = DBHelperSingleton.getDbInstance(v.getContext()).getCursorForArchived("1");
        noteModelList = UtilDBOperation.extractCommonData(cursor, noteModelList);

        if (LOG_DEBUG) {
            Log.d(TAG, "Loaded data (entry only with isArchived 1) to NoteModel Size : " + noteModelList.size());
            Log.d(TAG, "Loaded data (entry only with isArchived 1) to NoteModel List : " + noteModelList.toString());
        }

        if (noteModelList.size() == 0) {
            if (LOG_DEBUG) Log.d(TAG, " OOps, list is Empty..");
        } else {
            noteAdapter = new NoteAdapter(noteModelList, v.getContext(), FragTwo.this, multiSelectionList);
            recyclerView.setAdapter(noteAdapter);
            noteAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onPosBtnClicked(String whichOperation) {
        if (multiSelectionList.size() > 0) {
            NoteModel noteModel;

            if (whichOperation.contentEquals(UtilCommonConstants.RESTORE)) {

                for (int i = 0; i < multiSelectionList.size(); i++) {
                    noteModel = multiSelectionList.get(i);
                    int isArchived = noteModel.getIsArchived();
                    if (isArchived == 1) {
                        noteModel.setIsArchived(0);
                    } else {
                        noteModel.setIsArchived(isArchived);
                    }
                    long status = DBHelperSingleton.getDbInstance(v.getContext()).updateNote(noteModel);
                    if (LOG_DEBUG) {
                        Log.w(TAG, "update status : " + status);
                        Log.v(TAG, "removing item from list : " + multiSelectionList.get(i));
                    }
                    noteModelList.remove(multiSelectionList.get(i));
                }

                multiSelectionList.clear();
                noteAdapter.notifyDataSetChanged();

            } else if (whichOperation.contentEquals(UtilCommonConstants.DELETE)) {

                for (int i = 0; i < multiSelectionList.size(); i++) {
                    int _id = multiSelectionList.get(i).get_id();
                    int status = DBHelperSingleton.getDbInstance(v.getContext()).deleteNote(_id);
                    noteModelList.remove(multiSelectionList.get(i));

                }
                multiSelectionList.clear();
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


    private void showIntroCard() {
        Animation slideLeft = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_left_complete);
        iC_2rootLinear.setAnimation(slideLeft);
        iC_2rootLinear.setVisibility(View.VISIBLE);
    }

    private void hideIntroCard(boolean withAnimation) {
        if (withAnimation == true) {
            Animation slideRight = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_right_complete);
            iC_2rootLinear.setAnimation(slideRight);
            handlerIntroCard.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iC_2rootLinear.setVisibility(View.GONE);
                }
            }, 400);
        } else {
            iC_2rootLinear.setVisibility(View.GONE);

        }
    }

    @OnClick(R.id.intro_card2_dismiss_btn)
    public void onViewClicked() {
        hideIntroCard(true);
        fragTwoRoot.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.nav_header_bg_color));
    }

    @OnClick(R.id.fragTwo_archive_imv_info)
    public void onImvInfo() {
        UtilExtra.dialogArchiveInfo(v.getContext());

    }

    private void setUpArchiveDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
        final View customView = layoutInflater.inflate(R.layout.archive_dialog, null);
        builder.setView(customView);

        titleTv = (TextView) customView.findViewById(R.id.archive_dialog_titleTv);
        subTv = (TextView) customView.findViewById(R.id.archive_dialog_subTv);
        Button btnCancel = (Button) customView.findViewById(R.id.archive_dialog_dismissBtn);

        alertDialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
