package ulb.infof307.g07.database.dao.agenda;

import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.models.agenda.*;
import ulb.infof307.g07.models.agenda.exceptions.AgendaException;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AgendaDefaultDAO implements AgendaDAO {
    @Override
    public void createAgenda(Agenda agenda) throws AgendaException {
        var db = Database.getInstance();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int currentMenuId = db.query("INSERT INTO Agenda(name, date_begin, date_end) VALUES(?, ?, ?)",
                    List.of(agenda.getName(), formatter.format(agenda.getDateBegin()),
                            formatter.format(agenda.getDateEnd())),
                    Statement.RETURN_GENERATED_KEYS, result -> result.next() ? result.getInt(1) : null);
            for (LocalDate menusDay : agenda.getAllMenus().keySet()) {
                for (Menu menu : agenda.getAllMenusForDay(menusDay)) {
                    Menu.getDAO().createMenu(menu);
                    db.query("INSERT INTO AgendaMenu(agenda_id, menu_id, date) VALUES(?,?,?);", List.of(currentMenuId,
                            Menu.getDAO().getIdFromMenu(menu.getName()), formatter.format(menusDay)));
                }
            }
        } catch (DatabaseException | MenuException e) {
            throw new AgendaException("Error when creating an agenda ", e);
        }
    }

    @Override
    public ArrayList<String> getAllAgendaName() throws AgendaException {
        var db = Database.getInstance();

        try {
            var agendas = new ArrayList<String>();
            db.query("SELECT name FROM Agenda;", result -> {
                while (result.next()) {
                    agendas.add(result.getString("name"));
                }
                return agendas;
            });
            return agendas;
        } catch (DatabaseException e) {
            throw new AgendaException("Error when getting all agenda's name", e);
        }
    }

    @Override
    public Agenda getAgendaFromName(String name) throws AgendaException {
        var db = Database.getInstance();

        try {
            var id = getIdFromAgenda(name);
            assert id != null;
            Agenda agenda = db.query("SELECT * FROM Agenda WHERE id = ?", List.of(id), result -> {
                try {
                    return result.next() ? new Agenda(result.getString("name"),
                            DateRange.getDateFromString(result.getString("date_begin")),
                            DateRange.getDateFromString(result.getString("date_end"))) : null;
                } catch (AgendaException e) {
                    return null;
                }
            });
            if (agenda == null)
                throw new AgendaException("There is no agenda with this name");
            HashMap<LocalDate, ArrayList<Menu>> menus = getMenusFromAgenda(id);
            if (menus != null)
                agenda.setMenus(menus);
            return agenda;
        } catch (DatabaseException e) {
            throw new AgendaException("Error when getting an agenda from its name ", e);
        }
    }

    @Override
    public HashMap<LocalDate, ArrayList<Menu>> getMenusFromAgenda(int id) throws AgendaException {
        var db = Database.getInstance();

        try {
            HashMap<LocalDate, ArrayList<Menu>> menus = new HashMap<>();
            var content = db.query("SELECT menu_id, date FROM AgendaMenu WHERE agenda_id=? ORDER BY date ASC;",
                    List.of(id), result -> {
                        ArrayList<Menu> menu = new ArrayList<>();
                        String previousDate = null;
                        while (result.next()) {
                            if (previousDate == null) {
                                previousDate = result.getString("date");
                            }
                            var date = result.getString("date");
                            if (!Objects.equals(previousDate, date)) { // previousDate != date
                                menus.put(DateRange.getDateFromString(previousDate), menu);
                                menu = new ArrayList<>();
                            }
                            try {
                                menu.add(Menu.getDAO().getMenuFromId(result.getInt("menu_id")));
                            } catch (MenuException e) {
                                return null;
                            }
                            previousDate = date;
                        }
                        if (previousDate != null) {
                            menus.put(DateRange.getDateFromString(previousDate), menu);
                        }
                        return menus;
                    });
            if (content == null)
                throw new AgendaException("Couldn't load the menus from the agenda");
            return menus;
        } catch (DatabaseException | AgendaException e) {
            throw new AgendaException("Error when getting menus from an agenda ", e);
        }
    }

    @Override
    public Integer getIdFromAgenda(String name) throws AgendaException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM Agenda WHERE name=?;", List.of(name),
                    result -> result.next() ? result.getInt("id") : null);
        } catch (DatabaseException e) {
            throw new AgendaException("Error when getting id from an agenda ", e);
        }
    }

    @Override
    public void checkAgendaNameAlreadyExist(String agendaName) throws AgendaException {
        if (isAgendaNameInDb(agendaName))
            throw new AgendaException("An agenda with this name already exist");
    }

    @Override
    public boolean isAgendaNameInDb(String agendaName) throws AgendaException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT 1 FROM Agenda WHERE name=?;", List.of(agendaName), ResultSet::next);
        } catch (DatabaseException e) {
            return false; // if we change this to throw an error, we need to do a try catch in our if(check)
        }
    }

    @Override
    public void deleteAgendaWithId(int id) throws AgendaException {
        var db = Database.getInstance();

        try {
            db.begin();
            db.query("DELETE FROM Agenda WHERE id=?;", List.of(id));
            deleteAgendaMenu(id);
            db.commit();
        } catch (DatabaseException e) {
            throw new AgendaException("Error when deleting an agenda ", e);
        }
    }

    @Override
    public void deleteAgendaWithName(String name) throws AgendaException {
        deleteAgendaWithId(getIdFromAgenda(name));
    }

    @Override
    public void deleteAgendaMenu(int id) throws AgendaException {
        var db = Database.getInstance();

        try {
            db.query("DELETE FROM AgendaMenu WHERE agenda_id=?;", List.of((id)));
        } catch (DatabaseException e) {
            throw new AgendaException("Error when deleting a menu from an agenda ", e);
        }
    }

    @Override
    public boolean updateAgenda(int id, Agenda agenda) throws AgendaException {
        var db = Database.getInstance();

        try {
            // Check if this name is already taken or not
            if (isAgendaNameInDb(agenda.getName()) && getIdFromAgenda(agenda.getName()) != id) {
                return false;
            }
            db.begin();
            // Update Agenda
            db.query("UPDATE Agenda SET name=? , date_begin=?, date_end=? WHERE id=?;", List.of(agenda.getName(),
                    DateRange.getDateString(agenda.getDateBegin()), DateRange.getDateString(agenda.getDateEnd()), id));

            db.query("DELETE FROM AgendaMenu WHERE agenda_id=?;", List.of(id));

            // Insert into AgendaMenu
            for (LocalDate menuDate : agenda.getAllMenus().keySet()) {
                for (int i = 0; i < agenda.getAllMenus().get(menuDate).size(); i++) {
                    Menu.getDAO().createMenu(agenda.getAllMenus().get(menuDate).get(i));
                    db.query("INSERT INTO AgendaMenu(agenda_id, menu_id, date) VALUES(?,?,?);",
                            List.of(id,
                                    Menu.getDAO().getIdFromMenu(agenda.getAllMenus().get(menuDate).get(i).getName()),
                                    DateRange.getDateString(menuDate)));
                }
            }
            db.commit();
            return true;
        } catch (DatabaseException | MenuException e) {
            throw new AgendaException("Error updating an agenda ", e);
        }
    }

    @Override
    public ShoppingList generateShoppingList(Agenda agenda) throws AgendaException {
        return generateShoppingList(agenda, "Liste de course de l'agenda " + agenda.getName());
    }

    @Override
    public ShoppingList generateShoppingList(Agenda agenda, String shoppingListName) throws AgendaException {
        ShoppingList res = new ShoppingList(shoppingListName);
        var menus = getMenusFromAgenda(getIdFromAgenda(agenda.getName()));
        assert menus != null;

        for (ArrayList<Menu> list_menu : menus.values()) {
            for (Menu menu : list_menu) {
                try {
                    for (var product : Menu.getDAO().generateShoppingList(menu).getProducts().entrySet())
                        if (!res.hasProduct(product.getKey())) {
                            res.addProduct(product.getKey(), product.getValue());
                        }
                } catch (MenuException e) {
                    throw new AgendaException("Error when generating a shopping list from an agenda ", e);
                }
            }
        }
        return res;
    }
}
