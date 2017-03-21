package com.commitstrip.commitstripreader.common;

public interface JobListener {
  void onPreExecute();
  void onProgressUpdate(int percent);
  void onCancelled();
  void onPostExecute();
}
