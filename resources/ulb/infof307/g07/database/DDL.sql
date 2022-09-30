--### Implémentation en Sqlite

--### All tables about ShoppingLists
--#Table ShoppingList

CREATE TABLE "ShoppingList" (
	"id"	        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"name"	        TEXT NOT NULL,
	"is_archived"	INTEGER CHECK(is_archived == 0 OR is_archived == 1)
);

--#Table Product

CREATE TABLE "Product" (
    "id"	            INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
    "name"	            TEXT NOT NULL,
    "category"          TEXT NOT NULL CHECK(category == "VEGETABLE" OR category == "SNACK" OR category == "DRINK" OR
                            category == "SAUCE" OR category == "DAIRY" OR category == "TOOL" OR category == "PREPARED" OR
                            category == "FISH" OR category == "MEAT" OR category == "FRUIT" OR category == "BAKERY"
                            OR category == "SPICE" OR category == "CARB")
);

--#Table ProductShoppingList (jointure)

CREATE TABLE "ProductShoppingList" (
	"product_id"	    INTEGER NOT NULL,
	"shopping_list_id"	INTEGER NOT NULL,
	"quantity"	        REAL NOT NULL CHECK(quantity >= 0),
	"UNIT"              TEXT CHECK(unit == "UNIT" OR unit == "ML" OR unit == "L" OR unit == "G" OR unit == "KG"),
                        FOREIGN KEY ("product_id") REFERENCES Product ("id") ON DELETE CASCADE,
                        FOREIGN KEY ("shopping_list_id") REFERENCES ShoppingList ("id") ON DELETE CASCADE
);
----------------------------------------------------------------------------------------------------------------------
--### All tables about Recipe
--# Table Ingredient

CREATE TABLE "Ingredient" (
	"id"	    INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"name"   	TEXT NOT NULL
);


--#Table Recipe

CREATE TABLE "Recipe" (
	"id"	        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"name"	        TEXT NOT NULL,
	"number_people"	INTEGER NOT NULL CHECK(number_people >= 0),
	"type"	        TEXT NOT NULL check(type == "ENTREE" OR type == "MEAL" OR type == "DESSERT" OR type == "DRINK" OR type == "STEWED" OR type == "SOUP"),
	"instructions"	TEXT NOT NULL,
	"style"	        TEXT NOT NULL CHECK(style == "MEAT" OR style == "FISH" OR style == "VEGGIE" OR style == "VEGAN"),
	"time"	        INTEGER NOT NULL CHECK(time > 0)
);

--#Table IngredientRecipe (jointure)

CREATE TABLE "IngredientRecipe" (
	"ingredient_id"	    INTEGER NOT NULL,
	"recipe_id"	        INTEGER NOT NULL,
	"quantity"	        REAL NOT NULL CHECK(quantity >= 0),
	"unit"	            TEXT CHECK(unit == "UNIT" OR unit == "ML" OR unit == "L" OR unit == "G" OR unit == "KG"),
                        FOREIGN KEY ("ingredient_id") REFERENCES Ingredient ("id") ON DELETE CASCADE,
                        FOREIGN KEY ("recipe_id") REFERENCES Recipe ("id") ON DELETE CASCADE
);
----------------------------------------------------------------------------------------------------------------------
--#Table ProductIngredient

CREATE TABLE "ProductIngredient" (
	"product_id"	INTEGER NOT NULL,
	"ingredient_id"	INTEGER NOT NULL,
	                FOREIGN KEY ("product_id") REFERENCES Product ("id") ON DELETE CASCADE,
	                FOREIGN KEY ("ingredient_id") REFERENCES Ingredient ("id") ON DELETE CASCADE
);

----------------------------------------------------------------------------------------------------------------------
--### All tables about Menus
--#Table Menu

CREATE TABLE "Menu" (
	"id"	    INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"name"	    TEXT
);

--#Table MenuRecipe (jointure)

CREATE TABLE "MenuRecipe" (
	"menu_id"	INTEGER NOT NULL,
	"recipe_id"	INTEGER NOT NULL,
                FOREIGN KEY ("menu_id") REFERENCES Menu ("id") ON DELETE CASCADE,
                FOREIGN KEY ("recipe_id") REFERENCES Recipe ("id") ON DELETE CASCADE
);

----------------------------------------------------------------------------------------------------------------------
--### All tables about Agendas

