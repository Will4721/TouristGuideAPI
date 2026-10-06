#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char *argv[]) {
    const char* filename = "src/main/resources/test_map.osm";

    // 1. We now need TWO search terms!
    char* startTerm = "";
    char* endTerm = "";

    if(argc > 2){
        startTerm = argv[1];
        endTerm = argv[2];
        printf("Searching for Start: %s, and End: %s\n", startTerm, endTerm);
    } else {
        printf("Error: Please provide TWO program arguments! (e.g. \"Tivoli\" \"Nyhavn\")\n");
        return 1;
    }

    FILE *file = fopen(filename, "r");
    if (file == NULL) file = fopen("../src/main/resources/copenhagen.osm", "r");
    if (file == NULL) {
        printf("Error: Could not open file %s\n", filename);
        return 1;
    }

    printf("Pass 1: Finding coordinates for %s and %s... (Wait ~15s)\n", startTerm, endTerm);
    fflush(stdout);

    char line[2048];
    double startLat = 0.0, startLon = 0.0;
    double endLat = 0.0, endLon = 0.0;
    
    char startTarget[100], endTarget[100];
    sprintf(startTarget, "v=\"%s\"", startTerm);
    sprintf(endTarget, "v=\"%s\"", endTerm);

    double currentLat = 0.0, currentLon = 0.0;
    char *token;

    // --- PASS 1: Find Start and End Coordinates ---
    while (fgets(line, sizeof(line), file)) {
        if(strstr(line, "<node")){
            currentLat = 0.0; currentLon = 0.0;
        }

        // strtok modifies the string, so we must make a copy of the line!
        char lineCopy[2048];
        strcpy(lineCopy, line);
        
        token = strtok(lineCopy, " ");
        while(token != NULL){
            if (strncmp(token, "lat=\"", 5) == 0) sscanf(token, "lat=\"%lf\"", &currentLat);
            if (strncmp(token, "lon=\"", 5) == 0) sscanf(token, "lon=\"%lf\"", &currentLon);

            if(strncmp(token, startTarget, strlen(startTarget)) == 0) {
                startLat = currentLat; startLon = currentLon;
                printf("Found %s at %lf, %lf\n", startTerm, startLat, startLon);
            }
            if(strncmp(token, endTarget, strlen(endTarget)) == 0) {
                endLat = currentLat; endLon = currentLon;
                printf("Found %s at %lf, %lf\n", endTerm, endLat, endLon);
            }
            token = strtok(NULL, " ");
        }
    }

    printf("\nPass 2: Finding attractions BETWEEN them... (Wait ~15s)\n");
    fflush(stdout);

    // TODO 1: Figure out the Min and Max Latitude/Longitude between Start and End!
    // Hint: double minLat = (startLat < endLat) ? startLat : endLat;
    double minLat = (startLat < endLat) ? startLat : endLat;
    double maxLat = (startLat > endLat) ? startLat : endLat;
    double minLon = (startLon < endLon) ? startLon : endLon;
    double maxLon = (startLon > endLon) ? startLon : endLon;

    // Rewind the file back to the beginning so we can read it a second time!
    rewind(file);

    // --- PASS 2: Find all attractions inside the bounding box ---
    while (fgets(line, sizeof(line), file)) {
        if(strstr(line, "<node")){
            currentLat = 0.0; currentLon = 0.0;
        }

        char lineCopy[2048];
        strcpy(lineCopy, line);
        char name[100];
        token = strtok(lineCopy, " ");
        while(token != NULL){
            if (strncmp(token, "lat=\"", 5) == 0) sscanf(token, "lat=\"%lf\"", &currentLat);
            if (strncmp(token, "lon=\"", 5) == 0) sscanf(token, "lon=\"%lf\"", &currentLon);

            // TODO 2: If we see a tag like k="tourism" v="museum", check if currentLat/currentLon 
            // is INSIDE your minLat/maxLat box. If it is, print it!
            if(strncmp(token, "k=\"name\"",7)==0){
                token = strtok(NULL, " ");
                strcpy(name, token);

            }
            if(strncmp(token, "k=\"tourism\"",10)==0 && currentLat < maxLat && currentLat > minLat && currentLon < maxLon && currentLon > minLon){
                token = strtok(NULL, " ");
                printf("%s",name);
            }else{
            token = strtok(NULL, " ");
            }
        }
    }

    fclose(file);
    printf("Finished processing.\n");
    return 0;
}
