package dk35840.googlesigning1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dk on 5/22/2017.
 */

public class UserInformation implements Parcelable {
    String name;

    public UserInformation() {
    }
    String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public UserInformation(String name, String address) {
        this.name=name;
        this.address = address;


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.address);
        parcel.writeString(this.name);

    }

    protected UserInformation(Parcel in) {
        this.address = in.readString();
        this.name = in.readString();

    }

    public static final Creator<UserInformation> CREATOR = new Creator<UserInformation>() {
        @Override
        public UserInformation createFromParcel(Parcel source) {
            return new UserInformation(source);
        }

        @Override
        public UserInformation[] newArray(int size) {
            return new UserInformation[size];
        }
    };
}
