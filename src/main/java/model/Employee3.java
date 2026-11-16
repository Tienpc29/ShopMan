package model;


/** Thực thể Employee3 theo thiết kế chương 4. */
public class Employee3 extends Member3 {
    private static final long serialVersionUID = 1L;
    private String position;

    public Employee3() {}

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
