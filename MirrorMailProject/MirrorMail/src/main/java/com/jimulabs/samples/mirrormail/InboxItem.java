package com.jimulabs.samples.mirrormail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matt on 2014-02-13.
 */
public class InboxItem implements Parcelable {
    public final int avatarId;
    public final boolean flagVisible;
    public final String receiveTime;
    public final boolean bulletVisible;
    public final String sender;
    public final boolean hasAttachment;
    public final String subject;
    public final String snippet;

    public InboxItem(int avatarId, boolean flagVisible, String receiveTime,
                     boolean bulletVisible, String sender, boolean hasAttachment,
                     String subject, String snippet) {
        this.avatarId = avatarId;
        this.flagVisible = flagVisible;
        this.receiveTime = receiveTime;
        this.bulletVisible = bulletVisible;
        this.sender = sender;
        this.hasAttachment = hasAttachment;
        this.subject = subject;
        this.snippet = snippet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(avatarId);
        dest.writeString(receiveTime);
        dest.writeString(sender);
        dest.writeString(subject);
        dest.writeString(snippet);
        dest.writeBooleanArray(new boolean[] {
                flagVisible, bulletVisible, hasAttachment
        });
    }

    public static final Creator<InboxItem> CREATOR = new Creator<InboxItem>() {
        @Override
        public InboxItem createFromParcel(Parcel source) {
            int avatarId = source.readInt();
            String time = source.readString();
            String sender = source.readString();
            String subject = source.readString();
            String snippet = source.readString();
            boolean[] flags = new boolean[3];
            source.readBooleanArray(flags);
            boolean flagVisible = flags[0];
            boolean bulletVisible = flags[1];
            boolean hasAttachment = flags[2];

            return new InboxItem(avatarId, flagVisible, time, bulletVisible, sender,
                    hasAttachment, subject, snippet);
        }

        @Override
        public InboxItem[] newArray(int size) {
            return new InboxItem[0];
        }
    };
}