--#Table Agenda

CREATE TABLE "Agenda" (
	"id"	        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"name"	        TEXT NOT NULL,
	"date_begin"	TEXT NOT NULL,
	"date_end"	    TEXT NOT NULL
);

--#Table AgendaMenu (jointure)

CREATE TABLE "AgendaMenu" (
    "agenda_id"    INTEGER NOT NULL,
    "menu_id"      INTEGER NOT NULL,
    "date"         TEXT NOT NULL,
                   FOREIGN KEY ("menu_id") REFERENCES Menu ("id") ON DELETE CASCADE
);

----------------------------------------------------------------------------------------------------------------------
--### All tables about Stores

--#Table Store

CREATE TABLE "Store" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"name"	TEXT NOT NULL,
	"lat"	REAL NOT NULL,
	"lng"	REAL NOT NULL
);

--#Table StoreProduct (jointure)

CREATE TABLE "StoreProduct" (
	"store_id"	    INTEGER NOT NULL,
	"product_id"	INTEGER NOT NULL,
	"price"	            INTEGER NOT NULL CHECK(price >= 0),
                    FOREIGN KEY ("store_id") REFERENCES Store ("id") ON DELETE CASCADE,
                    FOREIGN KEY ("product_id") REFERENCES Product ("id") ON DELETE CASCADE
		
);

----------------------------------------------------------------------------------------------------------------------
--### All tables about Home

--#Table Home

CREATE TABLE "Home" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"lng"	REAL,
	"lat"	REAL
);

----------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------
---## Triggers

-- # After a delete on menu, we delete them from Agenda and MenuRecipe
DROP TRIGGER IF EXISTS "main"."trig_delete_menu";
CREATE TRIGGER trig_delete_menu
AFTER DELETE
ON Menu
FOR EACH ROW
    BEGIN
        DELETE FROM MenuRecipe WHERE menu_id = OLD.id;
		DELETE FROM AgendaMenu WHERE menu_id = OLD.id;
    END;

-- # After a delete on recipe, we delete the  corresponding ids from MenuRecipe
DROP TRIGGER IF EXISTS "main"."trig_delete_recipe";
CREATE TRIGGER trig_delete_recipe
AFTER DELETE
ON Recipe
FOR EACH ROW
    BEGIN
        DELETE FROM MenuRecipe WHERE recipe_id = OLD.id;
    END;

-- # After a delete on Agenda, we delete the  corresponding ids from AgendaMenu
DROP TRIGGER IF EXISTS "main"."trig_delete_agenda";
CREATE TRIGGER trig_delete_agenda
AFTER DELETE ON Agenda
FOR EACH ROW
    BEGIN
        DELETE FROM AgendaMenu WHERE agenda_id = OLD.id;
    END;


--## Trigger pour checker les names avant l'insertion

-- # check Agenda Name before insertion
DROP TRIGGER IF EXISTS "main"."trig_create_agenda";
CREATE TRIGGER trig_create_agenda
BEFORE INSERT
ON Agenda
BEGIN
    SELECT CASE WHEN ((SELECT id FROM Agenda WHERE name = NEW.name) IS NOT NULL) THEN
    RAISE(ABORT, 'This agenda name is already taken')
END;
END;

-- # check Recipe Name before insertion
DROP TRIGGER IF EXISTS "main"."trig_create_recipe";
CREATE TRIGGER trig_create_recipe
BEFORE INSERT
ON Recipe
BEGIN
    SELECT CASE WHEN ((SELECT id FROM Recipe WHERE name = NEW.name) IS NOT NULL) THEN
    RAISE(ABORT, 'This recipe name is already taken')
END;
END;


-- # check ShoppingList Name before insertion
DROP TRIGGER IF EXISTS "main"."trig_create_shoppingList";
CREATE TRIGGER trig_create_shoppingList
BEFORE INSERT
ON ShoppingList
BEGIN
    SELECT CASE WHEN ((SELECT id FROM ShoppingList WHERE name = NEW.name) IS NOT NULL) THEN
    RAISE(ABORT, 'This shopping list name is already taken')
END;
END;

-- # erase every home before insertion
DROP TRIGGER IF EXISTS "main"."trig_insert_home";
CREATE TRIGGER trig_insert_home
BEFORE INSERT
ON Home
BEGIN
    DELETE FROM Home;
END
