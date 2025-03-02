/*
 * Copyright (c) 2012, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package eu.janinko.andaria.editor.components;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static javafx.scene.input.KeyCode.ENTER;


public class TextAreaTableCell<S> extends TableCell<S, String> {

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return list -> new TextAreaTableCell<S>();
    }

    private TextArea textArea;


    public TextAreaTableCell() {
        this.getStyleClass().add("text-field-table-cell");
    }


    @Override
    public void startEdit() {
        if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            if (textArea == null) {
                textArea = createTextField(this);
            }

            startEdit(this, null, null, textArea);
        }
    }

    static void startEdit(final Cell<String> cell, final HBox hbox, final Node graphic, final TextArea textField) {
        textField.setText(getItemText(cell));
        cell.setText(null);

        if (graphic != null) {
            hbox.getChildren().setAll(graphic, textField);
            cell.setGraphic(hbox);
        } else {
            cell.setGraphic(textField);
        }

        textField.selectAll();

        // requesting focus so that key input can immediately go into the
        // TextField (see RT-28132)
        textField.requestFocus();
    }

    private static String getItemText(Cell<String> cell) {
        return cell.getItem() == null ? "" : cell.getItem();
    }

    static TextArea createTextField(final Cell<String> cell) {
        String itemText = getItemText(cell);
        final TextArea textArea1 = new TextArea(itemText);
        if(itemText.contains("\n")) {
            textArea1.setPrefRowCount(6);
        }else {
            textArea1.setPrefRowCount(1);
        }


        textArea1.addEventFilter(KeyEvent.KEY_PRESSED, new TabAndEnterHandler(
                () -> cell.commitEdit(textArea1.getText()),
                textArea1
        ));

        textArea1.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                cell.cancelEdit();
                t.consume();
            }
        });
        return textArea1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        cancelEdit(this, null);
    }

    static void cancelEdit(Cell<String> cell, Node graphic) {
        cell.setText(getItemText(cell));
        cell.setGraphic(graphic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, null, null, textArea);
    }

    static void updateItem(final Cell<String> cell, final HBox hbox, final Node graphic, final TextArea textField) {
        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setGraphic(null);
        } else {
            if (cell.isEditing()) {
                if (textField != null) {
                    textField.setText(getItemText(cell));
                }
                cell.setText(null);

                if (graphic != null) {
                    hbox.getChildren().setAll(graphic, textField);
                    cell.setGraphic(hbox);
                } else {
                    cell.setGraphic(textField);
                }
            } else {
                cell.setText(getItemText(cell));
                cell.setGraphic(graphic);
            }
        }
    }


    private static class TabAndEnterHandler implements EventHandler<KeyEvent> {
        private KeyEvent recodedEvent;
        private final Runnable onEnterPressed;
        private final TextArea myTextArea;

        TabAndEnterHandler(Runnable onEnterPressed, TextArea myTextArea) {
            this.onEnterPressed = onEnterPressed;
            this.myTextArea = myTextArea;
        }

        @Override
        public void handle(KeyEvent event) {
            if (recodedEvent != null) {
                recodedEvent = null;
                myTextArea.setPrefRowCount(6);
                return;
            }

            Parent parent = myTextArea.getParent();
            if (parent != null) {
                if (event.getCode() == ENTER) {
                    if (event.isControlDown() || event.isShiftDown()) {
                        recodedEvent = recodeWithoutControlDown(event);
                        myTextArea.fireEvent(recodedEvent);
                    } else {
                        onEnterPressed.run();
                    }
                    event.consume();
                }
            }
        }

        private KeyEvent recodeWithoutControlDown(KeyEvent event) {
            return new KeyEvent(
                    event.getEventType(),
                    event.getCharacter(),
                    event.getText(),
                    event.getCode(),
                    false,
                    false,
                    event.isAltDown(),
                    event.isMetaDown()
            );
        }
    }
}
