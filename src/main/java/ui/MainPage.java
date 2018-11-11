package ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.server.VaadinCDIServlet;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import dao.AlbumDao;
import dao.MusicianDao;
import dao.LabelDao;
import model.Label;
import model.Album;
import model.Musician;
import org.jetbrains.annotations.NotNull;
import ui.windowdialog.MusicianEditor;
import ui.windowdialog.AlbumEditor;
import ui.windowdialog.LabelEditor;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

@Theme("valo")
@CDIUI("")
public class MainPage extends UI {

    @NotNull
    private static final String ADD = "Добавить";
    @NotNull
    private static final String UPDATE = "Редактировать";
    @NotNull
    private static final String REMOVE = "Удалить";

    @NotNull
    private static final String LOOK = "Показать";

    @NotNull
    public static final String ALBUM_TABLE_NAME = "Альбомы";

    @NotNull
    public static final String MUSICIAN_TABLE_NAME = "Музыканты";

    @NotNull
    public static final String LABEL_TABLE_NAME = "Музыкальные лейблы";

    @NotNull
    public static final String ALL_ALBUMS_BY_MUSICIAN = "Альбомы по лейблу";

    @EJB
    private MusicianDao musicianDao;

    @EJB
    private AlbumDao albumDao;

    @EJB
    private LabelDao labelDao;

    @NotNull
    public static final BeanContainer<String, Album> ALBUM_BEAN_CONTAINER = new BeanContainer<>(Album.class);

    @NotNull
    public static final BeanContainer<String, Musician> MUSICIAN_CONTAINER = new BeanContainer<>(Musician.class);

    @NotNull
    public static final BeanContainer<String, Label> LABEL_CONTAINER = new BeanContainer<>(Label.class);


    @NotNull
    private final TabSheet tabsheet = new TabSheet();

    private Grid albumTable = new Grid();
    private Grid musicianTable = new Grid();
    private Grid labelTable = new Grid();
    private Grid queryYearTable = new Grid();

    @NotNull
    private final TextField year = new TextField();


    @NotNull
    private final Button lookYear = new Button(LOOK);

    @NotNull
    private final Button musicinsBut = new Button(LOOK);

    @NotNull
    private MenuBar menubar = new MenuBar();


    private final MenuBar.MenuItem file = menubar.addItem("Меню", null);


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        @NotNull final VerticalLayout layout = new VerticalLayout();
        initButtonSet();
        layout.addComponent(menubar);
        initTabSheet();
        layout.addComponent(tabsheet);

        int height = getCurrent().getPage().getBrowserWindowHeight() - 50;
        tabsheet.setHeight(String.valueOf(height));
        setContent(layout);
        this.setSizeFull();

        this.getPage().addBrowserWindowResizeListener((Page.BrowserWindowResizeListener) browserWindowResizeEvent -> {
            int height1 = getCurrent().getPage().getBrowserWindowHeight() - 50;
            tabsheet.setHeight(String.valueOf(height1));
            setContent(layout);
            setSizeFull();
        });

