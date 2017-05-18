/*
 * android-spinnerwheel
 * https://github.com/ai212983/android-spinnerwheel
 *
 * based on
 *
 * Android Wheel Control.
 * https://code.google.com/p/android-wheel/
 *
 * Copyright 2011 Yuri Kanivets
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ebanx.wheel;

import android.content.Context;

import com.ebanx.wheel.adapters.AbstractWheelTextAdapter;

import java.util.List;


/**
 * The simple Array spinnerwheel adapter
 * @param <T> the element type
 */
public class ListObjectWheelAdapter<T> extends AbstractWheelTextAdapter {

    // items
    private final List<T> items;

    /**
     * Constructor
     * @param context the current context
     * @param items the items
     */
    public ListObjectWheelAdapter(Context context, List<T> items) {
        super(context);

        //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.items = items;
    }

    @Override public String getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            if (items.get(index) instanceof HasName) {
                return ((HasName) items.get(index)).getName();
            }
        }
        return null;
    }

    public int getItemPosition(T obj) {
        if (obj != null) {
            for (int i = 0; i < getItemsCount(); i++) {
                T item = items.get(i);
                if (item.equals(obj)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public T getObject(int position) {
        return items.get(position);
    }



    @Override
    public int getItemsCount() {
        return items.size();
    }
}
