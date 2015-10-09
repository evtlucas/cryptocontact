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

package br.unisinos.evertonlucas.passshelter.analytics;

import com.google.android.gms.analytics.HitBuilders;

import br.unisinos.evertonlucas.passshelter.app.PassShelterApp;

/**
 * Class responsible for send event messages to Google Analytics
 * Created by everton on 07/10/15.
 */
public class AnalyticsMessage {

    public static void sendMessageToAnalytics(String screenName, String category, String action) {
        PassShelterApp.tracker.setScreenName(screenName);
        PassShelterApp.tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setValue(1)
                .build());
    }
}