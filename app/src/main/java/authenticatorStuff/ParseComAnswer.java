package authenticatorStuff;

import java.io.Serializable;

/**
 * Created by pyronaid on 23/11/2016.
 */
public class ParseComAnswer implements Serializable {
    private String error;
    private int error_code;
    private String title;
    private String error_msg;
    private User user;

    public ParseComAnswer(String error, String title, int error_code, String error_msg){
        this.error = error;
        this.title = title;
        this.error_code = error_code;
        this.error_msg = error_msg;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
