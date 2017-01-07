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
            editor.putInt(LAST_JOKE_POSITION, ++jokePosition);
            editor.commit();
        }
    }

    private String getJoke(int position) {
        List<String> jokes = Arrays.asList(
                "1.Babka hovori detom: Uhnite!! A deti uhnili",
                "2.Babka hovori detom: Uhnite!! A deti uhnili",
                "3. Babka hovori detom: Uhnite!! A deti uhnili",
                "je iba otazkou zornych uhlov,ci koliska nieje truhlou...",
                "Viete, co by sa stalo, keby sa Zem otacala 30x rychlejsie??? Muzi by mali stale vyplatu a zeny by vykrvacali..",
                "U usneho lekara: cistim si usi klincom, a zrazu ticho!",
                "Nedockavy mladik bol dlho odluceny od svojej milej, preto jej napisal v nahlivosti nasledovnu SMS: UzMIJEBEZTEBZDLHO! A bolo zle.",
                "co je to maximalny feminizmus? Keď zena pri milovani pouziva gumenu ancu...",
                "VRCHOL GEOMETRIE:Stat v rohu okruhlej miestnosti...",
                "Dve piskoty idu po ceste. Jednu prejde auto a druha hovori:Co sa mrvis?",
                "Da sa dvoma dierami zapchat jedna? Da, keď pichnete niekomu nos do zadku..",
                "Lezia na stole vedla seba banan a vibrator. Banan vravi vibratoru: co sa trasies, ty somar, teba aj tak nezozeru.",
                "Mucha, kam letis? No a co!",
                "aky je rozdiel medzi muchou a svokrou???....mucha otravuje len v lete....",
                "Viete kde najdete korytnacku bez noh?Tam kde ste ju nechali.",
                "co urobi blondinka z dvomi zeleznymi gulami? Jednu strati a druhu pokazi.",
                "Vies aky je rozdiel medzi tebou a samponom?? Ziadny.Obaja ste na hlavu...",
                "Vies, preco stvoril Boh muzom nohy do O?Lebo vsetko, co sa mu nepacilo dal do zatvorky.",
                "Pyta sa slepy bubenik hlucheho gitaristu:Uz tancuju? Preco? Uz hrame?",
                "Stale ta citim, keď spim, keď sa umyvam, keď jem... aka... ty si si zabudol u mna ponozky!",
                "Manzelka si dala na tvar bahnovu masku.Dva dni vyzerala lepsie,potom bahno opadalo...",
                "Ak na Teba niekto krici, usmej sa a maj ho v pici...",
                "Preco ma nocnik ucho? Aby pocul, keď sa cura vedla.",
                "Jozko, preco namacas tu stenu? Lekar mi vravel, ze si mam davat obklady na miesta, kde som sa udrel.",
                "80% zien sa uz nevydava, lebo prisli na to, ze pre 60 gramov klobasy sa nevyplaca chovat doma celu svinu.",
                "Skotacia deti na minovom poli, rozhadzuju rucickami, nozickami...",
                "Na recepcii sa chce ubytovat rodina Jebalovcov: Recepcny: Vase mena prosim.Pan Jebal: Ja som Jebal s manzelkou, dvoma dcerami a tuto synom. Recepcny: Ja mam v pici s kym jebete, zapisem Vas ako jednu velku skurvenu rodinu ",
                "mam ta rada ako zahradku kopat,kopat a kopat...",
                "Viete, aka je to najvacsia tma? Keď musite zapalit druhu zapalku, aby ste zistili, ci ta prva este hori",
                "Chcel som ti len povedat, ze vyzeras cim ďalej tym lepsie a cim blizsie tym horsie. .",
                "Keď som taku postavu ako je tvoja videl naposledy, dojili ju.",
                "Ako sa vola muz, ktory chce sex az na druhom rande? Pomaly!...",
                "Dezo chce spachat samovrazdu a preto vyskoci z okna z dvanasteho poschodia. Ako tak pada, pocita si poschodia, ktore uz presiel 12, 11, 10, 9, 8, 7, 8, 9, 10... docerta, zasa tie traky!",
                "Ta dnesna mladez je ale marnotratna hovori jedna spermia druhej, keď stekaju dole umyvadlom",
                "Milacik milujem ta tak, ze by som pre Teba skocil do najvacsieho ohna na svete, preplaval najvacsi ocean na svete a skocil aj do najhlbsej studne na svete.PS: Pridem zajtra k Tebe, ak nebude prsat.",
                "Ocko, co to mas take chlpate medzi nohami?To nic Jurko, to je iba jezko.Fiha, ten jezko ma ale penis.",
                "Vies jaky je rozdiel medzi motorovou pilo a toaletnym papierom???? ziadny jeden neopatrny pohyb a prsty su v riti...",
                "preco nohy smrdia? lebo rastu od rici!!!...",
                "Rozhovor dvoch chlapov: Vratil si mu ten noz? ano! A co povedal? Au!",
                "Zostanem s tebou v posteli iba chvilu. Pohladim tvoje intimne miesta, zacitis moju vonu a potom sa uz budem iba vznasat. Tvoj prd",
                "Vychodoslovensky policajti nasli v lese mrtvolu.Bola skareda a bez mozgu.Mam o teba strach,radsej mi prezvon",
                "Medved trtka kozu. Ty medved,akeho mas hrubeho a chlpateho. Kua,zabudol som si dat dole vevericku!",
                "Pride velmi tucna zena ku gynekologovi a horko-tazko sa usadi na kozu. Doktor pozera,pozera a po chvili hovori:Prepacte ,mohli by ste si prdnut? Aby som sa mohol zorientovat.",
                "Miesto rannej rozcvicky - strc kokota do picky!...",
                "Moze dostat medveď jarnu unavu, ak spal celu zimu?Ak si dal v septembri kavu.",
                "Na hasicskej stanici:- Akcia bola uspesne ukoncena. Poziar bol uhaseny, nezhorela ani jedna krava. Desat sa utopilo.",
                "Sedem rokov som nepil, nefajcil, nebehal za zenami. A potom som zacal chodit do zakladnej skoly.",
                "pride gay do masiarstva a pyta si klobasu, masiar sa ho pyta ,,chcete ju nakrajat? a gay mu odpovie ,,mate pocit ze mam prdelku na zetony?",
                "muzsky penis je to najubohejsie stvorenie na svete,nema ruky ani nohy,na hlave ma dieru,a ked si konecne mysli ze je na vrchole,tak je v pici :",
                "Otazka: Je dobre s manzelkou prehodit zopar slov po milovani? Odpoveď: Odporuca sa to. Veď na co uz mame tie mobilne telefony.",
                "zena vie sedemdesiat veci. Varenie a 69....",
                "kto druhemu jamu kope...zarobi a potom slope :",
                "Rozpravaju sa dve cibule a jedna hovori druhej:Fuj, ale ti smrdi z ust, co si jedla cesnak?",
                "heslo dna : fajcit sice budem ale pit neprestanem.",
                "Nepokusaj sa zial utopit v alkohole! Vie totiz plavat... ",
                "Ak ti vadi pri praci alkohol, nepracuj! ...",
                "Bodaj by sa ti EKG vyrovnalo!",
                "V ramci vladnej akcie SOS HLADOVy ROM Vam bol prideleny 1 rabujuci rom na vykrmovanie. Ak si ho nepridete vyzdvihnut do 24 hodin, bude Vam pridelena cela rodina!",
                "Chces byt debil? Buď sam sebou.",
                "Ide babka cez kolajnice a na zemi sa nieco leskne. Babka to zdvihne a to patkoruna.Ja mam teda stastny denTdn, tdn",
                "Jednooky a slepy idu s motorkou na diskoteku. Idu cez les a jednooky si na konari vypichne aj druhe oko. Vravi :No , a sme dosli! A slepy na to: caute baby!",
                "Mam sa dobre, ale zle to znasam.",
                "Kolekcia damskych nohaviciek: Monday, Tuesday, Wendsday, ...Kolekcia panskych slipov: January, February, March, ..",
                "Isiel som na ranajky. Ak sa nevratim do 12.30, tak aj na obed.",
                "Aka je to svata zena?Ta co lezi na krizoch a prijima telo pana.",
                "Priatelka mi nasla vo vrecku ruz. Povedal som jej, ze ju podvadzam s inou.Citil by som sa trapne, keby som jej mal povedat, ze predavam AVON.",
                "co je to totalne nic? Osupany balon.",
                "Vsetci okolo su blazni a zopar fajnoviek si hovori lietadlo. Ale iba ja som ponorka!",
                "Mami, poďme aj mi piknikovat! Ale Ivko, to su bezdomovci.",
                "Vyjdu dve kostry z hrobu a jedna si zoberie nahrobny kamen. Druha sa jej pyta: Naco ti to je? Musim mat predsa doklady."
        );

        return jokes.get(position);

    }
}