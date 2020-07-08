package com.google.sps.data;

/** An Input item. */
public final class Input {

  private final String comment;
  private final String imageUrl;
  private final String email;

  public Input(String comment, String imageUrl, String email) {
    this.comment = comment;
    this.imageUrl = imageUrl;
    this.email = email;
  }
}