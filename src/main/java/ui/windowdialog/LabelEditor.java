package ui.windowdialog;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import dao.LabelDao;
import model.Label;
import model.Album;
import ui.MainPage;

public class LabelEditor extends Window {

    private static final long serialVersionUID = -4768711085883226548L;

    public LabelEditor(Item projectItem, LabelDao labelDao, Grid projectTable) {
        FormLayout formLayout = new FormLayout();
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener((Button.ClickListener) clickEvent -> close());
        final BeanFieldGroup<Label> binder = new BeanFieldGroup<>(Label.class);
        binder.setItemDataSource(projectItem);
        Button saveButton = new Button("Сохранить");
        saveButton.addClickListener((Button.ClickListener) clickEvent -> {
            if (binder.isValid()) {
                try {
                    BeanItem<Label> beanItem = (BeanItem<Label>) projectItem;
                    binder.commit();
                    if (projectItem.getItemProperty("id").getValue() != null) {
                        Label oldEntity = labelDao.read(projectItem.getItemProperty("id").getValue());
                        Label newEntity = beanItem.getBean();
                        newEntity.setId(oldEntity.getId());
                        labelDao.update(newEntity);
                        projectTable.clearSortOrder();
                        close();
                        return;
                    }
                    Label newEntity = beanItem.getBean();
                    labelDao.create(newEntity);
                    MainPage.LABEL_CONTAINER.addBean(newEntity);
                    close();
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Ошибки при заполнении формы", Notification.Type.WARNING_MESSAGE);
            }
        });
        formLayout.addComponent(binder.buildAndBind("Название", "name"));
        formLayout.addComponent(binder.buildAndBind("Немного слов о лейбле", "description"));
        formLayout.addComponent(saveButton);
        formLayout.addComponent(cancelButton);
        setContent(formLayout);

    }
}
