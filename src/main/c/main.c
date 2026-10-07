#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char *argv[]) {
    if (argc < 3) {
        printf("Error: Please provide at least TWO arguments! (e.g. \"Tivoli\" \"Nyhavn\" [optional: \"small\"|\"large\"])\n");
        return 1;
    }

    char* startTerm = argv[1];
    char* endTerm = argv[2];

    // Default to large map (copenhagen.osm) unless 3rd argument is "small" or "test"
    const char* filename = "src/main/resources/copenhagen.osm";
    if (argc > 3 && (strcmp(argv[3], "small") == 0 || strcmp(argv[3], "test") == 0)) {
        filename = "src/main/resources/test_map.osm";
    }

    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        char fallback[256];
        sprintf(fallback, "../%s", filename);
        file = fopen(fallback, "r");
    }
    if (file == NULL) {
        printf("Error: Could not open file %s\n", filename);
        return 1;
    }

    char line[4096];
    double startLat = 0.0, startLon = 0.0;
    double endLat = 0.0, endLon = 0.0;

    char startTarget[150], endTarget[150];
    sprintf(startTarget, "v=\"%s\"", startTerm);
    sprintf(endTarget, "v=\"%s\"", endTerm);

    double currentLat = 0.0, currentLon = 0.0;

    // --- PASS 1: Find Start and End Coordinates ---
    while (fgets(line, sizeof(line), file)) {
        if (strstr(line, "<node")) {
            char *latPtr = strstr(line, "lat=\"");
            char *lonPtr = strstr(line, "lon=\"");
            if (latPtr) sscanf(latPtr, "lat=\"%lf\"", &currentLat);
            if (lonPtr) sscanf(lonPtr, "lon=\"%lf\"", &currentLon);
        }

        if (startLat == 0.0 && strstr(line, startTarget) != NULL) {
            startLat = currentLat;
            startLon = currentLon;
        }

        if (endLat == 0.0 && strstr(line, endTarget) != NULL) {
            endLat = currentLat;
            endLon = currentLon;
        }

        if (startLat != 0.0 && endLat != 0.0) {
            break;
        }
    }

    if (startLat == 0.0 || endLat == 0.0) {
        fclose(file);
        return 0;
    }

    double minLat = (startLat < endLat) ? startLat : endLat;
    double maxLat = (startLat > endLat) ? startLat : endLat;
    double minLon = (startLon < endLon) ? startLon : endLon;
    double maxLon = (startLon > endLon) ? startLon : endLon;

    rewind(file);

    // --- PASS 2: Find all attractions inside the bounding box ---
    currentLat = 0.0;
    currentLon = 0.0;
    char currentName[256] = "";

    while (fgets(line, sizeof(line), file)) {
        if (strstr(line, "<node")) {
            char *latPtr = strstr(line, "lat=\"");
            char *lonPtr = strstr(line, "lon=\"");
            if (latPtr) sscanf(latPtr, "lat=\"%lf\"", &currentLat);
            if (lonPtr) sscanf(lonPtr, "lon=\"%lf\"", &currentLon);
            currentName[0] = '\0';
        }

        if (strstr(line, "k=\"name\"")) {
            char *vPtr = strstr(line, "v=\"");
            if (vPtr) {
                sscanf(vPtr, "v=\"%[^\"]\"", currentName);
            }
        }

        if (strstr(line, "k=\"tourism\"") && currentLat < maxLat && currentLat > minLat && currentLon < maxLon && currentLon > minLon) {
            if (strlen(currentName) > 0) {
                if (strcmp(currentName, startTerm) != 0 && strcmp(currentName, endTerm) != 0) {
                    printf("%s\n", currentName);
                    currentName[0] = '\0';
                }
            }
        }
    }

    fclose(file);
    return 0;
}
