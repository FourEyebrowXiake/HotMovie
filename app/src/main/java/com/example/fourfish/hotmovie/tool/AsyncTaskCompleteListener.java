package com.example.fourfish.hotmovie.tool;

/**
 * Created by fourfish on 2017/2/16.
 */

public interface AsyncTaskCompleteListener<T> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */
    public void onTaskComplete(T result);
}
