package com.example.my_application_decartes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.Locale;

import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {


    private AlertDialog.Builder builder;
    private Button buttonEnvoyer;
    String message, title;


    //localisation
    FusedLocationProviderClient fusedLocationProviderClient;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        readFileCntent();

        this.buttonEnvoyer = (Button) this.findViewById(R.id.button2);
        builder = new AlertDialog.Builder(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            this.buttonEnvoyer.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      // selectionner una valeur random de la liste
                      TauxPollution selectVal = randValue();

                      if(selectVal.getCo2() <= 450) {
                          message = "Pour votre sante, veuillez ne pas y rester longtemps";
                          title = "Vous etes dans une zone a forte pollution";
                      }
                      else {
                          message = "Passez un agreable moment !";
                          title = "Vous n'etes pas dans une zone a forte pollution";
                      }
                      builder.setMessage(message)
                              .setCancelable(false)
                              .setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog, int id) {
                                      dialog.cancel();
                                  }
                              });
                      //Creating dialog box
                      AlertDialog alert = builder.create();
                      //Setting the title
                      alert.setTitle(title);
                      alert.show();
                  }
              });
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    // Methode qui importe le fichier csv, le lit et met son contenu dans une liste
    private List<TauxPollution> pol = new ArrayList<>();
    private void readFileCntent() {
        InputStream is = getResources().openRawResource(R.raw.data_);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            //sauter le header
            reader.readLine();

            while ((line = reader.readLine()) != null){
                //Split by ,
                String[] tokens = line.split(",");

                //read the data
                TauxPollution pollution = new TauxPollution();
                pollution.setId(Integer.parseInt(tokens[0]));
                pollution.setCodecapteur(Integer.parseInt(tokens[1]));
                pollution.setCapteurfullname(tokens[2]);
                pollution.setDimension(tokens[3]);
                pollution.setBatterylifetime(tokens[4]);
                pollution.setInstalation(tokens[5]);
                pollution.setTemperature(Float.parseFloat(tokens[6]));
                pollution.setCo2(Float.parseFloat(tokens[7]));
                pollution.setPression(Float.parseFloat(tokens[8]));
                pollution.setHumidity(Float.parseFloat(tokens[9]));
                pollution.setLat(Float.parseFloat(tokens[10]));
                pollution.setLng(Float.parseFloat(tokens[11]));
                pollution.setAddress(tokens[12]);
                pollution.setCapteurdistance(tokens[13]);
                pollution.setYearobservation(Integer.parseInt(tokens[14]));
                pollution.setMonthobservation(Integer.parseInt(tokens[15]));
                pollution.setDayobservation(Integer.parseInt(tokens[16]));
                pollution.setHourobservation(Integer.parseInt(tokens[17]));
                pollution.setMinuteobservation(Integer.parseInt(tokens[18]));

                pol.add(pollution);

                //Afficher les rows ajoutes !
                Log.d("MyActivity", "Just created : " +pollution );
            }
        } catch (IOException e){
            Log.wtf("MyActivity", "Error reading data file on linee" +line, e);
            e.printStackTrace();
        }

    }

    // methode qui selectionne une ligne en random dans le tableau et lui set la localisation actuelle
    private TauxPollution randValue()
    {
        Random random = new Random();
        int rand = random.nextInt(pol.size());
        TauxPollution AEnvoyer = pol.get(rand);

        // mettre la localisation actuelle dans la donnee a envoyer
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null)
                {
                    try {
                        Geocoder geocoder= new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                        AEnvoyer.setLat((float)addresses.get(0).getLatitude());
                        AEnvoyer.setLng((float)addresses.get(0).getLongitude());
                        Log.d("MyActivity", "AEnvoyer : " + AEnvoyer.toString() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        );

        return AEnvoyer;
    }


    // methode qui retourne la localisation ELLE N"EST PAS UTILISEE
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null)
                {
                    try {
                        Geocoder geocoder= new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                        Log.d("MyActivity", "LOcalisation : " + addresses.get(0).getLatitude() + addresses.get(0).getLongitude() + addresses.get(0).getCountryName()  + addresses.get(0).getAddressLine(0) );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        );
    }

}