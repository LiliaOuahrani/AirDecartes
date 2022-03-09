package com.example.my_application_decartes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private Button buttonEnvoyer;
    String message, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readFileCntent ();

        this.buttonEnvoyer = (Button) this.findViewById(R.id.button2);
        builder = new AlertDialog.Builder(this);

        this.buttonEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TauxPollution AEnvoyer = randValue();
                if(AEnvoyer.getCo2() <= 450) {
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
                //Setting the title manually
                alert.setTitle(title);
                alert.show();

            }
        });
    }


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

                //Ancien fichiers
                /*
                TauxPollution pollution = new TauxPollution();
                pollution.setYear(tokens[0]);
                pollution.setMonth(tokens[1]);
                pollution.setDay(tokens[2]);
                pollution.setLocation(tokens[3]);
                pollution.setCountry(tokens[4]);
                pollution.setLatitude(tokens[5]);
                pollution.setLongitude(tokens[6]);
                pollution.setCO2(tokens[7]);
                pollution.setPollution(tokens[8]);
                pollution.setTemperature(tokens[9]);
                pollution.setHumidite(tokens[10]);*/


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
                //Log.d("MyActivity", "Just created : " +pollution );
            }
        } catch (IOException e){
            Log.wtf("MyActivity", "Error reading data file on linee" +line, e);
            e.printStackTrace();
        }

    }

    private TauxPollution randValue()
    {
        Random random = new Random();
        int rand = random.nextInt(pol.size());
        return pol.get(rand);
    }
}