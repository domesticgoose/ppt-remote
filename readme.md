# PPT-REMOTE

---

## What is ppt-remote for?
ppt-remote is a simple app to control your PowerPoint Presentation with your phone.

### Use

- Connect your Laptop to your Phone HotSpot
- Start the ppt-remote Application
- Navigate to the specified ip on your phone
- Jump between PowerPoint Pages via your phone

---

## Build

to run the following commands you must be at the project root

### Compilation

> javac -d target/production/ppt-remote/ -verbose -encoding UTF-8 src/ai/geissler/Main.java

#### If you want to run the App as a Java Archive:
> jar -cvfm ppt-remote.jar src/META-INF/MANIFEST.MF -C src/resource/ index.html -C target/production/ppt-remote/ ai/geissler/Main.class 

---

## Run

> java -jar ppt-remote.jar **port**

The default port is 80