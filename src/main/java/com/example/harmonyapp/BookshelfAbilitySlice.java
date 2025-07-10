package com.example.harmonyapp;

import com.example.harmonyapp.model.Book;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.data.preferences.Preferences;
import ohos.data.preferences.PreferencesFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookshelfAbilitySlice extends AbilitySlice {
    private ListContainer listContainer;
    private Button switchViewButton;
    private boolean isGrid = true;
    private List<Book> bookList = new ArrayList<>();
    private Preferences preferences;
    private static final String BOOKS_KEY = "books";

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        DirectionalLayout layout = new DirectionalLayout(this);
        layout.setOrientation(DirectionalLayout.VERTICAL);
        layout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        layout.setPadding(20, 20, 20, 20);

        switchViewButton = new Button(this);
        switchViewButton.setText("切换视图");
        switchViewButton.setClickedListener(component -> {
            isGrid = !isGrid;
            updateListContainer();
        });
        layout.addComponent(switchViewButton);

        listContainer = new ListContainer(this);
        layout.addComponent(listContainer);
        listContainer.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        listContainer.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);

        setUIContent(layout);

        preferences = PreferencesFactory.getInstance().getPreferences(this, "bookshelf_prefs");
        loadBooks();
        updateListContainer();
    }

    private void loadBooks() {
        bookList.clear();
        String booksJson = preferences.getString(BOOKS_KEY, "");
        if (!booksJson.isEmpty()) {
            try {
                JSONArray array = new JSONArray(booksJson);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    bookList.add(new Book(
                        obj.optString("title"),
                        obj.optString("filePath"),
                        obj.optString("coverPath")
                    ));
                }
            } catch (JSONException e) {
                // ignore
            }
        }
    }

    private void updateListContainer() {
        listContainer.setItemProvider(new BookItemProvider(bookList, isGrid));
        listContainer.setItemClickedListener((container, component, pos, id) -> {
            Book book = bookList.get(pos);
            Intent intent = new Intent();
            intent.setParam("filePath", book.getFilePath());
            present(intent, ReaderAbility.class.getName());
        });
    }

    // 可扩展：添加书籍到书架
    private void saveBooks() {
        JSONArray array = new JSONArray();
        for (Book book : bookList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", book.getTitle());
                obj.put("filePath", book.getFilePath());
                obj.put("coverPath", book.getCoverPath());
            } catch (JSONException e) {
                // ignore
            }
            array.put(obj);
        }
        preferences.putString(BOOKS_KEY, array.toString());
        preferences.flushSync();
    }

    // 内部类：书籍适配器
    private static class BookItemProvider extends BaseItemProvider {
        private final List<Book> books;
        private final boolean isGrid;
        BookItemProvider(List<Book> books, boolean isGrid) {
            this.books = books;
            this.isGrid = isGrid;
        }
        @Override
        public int getCount() { return books.size(); }
        @Override
        public Object getItem(int i) { return books.get(i); }
        @Override
        public long getItemId(int i) { return i; }
        @Override
        public Component getComponent(int i, Component component, ComponentContainer parent) {
            DirectionalLayout itemLayout = new DirectionalLayout(parent.getContext());
            itemLayout.setOrientation(isGrid ? DirectionalLayout.VERTICAL : DirectionalLayout.HORIZONTAL);
            itemLayout.setWidth(isGrid ? 300 : ComponentContainer.LayoutConfig.MATCH_PARENT);
            itemLayout.setHeight(isGrid ? 400 : 120);
            Image cover = new Image(parent.getContext());
            cover.setWidth(100);
            cover.setHeight(140);
            // TODO: 加载封面图片，暂用默认
            cover.setPixelMap(ResourceTable.Media_ic_default_cover);
            Text title = new Text(parent.getContext());
            title.setText(books.get(i).getTitle());
            title.setTextSize(40);
            itemLayout.addComponent(cover);
            itemLayout.addComponent(title);
            return itemLayout;
        }
    }
}