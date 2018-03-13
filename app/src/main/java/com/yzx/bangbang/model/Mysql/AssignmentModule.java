package com.yzx.bangbang.model.Mysql;

//原先的Assignment类是这个类的子集，这个类的成员与数据库一样
public class AssignmentModule {
    public int id;
    public String title;
    public String content;
    public int employer_id;
    public String employer_name;
    public String date;
    public float price;
    public int repliers;
    public int images;
    public int floors;
    public double latitude;
    public double longitude;
    public AssignmentModule(int id, String title, String content, int employer_id, String employer_name, String date, float price, int repliers, int images, int floors, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.employer_id = employer_id;
        this.employer_name = employer_name;
        this.date = date;
        this.price = price;
        this.repliers = repliers;
        this.images = images;
        this.floors = floors;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
