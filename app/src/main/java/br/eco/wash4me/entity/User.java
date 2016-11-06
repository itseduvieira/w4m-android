package br.eco.wash4me.entity;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Integer id;
    private String name;
    private String email;
    private List<Car> myCars;
    private List<Place> myPlaces;
    private String token;
    private String facebookId;
    private Type type = Type.MEMBER;

    public User() {
        myCars = new ArrayList<>();
        myPlaces = new ArrayList<>();
    }

    public static final User VISITOR = buildVisitor();

    private static User buildVisitor() {
        User result = new User();
        result.setType(Type.VISITOR);
        result.setToken("JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6NSwidXNlcm5hbWUiOiJ2aXNpd" +
                "G9yQHdhc2g0bWUuZWNvLmJyIiwicGFzc3dvcmQiOiIkMmEkMTAkanhCdGouMTNsU2dMM2RDcFJHblhRZWxK" +
                "WFhCOFFrUXQ3VnlTNGU0Y0VRcThpWjdkMHRsRDYiLCJuYW1lIjoiVmlzaXRvciIsIm1vYmlsZSI6IjAiLCJ" +
                "0eXBlIjoiQyIsInZhbGlkIjpmYWxzZSwicm9sZXMiOlsiY3VzdG9tZXIiXSwidG90YWxPcmRlcnMiOjB9." +
                "WmmdSetrFKmbM7b7AYCKn8s9KllFrE4_ahl_iL-0l-8");

        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Car> getMyCars() {
        return myCars;
    }

    public void setMyCars(List<Car> myCars) {
        this.myCars = myCars;
    }

    public List<Place> getMyPlaces() {
        return myPlaces;
    }

    public void setMyPlaces(List<Place> myPlaces) {
        this.myPlaces = myPlaces;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public enum Type {
        VISITOR, MEMBER
    }
}
