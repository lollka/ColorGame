package me.lollka.spele;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    //Tiek definēti UI elementi
    Button Red;
    Button Blue;
    Button Green;
    Button Yellow;
    Button Start;
    Button Stop;
    Button Generate;
    TextView Score;
    TextView IsGood;
    TextView Taimeris;

    //Tiek definēti mainīgie
    String cred = "#ff0000";
    String cgreen = "#1dff00";
    String cblue = "#0033ff";
    String cyellow = "#ffff00";
    ColorStateList cgrey;
    Integer answer;
    Integer guess;
    Integer rezultats = 0;

    //Tiek definētas funkcijas un masīvi uzglabātajiem datiem
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<String> rezultati = new ArrayList<>();
    CharSequence[] cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Piešķir definētajiem UI elementiem atbilstošos elementus no UI izkārtojuma
        Red = (Button) findViewById(R.id.red);
        Blue = (Button) findViewById(R.id.blue);
        Green = (Button) findViewById(R.id.green);
        Yellow = (Button) findViewById(R.id.yellow);
        Start = (Button) findViewById(R.id.sakt);
        Stop = (Button) findViewById(R.id.beigt);
        Generate = (Button) findViewById(R.id.krasa);
        Score = (TextView) findViewById(R.id.rez);
        IsGood = (TextView) findViewById(R.id.ans);
        Taimeris = (TextView) findViewById(R.id.tim);

        //Izslēdz nevajadzīgās pogas
        Stop.setEnabled(false);
        Generate.setEnabled(false);
        Start.setEnabled(true);

        //Iegūst noklusējuma teksta krāsu
        cgrey = Taimeris.getTextColors();
        Generate.setBackgroundColor(Color.TRANSPARENT);

        //Uzstāda tekstu
        Score.setText("0");

        //Atrod aplikācijai atbilstošos lietotāju datus
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        //Palaiž pogu uzspiešanas klausītājus
        kontrole();
    }

    public void kontrole(){
        Start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rezultats = 0;
                saktspeli();
                Timer.start();
                Start.setEnabled(false);
                Stop.setEnabled(true);
            }
        });
        Stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                beigtspeli();
            }
        });
    }

    //1 minūtes taimeris
    CountDownTimer Timer = new CountDownTimer(60000, 1000) {
        public void onTick(long millisUntilFinished) {
            //Uzliek atlikušās sekundes
            if (millisUntilFinished / 1000 < 10) {
                Taimeris.setText("0:0"+String.valueOf(millisUntilFinished / 1000));
            } else {
                Taimeris.setText("0:"+String.valueOf(millisUntilFinished / 1000));
            }
        }
        //Kad beidzas taimeris, izsledz turpmāko spēlēšanu
        public void onFinish() {
            beigtspeli();
        }
    };

    public void krasasGenerators(){
        //Uzģenerē random krāsu, un tad šo krāsu piešķir apakšējai pogai, kura norāda kāda krāsa jāspiež
        Integer randomNum = 1 + (int)(Math.random() * 4);
        if (randomNum == 1) {
            Generate.setBackgroundColor(Color.parseColor(cred));
            answer = 1;
        } else if (randomNum == 2) {
            Generate.setBackgroundColor(Color.parseColor(cgreen));
            answer = 2;
        } else if (randomNum == 3) {
            Generate.setBackgroundColor(Color.parseColor(cblue));
            answer = 3;
        } else {
            Generate.setBackgroundColor(Color.parseColor(cyellow));
            answer = 4;
        }
    }

    public void saktspeli(){
        //Sāk spēli un ieslēdz krāsu pogu klausītājus
        krasasGenerators();
        Red.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                guess = 1;
                parbaude();
            }
        });
        Green.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                guess = 2;
                parbaude();
            }
        });
        Blue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                guess = 3;
                parbaude();
            }
        });
        Yellow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                guess = 4;
                parbaude();
            }
        });
    }

    public void beigtspeli(){
        //Procedūra spēles beigšanai
        IsGood.setTextColor(cgrey);
        IsGood.setText("SPIED SĀKT!");
        Timer.cancel();
        Taimeris.setText("0:00");
        Score.setText("0");
        Generate.setBackgroundColor(Color.TRANSPARENT);
        writerezultati();
    }

    public void parbaude(){
        //Pārbauda vai uzspiestā krāsas poga atbilst random izģenerētai krāsai
        if (guess == answer) {
            rezultats = rezultats + 1;
            Score.setText(rezultats.toString());
            IsGood.setTextColor(Color.parseColor(cgreen));
            IsGood.setText("PAREIZI!");
            saktspeli();
        } else {
            rezultats = rezultats - 1;
            Score.setText(rezultats.toString());
            IsGood.setTextColor(Color.parseColor(cred));
            IsGood.setText("NEPAREIZI!");
            saktspeli();
        }
    }

    public void writerezultati(){
        //Ieraksta iegūtos rezultātus aplikāciju datos
        Integer s = preferences.getInt("Skaits",0);
        editor.putInt("Rez"+s,rezultats);
        s = s + 1;
        editor.putInt("Skaits",s);
        editor.apply();
        rezultatuarray();

        //Atver logu kas parāda tavu rezultātu, un prasa vai vēlies redzēt visus rezultātus
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tavs rezultāts ir "+rezultats)
                .setMessage("Parādīt visus rezultātus?");
        builder.setPositiveButton("Jā", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                showrezultati();
            }
        });
        builder.setNegativeButton("Nē", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                end();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showrezultati(){
        //Atver logu ar visiem rezultātiem
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Visi rezultāti")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        end();
                    }
                });
        builder.setNegativeButton("Aizvērt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                end();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void rezultatuarray(){
        //Aizpilda iepriekš definētu masīvu ar datiem no iepriekšējām spēlēm, lai parādītu visus rezultātus
        int count = preferences.getInt("Skaits",0);
        for (int i = 0; i < count; i++) {
            Integer s = preferences.getInt("Rez"+i,0);
            rezultati.add(s.toString());
        }
        cs = rezultati.toArray(new CharSequence[rezultati.size()]);
    }

    public void end(){
        //Spēles restartēšanas funkcija
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);
    }
}
