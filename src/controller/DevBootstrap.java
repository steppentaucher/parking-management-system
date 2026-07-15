package controller;

import java.util.Arrays;
import java.util.List;

import model.Betreiber;
import model.Parkplatz;
import model.User;

/**
 * Kleines Hilfsprogramm für die Entwicklung/Testing.
 * Erzeugt einen Betreiber "Land Berlin" und legt zwei Parkplätze an.
 */
public class DevBootstrap {

    public static void main(String[] args) {
        PlattformManager manager = new PlattformManager();

        // 1) Nutzer registrieren (als Betreiber)
        User neu = manager.registriereNutzer("Land Berlin", "Berlin@Hauptstadt.de", "Betreiber");
        System.out.println("Registriert: " + neu.getName() + " <" + neu.getEmail() + ">");

        // 2) Login als dieser Nutzer (notwendig, damit parkplatzAnlegen() die Berechtigung hat)
        boolean loggedIn = manager.login("Berlin@Hauptstadt.de");
        System.out.println("Login erfolgreich: " + loggedIn);

        // 3) Parkplätze anlegen
        try {
            ParklatzValidierungUndAnlegen(manager, "BERL1", "Hauptbahnhof Parkhaus", "Berlin Hauptbahnhof", 200, 2.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "BERL2", "Brandenburger Tor Parkdeck", "Pariser Platz", 80, 3.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P1", "Deutsche Oper", "Bismarckstraße 35", 350, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P2", "Leibniz Kolonnaden", "Leibnizstraße 49", 368, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P3", "Los-Angeles-Platz", "Rankestraße 27-29", 210, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P4", "Am Zoobogen", "Budapester Straße 38", 640, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P5", "An der Gedächtniskirche", "Budapester Straße 45", 212, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P6", "Europacenter", "Nürnberger Straße 5-7", 1100, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P7", "Hardenbergplatz am Zoo", "Hardenbergplatz 1", 200, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P8", "Hotel Kempinski Plaza", "Uhlandstraße 181", 250, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P9", "KaDeWe Wittenbergplatz", "Passauer Straße 33-37", 990, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P10", "Knesebeckstraße", "Knesebeckstraße 72", 333, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P11", "Kudamm-Karree", "Kurfürstendamm 207", 945, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P12", "Ludwig-Erhard-Haus", "Fasanenstraße 85", 224, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P13", "Meinekestraße", "Meinekestraße 19", 1050, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P14", "Metropole Joachimsthaler", "Joachimsthaler Straße 37", 250, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P15", "Neues Kranzler-Eck", "Kantstraße 160", 560, 2.4, 2.4);
            ParklatzValidierungUndAnlegen(manager, "P16", "Uhland-Kant-Fasanenstraße", "Uhlandstraße 192", 500, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P17", "Wertheim Kudamm", "Kurfürstendamm 231", 500, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P18", "Messe Berlin", "Messedamm 22", 1400, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P19", "Parkhaus ICC", "Messedamm 11", 650, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P20", "Allee-Passage Friedrichshain", "Gabelsberger Straße 1", 296, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P21", "Plaza Passage", "Voigtstraße 38", 690, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P22", "Ring-Center 1", "Frankfurter Allee 111", 1000, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P23", "Dom-Aquarée", "Karl-Liebknecht-Straße 5", 616, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P24", "Parkhaus Dorotheenstraße", "Dorotheenstraße 30", 495, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P25", "Parkhaus Hauptbahnhof", "Clara-Jaschke-Straße 8", 860, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P26", "Friedrichstadt-Passagen Q 206", "Taubenstraße 14", 262, 4.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P27", "Friedrichstraße 50/55", "Friedrichstraße 50", 200, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P28", "Hotel Hilton Kronenstraße", "Kronenstraße 30", 400, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P29", "Hotel Westin Grand", "Behrenstraße 21", 600, 4.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P30", "Internationales Handelszentrum", "Dorotheenstraße 37", 457, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P31", "Alexa EKZ", "Grunerstraße 20", 1600, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P32", "Annenhof Garage", "Annenstraße 2", 462, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P33", "Georgenkirchstraße", "Georgenkirchstraße 10", 275, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P34", "Ostbahnhof Stralauer Platz", "Stralauer Platz 29", 500, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P35", "Rathauspassage", "Rathausstraße 5", 600, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P36", "Tiefgarage Staatsoper", "Unter den Linden 1", 463, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P37", "Allee Center Landsberger Allee", "Genslerstraße 5", 320, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P38", "Kulturbrauerei", "Sredzkistraße 1", 250, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P39", "Rathaus-Center", "Breite Straße 20", 840, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P40", "Schönhauser Allee Arkaden", "Schönhauser Allee 80", 325, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P41", "An der Philharmonie", "Herbert-von-Karajan-Straße 1", 320, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P42", "Sony Center", "Belvuestraße 3", 1600, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P43", "Daimler City Potsdamer Platz", "Ludwig-Beck-Straße 1", 6000, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P44", "S-Bahnhof Südkreuz", "Lotte-Laserstein-Straße 1", 208, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P45", "Bauhaus Schöneberg", "Bayreuther Straße 3", 360, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P46", "Kaiser Wilhelm Passage", "Feurigstraße 8", 266, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P47", "Kalckreuthstraße", "Kalckreuthstraße 4", 530, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P48", "Hotel Berlin Berlin", "Lützowplatz 17", 270, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P49", "Möbelhaus Hübner", "Genthiner Straße 41", 300, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P50", "Karstadt Hermannplatz", "Hermannplatz 1", 720, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P51", "Kindl Boulevard", "Mainzer Straße 2", 500, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P52", "Neukölln Arcaden", "Karl-Marx-Straße 66", 650, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P53", "Sinn-Leffers Donaustraße", "Donaustraße 10", 304, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P54", "Park Center Treptow", "Am Treptower Park 14", 1001, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P55", "Schloss-Straßen-Center", "Bornstraße 1", 365, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P56", "Rathaus Spandau", "Stabholzgarten 4", 300, 2.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P57", "Living Berlin", "Uhlandstraße 9-10", 158, 3.2, 3.2);
            ParklatzValidierungUndAnlegen(manager, "P58", "Plaza Friedrichshain", "Mildred-Harnack-Straße 11-13", 400, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P59", "Parkhaus Arena", "Tamara-Danz-Straße 11", 300, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P60", "Senefelderstraße", "Senefelderstraße 7-13", 802, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P61", "Car Park Cristal", "Schwanthalerstraße 36", 125, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P62", "Hacker Schwanthalerhöhe", "Schwanthalerstraße 113", 652, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P63", "Gollierstraße", "Gollierstraße 6", 180, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P64", "Theresienwiese Parkplatz", "Bavariaring 1", 1500, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P65", "Tiefgarage Hopfenpost", "Hopfenstraße 8", 180, 2.4, 2.4);
            ParklatzValidierungUndAnlegen(manager, "P66", "Tiefgarage Hotel ibis City", "Dachauer Straße 21", 587, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P67", "Karstadt-Parkhaus München", "Schützenstraße 14", 485, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P68", "Hotel Königshof", "Karlsplatz 25", 80, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P69", "Parkgarage Marsstraße", "Hirtenstraße 14", 700, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P70", "Alpina Parkhaus am Stachus", "Adolf-Kolping-Straße 10", 165, 4.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P71", "Sonnenhof-Parkgarage", "Landwehrstraße 10", 85, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P72", "Pschorr-Garage", "Altheimer Eck 14", 180, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P73", "Tiefgarage am Hauptbahnhof", "Bahnhofplatz 2", 213, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P74", "Färbergraben City-Parkhaus", "Färbergraben 5", 546, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P75", "Senefelderstraße 6", "Senefelderstraße 6", 245, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P76", "Parkgarage am Salvatorplatz", "Salvatorplatz 1", 418, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P77", "Tiefgarage vor der Oper", "Max-Joseph-Platz 4", 577, 4.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P78", "Tiefgarage Kempinski", "Marstallstraße 8", 90, 7.0, 7.0);
            ParklatzValidierungUndAnlegen(manager, "P79", "Parkhaus am Hofbräuhaus", "Hochbrückenstraße 9", 355, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P80", "Tiefgarage Marienplatz", "Marienplatz 1", 265, 6.5, 6.5);
            ParklatzValidierungUndAnlegen(manager, "P81", "Parkhaus Schrannenhalle", "Prälat-Zistl-Straße 1", 423, 4.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P82", "Park One Hilton City", "Rosenheimer Straße 15", 426, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P83", "Parkgarage Hofbräukeller", "Innere Wiener Straße 15", 117, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P84", "Parkhaus Elisenhof", "Luitpoldstraße 3", 451, 4.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P85", "Parkhaus Regerstraße", "Regerstraße 76", 180, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P86", "Parkhaus Rosenkavalierplatz", "Elektrastraße 3", 271, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P87", "Parkhaus Welfenhöfe", "Welfenstraße 62", 150, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P88", "City Parkhaus Köln", "Alte Wallgasse 31", 263, 2.8, 2.8);
            ParklatzValidierungUndAnlegen(manager, "P89", "Tiefgarage Rheinauhafen", "Harry-Blum-Platz 2", 1400, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P90", "LANXESS arena P1", "Opladener Straße 50", 1748, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P91", "LANXESS arena P2", "Opladener Straße 52", 266, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P92", "LANXESS arena P4", "Gummersbacher Straße 4", 309, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P93", "Parkhaus Dom", "Kurt-Hackenberg-Platz 2", 520, 2.4, 2.4);
            ParklatzValidierungUndAnlegen(manager, "P94", "Parkhaus Kaiser-Wilhelm-Ring", "Kaiser-Wilhelm-Ring 26", 600, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P95", "Parkhaus Klapperhof", "Im Klapperhof 13", 459, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P96", "Parkhaus Maastrichter Straße", "Maastrichter Straße 10", 684, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P97", "Tiefgarage Mediapark", "Im Mediapark 5", 1116, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P98", "Tiefgarage Ring Karree", "Im Klapperhof 49", 450, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P99", "Tiefgarage Rudolfplatz", "Habsburgerring 9-13", 400, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P100", "Tiefgarage Sparkasse KölnBonn", "Schaafenstraße 56", 240, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P101", "Köln Arcaden", "Barcelona-Allee 6", 1800, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P102", "Parkhaus Turmcenter", "Eschersheimer Landstraße 14", 350, 4.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P103", "P+R-Haus Borsigallee", "Borsigallee 1", 899, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P104", "Parkhaus Friedrichsheim", "Marienburgstraße 2", 227, 1.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P105", "Parkhaus Nordwestzentrum", "Lise-Meitner-Straße 2", 3085, 1.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P106", "Krankenhaus Höchst Parkhaus", "Gotenstraße 1", 472, 1.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P107", "Parkhaus Höchst", "Königsteiner Straße 1", 344, 0.5, 0.5);
            ParklatzValidierungUndAnlegen(manager, "P108", "Tiefgarage Grünhof", "Eschersheimer Landstraße 223", 338, 1.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P109", "Parkhaus Untermainanlage", "Wilhelm-Leuschner-Straße 5", 142, 2.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P110", "Tiefgarage Ordnungsamt", "Krifteler Straße 1", 143, 1.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P111", "City-Ost am Zoo", "Alfred-Brehm-Platz 1", 523, 1.7, 1.7);
            ParklatzValidierungUndAnlegen(manager, "P112", "Parkhaus Mousonturm", "Waldschmidtstraße 4", 252, 1.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P113", "Bürgerhaus Bornheim", "Arnsburger Straße 24", 179, 1.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P114", "Parkhaus WestendGate", "Hamburger Allee 2-10", 610, 3.9, 3.9);
            ParklatzValidierungUndAnlegen(manager, "P115", "Parkhaus Palmengarten", "Siesmayerstraße 61", 259, 1.0, 1.0);
            ParklatzValidierungUndAnlegen(manager, "P116", "Ladengalerie Bockenheimer Warte", "Adalbertstraße 10", 321, 1.8, 1.8);
            ParklatzValidierungUndAnlegen(manager, "P117", "Tiefgarage Börse", "Meisengasse 1", 920, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P118", "Parkhaus Messeturm", "Friedrich-Ebert-Anlage 49", 917, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P119", "Parkhaus Opernturm", "Bockenheimer Anlage 47", 580, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P120", "Congress Center Messe", "Theodor-Heuss-Allee 3", 461, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P121", "Parkhaus Schiller-Passage", "Taubenstraße 11", 415, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P122", "Parkhaus Kaiserplatz", "Kaiserstraße 22", 360, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P123", "Düsseldorf Arcaden", "Bachstraße 141", 832, 1.6, 1.6);
            ParklatzValidierungUndAnlegen(manager, "P124", "Altstadt Rheinufertunnel", "Rheinstraße 1", 390, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P125", "Carsch-Haus Parkhaus", "Kasernenstraße 10", 524, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P126", "Deutsch-Japanisches Center", "Immermannstraße 41", 550, 3.8, 3.8);
            ParklatzValidierungUndAnlegen(manager, "P127", "Q-Park Stadtmitte", "Liesegangstraße 14", 519, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P128", "Galeria Kaufhof Königsallee", "Heinrich-Heine-Allee 30", 431, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P129", "Kö-Galerie", "Königsallee 60", 777, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P130", "Parkhaus Kreuzstraße", "Kreuzstraße 27", 220, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P131", "Sevens", "Königsallee 56", 294, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P132", "Stilwerk", "Grünstraße 15", 464, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P133", "Trinkaus Galerie", "Königsallee 21", 146, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P134", "Q-Park GAP15", "Kasernenstraße 44", 541, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P135", "Q-Park Kö 59", "Königsallee 59", 307, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P136", "ParkCenter Kö", "Stresemannstraße 8", 700, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P137", "Schadow Arkaden", "Schadowstraße 11", 1000, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P138", "Neue Brücke/Königstraße", "Neue Brücke 8", 189, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P139", "Zeppelin-Carré", "Kronenstraße 20", 250, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P140", "Tiefgarage Kronprinzstraße", "Kronprinzstraße 26", 300, 3.6, 3.6);
            ParklatzValidierungUndAnlegen(manager, "P141", "Tiefgarage Stadtmitte", "Kronprinzstraße 6", 250, 3.6, 3.6);
            ParklatzValidierungUndAnlegen(manager, "P142", "Königsbau-Passagen", "Bolzstraße 5", 412, 3.0, 3.8);
            ParklatzValidierungUndAnlegen(manager, "P143", "Tiefgarage am Schlossplatz", "Stauffenbergstraße 5-1", 400, 3.3, 3.3);
            ParklatzValidierungUndAnlegen(manager, "P144", "Tiefgarage BW-Bank", "Kleiner Schlossplatz 11", 200, 3.3, 3.3);
            ParklatzValidierungUndAnlegen(manager, "P145", "City Garage", "Geschwister-Scholl-Straße 1", 150, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P146", "Tiefgarage Schillerplatz", "Schillerplatz 1", 200, 3.5, 3.5);
            ParklatzValidierungUndAnlegen(manager, "P147", "Marquart-Garage", "Stephanstraße 33", 180, 2.4, 2.9);
            ParklatzValidierungUndAnlegen(manager, "P148", "Rotebühlplatz", "Jobstweg 5", 298, 3.2, 3.2);
            ParklatzValidierungUndAnlegen(manager, "P149", "Stephangarage", "Kronenstraße 7", 250, 4.0, 4.0);
            ParklatzValidierungUndAnlegen(manager, "P150", "Bülow Carré", "Thouretstraße 8", 120, 3.2, 3.2);
            ParklatzValidierungUndAnlegen(manager, "P151", "Schlossgarten-Tiefgarage", "Schillerstraße 23", 300, 3.2, 3.2);
            ParklatzValidierungUndAnlegen(manager, "P152", "Galeria Kaufhof Stuttgart", "Königstraße 6", 250, 3.3, 3.3);
            ParklatzValidierungUndAnlegen(manager, "P153", "Parkhaus Steinstraße", "Steinstraße 4", 250, 3.8, 3.8);
            ParklatzValidierungUndAnlegen(manager, "P154", "Tiefgarage Olgastraße", "Olgastraße 86", 182, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P155", "Parkhaus Züblin", "Lazarettstraße 5", 597, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P156", "Tiefgarage Am Kursaal", "Königsplatz 1", 86, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P157", "Bildungshaus NeckarPark", "Hanna-Henning-Straße 3/1", 75, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P158", "Parkhaus Robert-Koch-Straße", "Robert-Koch-Straße 2", 81, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P159", "Parkhaus Vaihinger Markt", "Vaihinger Markt 10", 355, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P160", "Parkhaus Sillenbucher Markt", "Schempp-Straße 81", 144, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P161", "Tiefgarage Petersbogen", "Petersstraße 36-44", 74, 2.6, 2.6);
            ParklatzValidierungUndAnlegen(manager, "P162", "Burgplatz 5", "Burgplatz 5", 43, 2.6, 2.6);
            ParklatzValidierungUndAnlegen(manager, "P163", "Neumarkt Leipzig", "Neumarkt 30", 412, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P164", "Marktgalerie Leipzig", "Thomasgasse 2", 462, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P165", "Augustusplatz Leipzig", "Augustusplatz 14", 1244, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P166", "Am Bundesverwaltungsgericht", "Beethovenstraße 11", 311, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P167", "Fernbus-Terminal Hbf", "Sachsenseite 3", 550, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P168", "Höfe am Brühl", "Am Hallischen Tor 2", 820, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P169", "Martin-Luther-Ring", "Otto-Schill-Straße 3-5", 390, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P170", "Zentralstraße Leipzig", "Zentralstraße 1", 75, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P171", "Zoo Leipzig", "Parthenstraße 1", 1350, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P172", "Tiefgarage Wiener Platz", "Wiener Platz 1", 500, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P173", "Flughafen Dresden Parkhaus", "Wilhelmine-Reichard-Ring 1", 1500, 7.0, 7.0);
            ParklatzValidierungUndAnlegen(manager, "P174", "Dresden-Mitte", "Devrientstraße 1", 300, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P175", "Tiefgarage Semperoper", "Devrientstraße 10", 454, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P176", "Tiefgarage Altmarkt", "Altmarkt 1", 400, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P177", "Wöhrl/Media Markt", "Ludwigsplatz 1", 441, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P178", "Hauptmarkt Nürnberg", "Hans-Sachs-Platz 1", 300, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P179", "Augustinerhof Nürnberg", "Augustinerstraße 1", 200, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P180", "Tiefgarage Hans-Sachs-Platz", "Hans-Sachs-Platz 10", 525, 2.5, 2.5);
            ParklatzValidierungUndAnlegen(manager, "P181", "Jakobsmarkt", "Jakobsplatz 1", 452, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P182", "Maximum Nürnberg", "Färberstraße 1", 100, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P183", "Galeria Kaufhof Nürnberg", "Königstraße 1", 220, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P184", "City-Point Nürnberg", "Pfannenschmiedsgasse 1", 200, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P185", "Sterntor", "Grasersgasse 1", 476, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P186", "Herdentor Bremen", "Rembertiring 6", 350, 1.5, 1.5);
            ParklatzValidierungUndAnlegen(manager, "P187", "City Gate Bremen", "Bahnhofsplatz 1", 400, 2.4, 2.4);
            ParklatzValidierungUndAnlegen(manager, "P188", "BREPARK Mitte", "Am Brill 1", 1000, 3.0, 3.0);
            ParklatzValidierungUndAnlegen(manager, "P189", "BREPARK Stephani", "Stephanitorsteinweg 1", 407, 2.0, 2.0);
            ParklatzValidierungUndAnlegen(manager, "P190", "Sparkasse Campus Bremen", "Am Brill 2", 203, 1.0, 1.0);
        } catch (Exception e) {
            System.err.println("Fehler beim Anlegen: " + e.getMessage());
        }

        // 4) Ausgabe: alle Parkplätze der Plattform und des Betreibers
        System.out.println("\n--- Alle Parkplätze (Plattform) ---");
        for (var p : manager.getAlleParkplaetze()) {
            System.out.println(p.getId() + " - " + p.getBezeichnung() + " (" + p.getAdresse() + ")");
        }

        System.out.println("\n--- Parkplätze des Betreibers ---");
        Betreiber b = (Betreiber) manager.getAktuellerNutzer();
        for (var p : b.getMeineParkplaetze()) {
            System.out.println(p.getId() + " - " + p.getBezeichnung());
        }

        // Optional: Systemdaten speichern, damit der Betreiber und seine Parkplätze
        // nach einem Neustart erneut geladen werden können.
        try {
            manager.speichereSystemDaten();
            System.out.println("Systemdaten gespeichert.");
        } catch (Exception e) {
            System.err.println("Fehler beim Speichern der Systemdaten: " + e.getMessage());
        }
    }

    private static void ParklatzValidierungUndAnlegen(PlattformManager manager, String id, String bez, String adr, int kap, double satz, double sonder) {
	List<String> features = ermittleBeispielFeatures(id, bez, adr);

	Parkplatz p = new Parkplatz(id, bez, adr, kap, satz, sonder, features);
	manager.parkplatzAnlegen(p);
	System.out.println("Angelegt: " + p.getId() + " - " + p.getBezeichnung()
	+ " | Features: " + p.getFeaturesAlsText());
	}

	private static List<String> ermittleBeispielFeatures(String id, String bez, String adr) {
	String text = (id + " " + bez + " " + adr).toLowerCase();

	if (text.contains("hauptbahnhof") || text.contains("airport") || text.contains("flughafen")) {
	return Arrays.asList("E-Laden", "Überdacht", "Videoüberwacht");
	}

	if (text.contains("hilton") || text.contains("hotel") || text.contains("kempinski")) {
	return Arrays.asList("Überdacht", "Videoüberwacht");
	}

	if (text.contains("messe") || text.contains("arena") || text.contains("center") || text.contains("arcaden")) {
	return Arrays.asList("E-Laden", "Behindertengerecht", "Videoüberwacht");
	}

	if (text.contains("parkhaus") || text.contains("tiefgarage")) {
	return Arrays.asList("Überdacht", "Videoüberwacht");
	}

	if (text.contains("zoo") || text.contains("oper") || text.contains("philharmonie")) {
	return Arrays.asList("Behindertengerecht", "Videoüberwacht");
	}

	return Arrays.asList("Überdacht");
	}
}
