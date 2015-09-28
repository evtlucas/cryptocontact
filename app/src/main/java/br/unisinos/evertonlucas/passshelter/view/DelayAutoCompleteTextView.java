/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package br.unisinos.evertonlucas.passshelter.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Special auto complete text view for remote users
 * Created by everton on 26/09/15.
 */
public class DelayAutoCompleteTextView extends AutoCompleteTextView {

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;

    private int autoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private ProgressBar indicator;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DelayAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public DelayAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLoadingIndicator(ProgressBar progressBar){
        indicator = progressBar;
    }

    public void setAutoCompleteDelay(int autoCompleteDelay) {
        this.autoCompleteDelay = autoCompleteDelay;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (indicator != null) {
            indicator.setVisibility(VISIBLE);
        }
        handler.removeMessages(MESSAGE_TEXT_CHANGED);
        handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TEXT_CHANGED, text), autoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        if (indicator != null)
            indicator.setVisibility(GONE);
        super.onFilterComplete(count);
    }
}
