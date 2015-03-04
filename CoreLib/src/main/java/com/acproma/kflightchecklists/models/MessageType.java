package com.acproma.kflightchecklists.models;

import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Congee on 9/18/14.
 */
public enum MessageType implements Parcelable {
    ACTION_ITEM, CAUTION, NOTE;

	final static public Creator<MessageType> CREATOR = new ClassLoaderCreator<MessageType>() {
		@Override
		public MessageType createFromParcel(Parcel source, ClassLoader loader) {
			return createFromParcel(source);
		}

		@Override
		public MessageType createFromParcel(Parcel source) {
			return MessageType.valueOf(source.readString());
		}

		@Override
		public MessageType[] newArray(int size) {
			return new MessageType[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(dest.toString());
	}
}
