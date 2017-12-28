package com.redfox.nimbustodo.job;

import android.database.Cursor;
import android.os.Process;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.db.DBSchema;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.util.common_util.UtilDBOperation;

import java.util.ArrayList;
import java.util.List;


public class MoveToArchiveJob extends JobService {

    private final static String TAG = MoveToArchiveJob.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;

    private Thread myWorker = null;
    private List<NoteModel> noteModelList;


    @Override
    public boolean onStartJob(JobParameters job) {
        moveToArchived(job);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }


    //delete entries marked as archived
    private void moveToArchived(final JobParameters parameters) {
        myWorker = new Thread(new Runnable() {
            @Override
            public void run() {

                DBMgr dbMgr = new DBMgr(MoveToArchiveJob.this);
                dbMgr.openDataBase();
                Cursor cursor = dbMgr.getCursorForTaskDone("1");
                noteModelList = UtilDBOperation.extractCommonData(cursor, noteModelList);


                if (LOG_DEBUG) {
                    Log.d(TAG, "Loaded data (entry only with isTaskDOne 1) to NoteModel Size : " + noteModelList.size());
                    Log.d(TAG, "Loaded data (entry only with isTaskDOne 1) to NoteModel List : " + noteModelList.toString());
                }
                NoteModel noteModel;

                for (int i = 0; i < noteModelList.size(); i++) {
                    noteModel = noteModelList.get(i);
                    int isTaskDone = noteModel.getIsTaskDone();
                    if (isTaskDone == 1) {
                        noteModel.setIsArchived(1);
                    } else {
                        noteModel.setIsArchived(0);
                    }
                    long status = dbMgr.updateNote(noteModel);
                    if (LOG_DEBUG) Log.d(TAG, " update status : " + status);

                }

                dbMgr.closeDataBase();
                noteModelList.clear();
                jobFinished(parameters, false);
                if (LOG_DEBUG) Log.v(TAG, " jobFinished() called");
            }
        });
        myWorker.start();
        myWorker.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (LOG_DEBUG) Log.v(TAG, "onDestroy()");

        if (myWorker != null) {
            Thread thread = myWorker;
            thread.interrupt();
            myWorker = null;
        }
    }


}
