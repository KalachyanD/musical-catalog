package ui.windowdialog;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import dao.AlbumDao;
import dao.LabelDao;
import dao.MusicianDao;
import model.Album;
import model.Label;
import model.Musician;
import ui.MainPage;


public class AlbumEditor extends Window {

    private static final long serialVersionUID = -4905260526000303401L;

    private JPAContainer<Label> labels = JPAContainerFactory.make(Label.class, "main");
    private ComboBox labelComboBox = new ComboBox("Лейбл", labels.getItemIds());

    private JPAContainer<Musician> musicians = JPAContainerFactory.make(Musician.class, "main");
    private ComboBox musicianComboBox = new ComboBox("Музыкант", musicians.getItemIds());

    public AlbumEditor(Item menuItem, MusicianDao musicianDao, AlbumDao albumDao, LabelDao labelDao, Grid userTable) {
        FormLayout formLayout = new FormLayout();
        for (Object id : labelComboBox.getItemIds()) {
            labelComboBox.setItemCaption(id, labelDao.read(id).getName());
        }
        for (Object id : musicianComboBox.getItemIds()) {
            Musician musician = musicianDao.read(id);
            musicianComboBox.setItemCaption(id, musician.getName());
        }
        BeanFieldGroup<Album> binder = new BeanFieldGroup<>(Album.class);
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener((Button.ClickListener) clickEvent -> close());
        Button saveButton = new Button("Сохранить");

        binder.setItemDataSource(menuItem);
        BeanItem<Album> beanItem = (BeanItem<Album>) menuItem;
        labelComboBox.setNullSelectionAllowed(false);
        if (beanItem.getBean().getLabel() != null) {
            labelComboBox.setValue(beanItem.getBean().getLabel().getId());
        }

        musicianComboBox.setNullSelectionAllowed(false);
        if (beanItem.getBean().getMusician() != null) {
            musicianComboBox.setValue(beanItem.getBean().getMusician().getId());
        }

        formLayout.addComponent(labelComboBox);
        formLayout.addComponent(musicianComboBox);
        saveButton.addClickListener((Button.ClickListener) clickEvent -> {
            if (binder.isValid()) {
                try {
                    BeanItem<Album> entityBeanItem = (BeanItem<Album>) menuItem;
                    binder.commit();
                    if (menuItem.getItemProperty("id").getValue() != null) {
                        Album oldEntity = albumDao.read(menuItem.getItemProperty("id").getValue());
                        Album newEntity = entityBeanItem.getBean();
                        Label label = labelDao.read(labelComboBox.getValue());
                        newEntity.setLabel(label);
                        Musician musician = musicianDao.read(musicianComboBox.getValue());
                        newEntity.setMusician(musician);
                        newEntity.setId(oldEntity.getId());
                        albumDao.update(newEntity);
                        userTable.clearSortOrder();
                        close();
                        return;
                    }
                    Album newEntity = entityBeanItem.getBean();
                    Label label = labelDao.read(labelComboBox.getValue());
                    newEntity.setLabel(label);
                    Musician musician = musicianDao.read(musicianComboBox.getValue());
                    newEntity.setMusician(musician);
                    albumDao.create(newEntity);
                    MainPage.ALBUM_BEAN_CONTAINER.addBean(newEntity);
                    close();
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Ошибки при заполнении формы", Notification.Type.WARNING_MESSAGE);
            }
        });

        formLayout.addComponent(binder.buildAndBind("Название альбома", "name"));
        formLayout.addComponent(binder.buildAndBind("Количество песен", "countSong"));
        formLayout.addComponent(binder.buildAndBind("Год выхода альбома", "yearCreated"));
        formLayout.addComponent(saveButton);
        formLayout.addComponent(cancelButton);
        setContent(formLayout);

    }

}
