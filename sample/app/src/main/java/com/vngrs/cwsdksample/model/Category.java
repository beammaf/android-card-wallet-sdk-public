/*
 * TPS SDK Android Copyright (C) 2018 Fatih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vngrs.cwsdksample.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.vngrs.cwsdksample.base.Objects;

public class Category implements Parcelable {
  int x = 0;
  public final static Category INITIALIZE_CW_SDK = new Category("Initialize CW SDK");
  public final static Category MANAGE_CREDIT_CARDS = new Category("Manage Credit Cards");
  private String categoryName;

  private Category(String categoryName) {
    this.categoryName = categoryName;
  }

  private Category(Parcel input) {
    boolean hasCategoryName = input.readInt() == 1;
    if (hasCategoryName) {
      categoryName = input.readString();
    }
  }

  public String getCategoryName() {
    return categoryName;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    boolean hasCategoryName = !Objects.isNullOrEmpty(categoryName);
    if (hasCategoryName) {
      out.writeString(categoryName);
    }
  }

  @Override public int describeContents() {
    return 0;
  }

  public final static Creator<Category> CREATOR = new ClassLoaderCreator<Category>() {

    @Override public Category createFromParcel(Parcel source, ClassLoader loader) {
      return new Category(source);
    }

    @Override public Category createFromParcel(Parcel source) {
      return new Category(source);
    }

    @Override public Category[] newArray(int size) {
      return new Category[size];
    }
  };

  static class Builder {

    private String categoryName;

    Builder() {}

    public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }

    Category build() {
      return new Category(categoryName);
    }
  }
}