package com.cafeteria.free.findcafeteria.view;

public class CardViewDto {

    public int imageView;
    public String name;
    public String address;
    public String phoneNumber;
    public String time;

    public CardViewDto(int imageView, String name, String address, String phoneNumber, String time) {
        this.imageView = imageView;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.time = time;
    }
}
