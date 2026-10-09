#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#ifdef _WIN32
#include <windows.h>
#endif

int main(int argc, char *argv[]) {
// vi tjekker om det windows da mac og linux bruger utf-8 som standard
#ifdef _WIN32
    // vi gemmer i en int hvor mange argumenter vi tager imod
    int wargc;
    // vi henter kommandolinjen som en Unicode(utf-16) og så spilleter vi den op i et array af ord
    // vi gemmer så resultatet på addresen for wargc
    LPWSTR *wargv = CommandLineToArgvW(GetCommandLineW(), &wargc);

    // vi tjekker om der blev fundet nået
    if (wargv != NULL) {
        for (int i = 0; i < wargc && i < argc; i++) {
            // Find ud af hvor meget plads vi skal bruge til UTF-8 versionen altså hvor meget ekstra plads der skal være for æøå
            int utf8_len = WideCharToMultiByte(CP_UTF8, 0, wargv[i], -1, NULL, 0, NULL, NULL);
            // vi bruger så memory allocation til at få den mængde plads vi udregnede
            char *utf8_arg = malloc(utf8_len);
            // Udfør selve konverteringen fra Unicode til UTF-8
            WideCharToMultiByte(CP_UTF8, 0, wargv[i], -1, utf8_arg, utf8_len, NULL, NULL);
            argv[i] = utf8_arg; // Overskriv det gamle ANSI-argument med vores nye UTF-8 argument
        }
        // vi befrier den hukommelse vi brugte nu da vi er færdig med det
        LocalFree(wargv);
    }
    // Sæt terminalens output til at bruge UTF-8, så Java læser æøå korrekt
    SetConsoleOutputCP(CP_UTF8);
#endif

    // Tjek at brugeren har givet os de nødvendige parametre (Start og Slut)
    if (argc < 3) {
        printf("Error: Please provide at least TWO arguments! (e.g. \"Tivoli\" \"Nyhavn\" [optional: \"small\"|\"large\"])\n");
        return 1;
    }

    char* startTerm = argv[1];
    char* endTerm = argv[2];

    // Standardkortet er vores store københavn-fil, medmindre "small" tilføjes som 3. parameter
    const char* filename = "src/main/resources/copenhagen.osm";
    if (argc > 3 && (strcmp(argv[3], "small") == 0 || strcmp(argv[3], "test") == 0)) {
        filename = "src/main/resources/test_map.osm";
    }

    // Prøv at åbne kort-filen
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        // Hvis vi kører fra /bin mappen, skal vi gå et mappe-niveau tilbage ("../")
        char fallback[256];
        sprintf(fallback, "../%s", filename);
        file = fopen(fallback, "r");
    }
    if (file == NULL) {
        printf("Error: Could not open file %s\n", filename);
        return 1;
    }

    char line[4096]; // Buffer til at læse én linje ad gangen fra filen
    double startLat = 0.0, startLon = 0.0;
    double endLat = 0.0, endLon = 0.0;

    // Byg søgestrengene på forhånd, f.eks. leder vi efter teksten: v="Tivoli"
    char startTarget[150], endTarget[150];
    sprintf(startTarget, "v=\"%s\"", startTerm);
    sprintf(endTarget, "v=\"%s\"", endTerm);

    double currentLat = 0.0, currentLon = 0.0;

    // --- GENNEMLØB 1: Find koordinaterne for Start- og Slutattraktionen ---
    while (fgets(line, sizeof(line), file)) {
        // Når vi støder på et nyt punkt (node), udtrækker vi dets bredde- og længdegrad
        if (strstr(line, "<node")) {
            char *latPtr = strstr(line, "lat=\"");
            char *lonPtr = strstr(line, "lon=\"");
            if (latPtr) sscanf(latPtr, "lat=\"%lf\"", &currentLat);
            if (lonPtr) sscanf(lonPtr, "lon=\"%lf\"", &currentLon);
        }

        // Tjekker om den nuværende linje indeholder navnet på vores start-attraktion
        if (startLat == 0.0 && strstr(line, startTarget) != NULL) {
            startLat = currentLat;
            startLon = currentLon;
        }

        // Tjekker om den nuværende linje indeholder navnet på vores slut-attraktion
        if (endLat == 0.0 && strstr(line, endTarget) != NULL) {
            endLat = currentLat;
            endLon = currentLon;
        }

        // Hvis vi har fundet begge, behøver vi ikke søge mere i første gennemløb
        if (startLat != 0.0 && endLat != 0.0) {
            break;
        }
    }

    // Hvis brugeren har skrevet et navn der ikke findes på kortet, stopper vi her
    if (startLat == 0.0 || endLat == 0.0) {
        fclose(file);
        return 0;
    }

    // Udregn vores "Bounding Box" (den firkantede zone vi vil søge i)
    // Vi finder de henholdsvis mindste og største koordinater mellem start og slut
    double minLat = (startLat < endLat) ? startLat : endLat;
    double maxLat = (startLat > endLat) ? startLat : endLat;
    double minLon = (startLon < endLon) ? startLon : endLon;
    double maxLon = (startLon > endLon) ? startLon : endLon;

    // Spol filen helt tilbage til toppen, så vi kan starte forfra
    rewind(file);

    // --- GENNEMLØB 2: Find alle attraktioner inde i Bounding Boxen ---
    currentLat = 0.0;
    currentLon = 0.0;
    char currentName[256] = ""; // Holder midlertidigt navnet på det punkt vi kigger på

    while (fgets(line, sizeof(line), file)) {
        // Igen, opdater vores nuværende koordinater når vi ser en ny <node>
        if (strstr(line, "<node")) {
            char *latPtr = strstr(line, "lat=\"");
            char *lonPtr = strstr(line, "lon=\"");
            if (latPtr) sscanf(latPtr, "lat=\"%lf\"", &currentLat);
            if (lonPtr) sscanf(lonPtr, "lon=\"%lf\"", &currentLon);
            currentName[0] = '\0'; // Nulstil navnet for hver ny node
        }

        // Hvis punktet har et navn, gemmer vi navnet (f.eks. k="name" v="Rundetårn")
        if (strstr(line, "k=\"name\"")) {
            char *vPtr = strstr(line, "v=\"");
            if (vPtr) {
                // Udtræk alt imellem gåseøjnene og gem det i currentName
                sscanf(vPtr, "v=\"%[^\"]\"", currentName);
            }
        }

        // Tjek om punktet er markeret som turistattraktion (k="tourism")
        // OG tjek om punktets koordinater ligger strengt inde i vores Bounding Box
        if (strstr(line, "k=\"tourism\"") && currentLat < maxLat && currentLat > minLat && currentLon < maxLon && currentLon > minLon) {
            
            // Tjek at punktet faktisk har et navn, og at det ikke bare er start- eller slutattraktionen
            if (strlen(currentName) > 0) {
                if (strcmp(currentName, startTerm) != 0 && strcmp(currentName, endTerm) != 0) {
                    
                    // Print attraktionen, så vores Java Spring Boot server kan læse det
                    printf("%s\n", currentName);
                    
                    // Nulstil navnet, så vi ikke udskriver den samme attraktion to gange
                    currentName[0] = '\0';
                }
            }
        }
    }

    // Ryd op og luk filen
    fclose(file);
    return 0;
}
