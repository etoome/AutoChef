package ulb.infof307.g07.database.dao.agenda;

import ulb.infof307.g07.models.agenda.Agenda;
import ulb.infof307.g07.models.agenda.exceptions.AgendaException;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public interface AgendaDAO {
    /**
     * Method who create an agenda in the DB with an object Agenda if there was an error or the agenda's name is already
     * taken in the DB, or DB error throw an AgendaException
     *
     * @param agenda:
     *            an agenda
     */
    void createAgenda(Agenda agenda) throws AgendaException;

    /**
     * method that gets all agenda's name in the DB
     *
     * @return Arraylist containing all the agenda's name
     */
    ArrayList<String> getAllAgendaName() throws AgendaException;

    /**
     * with the name given in parameter, selecting the corresponding agenda informations and creating an agenda based on
     * these.
     *
     * @param name:
     *            a String
     *
     * @return the object agenda created
     */
    Agenda getAgendaFromName(String name) throws AgendaException;

    /**
     * method that gets all menus based on the id of an agenda.
     *
     * @param id:
     *            the id of the agenda
     *
     * @return a HashMap containing all the menus
     */
    HashMap<LocalDate, ArrayList<Menu>> getMenusFromAgenda(int id) throws AgendaException;

    /**
     * Method that get the ID of an agenda in the database, from a string
     *
     * @param name
     *            the given name
     *
     * @return the unique ID of an agenda in the database
     */
    Integer getIdFromAgenda(String name) throws AgendaException;

    void checkAgendaNameAlreadyExist(String agendaName) throws AgendaException;

    boolean isAgendaNameInDb(String agendaName) throws AgendaException;

    void deleteAgendaWithId(int id) throws AgendaException;

    void deleteAgendaWithName(String name) throws AgendaException;

    void deleteAgendaMenu(int id) throws AgendaException;

    /**
     * Update information for an agenda and the related menus
     *
     * @param id:
     *            id of the agenda that we want to update
     * @param agenda:
     *            an object agenda
     *
     * @return if the agenda is updated => true else (=> false): there was an error or the agenda's name is already
     *         taken in the DB, so we cannot update
     */
    boolean updateAgenda(int id, Agenda agenda) throws AgendaException;

    ShoppingList generateShoppingList(Agenda agenda) throws AgendaException;

    ShoppingList generateShoppingList(Agenda agenda, String shoppingListName) throws AgendaException;
}
