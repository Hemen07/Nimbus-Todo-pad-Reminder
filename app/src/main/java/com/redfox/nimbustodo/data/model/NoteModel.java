package com.redfox.nimbustodo.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.redfox.nimbustodo.util.common_util.UtilCal;


public class NoteModel implements Parcelable {

    //Gotta Parcel All as whole object will be passing across classes

    private int _id;
    private String title;
    private int imgUriPath;
    private int row_pos;
    private String sub_text;
    private long createDate;
    private long updateDate;
    private long scheduleTimeLong;
    private long scheduledWhenLong;
    private String scheduledTitle;
    private int isAlarmScheduled;
    private int isTaskDone;
    private int isArchived;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgUriPath() {
        return imgUriPath;
    }

    public void setImgUriPath(int imgUriPath) {
        this.imgUriPath = imgUriPath;
    }

    public int getRow_pos() {
        return row_pos;
    }

    public void setRow_pos(int row_pos) {
        this.row_pos = row_pos;
    }

    public String getSub_text() {
        return sub_text;
    }

    public void setSub_text(String sub_text) {
        this.sub_text = sub_text;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public long getScheduleTimeLong() {
        return scheduleTimeLong;
    }

    public void setScheduleTimeLong(long scheduleTimeLong) {
        this.scheduleTimeLong = scheduleTimeLong;
    }

    public long getScheduledWhenLong() {
        return scheduledWhenLong;
    }

    public void setScheduledWhenLong(long scheduledWhenLong) {
        this.scheduledWhenLong = scheduledWhenLong;
    }

    public String getScheduledTitle() {
        return scheduledTitle;
    }

    public void setScheduledTitle(String scheduledTitle) {
        this.scheduledTitle = scheduledTitle;
    }

    public int getIsAlarmScheduled() {
        return isAlarmScheduled;
    }

    public void setIsAlarmScheduled(int isAlarmScheduled) {
        this.isAlarmScheduled = isAlarmScheduled;
    }

    public int getIsTaskDone() {
        return isTaskDone;
    }

    public void setIsTaskDone(int isTaskDone) {
        this.isTaskDone = isTaskDone;
    }

    public int getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }

    @Override
    public String toString() {
        return " --" + "\n" +
                " , _id :" + String.valueOf(_id) + "\n" +
                " , title : " + getTitle() + "\n" +
                " , imageUriPath :" + getImgUriPath() + "\n" +
                " , rowClickedPos : " + getRow_pos() + "\n" +
                " , subText : " + getSub_text() + "\n" +
                " , createDate : " + getCreateDate() + " == " + UtilCal.formatDate(getCreateDate()) + "\n" +
                " , updateDate : " + getUpdateDate() + " == " + UtilCal.formatDate(getUpdateDate()) + "\n" +
                " , scheduledTimeLong : " + getScheduleTimeLong() + "\n" +
                " , scheduledWhenLong : " + getScheduledWhenLong() + " == " + UtilCal.formatDate(getScheduledWhenLong()) + "\n" +
                " , scheduledTitle : " + getScheduledTitle() + "\n" +
                " , isAlarmScheduled : " + getIsAlarmScheduled() + "\n" +
                " , isTaskDone : " + getIsTaskDone() + "\n" +
                " , isArchived : " + getIsArchived() + "\n" +
                "  -- ";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.title);
        dest.writeInt(this.imgUriPath);
        dest.writeInt(this.row_pos);
        dest.writeString(this.sub_text);
        dest.writeLong(this.createDate);
        dest.writeLong(this.updateDate);
        dest.writeLong(this.scheduleTimeLong);
        dest.writeLong(this.scheduledWhenLong);
        dest.writeString(this.scheduledTitle);
        dest.writeInt(this.isAlarmScheduled);
        dest.writeInt(this.isTaskDone);
        dest.writeInt(this.isArchived);
    }

    public NoteModel() {
    }

    protected NoteModel(Parcel in) {
        this._id = in.readInt();
        this.title = in.readString();
        this.imgUriPath = in.readInt();
        this.row_pos = in.readInt();
        this.sub_text = in.readString();
        this.createDate = in.readLong();
        this.updateDate = in.readLong();
        this.scheduleTimeLong = in.readLong();
        this.scheduledWhenLong = in.readLong();
        this.scheduledTitle = in.readString();
        this.isAlarmScheduled = in.readInt();
        this.isTaskDone = in.readInt();
        this.isArchived = in.readInt();
    }

    public static final Creator<NoteModel> CREATOR = new Creator<NoteModel>() {
        @Override
        public NoteModel createFromParcel(Parcel source) {
            return new NoteModel(source);
        }

        @Override
        public NoteModel[] newArray(int size) {
            return new NoteModel[size];
        }
    };
}



