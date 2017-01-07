package com.babel.kliky;

/**
 * Created by jan.babel on 29/12/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String LAST_JOKE_POSITION = "lastJoke";
    private int jokePosition;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String mobileNumber = sharedPref.getString(MyPreferencesActivity.MOBILE_NUMBER_PREF, "+421905856454");
//        String messageFromPreferences = sharedPref.getString(MyPreferencesActivity.SMS_MESSAGE_PREF, "Zase sa flakas!?");
        jokePosition = sharedPref.getInt(LAST_JOKE_POSITION, 0);
        String messageFromPreferences = getJoke(jokePosition);

        boolean shouldSendSms = sharedPref.getBoolean(MyPreferencesActivity.SENDING_SMS, false);
        String balance = "";
        int max = 0, sum = 0;
        Bundle bd = intent.getExtras();

        if (bd != null) {
            balance = (String) bd.get(MainActivity.BALANCE);
            max = bd.getInt(MainActivity.MAX);
            sum = bd.getInt(MainActivity.SUM);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageFromPreferences).append(" max:").append(max).append(" sum: ").append(sum).append(" balance: ").append(balance);

        if (shouldSendSms) {
            Toast.makeText(context, "Sending SMS", Toast.LENGTH_SHORT).show();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobileNumber, null, stringBuilder.toString(), null, null);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("LAST_JOKE_POSITION", jokePosition++);
            editor.apply();
        }
    }

    private String getJoke(int position) {
        List<String> jokes = Arrays.asList(
                "Babka hovorí deťom: Uhnite!! A deti uhnili",
                "je iba otazkou zornych uhlov,či kolíska nieje truhlou...",
                "Viete, čo by sa stalo, keby sa Zem otáčala 30x rýchlejšie??? Muži by mali stále výplatu a ženy by vykrvácali..",
                "U ušneho lekara: Čistím si uši klincom, a zrazu ticho!",
                "Nedočkavý mladík bol dlho odlúčený od svojej milej, preto jej napísal v náhlivosti nasledovnú SMS: UŽMIJEBEZTEBZDLHO! A bolo zle.",
                "Čo je to maximálny feminizmus? Keď žena pri milovaní používa gumenú anču...",
                "VRCHOL GEOMETRIE:Stáť v rohu okrúhlej miestnosti...",
                "Dve piškóty idú po ceste. Jednu prejde auto a druhá hovorí:Co sa mrvíš?",
                "Dá sa dvoma dierami zapchať jedna? Dá, keď pichnete niekomu nos do zadku..",
                "Ležia na stole vedľa seba banán a vibrátor. Banán vraví vibrátoru: Čo sa trasieš, ty somár, teba aj tak nezožerú.",
                "Mucha, kam letíš? No a čo!",
                "aky je rozdiel medzi muchou a svokrou???....mucha otravuje len v lete....",
                "Viete kde nájdete korytnačku bez nôh?Tam kde ste ju nechali.",
                "Čo urobí blondínka z dvomi železnými gulami? Jednu stratí a druhú pokazí.",
                "Vieš aký je rozdiel medzi tebou a šampónom?? Ziadny.Obaja ste na hlavu...",
                "Vieš, prečo stvoril Boh mužom nohy do O?Lebo všetko, čo sa mu nepáčilo dal do zátvorky.",
                "Pýta sa slepý bubeník hluchého gitaristu:Už tancujú? Prečo? Už hráme?",
                "Stále ťa cítim, keď spím, keď sa umývam, keď jem... aká... ty si si zabudol u mňa ponožky!",
                "Manželka si dala na tvár bahnovú masku.Dva dni vyzerala lepsie,potom bahno opadalo...",
                "Ak na Teba niekto kričí, usmej sa a maj ho v piči...",
                "Prečo má nočník ucho? Aby počul, keď sa čúra vedľa.",
                "Jožko, prečo namáčaš tu stenu? Lekár mi vravel, že si mám dávať obklady na miesta, kde som sa udrel.",
                "80% žien sa už nevydáva, lebo prišli na to, že pre 60 gramov klobásy sa nevypláca chovať doma celú sviňu.",
                "Skotačia deti na mínovom poli, rozhadzujú ručičkami, nožičkami...",
                "Na recepcii sa chce ubytovať rodina Jebalovcov: Recepčný: Vaše mená prosím.Pán Jebal: Ja som Jebal s manželkou, dvoma dcérami a tuto synom. Recepčný: Ja mám v piči s kým jebete, zapíšem Vás ako jednu veľkú skurvenú rodinu ",
                "mam ta rada ako zahradku kopat,kopat a kopat...",
                "Viete, aká je to najväčšia tma? Keď musíte zapáliť druhú zápalku, aby ste zistili, či tá prvá ešte horí",
                "Chcel som ti len povedať, že vyzeráš čím ďalej tým lepšie a čím bližšie tým horšie. .",
                "Keď som takú postavu ako je tvoja videl naposledy, dojili ju.",
                "Ako sa volá muž, ktorý chce sex až na druhom rande? Pomalý!...",
                "Dežo chce spáchať samovraždu a preto vyskočí z okna z dvanásteho poschodia. Ako tak padá, počíta si poschodia, ktoré už presiel 12, 11, 10, 9, 8, 7, 8, 9, 10... dočerta, zasa tie traky!",
                "Tá dnešná mládež je ale márnotratná hovorí jedna spermia druhej, keď stekajú dole umývadlom",
                "Miláčik milujem Ťa tak, že by som pre Teba skočil do najväčšieho ohňa na svete, preplával najväčší oceán na svete a skočil aj do najhlbšej studne na svete.PS: Prídem zajtra k Tebe, ak nebude pršať.",
                " Ocko, čo to máš také chlpaté medzi nohami?To nič Jurko, to je iba ježko.Fíha, ten ježko má ale penis.",
                "Vieš jaky je rozdiel medzi motorovou pilo a toaletným papierom???? Žiadny jeden neopatrny pohyb a prsty su v riti...",
                "prečo nohy smrdia? lebo rastu od rici!!!...",
                "Rozhovor dvoch chlapov: Vrátil si mu ten nôž? Áno! A čo povedal? Au!",
                "Zostanem s tebou v posteli iba chvíľu. Pohladím tvoje intímne miesta, zacítiš moju vôňu a potom sa už budem iba vznášať. Tvoj prd",
                "Vychodoslovensky policajti nasli v lese mrtvolu.Bola skareda a bez mozgu.Mam o teba strach,radsej mi prezvon",
                "Medved trtka kozu. Ty medved,akeho mas hrubeho a chlpateho. Kua,zabudol som si dat dole vevericku!",
                "Pride velmi tucna zena ku gynekologovi a horko-tazko sa usadi na kozu. Doktor pozera,pozera a po chvili hovori:Prepacte ,mohli by ste si prdnut? Aby som sa mohol zorientovat.",
                "Miesto rannej rozcvicky - strc kokota do picky!...",
                "Môže dostať medveď jarnú únavu, ak spal celú zimu?Ak si dal v septembri kávu.",
                "Na hasičskej stanici:- Akcia bola úspešne ukončená. Požiar bol uhasený, nezhorela ani jedna krava. Desať sa utopilo.",
                "Sedem rokov som nepil, nefajčil, nebehal za ženami. A potom som začal chodiť do základnej školy.",
                "pride gay do masiarstva a pyta si klobasu, masiar sa ho pyta ,,chcete ju nakrajat? a gay mu odpovie ,,mate pocit ze mam prdelku na žetony?",
                "mužský penis je to najubohejsie stvorenie na svete,nema ruky ani nohy,na hlave ma dieru,a ked si konecne mysli ze je na vrchole,tak je v pici :",
                "Otázka: Je dobré s manželkou prehodiť zopár slov po milovaní? Odpoveď: Odporúča sa to. Veď na čo už máme tie mobilné telefóny.",
                "Žena vie sedemdesiat vecí. Varenie a 69....",
                "kto druhemu jamu kope...zarobi a potom slope :",
                "Rozprávajú sa dve cibule a jedna hovorí druhej:Fúj, ale ti smrdí z úst, čo si jedla cesnak?",
                "heslo dňa : fajčiť síce budem ale piť neprestanem.",
                "Nepokúšaj sa žiaľ utopiť v alkohole! Vie totiž plávať... ",
                "Ak ti vadí pri práci alkohol, nepracuj! ...",
                "Bodaj by sa ti EKG vyrovnalo!",
                "V rámci vládnej akcie SOS HLADOVÝ ROM Vám bol pridelený 1 rabujúci róm na vykrmovanie. Ak si ho neprídete vyzdvihnúť do 24 hodín, bude Vám pridelená celá rodina!",
                "Chceš byť debil? Buď sám sebou.",
                "Ide babka cez kolajnice a na zemi sa niečo leskne. Babka to zdvihne a to päťkoruna.Ja mám teda štastný deňTdn, tdn",
                "Jednooký a slepý idú s motorkou na diskotéku. Idú cez les a jednooký si na konári vypichne aj druhé oko. Vraví :No , a sme došli! A slepý na to: Čaute baby!",
                "Mám sa dobre, ale zle to znášam.",
                "Kolekcia dámskych nohavičiek: Monday, Tuesday, Wendsday, ...Kolekcia pánskych slipov: January, February, March, ..",
                "Išiel som na raňajky. Ak sa nevrátim do 12.30, tak aj na obed.",
                "Aká je to svätá žena?Tá čo leží na krížoch a prijíma telo pána.",
                "Priateľka mi našla vo vrecku rúž. Povedal som jej, že ju podvádzam s inou.Cítil by som sa trápne, keby som jej mal povedať, že predávam AVON.",
                "Čo je to totálne nič? Ošúpaný balón.",
                "Všetci okolo sú blázni a zopár fajnoviek si hovorí lietadlo. Ale iba ja som ponorka!",
                "Mami, poďme aj mi piknikovať! Ale Ivko, to sú bezdomovci.",
                "Výjdu dve kostry z hrobu a jedna si zoberie náhrobný kameň. Druhá sa jej pýta: Načo ti to je? Musím mať predsa doklady."
        );

        return jokes.get(position);

    }
}