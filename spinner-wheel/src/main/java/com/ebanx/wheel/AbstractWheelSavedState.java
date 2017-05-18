package com.ebanx.wheel;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

class AbstractWheelSavedState extends View.BaseSavedState {
    //required field that makes Parcelables from a Parcel
    public static final Creator<AbstractWheelSavedState> CREATOR = new Creator<AbstractWheelSavedState>() {
        public AbstractWheelSavedState createFromParcel(Parcel in) {
            return new AbstractWheelSavedState(in);
        }

        public AbstractWheelSavedState[] newArray(int size) {
            return new AbstractWheelSavedState[size];
        }
    };
    int currentItem;

    AbstractWheelSavedState(Parcelable superState) {
        super(superState);
    }

    private AbstractWheelSavedState(Parcel in) {
        super(in);
        this.currentItem = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(this.currentItem);
    }
}
