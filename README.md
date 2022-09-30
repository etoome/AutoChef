# <img src="resources/ulb/infof307/g07/images/icon_background.png" width="50" height="50" /> AutoChef

## App
Our app is divided into 4 main parts : Recipe, Agenda, Shopping list and Map.

### Recipe
- Create, modify and delete recipes;
- Import your own recipes in JSON format;
- Filter recipes with search bar;

### Agenda
- Create, modify and delete agendas;
- Create, modify and delete menus;
- Drag and drop recipes and menus into your agenda;
- Generate an agenda based on your own criteria (date, number of meals per day, type of meal, etc.);
- Generate a shopping list based on an agenda;
- Filter recipes and menus with search bar;

### Shopping list
- Create, modify and delete shopping lists;
- Add products;
- Archive shopping lists;
- Export shopping list into pdf format;

### Map
- Add and delete stores on the map;
- Modify home location;
- Add products in a store by hand;
- Import product in a store via a csv file;
- Find the closest store that contains all products from a shopping list;
- Find the cheapest store that contains all products from a shopping list;
- Show the shortest path to a store;

## Libs
- JavaFX
- JUnit
- OpenCSV
- iText
- Leaflet

## Database
- Sqlite3

## Development Setup

- Java SDK 17 (OpenJDK), refer to your platform for proper installation (or use IntelliJ to do it)
- [Maven](https://maven.apache.org/install.html)

## Workflow (Projects) :wrench:

Read the readme in the project tab, click on the button on the top right "Project details" to see it.
It describe general workflow for task resolution.

## Bug Reporting (I've found a bug on master, what now?)

### It's a small bug that I can fix quickly

- Don't create a new issue
- Create a new branch from master (not your current task) (name it "bug-XXX" so we know it is a bug branch)
- Write the bugfix (and ONLY the bugfix, no partial task code inside)
- Write regression test for the bug
- Open PR and write a short description of the bug

### Ok, maybe it's not such a small bug and I probably can't fix it on my own

- Create new issue
- In the description be precise:
  - What happens?
  - What is the expected behaviour
  - What have you tried quickly to fix it, but didn't work
  - What platform are you on
  - Steps to reproduce the bug
- Label the issue and don't forget the bug label specially
- Add it to milestone if it is a breaking bug
- If the bug is really bad, add the urgent label

## Authors
* **[etoome](https://github.com/etoome)**
* **[sebaarte](https://github.com/sebaarte)**
* **[edgardocuellar](https://github.com/edgardocuellar)**
* **[adiscepo](https://github.com/adiscepo)**
* **[aedubois](https://github.com/aedubois)**
* **[mathleng](https://github.com/mathleng)**
* **[maluyckx](https://github.com/maluyckx)**
* **[timarque](https://github.com/timarque)**
* **[vpiryns](https://github.com/vpiryns)**

## Acknoledgements
Thanks to **Diane BRISON** for our logo and product category images.
