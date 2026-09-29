from xml.etree import ElementTree as ET
from pathlib import Path
p=Path("pom.xml")
ns = {"m":"http://maven.apache.org/POM/4.0.0"}
tree = ET.parse(p)
root = tree.getroot()
dependencies = root.find("m:dependencies", ns)
if dependencies is None:
    dependencies = ET.SubElement(root, "{http://maven.apache.org/POM/4.0.0}dependencies")
exists=False
for dep in dependencies.findall("m:dependency", ns):
    gid = dep.find("m:groupId", ns)
    aid = dep.find("m:artifactId", ns)
    if gid is not None and aid is not None and gid.text=="org.flywaydb" and aid.text=="flyway-postgresql":
        exists=True
        break
if not exists:
    dep = ET.SubElement(dependencies, "{http://maven.apache.org/POM/4.0.0}dependency")
    gid = ET.SubElement(dep, "{http://maven.apache.org/POM/4.0.0}groupId")
    gid.text="org.flywaydb"
    aid = ET.SubElement(dep, "{http://maven.apache.org/POM/4.0.0}artifactId")
    aid.text="flyway-postgresql"
    ver = ET.SubElement(dep, "{http://maven.apache.org/POM/4.0.0}version")
    ver.text="10.6.0"
    tree.write(p, encoding="utf-8", xml_declaration=True)
    print("inserted")
else:
    print("present")
