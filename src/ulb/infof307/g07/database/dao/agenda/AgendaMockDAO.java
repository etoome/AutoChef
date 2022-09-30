package ulb.infof307.g07.database.dao.agenda;

import ulb.infof307.g07.models.agenda.Agenda;
import ulb.infof307.g07.models.agenda.exceptions.AgendaException;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AgendaMockDAO implements AgendaDAO {
    HashMap<Integer, Agenda> idToAgenda = new HashMap<>();

    @Override
    public boolean isAgendaNameInDb(String name) {
        for (Map.Entry<Integer, Agenda> entry : idToAgenda.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void createAgenda(Agenda agenda) {
        int id = idToAgenda.size() + 1;
        for (Map.Entry<Integer, Agenda> entry : idToAgenda.entrySet()) {
            if (entry.getValue().getName().equals(agenda.getName())) {
                return;
            }
        }
        idToAgenda.put(id, agenda);
    }

    @Override
    public ArrayList<String> getAllAgendaName() {
        ArrayList<String> res = new ArrayList<>();
        for (Map.Entry<Integer, Agenda> entry : idToAgenda.entrySet()) {
            res.add(entry.getValue().getName());
        }
        return res;
    }

    @Override
    public Agenda getAgendaFromName(String name) {
        for (Map.Entry<Integer, Agenda> entry : idToAgenda.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public HashMap<LocalDate, ArrayList<Menu>> getMenusFromAgenda(int id) {
        var res = idToAgenda.get(id);
        if (res == null) {
            return null;
        }
        return (HashMap<LocalDate, ArrayList<Menu>>) res.getAllMenus();
    }

    @Override
    public Integer getIdFromAgenda(String name) {
        for (Map.Entry<Integer, Agenda> entry : idToAgenda.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void checkAgendaNameAlreadyExist(String agenda_name) throws AgendaException {
        for (Map.Entry<Integer, Agenda> entry : idToAgenda.entrySet()) {
            if (entry.getValue().getName().equals(agenda_name)) {
                throw new AgendaException("agenda with same name already exists");
            }
        }
    }

    @Override
    public void deleteAgendaWithId(int id) {
        idToAgenda.remove(id);
    }

    @Override
    public void deleteAgendaWithName(String name) {
        for (Map.Entry<Integer, Agenda> entry : idToAgenda.entrySet()) {
            if (Objects.equals(entry.getValue().getName(), name)) {
                idToAgenda.remove(entry.getKey());
            }
        }
    }

    @Override
    public void deleteAgendaMenu(int id) {
        var temp = idToAgenda.get(id);
        for (Map.Entry<LocalDate, ArrayList<Menu>> entry : temp.getAllMenus().entrySet()) {
            temp.getAllMenus().put(entry.getKey(), new ArrayList<>());
        }
    }

    @Override
    public boolean updateAgenda(int id, Agenda agenda) {
        idToAgenda.put(id, agenda);
        return true;
    }

    @Override
    public ShoppingList generateShoppingList(Agenda agenda) throws AgendaException {
        ShoppingList sl = new ShoppingList("generated");
        try {
            ShoppingList.getDAO().fillShoppingListWithProducts(sl);
        } catch (ShoppingListException e) {
            throw new AgendaException("Couldn't generate the shopping list.", e);
        }
        return sl;
    }

    @Override
    public ShoppingList generateShoppingList(Agenda agenda, String shoppingListName) throws AgendaException {
        ShoppingList sl = new ShoppingList(shoppingListName);
        try {
            ShoppingList.getDAO().fillShoppingListWithProducts(sl);
        } catch (ShoppingListException e) {
            throw new AgendaException("Couldn't generate the shopping list.", e);
        }
        return sl;
    }
}
