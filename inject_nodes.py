import os

nodes_xml = """
  <node id="-1" lat="55.6736" lon="12.5681"><tag k="name" v="Tivoli"/><tag k="tourism" v="theme_park"/></node>
  <node id="-2" lat="55.6929" lon="12.5993"><tag k="name" v="Den Lille Havfrue"/><tag k="tourism" v="artwork"/></node>
  <node id="-3" lat="55.7029" lon="12.5724"><tag k="name" v="Parken"/><tag k="tourism" v="stadium"/></node>
  <node id="-4" lat="55.9691" lon="12.5434"><tag k="name" v="Lousiana"/><tag k="tourism" v="museum"/></node>
  <node id="-5" lat="55.6719" lon="12.5220"><tag k="name" v="Københavns Zoo"/><tag k="tourism" v="zoo"/></node>
  <node id="-6" lat="55.6736" lon="12.5996"><tag k="name" v="Christiania"/><tag k="tourism" v="attraction"/></node>
  <node id="-7" lat="55.6814" lon="12.5759"><tag k="name" v="Rundetårn"/><tag k="tourism" v="viewpoint"/></node>
  <node id="-8" lat="55.6795" lon="12.5894"><tag k="name" v="Nyhavn"/><tag k="tourism" v="attraction"/></node>
  <node id="-9" lat="55.6762" lon="12.5806"><tag k="name" v="Christiansborg Slot"/><tag k="tourism" v="museum"/></node>
  <node id="-10" lat="55.6841" lon="12.5933"><tag k="name" v="Amalienborg Slot"/><tag k="tourism" v="museum"/></node>
</osm>
"""

file_path = "src/main/resources/copenhagen.osm"

with open(file_path, "r+", encoding="utf-8") as f:
    # Seek to near the end of the file
    f.seek(0, os.SEEK_END)
    pos = f.tell() - 200
    if pos < 0: pos = 0
    f.seek(pos)
    
    # Read the end to find exactly where </osm> starts
    content = f.read()
    osm_index = content.rfind("</osm>")
    
    if osm_index != -1:
        # Seek to the exact start of </osm>
        f.seek(pos + osm_index)
        # Overwrite from here with our new nodes (which includes </osm> at the end)
        f.write(nodes_xml)
        f.truncate()
        print("Successfully injected 10 database attractions into copenhagen.osm!")
    else:
        print("Could not find </osm> at the end of the file.")
