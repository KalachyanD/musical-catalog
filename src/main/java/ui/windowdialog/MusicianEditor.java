package ui.windowdialog;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import dao.MusicianDao;
import model.Musician;
import ui.MainPage;

public class MusicianEditor extends Window {
    private static final long serialVersionUID = -7993306702939202239L;

    public MusicianEditor(Item userItem, MusicianDao musicianDao, Grid userTable) {
        FormLayout formLayout = new FormLayout();
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener((Button.ClickListener) clickEvent -> close());
        final BeanFieldGroup<Musician> binder = new BeanFieldGroup<>(Musician.class);
        binder.setItemDataSource(userItem);
        Button saveButton = new Button("Сохранить");
        saveButton.addClickListener((Button.ClickListener) clickEvent -> {
            if (binder.isValid()) {
                try {
                    BeanItem<Musician> beanItem = (BeanItem<Musician>) userItem;
                    binder.commit();
                    if (userItem.getItemProperty("id").getValue() != null) {
                        Musician oldEntity = musicianDao.read(userItem.getItemProperty("id").getValue());
                        Musician newEntity = beanItem.getBean();
                        newEntity.setId(oldEntity.getId());
                        musicianDao.update(newEntity);
                        userTable.clearSortOrder();
                        close();
                        return;
                    }
                    Musician newEntity = beanItem.getBean();
                    musicianDao.create(newEntity);
                    MainPage.MUSICIAN_CONTAINER.addBean(newEntity);
                    close();
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Ошибки при заполнении формы", Notification.Type.WARNING_MESSAGE);
            }
        });
        formLayout.addComponent(binder.buildAndBind("Название коллектива", "name"));
        formLayout.addComponent(binder.buildAndBind("Год создания", "yearCreated"));
        formLayout.addComponent(binder.buildAndBind("Количество участников", "countMembers"));
        formLayout.addComponent(saveButton);
        formLayout.addComponent(cancelButton);
        setContent(formLayout);

    }

}
