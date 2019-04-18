package com.cafeteria.free.findcafeteria.model.room.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

  /*  address:
            "충청북도 청주시 상당구 원봉로 52(용암동)"
    address2:
            "충청북도 청주시 상당구 용암동 2108, 용암주공@ 1,2단지"
    date:
            "월~금"
    date2:
            "2018-06-18"
    endTime:
            ""
    facilityName:
            "용암종합사회복지관"
    latitude:
            "36.614349"
    location:
            "용암종합사회복지관"
    longitude:
            "127.509664"
    offerCode:
            "5710000"
    offerName:
            "충청북도 청주시"
    operatingName:
            "용암종합사회복지관"
    phone:
            "043-293-9193"
    startTime:
            ""
    target:
            "저소득층 노인"
    time:
            "11:30~12:30"*/

@Entity(tableName = "cafeteria_table")
public class CafeteriaData implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String address = "";
    private String address2 = "";

    private String date;
    private String date2;

    private String endTime;

    private String facilityName;

    private String latitude;
    private String longitude;
    private String offerName;
    private String operatingName;
    private String phone;
    private String startTime;

    private String target;
    private String time;

    private String offerCode;
    private String location;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    private boolean isFavorite;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getOperatingName() {
        return operatingName;
    }

    public void setOperatingName(String operatingName) {
        this.operatingName = operatingName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CafeteriaData(String address,
                         String address2,
                         String date,
                         String date2,
                         String endTime,
                         String facilityName,
                         String latitude,
                         String longitude,
                         String offerName,
                         String operatingName,
                         String phone,
                         String startTime,
                         String target,
                         String time) {

        this.address = address;
        this.address2 = address2;
        this.date = date;
        this.date2 = date2;
        this.endTime = endTime;
        this.facilityName = facilityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.offerName = offerName;
        this.operatingName = operatingName;
        this.phone = phone;
        this.startTime = startTime;
        this.target = target;
        this.time = time;
    }

    public CafeteriaData() {
    }


    protected CafeteriaData(Parcel in) {
        address = in.readString();
        address2 = in.readString();
        date = in.readString();
        date2 = in.readString();
        endTime = in.readString();
        facilityName = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        offerName = in.readString();
        operatingName = in.readString();
        phone = in.readString();
        startTime = in.readString();
        target = in.readString();
        time = in.readString();
        offerCode = in.readString();
        location = in.readString();
    }

    public static final Creator<CafeteriaData> CREATOR = new Creator<CafeteriaData>() {
        @Override
        public CafeteriaData createFromParcel(Parcel in) {
            return new CafeteriaData(in);
        }

        @Override
        public CafeteriaData[] newArray(int size) {
            return new CafeteriaData[size];
        }
    };



    @Override
    public String toString() {
        return "CafeteriaData{" +
                "address='" + address + '\'' +
                ", address2='" + address2 + '\'' +
                ", date='" + date + '\'' +
                ", date2='" + date2 + '\'' +
                ", endTime='" + endTime + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", offerName='" + offerName + '\'' +
                ", operatingName='" + operatingName + '\'' +
                ", phone='" + phone + '\'' +
                ", startTime='" + startTime + '\'' +
                ", target='" + target + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(address2);
        dest.writeString(date);
        dest.writeString(date2);
        dest.writeString(endTime);
        dest.writeString(facilityName);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(offerName);
        dest.writeString(operatingName);
        dest.writeString(phone);
        dest.writeString(startTime);
        dest.writeString(target);
        dest.writeString(time);
        dest.writeString(offerCode);
        dest.writeString(location);
    }
}
