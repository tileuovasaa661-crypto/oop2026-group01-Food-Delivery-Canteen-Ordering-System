package util;

public final class Result<T> {

    private final boolean ok;
    private final T data;
    private final String error;

    private Result(boolean ok, T data, String error) {
        this.ok = ok;
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(true, data, null);
    }

    public static <T> Result<T> fail(String error) {
        return new Result<>(false, null, error);
    }

    public boolean isOk() {
        return ok;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
