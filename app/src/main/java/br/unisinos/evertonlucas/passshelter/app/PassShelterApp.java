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

package br.unisinos.evertonlucas.passshelter.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.io.Serializable;

import br.unisinos.evertonlucas.passshelter.analytics.AnalyticsTrackers;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.service.InstallService;
import br.unisinos.evertonlucas.passshelter.service.KeyService;

/**
 * Class designed for control the application start process
 * Created by everton on 05/09/15.
 */
public class PassShelterApp extends Application implements Serializable {

    private InstallService service = null;
    private static PassShelterApp singleton;
    private KeyService keyService = null;
    private LocalUserRep localUserRepository = null;
    private static String localUser = "";
    public static GoogleAnalytics analytics;
    public static Tracker tracker;


    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        service = new InstallService(this.getApplicationContext());
        service.initialize();

        initializeGoogleAnalytics();
    }

    private void initializeGoogleAnalytics() {
        AnalyticsTrackers.initialize(this.getApplicationContext());

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-68582236-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    public static PassShelterApp getInstance() {
        return singleton;
    }

    public InstallService getInstallService() {
        return service;
    }

    public KeyService getKeyService() {
        return keyService;
    }

    public static KeyService createKeyService(UpdateStatus status, Activity activity) {
        if (getInstance().keyService == null) {
            getInstance().keyService = new KeyService(status, activity);
        } else {
            getInstance().keyService.setUpdateCertificateStatus(status);
            getInstance().keyService.setActivity(activity);
        }
        return getInstance().keyService;
    }

    public static LocalUserRep createUserRep(Context context, SymmetricEncryption symmetricEncryption) {
        if (getInstance().localUserRepository == null) {
            getInstance().localUserRepository = new LocalUserRep(context, symmetricEncryption);
        } else {
            getInstance().localUserRepository.setContext(context);
        }
        try {
            localUser = getInstance().localUserRepository.getUser();
        } catch (Exception e) {
        }
        return getInstance().localUserRepository;
    }

    public static String getLocalUser() {
        return localUser;
    }
}
