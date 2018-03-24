package com.redfox.nimbustodo.ui.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.ui.interfaces.AdapterCallBack;
import com.redfox.nimbustodo.util.common_util.UtilCal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.redfox.nimbustodo.util.common_util.UtilExtra.getColoredSpanned;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    private final static String TAG = NoteAdapter.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    private long scheduledTimeLong;
    private int isAlarmScheduled;
    private int isTaskDone;
    private String hasExtraNote;

    public List<NoteModel> noteModelList = new ArrayList<>();
    public List<NoteModel> multiSelectionList = new ArrayList<>();

    private Context mContext;

    private AdapterCallBack mAdapterCallBack;
    private View v;


    public NoteAdapter(List<NoteModel> noteModelList, Context mContext, AdapterCallBack mAdapterCallBack, List<NoteModel> multiSelectionList) {
        this.noteModelList = noteModelList;
        this.mContext = mContext;
        this.mAdapterCallBack = mAdapterCallBack;
        this.multiSelectionList = multiSelectionList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.note_row_Imv_tag)
        ImageView tagImv;
        @BindView(R.id.note_row_tv_title)
        TextView nrTitleTv;
        @BindView(R.id.note_row_frame)
        FrameLayout nrFrameRoot;
        @BindView(R.id.note_row_container)
        RelativeLayout nrContainer;
        @BindView(R.id.note_row_alarm_container)
        RelativeLayout nrAlarmContainer;
        @BindView(R.id.note_row_imv_alarm)
        ImageView nrImvAlarm;
        @BindView(R.id.note_row_tv_time)
        TextView nrTvTime;
        @BindView(R.id.note_row_tv_date)
        TextView nrTvDate;
        @BindView(R.id.note_row_extraNote_container)
        RelativeLayout nrExtraContainer;
        @BindView(R.id.note_row_imv_extra_note)
        ImageView nrImvExtra;
        @BindView(R.id.note_row_tv_extra)
        TextView nrTvExtra;
        @BindView(R.id.note_row_imv_done)
        ImageView nrImvTaskDone;


        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_custom_row, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);

        myViewHolder.nrContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallBack.onRowClick(myViewHolder.getAdapterPosition(), v, myViewHolder.tagImv);

            }
        });
        myViewHolder.nrContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAdapterCallBack.onLongRowClick(myViewHolder.getAdapterPosition(), v);
                return true;
            }
        });

        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        if (mContext != null) {
            NoteModel noteModel = noteModelList.get(position);
            loadDataToFields(noteModel, myViewHolder);

            setUpMultiSelectedBackground(myViewHolder, position, noteModel);
            setUpImageAndTransition(myViewHolder, noteModel);
            setUpGravity(myViewHolder);
            setUpAlarmVisibility(myViewHolder);
            setUpExtraNoteVisibility(myViewHolder);
            setUpTaskDoneVisibility(myViewHolder);
        }

    }

    public void setUpMultiSelectedBackground(MyViewHolder myViewHolder, int position, NoteModel noteModel) {

        if (multiSelectionList.contains(noteModelList.get(position))) {
            if (LOG_DEBUG) Log.e(TAG, " multiList has selected the row");
            myViewHolder.nrFrameRoot.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_selected_color));

        } else {
            myViewHolder.nrFrameRoot.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_normal_color));
            if (LOG_DEBUG) Log.e(TAG, " multiList has de-selected row");
        }
    }

    @Override
    public int getItemCount() {
        return (noteModelList != null ? noteModelList.size() : 0);
    }

    private void setUpImageAndTransition(MyViewHolder myViewHolder, NoteModel noteModel) {
        Glide.with(v.getContext()).load(noteModel.getImgUriPath()).apply(RequestOptions.circleCropTransform()).into(myViewHolder.tagImv);
        ViewCompat.setTransitionName(myViewHolder.tagImv, "imageViewTransName");
    }

    private void loadDataToFields(NoteModel noteModel, MyViewHolder myViewHolder) {
        scheduledTimeLong = noteModel.getScheduleTimeLong();
        isAlarmScheduled = noteModel.getIsAlarmScheduled();
        isTaskDone = noteModel.getIsTaskDone();
        hasExtraNote = noteModel.getSub_text();
        myViewHolder.nrTitleTv.setText(noteModel.getTitle());
        myViewHolder.nrTvDate.setText(UtilCal.formatDatePattern3(noteModel.getCreateDate()));
        String time = getColoredSpanned(UtilCal.formatDatePattern1(scheduledTimeLong), "#FF6F00");
        String separator = getColoredSpanned(" / ", "#ffffff");
        String date = getColoredSpanned(UtilCal.formatDatePattern2(scheduledTimeLong), "#1DE9B6");
        myViewHolder.nrTvTime.setText(Html.fromHtml(time + separator + date));
        myViewHolder.nrTvExtra.setText(hasExtraNote);

    }


    private void setUpGravity(MyViewHolder myViewHolder) {
        if (isAlarmScheduled == 1 && hasExtraNote.length() != 0) {
            myViewHolder.nrTitleTv.setGravity(Gravity.LEFT | Gravity.START);
        } else if (isAlarmScheduled == 0 && hasExtraNote.length() != 0) {
            myViewHolder.nrTitleTv.setGravity(Gravity.LEFT | Gravity.START);
        } else if (isAlarmScheduled == 1 && hasExtraNote.length() == 0) {
            myViewHolder.nrTitleTv.setGravity(Gravity.LEFT | Gravity.START);
        } else if (isAlarmScheduled == 0 && hasExtraNote.length() == 0) {
            myViewHolder.nrTitleTv.setGravity(Gravity.CENTER | Gravity.LEFT | Gravity.START);
        }
    }

    private void setUpAlarmVisibility(MyViewHolder myViewHolder) {
        if (isAlarmScheduled == 1) {
            myViewHolder.nrAlarmContainer.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.nrAlarmContainer.setVisibility(View.GONE);
        }

    }

    private void setUpExtraNoteVisibility(MyViewHolder myViewHolder) {
        if (hasExtraNote.length() > 0) {
            myViewHolder.nrExtraContainer.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.nrExtraContainer.setVisibility(View.GONE);
        }
    }

    private void setUpTaskDoneVisibility(MyViewHolder myViewHolder) {
        if (isTaskDone == 1) {
            myViewHolder.nrTitleTv.setPaintFlags(myViewHolder.nrTitleTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            myViewHolder.nrImvTaskDone.setVisibility(View.VISIBLE);

        } else {
            myViewHolder.nrTitleTv.setPaintFlags(myViewHolder.nrTitleTv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            myViewHolder.nrImvTaskDone.setVisibility(View.GONE);

        }
    }
}
