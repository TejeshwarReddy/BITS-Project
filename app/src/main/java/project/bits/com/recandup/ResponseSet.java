package project.bits.com.recandup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tejeshwar on 29/1/17.
 */

public class ResponseSet {

    @SerializedName("success")
    @Expose
    private Integer success;

    /**
     * No args constructor for use in serialization
     *
     */
    public ResponseSet() {
    }

    /**
     *
     * @param success
     */
    public ResponseSet(Integer success) {
        super();
        this.success = success;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

}