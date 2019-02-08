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
import android.widget.TextView;

class PasteEditableMask implements EditableMask {

  private final TextView viewText;
  private final char specialChar;
  private final int specialCharPosition;

  private static final int MIN_MASK_COUNT = 1;

  private boolean available;

  PasteEditableMask(TextView viewText, char specialChar, int specialCharPosition) {
    this.viewText = viewText;
    this.specialChar = specialChar;
    this.specialCharPosition = specialCharPosition;
  }

  @Override public boolean isAvailable() {
    return available;
  }

  @Override public void mask(Editable s) {
    if (available) {
      String text = s.toString();
      StringBuilder str = new StringBuilder(text);
      int times = s.length() / (specialCharPosition - 1);
      for (int i = 1; i <= times; i++) {
        int index = i * (specialCharPosition - 1) + (i - 1);
        if (str.charAt(index) != specialChar) { // if we do not have it in pasted then we skip this part
          str.insert(index, String.valueOf(specialChar));
        }
      }
      viewText.setText(str.toString()); // we paste it with specialChar then we do complete code movement
    }
  }

  @Override public void checkIfMaskingAvailable(int count, CharSequence text) {
    available = count > MIN_MASK_COUNT && text.toString().indexOf(specialChar) == -1; // if it contains special char we can not recognize it with spacing
  }
}