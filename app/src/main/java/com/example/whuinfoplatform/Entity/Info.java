package com.example.whuinfoplatform.Entity;

import org.litepal.crud.DataSupport;

public class Info extends DataSupport {
    private int id;
    private int owner_id;
    private String send_date;
    private int answered;
    private int form;
    private int fd_form;
    private int help_form;
    private double price;
    private String date;
    private String place;
    private String lesson;
    private int score;
    private String detail;
    private double reward;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getOwner_id(){
        return owner_id;
    }
    public void setOwner_id(int owner_id){
        this.owner_id = owner_id;
    }
    public String getSend_date(){
        return send_date;
    }
    public void setSend_date(String send_date){
        this.send_date = send_date;
    }
    public int getAnswered(){
        return answered;
    }
    public void setAnswered(int answered){
        this.answered=answered;
    }

    public int getForm() {
        return form;
    }
    public void setForm(int form){
        this.form = form;
    }

    public int getFd_form() {
        return fd_form;
    }
    public void setFd_form(int fd_form) {
        this.fd_form = fd_form;
    }

    public int getHelp_form() {
        return help_form;
    }
    public void setHelp_form(int help_form) {
        this.help_form = help_form;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    public double getReward() {
        return reward;
    }
    public void setReward(double reward) {
        this.reward = reward;
    }
}


