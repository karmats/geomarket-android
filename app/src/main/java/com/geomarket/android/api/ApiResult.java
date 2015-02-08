package com.geomarket.android.api;

/**
 * Class that represents a result from the GeoMarket api.
 * <p/>
 * <code>R</code> The expected result class
 */
public class ApiResult<R> {

    private Integer code;
    private R data;
    private String error;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public R getData() {
        return data;
    }

    public void setData(R data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
