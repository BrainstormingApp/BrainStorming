package authenticatorStuff;

import java.io.Serializable;

/**
 * Created by pyronaid on 23/11/2016.
 */
public class User implements Serializable {
    private String uid;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String birthday;
    private String timeRegistration;
    private String timeLastLogin;
    public String authType;
    private String authToken;

    private boolean refreshInfo;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeRegistration() {
        return timeRegistration;
    }

    public void setTimeRegistration(String timeRegistration) {
        this.timeRegistration = timeRegistration;
    }

    public String getTimeLastLogin() {
        return timeLastLogin;
    }

    public void setTimeLastLogin(String timeLastLogin) {
        this.timeLastLogin = timeLastLogin;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean getRefreshInfo() {
        return refreshInfo;
    }

    public void setRefreshInfo(boolean refreshInfo) {
        this.refreshInfo= refreshInfo;
    }
}