        lookYear.addClickListener((Button.ClickListener) clickEvent -> {

            Grid grid = queryYearTable;
            BeanContainer<String, Album> container = new BeanContainer<>(Album.class);
            container.removeAllItems();
            grid.setEditorEnabled(false);
            container.setBeanIdProperty("id");
            try {
                Label label = new Label();
                label.setName(year.getValue());
                container.addAll(albumDao.getAllByLabel(label));
            } catch (NumberFormatException e) {
                Notification.show("Введите год в формате yyyy", Notification.Type.ERROR_MESSAGE);
            }

            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            grid.setContainerDataSource(container);
            if (grid.getColumn("id") != null) {
                grid.setColumnOrder(
                        "id",
                        "name",
                        "countSong",
                        "musician",
                        "label",
                        "yearCreated"
                );
                grid.removeColumn("id");
            } else {
                grid.setColumnOrder(
                        "name",
                        "countSong",
                        "musician",
                        "label",
                        "yearCreated"
                );
            }
            grid.getColumn("name").setHeaderCaption("Название");
            grid.getColumn("countSong").setHeaderCaption("Количество песен");
            grid.getColumn("musician").setHeaderCaption("Музыкант");
            grid.getColumn("label").setHeaderCaption("Лейбл");
            grid.getColumn("yearCreated").setHeaderCaption("Год выхода альбома");
        });




    }


    private void initButtonSet() {
        file.addItem(ADD, addButton);
        file.addItem(REMOVE, removeButton);
        file.addItem(UPDATE, updateButton);
    }

    private void initTabSheet() {
        fillAlbumTable();
        albumTable.setSizeFull();
        @NotNull VerticalLayout ordersTabSheet = new VerticalLayout();
        ordersTabSheet.setCaption(ALBUM_TABLE_NAME);
        ordersTabSheet.addComponent(albumTable);
        ordersTabSheet.setSizeFull();
        tabsheet.addTab(ordersTabSheet, ALBUM_TABLE_NAME);
        ordersTabSheet.setMargin(true);

        fillMusicianTable();
        musicianTable.setSizeFull();
        @NotNull VerticalLayout projectTabSheet = new VerticalLayout();
        projectTabSheet.setCaption(MUSICIAN_TABLE_NAME);
        projectTabSheet.addComponent(musicianTable);
        projectTabSheet.setSizeFull();
        tabsheet.addTab(projectTabSheet, MUSICIAN_TABLE_NAME);
        projectTabSheet.setMargin(true);

        fillLabelTable();
        labelTable.setSizeFull();
        @NotNull VerticalLayout userProjectTabSheet = new VerticalLayout();
        userProjectTabSheet.setCaption(LABEL_TABLE_NAME);
        userProjectTabSheet.addComponent(labelTable);
        userProjectTabSheet.setSizeFull();
        tabsheet.addTab(userProjectTabSheet, LABEL_TABLE_NAME);
        userProjectTabSheet.setMargin(true);

        year.setImmediate(true);
        year.setInputPrompt("название");

        VerticalLayout queryTabSheet = new VerticalLayout();
        queryTabSheet.setSpacing(true);
        HorizontalLayout headTabSheet = new HorizontalLayout();
        queryTabSheet.setCaption(ALL_ALBUMS_BY_MUSICIAN);
        headTabSheet.addComponent(year);
        headTabSheet.addComponent(lookYear);
        headTabSheet.setSpacing(true);
        com.vaadin.ui.Label head = new com.vaadin.ui.Label("Все альбомы определенной группы");
        queryTabSheet.addComponent(head);
        queryTabSheet.addComponent(queryYearTable);
        int width = getCurrent().getPage().getBrowserWindowWidth();
        int height = getCurrent().getPage().getBrowserWindowHeight() - 200;
        queryYearTable.setWidth(String.valueOf(width - 15));
        queryYearTable.setHeight(String.valueOf(height));
        queryTabSheet.addComponent(headTabSheet);
        tabsheet.addTab(queryTabSheet, ALL_ALBUMS_BY_MUSICIAN);
        queryTabSheet.setMargin(true);

    }

    private void fillLabelTable() {
        LABEL_CONTAINER.removeAllItems();
        labelTable.setEditorEnabled(false);
        LABEL_CONTAINER.setBeanIdProperty("id");
        LABEL_CONTAINER.addAll(labelDao.getAll());
        labelTable.setSelectionMode(Grid.SelectionMode.SINGLE);
        labelTable.setContainerDataSource(LABEL_CONTAINER);
        labelTable.setColumnOrder(
                "id",
                "name",
                "description"
        );
        labelTable.removeColumn("id");
        labelTable.getColumn("name").setHeaderCaption("Название");
        labelTable.getColumn("description").setHeaderCaption("Немного слов о лейбле");
    }

    private void fillMusicianTable() {
        MUSICIAN_CONTAINER.removeAllItems();
        musicianTable.setEditorEnabled(false);
        MUSICIAN_CONTAINER.setBeanIdProperty("id");
        MUSICIAN_CONTAINER.addAll(musicianDao.getAll());
        musicianTable.setSelectionMode(Grid.SelectionMode.SINGLE);
        musicianTable.setContainerDataSource(MUSICIAN_CONTAINER);
        musicianTable.setColumnOrder(
                "id",
                "name",
                "yearCreated",
                "countMembers"
        );
        musicianTable.removeColumn("id");
        musicianTable.getColumn("name").setHeaderCaption("Название");
        musicianTable.getColumn("yearCreated").setHeaderCaption("Год создания группы");
        musicianTable.getColumn("countMembers").setHeaderCaption("Количество участников");
    }

    private void fillAlbumTable() {
        ALBUM_BEAN_CONTAINER.removeAllItems();
        albumTable.setEditorEnabled(false);
        ALBUM_BEAN_CONTAINER.setBeanIdProperty("id");
        ALBUM_BEAN_CONTAINER.addAll(albumDao.getAll());
        albumTable.setSelectionMode(Grid.SelectionMode.SINGLE);
        albumTable.setContainerDataSource(ALBUM_BEAN_CONTAINER);
        albumTable.setColumnOrder(
                "id",
                "name",
                "countSong",
                "musician",
                "label",
                "yearCreated"
        );
        albumTable.removeColumn("id");
        albumTable.getColumn("name").setHeaderCaption("Название");
        albumTable.getColumn("countSong").setHeaderCaption("Количество песен");
        albumTable.getColumn("musician").setHeaderCaption("Музыкант");
        albumTable.getColumn("label").setHeaderCaption("Лейбл");
        albumTable.getColumn("yearCreated").setHeaderCaption("Год выхода альбома");
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainPage.class, productionMode = false)
    public static class MyUIServlet extends VaadinCDIServlet {
    }

    private MenuBar.Command updateButton = (MenuBar.Command) selectedItem -> {
        String id = null;
        if (tabsheet.getSelectedTab().getCaption().equals(ALBUM_TABLE_NAME) && albumTable.getSelectedRow() != null) {
            id = (String) albumTable.getSelectedRow();
            final BeanItem<Album> newUserItem = new BeanItem<>(ALBUM_BEAN_CONTAINER.getItem(id).getBean());
            addWindow(new AlbumEditor(newUserItem, musicianDao, albumDao, labelDao, albumTable));
            albumTable.deselect(id);
        } else if (tabsheet.getSelectedTab().getCaption().equals(MUSICIAN_TABLE_NAME) && musicianTable.getSelectedRow() != null) {
            id = (String) musicianTable.getSelectedRow();
            final BeanItem<Musician> newCustomeItem = new BeanItem<>(MUSICIAN_CONTAINER.getItem(id).getBean());
            addWindow(new MusicianEditor(newCustomeItem, musicianDao, musicianTable));
            musicianTable.deselect(id);
        } else if (tabsheet.getSelectedTab().getCaption().equals(LABEL_TABLE_NAME) && labelTable.getSelectedRow() != null) {
            id = (String) labelTable.getSelectedRow();
            final BeanItem<Label> newTariffItem = new BeanItem<>(LABEL_CONTAINER.getItem(id).getBean());
            addWindow(new LabelEditor(newTariffItem, labelDao, labelTable));
            labelTable.deselect(id);
        } else {
            Notification.show("Не выбрана строка в таблице.", Notification.Type.HUMANIZED_MESSAGE);
        }
    };

    private MenuBar.Command removeButton = (MenuBar.Command) selectedItem -> {
        String id = null;
        if (tabsheet.getSelectedTab().getCaption().equals(ALBUM_TABLE_NAME) && albumTable.getSelectedRow() != null) {
            id = (String) albumTable.getSelectedRow();
            albumDao.delete(id);
            ALBUM_BEAN_CONTAINER.removeItem(id);
            albumTable.deselect(id);
        } else if (tabsheet.getSelectedTab().getCaption().equals(MUSICIAN_TABLE_NAME) && musicianTable.getSelectedRow() != null) {
            id = (String) musicianTable.getSelectedRow();
            if (albumDao.getAllByMusician(musicianDao.read(id)).size() > 0) {
                Notification.show("Сначала удалите все альбомы музыканта", Notification.Type.ERROR_MESSAGE);
                return;
            }
            musicianDao.delete(id);
            MUSICIAN_CONTAINER.removeItem(id);
            musicianTable.deselect(id);
        } else if (tabsheet.getSelectedTab().getCaption().equals(LABEL_TABLE_NAME) && labelTable.getSelectedRow() != null) {
            id = (String) labelTable.getSelectedRow();
            if (albumDao.getAllByLabel(labelDao.read(id)).size() > 0) {
                Notification.show("Сначала удалите все альбомы с этого лейбла", Notification.Type.ERROR_MESSAGE);
                return;
            }
            labelDao.delete(id);
            LABEL_CONTAINER.removeItem(id);
            labelTable.deselect(id);
        } else {
            Notification.show("Не выбрана строка в таблице.", Notification.Type.HUMANIZED_MESSAGE);
        }
    };

    private MenuBar.Command addButton = (MenuBar.Command) selectedItem -> {
        String id = null;
        if (tabsheet.getSelectedTab().getCaption().equals(ALBUM_TABLE_NAME)) {
            id = (String) albumTable.getSelectedRow();
            final BeanItem<Album> newUserOnProjectItem = new BeanItem<>(new Album());
            addWindow(new AlbumEditor(newUserOnProjectItem, musicianDao, albumDao, labelDao, albumTable));
            albumTable.deselect(id);
        } else if (tabsheet.getSelectedTab().getCaption().equals(MUSICIAN_TABLE_NAME)) {
            id = (String) musicianTable.getSelectedRow();
            final BeanItem<Musician> newUserItem = new BeanItem<>(new Musician());
            addWindow(new MusicianEditor(newUserItem, musicianDao, musicianTable));
            musicianTable.deselect(id);
        } else if (tabsheet.getSelectedTab().getCaption().equals(LABEL_TABLE_NAME)) {
            id = (String) labelTable.getSelectedRow();
            final BeanItem<Label> newProjectItem = new BeanItem<>(new Label());
            addWindow(new LabelEditor(newProjectItem, labelDao, labelTable));
            labelTable.deselect(id);
        }
    };

    @Override
    public int hashCode() {
        return super.hashCode() + 2;
    }
}