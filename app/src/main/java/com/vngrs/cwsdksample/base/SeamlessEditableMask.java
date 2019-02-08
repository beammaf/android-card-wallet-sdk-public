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
package com.vngrs.cwsdksample.base;

import android.text.Editable;
import android.text.TextUtils;

class SeamlessEditableMask implements EditableMask {

  private final char specialChar;
  private final int specialCharPosition;

  private boolean available;

  SeamlessEditableMask(char specialChar, int specialCharPosition) {
    this.specialChar = specialChar;
    this.specialCharPosition = specialCharPosition;
  }

  @Override public boolean isAvailable() {
    return available;
  }

  @Override public void mask(Editable s) {
    // Remove spacing char
    if (s.length() > 0 && (s.length() % specialCharPosition) == 0) {
      final char c = s.charAt(s.length() - 1);
      if (specialChar == c) {
        s.delete(s.length() - 1, s.length());
      }
    }
    // Insert char where needed.
    if (s.length() > 0 && (s.length() % specialCharPosition) == 0) {
      char c = s.charAt(s.length() - 1);
      // Only if its a digit where there should be a space we insert a space
      if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(specialChar)).length <= specialCharPosition - 2) {
        s.insert(s.length() - 1, String.valueOf(specialChar));
      }
    }
  }

  @Override public void checkIfMaskingAvailable(int count, CharSequence text) {
    available = true;
  }
}